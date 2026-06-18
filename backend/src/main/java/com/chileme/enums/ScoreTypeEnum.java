package com.chileme.enums;

public enum ScoreTypeEnum {
    CHECK_IN("check_in", "准时打卡"),
    CHECK_IN_LATE("check_in_late", "迟到打卡"),
    BREAK_CARD("break_card", "补签卡兑换"),
    RANK_REWARD("rank_reward", "排行奖励"),
    BREAK_CARD_EXCHANGE("break_card_exchange", "补签卡使用");

    private final String type;
    private final String desc;

    ScoreTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() { return type; }
    public String getDesc() { return desc; }
}
