package com.zshs.exampleclient.service.impl;

import com.zshs.exampleclient.service.UserService;
import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import com.zshs.rpcframeworksimple.remoting.transport.socket.HelloClient;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {



    @Override
    public String sayHello(String name) {


        // HelloService helloService = new HelloServiceImpl();
        // helloService.sayHello(name);
        HelloClient helloClient = new HelloClient();
        Class<?>[] paramTypes = new Class[]{String.class};
        Object[] parameters = new Object[]{ "lidaopang"};
        RpcRequest rpcRequest = RpcRequest.builder().requestId("123456789").interfaceName("com.zshs.exampleserver.service.impl.HelloServiceImpl").methodName("sayHello").parameters(parameters).paramTypes(paramTypes).group("1").build();

        RpcResponse rpcResponse = (RpcResponse) helloClient.send(rpcRequest, "127.0.0.1", 6666);
        String data = (String) rpcResponse.getData();
        System.out.println("client receive data:" + rpcResponse.getData());
        return data;
    }
}
