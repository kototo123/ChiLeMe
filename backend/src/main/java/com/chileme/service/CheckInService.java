package com.chileme.service;

import com.chileme.dto.CheckInDTO;
import com.chileme.vo.CalendarVO;
import com.chileme.vo.CheckInVO;

import java.util.List;

public interface CheckInService {
    CheckInVO checkIn(Long userId, CheckInDTO dto);
    CheckInVO useBreakCard(Long userId);
    CheckInVO getTodayCheckIn(Long userId);
    CalendarVO getCalendar(Long userId, int year, int month);
    List<CheckInVO> getHistory(Long userId, int page, int pageSize);
}
