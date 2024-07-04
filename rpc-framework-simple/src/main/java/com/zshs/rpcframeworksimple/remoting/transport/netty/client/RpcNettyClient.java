package com.zshs.rpcframeworksimple.remoting.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName RpcNettyClient
 * @Description
 * @Author lidaopang
 * @Date 2024/7/4 上午9:59
 * @Version 1.0
 */

@Slf4j
public class RpcNettyClient {




    public static void main(String[] args) throws InterruptedException {

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
            ChannelFuture future = bootstrap.connect("localhost", 8081).sync();

            // 获取 Channel 对象
            Channel channel = future.channel();

            // 发送消息
            channel.writeAndFlush("Hello, Netty!");

            // 等待关闭连接
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
