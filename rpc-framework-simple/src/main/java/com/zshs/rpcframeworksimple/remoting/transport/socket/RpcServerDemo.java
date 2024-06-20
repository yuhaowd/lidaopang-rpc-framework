package com.zshs.rpcframeworksimple.remoting.transport.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zshs.rpcframeworkcommon.registry.ServiceRegistry;
import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcServerDemo {


    private static final ExecutorService executorService = Executors.newFixedThreadPool(10); // 线程池大小可以根据实际情况调整

    public void start(int port) {
        // 创建 ServerSocket 对象并且绑定一个端口
        try (ServerSocket server = new ServerSocket(port)) {
            log.info("Server started on port {}", port);
            // 通过 accept() 方法监听客户端请求
            while (true) {
                final Socket socket = server.accept();
                log.info("Client connected");
                // 使用线程池处理客户端请求
                executorService.execute(() -> {
                    processClientRequest(socket);
                });
            }
        } catch (IOException e) {
            log.error("Occur IOException:", e);
        }
    }

    private void processClientRequest(Socket socket) {
        // 对每个客户端请求使用新的线程进行处理
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            // 通过输入流读取客户端发送的请求信息
            Object object = objectInputStream.readObject();
            if (object instanceof RpcRequest) {
                // 获取RpcRequest 传递的信息
                RpcRequest rpcRequest = (RpcRequest) object;
                // 进行必要的输入验证和处理
                // 通过输出流向客户端发送响应信息
                String o = (String)invokeMethod(rpcRequest);
                RpcResponse<String> rpcResponse = new RpcResponse<>();
                rpcResponse.setData(o);
                objectOutputStream.writeObject(rpcResponse);
                objectOutputStream.flush();
            } else {
                log.error("Received object is not an instance of Message");
            }
        } catch (IOException e) {
            log.error("IO Exception occurred", e);
        } catch (ClassNotFoundException e) {
            log.error("Class not found during deserialization", e);
        } finally {
            // 关闭 socket 连接
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                log.error("Error while closing socket", e);
            }
        }
    }

    // 执行目标方法
    public Object invokeMethod(RpcRequest rpcRequest) {
        // 获取接口名
        String interfaceName = rpcRequest.getInterfaceName();
        // 获取方法名
        String methodName = rpcRequest.getMethodName();
        // 获取参数列表
        Object[] parameters = rpcRequest.getParameters();
        // 获取参数类型列表
        Class<?>[] parameterTypes = rpcRequest.getParamTypes();
        // 获取接口实例
        log.info("Server receive interfaceName: {}", interfaceName);
        log.info("Server receive methodName: {}", methodName);
        log.info("Server receive parameters: {}", parameters);

        try {
            Object service = ServiceRegistry.getService(interfaceName);
            // 获取方法
            Method method = service.getClass().getMethod(methodName, parameterTypes);
            // 调用方法并返回结果
            return method.invoke(service, parameters);
        } catch (Exception e) {
            log.error("Error occurred during invoking method:", e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        RpcServerDemo rpcServer = new RpcServerDemo();
        rpcServer.start(6666);
    }
}