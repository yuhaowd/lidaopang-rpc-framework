package com.zshs.rpcframeworksimple.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName NettyChannelConfig
 * @Description
 * @Author lidaopang
 * @Date 2024/7/4 上午10:38
 * @Version 1.0
 */

@Configuration
public class NettyChannelConfig {


    @Bean
    public Channel channel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel channel = null;
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
            channel = future.channel();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return channel;
    }

}
