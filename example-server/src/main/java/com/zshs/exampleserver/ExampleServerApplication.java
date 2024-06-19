package com.zshs.exampleserver;

import com.zshs.exampleserver.service.impl.HelloServiceImpl;
import com.zshs.rpcframeworkcommon.registry.ServiceRegistry;
import com.zshs.rpcframeworksimple.remoting.transport.socket.RpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleServerApplication.class, args);


        // 将服务注册进注册中心
//        ServiceRegistry.register("com.zshs.exampleserver.service.HelloService", new HelloServiceImpl());

        // 开启 Socket 服务端
        // TODO 优化开启监听的方式
        RpcServer rpcServer = new RpcServer();
        rpcServer.start(6666);

    }

}
