package com.chileme.enums;

public enum RankTypeEnum {
    ON_TIME("on_time", "准时早餐榜"),
    CONTINUOUS("continuous", "连续早起榜"),
    SCORE("score", "积分总榜");

    private final String type;
    private final String desc;

    RankTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() { return type; }
    public String getDesc() { return desc; }
}
