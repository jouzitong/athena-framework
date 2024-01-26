//package org.athena.framework.mybatis.handler;//package com.zhouzhitong.lib.mapper.handler;
//
//import com.zhouzhitong.lib.common.base.Idempotent;
//import com.zhouzhitong.lib.common.constant.CodeConstant;
//import com.zhouzhitong.lib.common.exception.BaseException;
//import com.zhouzhitong.lib.common.utils.JacksonJsonUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.stereotype.Component;
//import org.springframework.util.DigestUtils;
//
//import java.lang.reflect.Method;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author zhouzhitong
// * @since 2023/1/8
// */
//@Component
//@Aspect
//public class CheckIdempotentHandler {
//
//    /**
//     * redis缓存key的模板
//     */
//    private static final String KEY_TEMPLATE = "idempotent:%s";
//
//    private static final String KEY_GROUP = ":";
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Pointcut("@annotation(com.zhouzhitong.lib.common.base.Idempotent)")
//    public void executeIdempotent() {
//    }
//
//    @Before("executeIdempotent()")
//    public void check(JoinPoint joinPoint) {
//        //获取方法
//        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
//        //获取幂等注解
//        Idempotent idempotent = method.getAnnotation(Idempotent.class);
//        //根据 key前缀 + @Idempotent.value() + 方法签名 + 参数 构建缓存键值
//        //确保幂等处理的操作对象是：同样的 @Idempotent.value() + 方法签名 + 参数
//        String key = String.format(KEY_TEMPLATE, idempotent.value() + KEY_GROUP + generate(method, joinPoint.getArgs()));
//        //通过setnx确保只有一个接口能够正常访问
//        //调用KeyUtil工具类生成key
//        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
//        Boolean s = stringStringValueOperations.setIfAbsent(key, key, idempotent.expireMillis(), TimeUnit.MILLISECONDS);
//        if (!s) {
//            throw new BaseException(CodeConstant.DUPLICATE_REQUEST);
//        }
//    }
//
//
//    /**
//     * 根据{方法名 + 参数列表}和md5转换生成key
//     */
//    public String generate(Method method, Object... args) {
//        StringBuilder sb = new StringBuilder(method.toString());
//        for (Object arg : args) {
//            sb.append(toString(arg));
//        }
//        return DigestUtils.md5DigestAsHex(sb.toString().getBytes());
//    }
//
//    private static String toString(Object object) {
//        if (object == null) {
//            return "null";
//        }
//        if (object instanceof Number) {
//            return object.toString();
//        }
//        //调用json工具类转换成String
//        return JacksonJsonUtils.writeValueAsString(object);
//    }
//
//}
