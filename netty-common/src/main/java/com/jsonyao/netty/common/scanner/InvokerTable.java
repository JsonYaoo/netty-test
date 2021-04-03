package com.jsonyao.netty.common.scanner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty最佳实践: 自定义注解方法执行者保存列表
 */
public class InvokerTable {

    /**
     * module-(cmd-Invoker Map) Map
     */
    private static ConcurrentHashMap<String, Map<String, Invoker>> invokerTable = new ConcurrentHashMap<String, Map<String, Invoker>>();

    /**
     * 添加Invoker实例
     * @param module
     * @param cmd
     * @param invoker
     */
    public static void addInvoker(String module, String cmd, Invoker invoker){
        Map<String, Invoker> cmdInvokerMap = invokerTable.get(module);
        if(cmdInvokerMap == null){
            cmdInvokerMap = new HashMap<>();
            invokerTable.put(module, cmdInvokerMap);
        }
        cmdInvokerMap.put(cmd, invoker);
    }

    /**
     * 获取Invoker实例
     * @param module
     * @param cmd
     * @return
     */
    public static Invoker getInvoker(String module, String cmd) {
        Map<String, Invoker> cmdInvokerMap = invokerTable.get(module);
        if(cmdInvokerMap != null){
            return cmdInvokerMap.get(cmd);
        }
        return null;
    }
}
