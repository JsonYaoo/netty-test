package com.jsonyao.netty.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.jsonyao.netty.common.annotation.Cmd;
import com.jsonyao.netty.common.annotation.Module;
import com.jsonyao.netty.common.protobuf.Result;
import com.jsonyao.netty.common.protobuf.UserModule;
import org.springframework.stereotype.Service;

/**
 * Netty最佳实践: User服务
 */
@Service
@Module(module = "user")
public class UserService {

    @Cmd(cmd = "save")
    public Result<?> save(byte[] data){
        UserModule.User user = null;
        try {
            user = UserModule.User.parseFrom(data);
            System.err.println("save ok, userId: " + user.getUserId() + ", userName: " + user.getUserName());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return Result.FAILURE();
        }
        return Result.SUCCESS(user);
    }

    @Cmd(cmd = "update")
    public Result<?> update(byte[] data){
        UserModule.User user = null;
        try {
            user = UserModule.User.parseFrom(data);
            System.err.println("update ok, userId: " + user.getUserId() + ", userName: " + user.getUserName());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return Result.FAILURE();
        }
        return Result.SUCCESS(user);
    }
}
