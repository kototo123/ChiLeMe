package com.chileme.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class CheckInVO {
    private Long id;
    private LocalDate checkDate;
    private LocalTime checkTime;
    private Integer status;
    private String statusDesc;
    private String content;
    private String image;
    private List<String> tags;
    private Integer score;
    private String aiComment;
    private LocalDateTime createdAt;
}
