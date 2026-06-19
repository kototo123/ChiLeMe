package com.chileme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chileme.entity.CheckIn;
import com.chileme.entity.RankSnapshot;
import com.chileme.entity.User;
import com.chileme.enums.RankTypeEnum;
import com.chileme.mapper.CheckInMapper;
import com.chileme.mapper.RankSnapshotMapper;
import com.chileme.mapper.UserMapper;
import com.chileme.service.RankService;
import com.chileme.vo.RankVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RankServiceImpl implements RankService {

    private final UserMapper userMapper;
    private final CheckInMapper checkInMapper;
    private final RankSnapshotMapper rankSnapshotMapper;
    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    private static final String RANK_ON_TIME_KEY = "rank:on_time";
    private static final String RANK_CONTINUOUS_KEY = "rank:continuous";
    private static final String RANK_SCORE_KEY = "rank:score";

    public RankServiceImpl(UserMapper userMapper, CheckInMapper checkInMapper,
                           RankSnapshotMapper rankSnapshotMapper) {
        this.userMapper = userMapper;
        this.checkInMapper = checkInMapper;
        this.rankSnapshotMapper = rankSnapshotMapper;
    }

    @Override
    public List<RankVO> getOnTimeRank(int topN) {
        if (redisTemplate != null) return getRankFromRedis(RANK_ON_TIME_KEY, topN);
        return getRankFromDb(RankTypeEnum.ON_TIME, topN);
    }

    @Override
    public List<RankVO> getContinuousRank(int topN) {
        if (redisTemplate != null) return getRankFromRedis(RANK_CONTINUOUS_KEY, topN);
        return getRankFromDb(RankTypeEnum.CONTINUOUS, topN);
    }

    @Override
    public List<RankVO> getScoreRank(int topN) {
        if (redisTemplate != null) return getRankFromRedis(RANK_SCORE_KEY, topN);
        return getRankFromDb(RankTypeEnum.SCORE, topN);
    }

    @Override
    public List<RankVO> getFollowRank(Long userId, int topN) {
        return getOnTimeRank(topN);
    }

    @Override
    public void refreshAllRanks() {
        YearMonth ym = YearMonth.now();
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>().eq(User::getStatus, 1));

        if (redisTemplate != null) {
            refreshOnTimeRank(users, startDate, endDate);
            refreshContinuousRank(users);
            refreshScoreRank(users);
        }
    }

    private void refreshOnTimeRank(List<User> users, LocalDate startDate, LocalDate endDate) {
        redisTemplate.delete(RANK_ON_TIME_KEY);
        Map<String, Double> members = new HashMap<>();
        for (User u : users) {
            int onTimeCount = checkInMapper.countOnTimeByMonth(u.getId(), startDate, endDate);
            int tagCount = checkInMapper.countDistinctTagsByMonth(u.getId(), startDate, endDate);
            double score = onTimeCount * 1000.0 + tagCount;
            if (onTimeCount > 0) {
                members.put(String.valueOf(u.getId()), score);
            }
        }
        if (!members.isEmpty()) {
            members.forEach((k, v) -> redisTemplate.opsForZSet().add(RANK_ON_TIME_KEY, k, v));
        }
    }

    private void refreshContinuousRank(List<User> users) {
        redisTemplate.delete(RANK_CONTINUOUS_KEY);
        for (User u : users) {
            if (u.getContinuousDays() > 0) {
                redisTemplate.opsForZSet().add(RANK_CONTINUOUS_KEY,
                        String.valueOf(u.getId()), u.getContinuousDays());
            }
        }
    }

    private void refreshScoreRank(List<User> users) {
        redisTemplate.delete(RANK_SCORE_KEY);
        for (User u : users) {
            if (u.getCurrentMonthScore() > 0) {
                redisTemplate.opsForZSet().add(RANK_SCORE_KEY,
                        String.valueOf(u.getId()), u.getCurrentMonthScore());
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void persistRanks() {
        if (redisTemplate == null) return;
        persistRank(RankTypeEnum.ON_TIME.getType(), RANK_ON_TIME_KEY);
        persistRank(RankTypeEnum.CONTINUOUS.getType(), RANK_CONTINUOUS_KEY);
        persistRank(RankTypeEnum.SCORE.getType(), RANK_SCORE_KEY);
    }

    private void persistRank(String rankType, String redisKey) {
        String month = YearMonth.now().toString();
        Set<String> members = redisTemplate.opsForZSet()
                .reverseRangeByScore(redisKey, 0, Double.MAX_VALUE);
        if (members == null) return;

        int rank = 1;
        for (String userId : members) {
            Double score = redisTemplate.opsForZSet().score(redisKey, userId);
            RankSnapshot snapshot = new RankSnapshot();
            snapshot.setRankType(rankType);
            snapshot.setUserId(Long.parseLong(userId));
            snapshot.setScoreValue(score != null ? score.intValue() : 0);
            snapshot.setRankNum(rank++);
            snapshot.setMonth(month);
            rankSnapshotMapper.insert(snapshot);
        }
    }

    private List<RankVO> getRankFromRedis(String key, int topN) {
        Set<String> members = redisTemplate.opsForZSet()
                .reverseRange(key, 0, topN - 1);
        if (members == null || members.isEmpty()) return List.of();

        List<RankVO> list = new ArrayList<>();
        int rank = 1;
        for (String userId : members) {
            Double score = redisTemplate.opsForZSet().score(key, userId);
            User user = userMapper.selectById(Long.parseLong(userId));
            if (user != null) {
                RankVO vo = new RankVO();
                vo.setRank(rank++);
                vo.setUserId(user.getId());
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
                vo.setScoreValue(score != null ? score.intValue() : 0);
                list.add(vo);
            }
        }
        return list;
    }

    private List<RankVO> getRankFromDb(RankTypeEnum type, int topN) {
        YearMonth ym = YearMonth.now();
        LambdaQueryWrapper<RankSnapshot> qw = new LambdaQueryWrapper<RankSnapshot>()
                .eq(RankSnapshot::getRankType, type.getType())
                .eq(RankSnapshot::getMonth, ym.toString())
                .orderByAsc(RankSnapshot::getRankNum)
                .last("LIMIT " + topN);
        List<RankSnapshot> snapshots = rankSnapshotMapper.selectList(qw);
        if (snapshots.isEmpty()) return List.of();

        List<RankVO> list = new ArrayList<>();
        for (RankSnapshot s : snapshots) {
            User user = userMapper.selectById(s.getUserId());
            if (user != null) {
                RankVO vo = new RankVO();
                vo.setRank(s.getRankNum());
                vo.setUserId(user.getId());
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
                vo.setScoreValue(s.getScoreValue());
                list.add(vo);
            }
        }
        return list;
    }
}
