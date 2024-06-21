package com.zshs.rpcframeworksimple.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@Data
@ConfigurationProperties(prefix = "rpc.server")
public class RpcServerProperties {

    private int port = 6666; // 默认端口号

    private String host = "127.0.0.1";

}