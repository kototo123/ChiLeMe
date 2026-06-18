package com.chileme.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ranking_snapshot")
public class RankSnapshot {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String rankType;
    private Long userId;
    private Integer scoreValue;
    private Integer rankNum;
    private String month;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
