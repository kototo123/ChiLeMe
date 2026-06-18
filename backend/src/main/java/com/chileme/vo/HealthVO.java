package com.chileme.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HealthVO {
    private Long id;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bmi;
    private LocalDate recordDate;
    private String aiAdvice;
}
