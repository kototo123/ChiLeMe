package com.chileme.enums;

public enum PostStatusEnum {
    PENDING(0, "待审核"),
    PUBLISHED(1, "已发布"),
    VIOLATION(2, "违规删除");

    private final int code;
    private final String desc;

    PostStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }

    public static PostStatusEnum of(int code) {
        for (PostStatusEnum v : values()) {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
