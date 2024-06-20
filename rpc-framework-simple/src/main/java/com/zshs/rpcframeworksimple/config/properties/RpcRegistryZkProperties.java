package com.zshs.rpcframeworksimple.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "rpc.server.registry.zookeeper")
public class RpcRegistryZkProperties {


    String address = "127.0.0.1:2181";
}
