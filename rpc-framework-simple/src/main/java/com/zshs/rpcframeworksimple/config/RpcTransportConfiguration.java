package com.zshs.rpcframeworksimple.config;

import com.zshs.rpcframeworksimple.remoting.transport.RpcRequestTransport;
import com.zshs.rpcframeworksimple.remoting.transport.netty.client.RpcNettyClient;
import com.zshs.rpcframeworksimple.remoting.transport.socket.RpcSocketClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ServiceLoader;

@Configuration
public class RpcTransportConfiguration {

    @Value("${rpc.transport.type:socket}")
    private String transportType;

    @Bean
    public RpcRequestTransport rpcRequestTransport() {
        ServiceLoader<RpcRequestTransport> loader = ServiceLoader.load(RpcRequestTransport.class);

        for (RpcRequestTransport transport : loader) {
            if ("netty".equalsIgnoreCase(transportType) && transport instanceof RpcNettyClient) {
                return transport;
            } else if ("socket".equalsIgnoreCase(transportType) && transport instanceof RpcSocketClient) {
                return transport;
            }
        }
        throw new IllegalArgumentException("Unknown transport type: " + transportType);
    }
}
