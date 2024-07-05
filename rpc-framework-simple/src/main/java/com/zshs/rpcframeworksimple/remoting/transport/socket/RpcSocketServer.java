package com.zshs.rpcframeworksimple.remoting.transport.socket;

import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
@Slf4j
public class RpcSocketServer {

    @Resource
    private ServerSocket serverSocket;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private volatile boolean running = true;


    @PostConstruct
    public void start() {
        new Thread(() -> {
            try {
                log.info("Socket server started");
                while (running) {
                    Socket socket = serverSocket.accept();
                    log.info("Client connected");
                    // 使用线程池处理客户端请求
                    executorService.execute(() -> processClientRequest(socket));
                }
            } catch (IOException e) {
                if (running) {
                    log.error("Error starting server: {}", e.getMessage());
                }
            }
        }).start();
    }

    @PreDestroy
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            executorService.shutdown();
            log.info("Socket server stopped");
        } catch (IOException e) {
            log.error("Error stopping server: {}", e.getMessage());
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
            // TODO 获取对象的方式可以在优化下
            Class<?> aClass = Class.forName(interfaceName);
            Object service = aClass.newInstance();
//            Object service = ServiceRegistry.getService(interfaceName);
            // 获取方法
            Method method = service.getClass().getMethod(methodName, parameterTypes);
            // 调用方法并返回结果
            return method.invoke(service, parameters);
        } catch (Exception e) {
            log.error("Error occurred during invoking method:", e);
            throw new RuntimeException(e);
        }
    }
}