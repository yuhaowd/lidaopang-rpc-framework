package com.zshs.rpcframeworksimple.remoting.transport.netty.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new StringEncoder());
                        }
                    });

            // 连接服务器
            ChannelFuture future = bootstrap.connect("localhost",8087).sync();

            // 获取 Channel 对象
            Channel channel = future.channel();

            // 发送消息
            channel.writeAndFlush("Hello, Netty!").addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("Message sent successfully");
                    } else {
                        System.err.println("Message send failed");
                        future.cause().printStackTrace();
                    }
                    // Close the connection after the message is sent
                    channel.close();
                }
            });

            // 等待关闭连接
            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
