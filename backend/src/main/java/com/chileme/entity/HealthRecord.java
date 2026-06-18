package com.chileme.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("health_record")
public class HealthRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bmi;
    private LocalDate recordDate;
    private String aiAdvice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
