//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.redis.aop;

import com.ravey.common.redis.LockHandler;
import com.ravey.common.redis.LockParam;
import com.ravey.common.redis.annotation.CustomLock;
import com.ravey.common.redis.execption.CustomRedisException;
import com.ravey.common.redis.execption.RedisError;
import io.lettuce.core.dynamic.support.StandardReflectionParameterNameDiscoverer;
import java.lang.reflect.Method;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Aspect
public class CustomLockAop {
    private final Logger logger = LoggerFactory.getLogger(CustomLockAop.class);
    @Autowired
    Map<String, LockHandler> lockHandlerMap;

    public CustomLockAop() {
    }
    @Around("@annotation(com.ravey.common.redis.annotation.CustomLock)")
    public Object lock(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        CustomLock customLock = method.getAnnotation(CustomLock.class);
        if (customLock == null) return pjp.proceed();

        LockHandler lockHandler = lockHandlerMap.get(customLock.lockHandleName());
        if (lockHandler == null) return pjp.proceed();

        String redisKey = resolveElExpression(customLock.key(), signature.getParameterNames(), pjp.getArgs());
        LockParam lockParam = new LockParam(redisKey);

        boolean isLock = lockHandler.tryLock(lockParam, customLock.timeout(), customLock.tryInterval(), customLock.lockExpireTime());
        if (!isLock) throw new CustomRedisException(RedisError.LOCK_FAILED);

        try {
            return pjp.proceed();
        } finally {
            lockHandler.releaseLock(lockParam);
        }
    }


    private String resolveElExpression(String expressionStr, String[] paramNames, Object[] args) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(expressionStr);
        EvaluationContext context = new StandardEvaluationContext();

        for(int i = 0; i < args.length; ++i) {
            context.setVariable(paramNames[i], args[i]);
        }

        return (String)expression.getValue(context, String.class);
    }

    public String[] getParamNames(Method method) {
        StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();
        return discoverer.getParameterNames(method);
    }
}
