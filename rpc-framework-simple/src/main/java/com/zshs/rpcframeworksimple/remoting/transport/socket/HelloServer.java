package com.zshs.rpcframeworksimple.remoting.transport.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import com.zshs.rpcframeworksimple.remoting.transport.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sun.java.browser.dom.DOMService.getService;

public class HelloServer {

    private static final Logger logger = LoggerFactory.getLogger(HelloServer.class);
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10); // 线程池大小可以根据实际情况调整

    public void start(int port) {
        // 创建 ServerSocket 对象并且绑定一个端口
        try (ServerSocket server = new ServerSocket(port)) {
            logger.info("Server started on port {}", port);
            // 通过 accept() 方法监听客户端请求
            while (true) {
                final Socket socket = server.accept();
                logger.info("Client connected");
                // 使用线程池处理客户端请求
                executorService.execute(() -> {
                    processClientRequest(socket);
                });
            }
        } catch (IOException e) {
            logger.error("Occur IOException:", e);
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
                String requestId = rpcRequest.getRequestId();
                // 接口名
                String interfaceName = rpcRequest.getInterfaceName();
                // 方法名
                String methodName = rpcRequest.getMethodName();
                // 参数列表
                Object[] parameters = rpcRequest.getParameters();
                logger.info("Server receive interfaceName: {}", interfaceName);
                logger.info("Server receive methodName: {}", methodName);
                logger.info("Server receive parameters: {}", parameters);

                // 进行必要的输入验证和处理
//                rpcRequest.setContent("hello world");
                // 通过输出流向客户端发送响应信息
                String o = (String)invokeMethod(rpcRequest);
                RpcResponse<String> rpcResponse = new RpcResponse<>();
                rpcResponse.setData(o);
                objectOutputStream.writeObject(rpcResponse);
                objectOutputStream.flush();
            } else {
                logger.error("Received object is not an instance of Message");
            }
        } catch (IOException e) {
            logger.error("IO Exception occurred", e);
        } catch (ClassNotFoundException e) {
            logger.error("Class not found during deserialization", e);
        } finally {
            // 关闭 socket 连接
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                logger.error("Error while closing socket", e);
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

        try {
            // 获取目标类
            Class<?> targetClass = Class.forName(interfaceName);
            // 创建目标类实例
            Object targetInstance = targetClass.getDeclaredConstructor().newInstance();
            // 获取目标方法
            Method targetMethod = targetClass.getMethod(methodName, parameterTypes);
            // 调用目标方法并返回结果
            return targetMethod.invoke(targetInstance, parameters);

        } catch (Exception e) {
            logger.error("Error occurred during invoking method:", e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        HelloServer helloServer = new HelloServer();
        helloServer.start(6666);
    }
}