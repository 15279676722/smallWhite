package com.example.smallwhite.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: yangqiang
 * @create: 2020-03-27 15:56
 */
@Getter
@AllArgsConstructor
public enum OperationEnum {
    DELETE("delete", "删除"),
    INSERT("insert", "新增"),
    SELECT("select", "查询"),
    UPDATE("update", "修改");
    private String value;
    private String name;

    public static String getName(String value) {
        for (OperationEnum operationEnum : OperationEnum.values()) {
            if (operationEnum.value.equals(value)) {
                return operationEnum.name;
            }
        }
        return null;
    }

}
