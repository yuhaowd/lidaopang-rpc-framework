package com.zshs.rpcframeworksimple.registry.zk.impl;

import com.zshs.rpcframeworkcommon.exception.RpcException;
import com.zshs.rpcframeworksimple.registry.zk.ServiceDiscovery;
import com.zshs.rpcframeworksimple.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * 服务发现（基于zookeeper实现）
 */
@Service
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {


    @Resource
    private CuratorFramework zkClient;



    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        return null;
//        CuratorFramework zkClient = CuratorUtils.getZkClient();
//        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
//        if (serviceUrlList.size() == 0) {
//            throw new RpcException(RpcErrorMessage.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
//        }
//        // load balancing
//        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList);
//        log.info("Successfully found the service address:[{}]", targetServiceUrl);
//        String[] socketAddressArray = targetServiceUrl.split(":");
//        String host = socketAddressArray[0];
//        int port = Integer.parseInt(socketAddressArray[1]);
//        return new InetSocketAddress(host, port);
    }
}