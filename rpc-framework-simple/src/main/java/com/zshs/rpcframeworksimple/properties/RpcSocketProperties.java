package com.zshs.rpcframeworksimple.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName RpcSocketProperties
 * @Description
 * @Author lidaopang
 * @Date 2024/7/4 下午1:37
 * @Version 1.0
 */

@Component
@Data
@ConfigurationProperties(prefix = "rpc.server.socket")
public class RpcSocketProperties {

    private int port = 2222;
    private String host = "127.0.0.1";


}
