package com.chileme.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
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
}
