package com.chileme.controller;

import com.chileme.common.result.Result;
import com.chileme.dto.CheckInDTO;
import com.chileme.service.CheckInService;
import com.chileme.vo.CalendarVO;
import com.chileme.vo.CheckInVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping
    public Result<CheckInVO> checkIn(HttpServletRequest request,
                                     @Valid @RequestBody CheckInDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(checkInService.checkIn(userId, dto));
    }

    @PostMapping("/break-card")
    public Result<CheckInVO> useBreakCard(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(checkInService.useBreakCard(userId));
    }

    @GetMapping("/today")
    public Result<CheckInVO> getTodayCheckIn(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(checkInService.getTodayCheckIn(userId));
    }

    @GetMapping("/calendar")
    public Result<CalendarVO> getCalendar(HttpServletRequest request,
                                          @RequestParam int year,
                                          @RequestParam int month) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(checkInService.getCalendar(userId, year, month));
    }

    @GetMapping("/history")
    public Result<List<CheckInVO>> getHistory(HttpServletRequest request,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(checkInService.getHistory(userId, page, pageSize));
    }
}
