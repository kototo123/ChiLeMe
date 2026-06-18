package com.chileme.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("check_in")
public class CheckIn {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private LocalDate checkDate;
    private LocalTime checkTime;
    private Integer status;
    private String content;
    private String image;
    private String tags;
    private Integer score;
    private String aiComment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
