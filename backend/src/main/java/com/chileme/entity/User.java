package com.chileme.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String openid;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String city;

    private Integer totalScore;
    private Integer continuousDays;
    private Integer maxContinuousDays;
    private Integer currentMonthOnTime;
    private Integer currentMonthScore;
    private Integer breakCardCount;
    private Integer monthlyBreakUsed;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
