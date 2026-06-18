package com.chileme.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CalendarVO {
    private String month;
    private int onTimeCount;
    private int lateCount;
    private int breakCount;
    private int totalScore;
    private Map<String, Integer> dayStatus;
}
