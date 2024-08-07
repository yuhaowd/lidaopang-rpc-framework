package com.zshs.rpcframeworksimple.remoting.transport.netty.client;

import com.zshs.rpcframeworksimple.registry.zk.impl.ZkServiceDiscovery;
import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import com.zshs.rpcframeworksimple.remoting.transport.RpcRequestTransport;
import com.zshs.rpcframeworksimple.remoting.transport.netty.codec.RpcMessageCodec;
import com.zshs.rpcframeworksimple.util.ThreadLocalUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName RpcNettyClient
 * @Description
 * @Author lidaopang
 * @Date 2024/7/4 上午9:59
 * @Version 1.0
 */

@Slf4j
public class RpcNettyClient implements RpcRequestTransport {

    @Resource
    private ZkServiceDiscovery zkServiceDiscovery;

    @Resource
    private RpcMessageCodec rpcMessageCodec;


    @Override
    public RpcResponse sendRpcRequest(RpcRequest rpcRequest) {
        // 寻找服务
        String serviceName = rpcRequest.getServiceName();
        InetSocketAddress inetSocketAddress = zkServiceDiscovery.lookupService(serviceName, "netty");
        rpcRequest.setServiceImplName(ThreadLocalUtil.get());
        NioEventLoopGroup group = new NioEventLoopGroup();
        AtomicReference<RpcResponse> rpcResponseRef = new AtomicReference<>();
        try {
            Channel channel = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.INFO))
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 12, 4, 0, 0))
//                                    .addLast(new RpcMessageCodec())
                                    .addLast(rpcMessageCodec)
                                    .addLast(new SimpleChannelInboundHandler<RpcResponse>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
                                            rpcResponseRef.set((rpcResponse));
                                            log.info("receive from server: {}", rpcResponse);
                                            ctx.close();
                                        }
                                    });
                        }
                    })
                    .connect("localhost", inetSocketAddress.getPort())
                    .sync()
                    .channel();
            channel.writeAndFlush(rpcRequest);
            channel.closeFuture().sync();
            log.info("关闭客户端");

            return rpcResponseRef.get();
        } catch (InterruptedException e) {
            log.info("连接出错");
        } finally {
            group.shutdownGracefully();
        }
        return null;
    }

    public byte[] rpcRequestTO(RpcRequest rpcRequest) throws IOException {
        // 序列化 RpcResponse 对象
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(rpcRequest);
        oos.flush();
        byte[] rpcRequestBytes = bos.toByteArray();
        oos.close();
        bos.close();
        return rpcRequestBytes;
    }

    public RpcResponse toRpcResponse(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        // 反序列化为 RpcRequest 对象
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        RpcResponse rpcResponse = null;
        try {
            ois = new ObjectInputStream(bis);
            rpcResponse = (RpcResponse) ois.readObject();
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
        return rpcResponse;
    }
}