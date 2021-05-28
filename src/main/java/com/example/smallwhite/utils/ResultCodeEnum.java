package com.example.smallwhite.utils;

import lombok.Getter;
/**
 *     success 响应是否成功
 *     code 响应状态码
 *     message 响应信息
 *
 * @author yangqiang
 * */
@Getter
public enum ResultCodeEnum {
    //返回成功
    SUCCESS(true,200,"成功"),
    UNKNOWN_ERROR(false,201,"未知错误"),
    PARAM_ERROR(false,202,"参数错误"),
    NULL_POINT(false,404,"空指针异常"),
    HTTP_CLIENT_ERROR(false,10000,"未知异常"),
    NOT_TABLE(false,202,"没有获取到getTableName方法，请在vo中定义"),
    CAST_CLASS_ERROR(false,203,"类型转换错误"),

    ;

    private Boolean success;

    private Integer code;

    private String message;

    ResultCodeEnum(boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
}
