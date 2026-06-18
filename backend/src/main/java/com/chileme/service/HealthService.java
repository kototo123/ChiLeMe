package com.chileme.service;

import com.chileme.dto.HealthRecordDTO;
import com.chileme.vo.HealthVO;

import java.util.List;

public interface HealthService {
    HealthVO addRecord(Long userId, HealthRecordDTO dto);
    List<HealthVO> getRecords(Long userId);
}
