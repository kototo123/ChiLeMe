package com.chileme.controller;

import com.chileme.common.result.Result;
import com.chileme.dto.ReminderDTO;
import com.chileme.entity.ReminderSetting;
import com.chileme.service.ReminderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reminder")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping
    public Result<ReminderSetting> getSetting(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(reminderService.getSetting(userId));
    }

    @PutMapping
    public Result<ReminderSetting> updateSetting(HttpServletRequest request,
                                                 @RequestBody ReminderDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(reminderService.updateSetting(userId, dto));
    }
}
