package com.jsonyao.netty.common.scanner;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Netty最佳实践: 自定义注解方法执行者
 */
@Data
@Builder
public class Invoker {

    private Method method;

    private Object target;

    /**
     * 创建Invoker对象
     * @param method
     * @param target
     * @return
     */
    public static Invoker createInvoker(Method method, Object target) {
        return Invoker.builder()
                .method(method)
                .target(target)
                .build();
    }

    /**
     * Invoker对象执行方法
     * @param args
     * @return
     */
    public Object invoke(Object... args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
