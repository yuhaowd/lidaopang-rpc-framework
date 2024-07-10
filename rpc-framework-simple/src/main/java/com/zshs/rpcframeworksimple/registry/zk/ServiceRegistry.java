package com.zshs.rpcframeworksimple.registry.zk;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * 服务注册
 */
public interface ServiceRegistry {

    /**
     * 注册服务到注册中心
     * @param rpcServiceName
     * @param addressMap
     */
    void registerService(String rpcServiceName, Map<String, InetSocketAddress> addressMap);

}