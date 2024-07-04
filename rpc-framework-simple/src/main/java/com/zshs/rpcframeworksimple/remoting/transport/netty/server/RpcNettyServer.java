package com.zshs.rpcframeworksimple.remoting.transport.netty.server;

import com.zshs.rpcframeworksimple.properties.RpcNettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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
    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new StringDecoder());
                            p.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.info("server receive: {}", msg);
                                }
                            });
                        }
                    });
            ChannelFuture f = null;
            f = b.bind(rpcNettyProperties.getHost(), rpcNettyProperties.getPort());
            f.channel().closeFuture();
        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
        }
    }
}