package com.zshs.rpcframeworksimple.registry.zk.impl;

import com.zshs.rpcframeworksimple.registry.zk.ServiceRegistry;
import com.zshs.rpcframeworksimple.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

/**
 * 服务注册（基于zookeeper实现）
 */
@Service
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}