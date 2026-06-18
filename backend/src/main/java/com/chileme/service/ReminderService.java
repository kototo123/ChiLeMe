package com.chileme.service;

import com.chileme.dto.ReminderDTO;
import com.chileme.entity.ReminderSetting;

public interface ReminderService {
    ReminderSetting getSetting(Long userId);
    ReminderSetting updateSetting(Long userId, ReminderDTO dto);
    void processReminders();
}
