package com.example.smallwhite.utils;

/**
 * 通用异常处理
 * @author: yangqiang
 * @create: 2020-03-24 19:35
 */
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**-------- 通用异常处理方法 --------**/
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultData error(Exception e) {
        log.error(ExceptionUtil.getMessage(e));

        // 通用异常结果
        return ResultData.error();
    }

    /**-------- 指定异常处理方法 --------**/
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResultData error(NullPointerException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ResultData.setResult(ResultCodeEnum.NULL_POINT);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public ResultData error(IndexOutOfBoundsException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ResultData.setResult(ResultCodeEnum.HTTP_CLIENT_ERROR);
    }

    /**-------- 自定义定异常处理方法 --------**/
    @ExceptionHandler(BaseBusinessException.class)
    @ResponseBody
    public ResultData error(BaseBusinessException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ResultData.error().message(e.getMessage()).code(e.getCode());
    } /**-------- 自定义定异常处理方法 --------**/
    @ExceptionHandler(ClassCastException.class)
    @ResponseBody
    public ResultData error(ClassCastException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ResultData.setResult(ResultCodeEnum.CAST_CLASS_ERROR);
    }

    /**-------- vo输入控制异常 --------**/
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResultData error(BindException e) {
        log.error(ExceptionUtil.getMessage(e));
        BindingResult result = e.getBindingResult();
        StringBuilder errorMsg = new StringBuilder() ;
        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            fieldErrors.forEach(error -> {
                log.info("field" + error.getField() + ", msg:" + error.getDefaultMessage());
                errorMsg.append(error.getField()).append(error.getDefaultMessage()).append("!");
            });
        }

        return ResultData.error().message(errorMsg.toString());
    }
}