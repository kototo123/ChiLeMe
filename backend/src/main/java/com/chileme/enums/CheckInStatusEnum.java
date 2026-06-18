package com.chileme.enums;

public enum CheckInStatusEnum {
    ON_TIME(1, "准时"),
    LATE(2, "迟到"),
    BREAK_CARD(3, "补签");

    private final int code;
    private final String desc;

    CheckInStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }

    public static CheckInStatusEnum of(int code) {
        for (CheckInStatusEnum v : values()) {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
