package com.zshs.exampleserver;

import com.zshs.rpcframeworksimple.remoting.transport.socket.RpcServerDemo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleServerApplication.class, args);
    }

}
