package com.zshs.rpcframeworksimple.registry.zk.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zshs.rpcframeworksimple.registry.zk.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * 服务发现（基于zookeeper实现）
 */
@Service
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {


    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    @Resource
    private CuratorFramework zkClient;

    @Override
    public InetSocketAddress lookupService(String rpcServiceName, String serverName) {
        try {
            String servicePath = "/services/" + serverName + "/" + rpcServiceName;

            // 获取服务地址列表
            byte[] data = zkClient.getData().forPath(servicePath);
            if (data == null || data.length == 0) {
                log.info("No service found for: " + rpcServiceName);
                return null;
            }

            List<String> addresses = objectMapper.readValue(data, List.class);

            if (addresses.isEmpty()) {
                log.info("No service found for: " + rpcServiceName);
                return null;
            }

            // 随机选择一个地址
            String address = addresses.get(random.nextInt(addresses.size()));
            String[] addressParts = address.split(":");
            String host = addressParts[0];
            int port = Integer.parseInt(addressParts[1]);
            log.info("Service found: " + rpcServiceName + " -> " + address);
            return new InetSocketAddress(host, port);
        } catch (Exception e) {
            log.info("Failed to lookup service: " + rpcServiceName);
            e.printStackTrace();
            return null;
        }
    }
}