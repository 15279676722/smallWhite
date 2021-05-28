package com.example.smallwhite.utils;

import lombok.Getter;

/**
 * 自定义异常
 * @author: yangqiang
 * @create: 2020-03-24 19:31
 *
 */
@Getter
public class BaseBusinessException extends RuntimeException {
    private Integer code;

    public BaseBusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    public BaseBusinessException(String message) {
        super(message);
        this.code = 999;
    }
    public BaseBusinessException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

}
