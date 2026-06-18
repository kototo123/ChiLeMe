package com.chileme.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class ReminderDTO {
    private Boolean enabled;
    private LocalTime remindTime;
    private Boolean weekdaysOnly;
    private LocalTime weekendTime;
}
