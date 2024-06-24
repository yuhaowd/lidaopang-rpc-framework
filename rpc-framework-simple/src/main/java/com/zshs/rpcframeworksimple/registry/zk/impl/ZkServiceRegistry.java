package com.zshs.rpcframeworksimple.registry.zk.impl;

import com.zshs.rpcframeworksimple.registry.zk.ServiceRegistry;
import com.zshs.rpcframeworksimple.util.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetSocketAddress;


/**
 * 服务注册（基于zookeeper实现）
 */
@Service
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    @Resource
    private CuratorFramework zkClient;

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        try {
            String servicePath = "/services/" + rpcServiceName;
            String address = inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort();

            // 检查节点是否存在，如果不存在则创建
            if (zkClient.checkExists().forPath(servicePath) == null) {
                ZookeeperUtil.createNode(zkClient, servicePath, new byte[0]);
            }

            // 将服务地址添加到地址列表中
            ZookeeperUtil.addAddressToNode(zkClient, servicePath, address);

            log.info("Service registered: {} -> {}", rpcServiceName, address);
        } catch (Exception e) {
            log.info("Failed to register service: {}", rpcServiceName);
        }
    }
}