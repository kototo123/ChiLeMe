package com.chileme.controller;

import com.chileme.common.result.Result;
import com.chileme.dto.HealthRecordDTO;
import com.chileme.service.HealthService;
import com.chileme.vo.HealthVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @PostMapping("/record")
    public Result<HealthVO> addRecord(HttpServletRequest request,
                                      @Valid @RequestBody HealthRecordDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(healthService.addRecord(userId, dto));
    }

    @GetMapping("/records")
    public Result<List<HealthVO>> getRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(healthService.getRecords(userId));
    }
}
