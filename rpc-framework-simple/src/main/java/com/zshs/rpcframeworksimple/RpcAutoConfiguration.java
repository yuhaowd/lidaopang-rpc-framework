package com.zshs.rpcframeworksimple;

import com.zshs.rpcframeworksimple.config.properties.RpcServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.ServerSocket;

@Configuration
@ComponentScan(basePackages = "com.zshs.rpcframeworksimple")
@Slf4j
public class RpcAutoConfiguration {


    @Resource
    private RpcServerProperties properties;

    @Bean
    public ServerSocket getServerSocket() {

        try {
            return new ServerSocket(properties.getPort());
        } catch (IOException e) {
            log.info("ServerSocket Bean 创建失败");
        }
        return null;
    }
}
