package com.zshs.rpcframeworksimple.remoting.transport.socket;


import com.zshs.rpcframeworksimple.registry.zk.impl.ZkServiceDiscovery;
import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import com.zshs.rpcframeworksimple.remoting.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


@Slf4j
public class RpcSocketClient implements RpcRequestTransport {


    @Resource
    private ZkServiceDiscovery zkServiceDiscovery;





    @Override
    public RpcResponse sendRpcRequest(RpcRequest rpcRequest) {
        // 寻找服务
        String serviceName = rpcRequest.getServiceName();
        InetSocketAddress inetSocketAddress = zkServiceDiscovery.lookupService(serviceName);
        //1. 创建Socket对象并且指定服务器的地址和端口号
        try (Socket socket = new Socket("localhost", 7776)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //2.通过输出流向服务器端发送请求信息
            objectOutputStream.writeObject(rpcRequest);
            //3.通过输入流获取服务器响应的信息
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return (RpcResponse) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception:", e);
        }
        return null;
    }
}