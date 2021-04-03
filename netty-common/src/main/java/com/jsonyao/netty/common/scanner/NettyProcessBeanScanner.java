package com.jsonyao.netty.common.scanner;

import com.jsonyao.netty.common.annotation.Cmd;
import com.jsonyao.netty.common.annotation.Module;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Netty最佳实践: 扫描自定义注解 => 集成Netty和SpringBoot的基础, 方便Server端根据module和cmd字符串调用相应的服务方法
 *	    1、使用BeanPostProcessor 在bean初始化之后加载所有的bean
 *	    2、然后找到带有@Module 的bean对象
 *	    3、接下来进行扫描bean对象下的方法中带有 @Cmd, 注解的方法
 *	    4、最后创建对应的Invoker, 并把他们加入到InvokerTable中
 */
@Component
public class NettyProcessBeanScanner implements BeanPostProcessor {

    /**
     * do nothing
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 扫描自定义注解: 获取到配置@Module和@Cmd的Invoker实例
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 1. 获取当前Bean的Class类型
        Class<?> clazz = bean.getClass();

        // 2. 反射获取所有方法
        if(clazz.isAnnotationPresent(Module.class)){
            Module module = clazz.getAnnotation(Module.class);
            String moduleValue = module.module();

            Method[] methods = clazz.getMethods();
            if(methods != null && methods.length > 0){
                for (Method method : methods) {
                    Cmd cmd = method.getAnnotation(Cmd.class);
                    if (cmd == null) continue;
                    String cmdValue = cmd.cmd();

                    //	只需要把moduleValue + cmdValue的值与对应的反射对象(invoker) 管理起来(map)
                    if(InvokerTable.getInvoker(moduleValue, cmdValue) == null){
                        InvokerTable.addInvoker(moduleValue, cmdValue, Invoker.createInvoker(method, bean));
                    } else {
                        System.err.println("模块下命令对象的程序缓存已经存在, module: " + moduleValue + ", cmd: " + cmdValue);
                    }
                }
            }
        }

        return null;
    }
}
