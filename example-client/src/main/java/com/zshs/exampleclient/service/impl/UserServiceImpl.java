package com.zshs.exampleclient.service.impl;

import com.zshs.exampleclient.service.UserService;
import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import com.zshs.rpcframeworksimple.remoting.transport.socket.RpcClient;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {


    @Override
    public String sayHello(String name) {

        // 优化发送的逻辑,可以将这部分代码放在代理对象中

        RpcClient rpcClient = new RpcClient();
        Class<?>[] paramTypes = new Class[]{String.class};
        Object[] parameters = new Object[]{name};
        RpcRequest rpcRequest = RpcRequest.builder().requestId("123456789").interfaceName("com.zshs.exampleserver.service.impl.HelloServiceImpl").methodName("sayHello").parameters(parameters).paramTypes(paramTypes).group("1").build();

        RpcResponse rpcResponse = (RpcResponse) rpcClient.send(rpcRequest, "127.0.0.1", 6666);
        String data = (String) rpcResponse.getData();
        System.out.println("client receive data:" + rpcResponse.getData());
        return data;
    }
}