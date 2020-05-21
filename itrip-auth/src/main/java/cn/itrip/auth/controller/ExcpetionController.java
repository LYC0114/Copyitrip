package cn.itrip.auth.controller;

import cn.itrip.auth.AuthException;
import cn.itrip.beans.dto.Dto;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;

@ControllerAdvice//曾强版的Controller，配合ExceptionHandler的形式最长用，
                // 只有@ExceptionHandler时只能处理当前类中的异常，但是配合ControllerAdvice,就可以处理全局异常。
public class ExcpetionController {
    @ExceptionHandler(AuthException.class)
    public Dto handleAuthException(Exception e){
        //在控制台打印异常信息
        e.printStackTrace();
        return DtoUtil.returnFail("Auth未知异常",ErrorCode.AUTH_UNKNOWN);
    }
    @ExceptionHandler(Exception.class)
    public Dto HandlerExcpetion(Exception e){
        e.printStackTrace();
        return DtoUtil.returnFail("系统未知异常",ErrorCode.AUTH_UNKNOWN);
    }
}
