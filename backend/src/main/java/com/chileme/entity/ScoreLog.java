package com.chileme.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("score_log")
public class ScoreLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer score;
    private String type;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
