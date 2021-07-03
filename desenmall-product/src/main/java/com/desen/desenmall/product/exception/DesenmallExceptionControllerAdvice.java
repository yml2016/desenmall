package com.desen.desenmall.product.exception;

import com.desen.common.exception.BizCode;
import com.desen.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集中处理所有异常
 */
@Slf4j
@RestControllerAdvice(basePackages="com.desen.desenmall.product.controller")
public class DesenmallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}，异常类型：{}", e.getMessage(), e.getClass());
        Map<String,String> errMap = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> allErrors = bindingResult.getFieldErrors();
        allErrors.forEach(error->{
            String name = error.getField();
            String message = error.getDefaultMessage();
            errMap.put(name,message);
        });
        return R.error(BizCode.VALID_EXCEPTION.getCode(),BizCode.VALID_EXCEPTION.getMsg())
                .put("data",errMap);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleValidException(Throwable throwable){
        log.error("系统未知异常",throwable);
        return R.error(BizCode.UNKNOW_EXCEPTION.getCode(),BizCode.UNKNOW_EXCEPTION.getMsg());
    }
}
