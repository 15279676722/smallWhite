package com.example.smallwhite.utils;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
/**
 * 构造器私有
 * @author yangqiang
 * */
@Data
public class ResultData {
    private Boolean success;

    private Integer code=200;

    private String message;

    private Map<String, Object> data = new HashMap<>();

    private ResultData(){}

    /**
     * 通用返回成功
     * @return resultData
     *
     */
    public static ResultData ok() {
        ResultData resultData = new ResultData();
        resultData.setSuccess(ResultCodeEnum.SUCCESS.getSuccess());
        resultData.setCode(ResultCodeEnum.SUCCESS.getCode());
        resultData.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        return resultData;
    }
    /**
     * 通用返回失败，未知错误
     * @return resultData
     *
     */
    public static ResultData error() {
        ResultData resultData = new ResultData();
        resultData.setSuccess(ResultCodeEnum.UNKNOWN_ERROR.getSuccess());
        resultData.setCode(ResultCodeEnum.UNKNOWN_ERROR.getCode());
        resultData.setMessage(ResultCodeEnum.UNKNOWN_ERROR.getMessage());
        return resultData;
    }
    /**
     * 设置结果，形参为结果枚举
     * @param result
     * @return resultData
     *
     */

    public static ResultData setResult(ResultCodeEnum result) {
        ResultData resultData = new ResultData();
        resultData.setSuccess(result.getSuccess());
        resultData.setCode(result.getCode());
        resultData.setMessage(result.getMessage());
        return resultData;
    }

    /**------------使用链式编程，返回类本身-----------**/
    /**
     * 自定义返回数据
     * @param map
     * @return resultData
     *
     */

    public ResultData data(Map<String,Object> map) {
        this.setData(map);
        return this;
    }
    /**
     *  通用设置data
     * @param key
     * @param value
     * @return resultData
     */

    public ResultData data(String key,Object value) {
        this.data.put(key, value);
        return this;
    }
    /**
     *  自定义状态信息
     * @param message
     * @return resultData
     */

    public ResultData message(String message) {
        this.setMessage(message);
        return this;
    }
    /**
     *   自定义状态码
     * @param code
     * @return resultData
     */

    public ResultData code(Integer code) {
        this.setCode(code);
        return this;
    }
    /**
     *   自定义返回结果
     * @param success
     * @return ResultData
     */

    public ResultData success(Boolean success) {
        this.setSuccess(success);
        return this;
    }
}
