package com.jsonyao.netty.controller;

import com.jsonyao.netty.client.Client;
import com.jsonyao.netty.common.protobuf.UserModule;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
	
	
	@RequestMapping("/index")
	public String index() {
		return "index";
	}
	
	@RequestMapping("/save")
	public String save() {
		Client.getInstance().sendMessage("user", "save",
				UserModule.User.newBuilder().setUserId("001").setUserName("张三").build());
		return "save";
	}
	
	@RequestMapping("/update")
	public String update() {
		Client.getInstance().sendMessage("user", "update",
				UserModule.User.newBuilder().setUserId("002").setUserName("李四").build());
		return "update";
	}
	
}
