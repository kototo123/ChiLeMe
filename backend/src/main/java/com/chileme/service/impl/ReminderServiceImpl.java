package com.chileme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chileme.dto.ReminderDTO;
import com.chileme.entity.ReminderSetting;
import com.chileme.mapper.ReminderSettingMapper;
import com.chileme.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderSettingMapper reminderSettingMapper;

    @Override
    public ReminderSetting getSetting(Long userId) {
        ReminderSetting setting = reminderSettingMapper.selectOne(
                new LambdaQueryWrapper<ReminderSetting>()
                        .eq(ReminderSetting::getUserId, userId));
        if (setting == null) {
            setting = new ReminderSetting();
            setting.setUserId(userId);
            setting.setEnabled(1);
            setting.setRemindTime(LocalTime.of(7, 30));
            setting.setWeekdaysOnly(1);
            setting.setWeekendTime(LocalTime.of(8, 30));
            reminderSettingMapper.insert(setting);
        }
        return setting;
    }

    @Override
    public ReminderSetting updateSetting(Long userId, ReminderDTO dto) {
        ReminderSetting setting = getSetting(userId);
        if (dto.getEnabled() != null) setting.setEnabled(dto.getEnabled() ? 1 : 0);
        if (dto.getRemindTime() != null) setting.setRemindTime(dto.getRemindTime());
        if (dto.getWeekdaysOnly() != null) setting.setWeekdaysOnly(dto.getWeekdaysOnly() ? 1 : 0);
        if (dto.getWeekendTime() != null) setting.setWeekendTime(dto.getWeekendTime());
        reminderSettingMapper.updateById(setting);
        return setting;
    }

    private static final ZoneId CST = ZoneId.of("Asia/Shanghai");

    @Override
    @Scheduled(cron = "${chileme.reminder.cron}")
    public void processReminders() {
        LocalTime now = LocalTime.now(CST).withSecond(0).withNano(0);
        LocalDate today = LocalDate.now(CST);
        DayOfWeek dow = today.getDayOfWeek();
        boolean isWeekend = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;

        List<ReminderSetting> settings = reminderSettingMapper.selectAllEnabled();
        for (ReminderSetting s : settings) {
            LocalTime targetTime;
            if (isWeekend && s.getWeekendTime() != null) {
                targetTime = s.getWeekendTime();
            } else {
                targetTime = s.getRemindTime();
            }
            if (now.equals(targetTime.withSecond(0).withNano(0))) {
                log.info("Sending reminder to user {} at {}", s.getUserId(), now);
                // TODO: 调用微信订阅消息接口发送模板消息
            }
        }
    }
}
