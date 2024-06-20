package com.zshs.rpcframeworksimple.registry.zk;

import java.net.InetSocketAddress;


/**
 * 服务发现
 */
public interface ServiceDiscovery {
    /**
     * 根据 rpcServiceName 获取远程服务地址
     *
     * @param rpcServiceName 完整的服务名称（class name+group+version）
     * @return 远程服务地址
     */
    InetSocketAddress lookupService(String rpcServiceName);
}