package com.jsonyao.netty.service;

import com.jsonyao.netty.common.annotation.Cmd;
import com.jsonyao.netty.common.annotation.Module;
import com.jsonyao.netty.common.protobuf.MessageModule;
import org.springframework.stereotype.Service;

/**
 * Netty最佳实践: User回调服务
 */
@Service
@Module(module = "user-return")
public class UserReturnService {

    /**
     * User回调服务: 模拟save方法客户端回调逻辑
     */
    @Cmd(cmd = "save-return")
    public void saveReturn(MessageModule.ResultType resultType, byte[] data){
        if(MessageModule.ResultType.SUCCESS.equals(resultType)) {
            System.err.println("处理 user save 方法成功!");
        } else {
            System.err.println("处理 user save 方法失败!");
        }
    }

    /**
     * User回调服务: 模拟update方法客户端回调逻辑
     */
    @Cmd(cmd = "update-return")
    public void updateReturn(MessageModule.ResultType resultType, byte[] data){
        if(MessageModule.ResultType.SUCCESS.equals(resultType)) {
            System.err.println("处理 user update 方法成功!");
        } else {
            System.err.println("处理 user update 方法失败!");
        }
    }
}
