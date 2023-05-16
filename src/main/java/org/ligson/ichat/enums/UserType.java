package org.ligson.ichat.enums;

import lombok.Getter;

@Getter
public enum UserType {
    NORMAL(1, "普通用户"), ADMIN(100, "管理员");
    private final int code;
    private final String msg;

    UserType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static UserType fromCode(int level) {
        for (UserType userLevel : UserType.values()) {
            if (userLevel.code == level) {
                return userLevel;
            }
        }
        return null;
    }
}
