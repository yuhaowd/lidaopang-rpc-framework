package com.zshs.rpcframeworksimple.remoting.transport.netty.test;

import com.sun.xml.internal.stream.util.BufferAllocator;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Scanner;


@Slf4j
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new StringEncoder())
                                .addLast(new LoggingHandler(LogLevel.INFO))
                                .addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.info("receive from server: {}",buf.toString(Charset.defaultCharset()));
                                    }
                                });
                    }
                })
                .connect(new InetSocketAddress("localhost", 8081))
                .sync()
                .channel();

        new Thread(()->{
            Scanner sc = new Scanner(System.in);
            while (true) {
                String line = sc.nextLine();
                if ("q".equals(line)) {
                    channel.close();

                    break;
                }
                channel.writeAndFlush(Unpooled.copiedBuffer(line, Charset.defaultCharset()));
            }
        },"input").start();

        channel.closeFuture().sync();
        log.info("退出之后的逻辑");
        group.shutdownGracefully();
    }
}
