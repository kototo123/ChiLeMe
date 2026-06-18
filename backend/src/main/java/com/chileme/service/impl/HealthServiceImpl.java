package com.chileme.service.impl;

import com.chileme.common.exception.BusinessException;
import com.chileme.dto.HealthRecordDTO;
import com.chileme.entity.HealthRecord;
import com.chileme.mapper.CheckInMapper;
import com.chileme.mapper.HealthRecordMapper;
import com.chileme.service.AIService;
import com.chileme.service.HealthService;
import com.chileme.vo.HealthVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthServiceImpl implements HealthService {

    private final HealthRecordMapper healthRecordMapper;
    private final CheckInMapper checkInMapper;
    private final AIService aiService;

    @Override
    public HealthVO addRecord(Long userId, HealthRecordDTO dto) {
        LocalDate recordDate = dto.getRecordDate() != null ? dto.getRecordDate() : LocalDate.now();
        BigDecimal height = dto.getHeight();
        BigDecimal weight = dto.getWeight();
        BigDecimal bmi = null;

        if (height != null && height.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightM = height.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            bmi = weight.divide(heightM.multiply(heightM), 1, RoundingMode.HALF_UP);
        }

        String aiAdvice = "";
        if (bmi != null) {
            List<String> recentTags = new ArrayList<>();
            // get recent tags within 30 days
            LocalDate startDate = recordDate.minusDays(30);
            var checkIns = checkInMapper.selectByUserAndDateRange(userId, startDate, recordDate);
            for (var c : checkIns) {
                if (c.getTags() != null && !c.getTags().isEmpty()) {
                    java.util.Collections.addAll(recentTags, c.getTags().split(","));
                }
            }
            Double weightChange = null;
            var prev = healthRecordMapper.selectByUserId(userId);
            if (!prev.isEmpty() && prev.size() > 1) {
                weightChange = prev.get(0).getWeight().subtract(weight).doubleValue();
            }
            aiAdvice = aiService.generateHealthAdvice(recentTags, weightChange);
        }

        HealthRecord record = new HealthRecord();
        record.setUserId(userId);
        record.setHeight(height);
        record.setWeight(weight);
        record.setBmi(bmi);
        record.setRecordDate(recordDate);
        record.setAiAdvice(aiAdvice);
        healthRecordMapper.insert(record);

        return toHealthVO(record);
    }

    @Override
    public List<HealthVO> getRecords(Long userId) {
        return healthRecordMapper.selectByUserId(userId).stream()
                .map(this::toHealthVO)
                .collect(Collectors.toList());
    }

    private HealthVO toHealthVO(HealthRecord record) {
        HealthVO vo = new HealthVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
}
