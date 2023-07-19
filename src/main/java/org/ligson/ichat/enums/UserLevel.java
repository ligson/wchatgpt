package org.ligson.ichat.enums;

import lombok.Getter;

@Getter
public enum UserLevel {
    FREE(1, "免费"), FOREVER(2, "永久"), GPT4(3, "gpt4");
    private final int code;
    private final String msg;

    UserLevel(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static UserLevel fromCode(int level) {
        for (UserLevel userLevel : UserLevel.values()) {
            if (userLevel.code == level) {
                return userLevel;
            }
        }
        return null;
    }
}
