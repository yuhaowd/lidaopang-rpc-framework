package com.zshs.exampleclient;

import com.zshs.exampleclient.service.UserService;

import com.zshs.exampleclient.service.impl.UserServiceImpl;
import io.netty.channel.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class ExampleClientApplicationTests {


    @Resource
    private Channel channel;


    @Test
    public void contextLoads() {

    }

    @Test
    public void testRpc() {
        UserService userService = new UserServiceImpl();
        String data = userService.sayHello("lidaopang");
        System.out.println(data);
    }


    @Test
    public void testRpcNettyClient() {
        System.out.println(channel);
        channel.writeAndFlush("hello netty!");
    }



}
