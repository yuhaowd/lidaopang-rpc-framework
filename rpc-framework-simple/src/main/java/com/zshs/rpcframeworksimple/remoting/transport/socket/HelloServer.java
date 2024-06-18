package com.zshs.rpcframeworksimple.remoting.transport.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import com.zshs.rpcframeworksimple.remoting.transport.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                RpcResponse<String> rpcResponse = new RpcResponse<>();
                rpcResponse.setData(interfaceName + "." + methodName);
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

    public static void main(String[] args) {
        HelloServer helloServer = new HelloServer();
        helloServer.start(6666);
    }
}