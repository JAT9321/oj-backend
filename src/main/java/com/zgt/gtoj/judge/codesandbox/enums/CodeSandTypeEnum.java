package com.zgt.gtoj.judge.codesandbox.enums;

import com.zgt.gtoj.model.enums.JudgeInfoMessageEnum;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CodeSandTypeEnum {

    EXAMPLE(0, "example"),
    REMOTE(1, "remote"),
    THIRDPARTY(2, "thirdParty");

    /**
     * 获取值列表
     *
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.type).collect(Collectors.toList());
    }

    /**
     * 根据 type 获取枚举
     *
     * @param type
     */
    public static CodeSandTypeEnum getEnumByValue(String type) {
        if (ObjectUtils.isEmpty(type)) {
            return null;
        }
        for (CodeSandTypeEnum anEnum : CodeSandTypeEnum.values()) {
            if (anEnum.type.equals(type)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    private final String type;
    private final int typeIndex;

    public int getTypeIndex() {
        return typeIndex;
    }

    CodeSandTypeEnum(int typeIndex, String type) {
        this.type = type;
        this.typeIndex = typeIndex;
    }
}
