package com.jinlin.kuaimianshibackend.enums;

import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 用户角色枚举
 */
public enum UserRoleEnum {
    USER("用户","user"),
    ADMIN("管理员","admin"),
    BAN("被封号","bam");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValues(String value) {
        if(ObjectUtils.isEmpty(value)){
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if(anEnum.value.equals(value)){
                return anEnum;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
