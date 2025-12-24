package com.ravey.common.service.web.handler;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.ravey.common.api.exception.ServiceException;
import com.ravey.common.service.web.result.HttpResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler({ServiceException.class})
    public HttpResult<?> handleServiceException(HttpServletRequest request, ServiceException e) {
        LOGGER.error("请求[{}, {}]业务出现错误, 异常为：", new Object[]{request.getMethod(), request.getRequestURI(), e});
        return HttpResult.failure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public HttpResult<?> handleInvalidFormatException(HttpServletRequest request, HttpMessageNotReadableException e) {
        LOGGER.error("请求[{}, {}]反序列化出现错误, 异常为：", new Object[]{request.getMethod(), request.getRequestURI(), e});
        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException formatException = (InvalidFormatException)cause;
            List<JsonMappingException.Reference> path = formatException.getPath();
            Object value = formatException.getValue();
            if (!CollectionUtils.isEmpty(path)) {
                return HttpResult.failure(-2, String.format("参数[%s=%s]格式转换异常", ((JsonMappingException.Reference)path.get(0)).getFieldName(), value));
            }
        }

        return HttpResult.failure(-2, e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public HttpResult<?> handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        LOGGER.error("请求[{}, {}]参数校验错误，异常为：", new Object[]{request.getMethod(), request.getRequestURI(), e});
        return HttpResult.failure(-2, e.getMessage());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public HttpResult<?> handleMissingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException e) {
        LOGGER.error("请求[{}, {}]参数不存在，异常为：", new Object[]{request.getMethod(), request.getRequestURI(), e});
        return HttpResult.failure(-2, e.getParameterName());
    }

    @ExceptionHandler({BindException.class})
    public HttpResult<?> handleBindException(HttpServletRequest request, BindException e) {
        LOGGER.error("请求[{}, {}]对象参数绑定失败，异常为：", new Object[]{request.getMethod(), request.getRequestURI(), e});
        return HttpResult.failure(-2, ((FieldError)Objects.requireNonNull(e.getBindingResult().getFieldError())).getDefaultMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<HttpResult<?>> handleRuntimeException(HttpServletRequest request, RuntimeException e) {
        LOGGER.error("请求[{}, {}]出现系统运行时错误, 异常为：", new Object[]{request.getMethod(), request.getRequestURI(), e});
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResult.failure(-1, "系统内部异常"));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public HttpResult<?> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        LOGGER.error("请求[{}, {}]对象参数绑定失败，异常为：", new Object[]{request.getMethod(), request.getRequestURI(), e});
        return HttpResult.failure(-1, ((ObjectError)((List)Objects.requireNonNull(e.getBindingResult().getAllErrors())).get(0)).getDefaultMessage());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<HttpResult<?>> handleException(HttpServletRequest request, Exception e) {
        LOGGER.error("请求[{}, {}]出现系统错误, 异常为：", new Object[]{request.getMethod(), request.getRequestURI(), e});
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResult.failure(-1, "系统内部异常"));
    }
}
