package com.zshs.rpcframeworksimple.registry.zk.impl;

import com.zshs.rpcframeworksimple.registry.zk.ServiceRegistry;
import com.zshs.rpcframeworksimple.util.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Map;


/**
 * 服务注册（基于zookeeper实现）
 */
@Service
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {


    @Resource
    private CuratorFramework zkClient;

    @Override
    public void registerService(String rpcServiceName, Map<String, InetSocketAddress> addressMap) {
        addressMap.forEach((serverName, inetSocketAddress) -> {
            String rpcService = serverName + "/" + rpcServiceName;
            String servicePath = "/services/" + rpcService;

            String address = inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort();
            try {
                // 检查节点是否存在，如果不存在则创建
                if (zkClient.checkExists().forPath(servicePath) == null) {
                    ZookeeperUtil.createNode(zkClient, servicePath, new byte[0]);
                }
                // 将服务地址添加到地址列表中
                ZookeeperUtil.addAddressToNode(zkClient, servicePath, address);
                // 注册优雅关闭钩子
                log.info("注册  {}  节点的删除钩子", rpcServiceName);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        log.info("-------------开始删除  {}  节点-------------", rpcService);
                        ZookeeperUtil.deleteNode(zkClient, servicePath, address);
                        log.info("节点  {}  删除成功", servicePath);
                    } catch (Exception e) {
                        log.info("-------------节点  {}  删除失败-------------", servicePath);
                    }
                }));
                log.info("Service registered: {} -> {}", rpcServiceName, address);
            } catch (Exception ex) {
                log.error("error: {}", ex.getMessage());
            }
        });
        // 使用双重锁保证只注册一次
        /**
         * 双重检查锁
         * if (!shutdownHookRegistered) {
         *                 synchronized (this) {
         *                     if (!shutdownHookRegistered) {
         *
         *                         shutdownHookRegistered = true;
         *                     }
         *                 }
         *             }
         */
    }
}