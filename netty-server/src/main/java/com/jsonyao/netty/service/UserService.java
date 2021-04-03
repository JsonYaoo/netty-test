package com.jsonyao.netty.service;

import com.jsonyao.netty.common.annotation.Cmd;
import com.jsonyao.netty.common.annotation.Module;
import org.springframework.stereotype.Service;

/**
 * Netty最佳实践: User服务
 */
@Service
@Module(module = "user")
public class UserService {

    @Cmd(cmd = "save")
    public Object save(){
        return null;
    }

    @Cmd(cmd = "update")
    public Object update(){
        return null;
    }
}
