package com.zshs.rpcframeworksimple.remoting.transport.netty.server;

import com.zshs.rpcframeworksimple.properties.RpcNettyProperties;
import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import com.zshs.rpcframeworksimple.remoting.transport.netty.codec.RpcMessageCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName RpcNettyServer
 * @Description
 * @Author lidaopang
 * @Date 2024/7/4 上午9:54
 * @Version 1.0
 */
@Service
@Slf4j
public class RpcNettyServer {

    @Resource
    private RpcNettyProperties rpcNettyProperties;

    @PostConstruct
    public void startServer() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture future = new ServerBootstrap().group(group).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LoggingHandler(LogLevel.INFO))
                        .addLast(new LengthFieldBasedFrameDecoder(65536, 12, 4, 0, 0))
                        .addLast(new RpcMessageCodec())
                        .addLast(new SimpleChannelInboundHandler<RpcRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
                                log.info("进入channelRead");
                                log.info("rpcRequest : {}", rpcRequest);
                                Object data = invokeMethod(rpcRequest);
                                RpcResponse<Object> rpcResponse = new RpcResponse<>();
                                rpcResponse.setData(data);
                                log.info("rpcResponse: {}", rpcResponse);
                                // 处理数据并发送回客户端
                                ctx.writeAndFlush(rpcResponse);

                            }
                        });
            }
        }).bind(rpcNettyProperties.getPort()).sync();


        // 异步关闭，避免阻塞当前线程
        future.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.info("异步关闭服务端");
                group.shutdownGracefully();
            }
        });
    }


    public RpcRequest toRpcRequest(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        // 反序列化为 RpcRequest 对象
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        RpcRequest rpcRequest = null;
        try {
            ois = new ObjectInputStream(bis);
            rpcRequest = (RpcRequest) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rpcRequest;
    }

    public byte[] rpcResponseTO(RpcResponse rpcResponse) throws IOException {
        // 序列化 RpcResponse 对象
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(rpcResponse);
        oos.flush();
        byte[] rpcResponseBytes = bos.toByteArray();
        oos.close();
        bos.close();
        return rpcResponseBytes;
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
            // TODO 获取对象的方式可以在优化下
            Class<?> aClass = Class.forName(interfaceName);
            Object service = aClass.getDeclaredConstructor().newInstance();
            // 获取方法
            Method method = service.getClass().getMethod(methodName, parameterTypes);
            // 调用方法并返回结果
            Object result = method.invoke(service, parameters);


            // 获取返回类型
            Class<?> returnType = rpcRequest.getReturnType();
            return result;
        } catch (ClassNotFoundException e) {
            log.error("Class not found: {}", interfaceName, e);
        } catch (NoSuchMethodException e) {
            log.error("Method not found: {} in class: {}", methodName, interfaceName, e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error occurred during invoking method: {}", methodName, e);
        } catch (ClassCastException e) {
            log.error("Return type cast error: {}", e.getMessage(), e);
        }
        return null;
    }
}