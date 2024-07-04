package com.zshs.exampleclient;

import com.zshs.exampleclient.service.UserService;

import com.zshs.exampleclient.service.impl.UserServiceImpl;
import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.transport.netty.client.RpcNettyClient;
import io.netty.channel.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class ExampleClientApplicationTests {

    @Resource
    private RpcNettyClient rpcNettyClient;


//    @Resource
//    private Channel channel;


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


        String serviceName = "lidaopang/" + "1.0.0/" + "com.zshs.exampleserver.service.impl.HelloServiceImpl";
        RpcRequest rpcRequest = RpcRequest.builder().serviceName(serviceName).build();
        rpcNettyClient.sendRpcRequest(rpcRequest);


    }



}
