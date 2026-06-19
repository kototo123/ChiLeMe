package com.chileme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chileme.common.exception.BusinessException;
import com.chileme.dto.CheckInDTO;
import com.chileme.dto.PostDTO;
import com.chileme.entity.CheckIn;
import com.chileme.entity.User;
import com.chileme.entity.ScoreLog;
import com.chileme.enums.CheckInStatusEnum;
import com.chileme.enums.ScoreTypeEnum;
import com.chileme.mapper.CheckInMapper;
import com.chileme.mapper.UserMapper;
import com.chileme.mapper.ScoreLogMapper;
import com.chileme.service.AIService;
import com.chileme.service.CheckInService;
import com.chileme.service.PostService;
import com.chileme.service.RankService;
import com.chileme.vo.CalendarVO;
import com.chileme.vo.CheckInVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final CheckInMapper checkInMapper;
    private final UserMapper userMapper;
    private final ScoreLogMapper scoreLogMapper;
    private final AIService aiService;
    private final PostService postService;
    private final RankService rankService;
    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Value("${chileme.check-in.start-time}")
    private String startTimeStr;

    @Value("${chileme.check-in.end-time}")
    private String endTimeStr;

    @Value("${chileme.check-in.on-time-score}")
    private int onTimeScore;

    @Value("${chileme.check-in.late-score}")
    private int lateScore;

    @Value("${chileme.check-in.break-card-limit}")
    private int breakCardLimit;

    @Override
    @Transactional
    public CheckInVO checkIn(Long userId, CheckInDTO dto) {
        LocalDate today = LocalDate.now();
        String lockKey = "checkin:lock:" + userId + ":" + today;
        if (redisTemplate != null) {
            Boolean locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(locked)) {
                throw new BusinessException("请勿重复提交");
            }
        }
        try {
            CheckIn existing = checkInMapper.selectByUserAndDate(userId, today);
            if (existing != null) {
                throw new BusinessException("今日已打卡");
            }
            User user = userMapper.selectById(userId);
            if (user == null) throw new BusinessException("用户不存在");

            LocalTime now = LocalTime.now();
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            int status;
            int score;
            if (!now.isBefore(startTime) && !now.isAfter(endTime)) {
                status = CheckInStatusEnum.ON_TIME.getCode();
                score = onTimeScore;
                userMapper.incrementContinuousDays(userId);
                user.setContinuousDays(user.getContinuousDays() + 1);
            } else {
                status = CheckInStatusEnum.LATE.getCode();
                score = lateScore;
                userMapper.resetContinuousDays(userId);
                user.setContinuousDays(0);
            }
            userMapper.addScore(userId, score);
            user.setTotalScore(user.getTotalScore() + score);
            user.setCurrentMonthScore(user.getCurrentMonthScore() + score);

            String tagsStr = dto.getTags() != null ?
                    String.join(",", dto.getTags()) : "";
            String aiComment = aiService.generateEncouragement(dto.getContent(), dto.getTags());

            CheckIn checkIn = new CheckIn();
            checkIn.setUserId(userId);
            checkIn.setCheckDate(today);
            checkIn.setCheckTime(now);
            checkIn.setStatus(status);
            checkIn.setContent(dto.getContent());
            checkIn.setImage(dto.getImage() != null ? dto.getImage() : "");
            checkIn.setTags(tagsStr);
            checkIn.setScore(score);
            checkIn.setAiComment(aiComment);
            checkInMapper.insert(checkIn);

            ScoreLog logEntry = new ScoreLog();
            logEntry.setUserId(userId);
            logEntry.setScore(score);
            logEntry.setType(status == 1 ? ScoreTypeEnum.CHECK_IN.getType() : ScoreTypeEnum.CHECK_IN_LATE.getType());
            logEntry.setRemark(status == 1 ? "准时打卡" : "迟到打卡");
            scoreLogMapper.insert(logEntry);

            PostDTO postDTO = new PostDTO();
            postDTO.setContent(dto.getContent() != null && !dto.getContent().isBlank()
                    ? dto.getContent() : "今日早餐打卡");
            if (dto.getImage() != null && !dto.getImage().isBlank()) {
                postDTO.setImages(List.of(dto.getImage()));
            }
            postDTO.setTags(dto.getTags());
            postDTO.setUseAi(false);
            try {
                postService.createPost(userId, postDTO);
            } catch (Exception e) {
                log.error("自动发布社区帖子失败", e);
            }

            rankService.refreshAllRanks();

            return toCheckInVO(checkIn);
        } finally {
            if (redisTemplate != null) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    @Override
    @Transactional
    public CheckInVO useBreakCard(Long userId) {
        LocalDate today = LocalDate.now();
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (user.getBreakCardCount() <= 0) throw new BusinessException("补签卡不足");
        if (user.getMonthlyBreakUsed() >= breakCardLimit) {
            throw new BusinessException("本月补签次数已达上限");
        }
        CheckIn existing = checkInMapper.selectByUserAndDate(userId, today);
        if (existing != null) throw new BusinessException("今日已打卡");

        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(userId);
        checkIn.setCheckDate(today);
        checkIn.setCheckTime(LocalTime.now());
        checkIn.setStatus(CheckInStatusEnum.BREAK_CARD.getCode());
        checkIn.setContent("补签打卡");
        checkIn.setScore(0);
        checkInMapper.insert(checkIn);

        userMapper.useBreakCard(userId);

        ScoreLog logEntry = new ScoreLog();
        logEntry.setUserId(userId);
        logEntry.setScore(0);
        logEntry.setType(ScoreTypeEnum.BREAK_CARD_EXCHANGE.getType());
        logEntry.setRemark("使用补签卡");
        scoreLogMapper.insert(logEntry);

        return toCheckInVO(checkIn);
    }

    @Override
    public CheckInVO getTodayCheckIn(Long userId) {
        CheckIn checkIn = checkInMapper.selectByUserAndDate(userId, LocalDate.now());
        if (checkIn == null) return null;
        return toCheckInVO(checkIn);
    }

    @Override
    public CalendarVO getCalendar(Long userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<CheckIn> records = checkInMapper.selectByUserAndDateRange(userId, startDate, endDate);
        Map<String, Integer> dayStatus = new LinkedHashMap<>();
        int onTimeCount = 0, lateCount = 0, breakCount = 0, totalScore = 0;

        for (CheckIn c : records) {
            String day = String.valueOf(c.getCheckDate().getDayOfMonth());
            dayStatus.put(day, c.getStatus());
            if (c.getStatus() == 1) onTimeCount++;
            else if (c.getStatus() == 2) lateCount++;
            else if (c.getStatus() == 3) breakCount++;
            totalScore += c.getScore();
        }

        CalendarVO vo = new CalendarVO();
        vo.setMonth(ym.toString());
        vo.setOnTimeCount(onTimeCount);
        vo.setLateCount(lateCount);
        vo.setBreakCount(breakCount);
        vo.setTotalScore(totalScore);
        vo.setDayStatus(dayStatus);
        return vo;
    }

    @Override
    public List<CheckInVO> getHistory(Long userId, int page, int pageSize) {
        Page<CheckIn> p = checkInMapper.selectPage(
                new Page<>(page, pageSize),
                new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getUserId, userId)
                        .orderByDesc(CheckIn::getCheckDate));
        return p.getRecords().stream().map(this::toCheckInVO).collect(Collectors.toList());
    }

    private CheckInVO toCheckInVO(CheckIn c) {
        CheckInVO vo = new CheckInVO();
        vo.setId(c.getId());
        vo.setCheckDate(c.getCheckDate());
        vo.setCheckTime(c.getCheckTime());
        vo.setStatus(c.getStatus());
        vo.setStatusDesc(CheckInStatusEnum.of(c.getStatus()).getDesc());
        vo.setContent(c.getContent());
        vo.setImage(c.getImage());
        if (c.getTags() != null && !c.getTags().isEmpty()) {
            vo.setTags(Arrays.asList(c.getTags().split(",")));
        }
        vo.setScore(c.getScore());
        vo.setAiComment(c.getAiComment());
        vo.setCreatedAt(c.getCreatedAt());
        return vo;
    }
}
