package com.zshs.rpcframeworksimple.remoting.transport.socket;


import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


@Slf4j
public class RpcClient  {

    public Object send(RpcRequest rpcRequest, String host, int port) {
        //1. 创建Socket对象并且指定服务器的地址和端口号
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //2.通过输出流向服务器端发送请求信息
            objectOutputStream.writeObject(rpcRequest);
            //3.通过输入流获取服务器响应的信息
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception:", e);
        }
        return null;
    }

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient();
        Class<?>[] paramTypes = new Class[]{String.class, String.class};
        Object[] parameters = new Object[]{"13800000000", "hello"};
        RpcRequest rpcRequest = RpcRequest.builder().requestId("123456789").interfaceName("com.zshs.rpcframeworksimple.proxy.test.SmsServiceImpl").methodName("send").parameters(parameters).paramTypes(paramTypes)  .group("1").build();

        for (int i = 0; i < 10; i++) {
            rpcRequest.setVersion("1." + i);
            RpcResponse rpcResponse = (RpcResponse) rpcClient.send(rpcRequest, "127.0.0.1", 6666);
            System.out.println("client receive data:" + rpcResponse.getData());
        }
    }
}