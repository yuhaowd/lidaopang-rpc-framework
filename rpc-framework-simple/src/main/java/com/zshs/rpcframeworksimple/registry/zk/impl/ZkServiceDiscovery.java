package com.zshs.rpcframeworksimple.registry.zk.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zshs.rpcframeworkcommon.utils.CollectionUtil;
import com.zshs.rpcframeworksimple.exception.RpcException;
import com.zshs.rpcframeworksimple.registry.zk.ServiceDiscovery;
import com.zshs.rpcframeworksimple.util.ThreadLocalUtil;
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

        // 判断 实现类是否为空
        String[] split = rpcServiceName.split("/");
        int length = split.length;
        String servicePath = "/services/" + serverName + "/" + rpcServiceName;
        if (length == 3) {
            log.info("rpcServiceName: {}", rpcServiceName);
            try {
                List<String> impls = zkClient.getChildren().forPath(servicePath);
                if (CollectionUtil.isEmpty(impls)) {
                    log.info("未发现服务 : rpcServiceName: {}", rpcServiceName);
                    return null;
                }
                if (impls.size() != 1) {
                    log.info("发现多个实现，请指定使用哪个实现");
                    return null;
                }
                // 向 thread local中存储实现类的全限定类名
                ThreadLocalUtil.set(impls.get(0));
                servicePath = servicePath + "/" + impls.get(0);
                return getInetSocketAddress(rpcServiceName, servicePath);
            } catch (RpcException e) {
                throw new RpcException("未指定实现类");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 向 thread local中存储实现类的全限定类名
        ThreadLocalUtil.set(split[length - 1]);
        return getInetSocketAddress(rpcServiceName, servicePath);
    }

    private InetSocketAddress getInetSocketAddress(String rpcServiceName, String servicePath) {
        try {
            // 获取服务地址列表
            byte[] data = zkClient.getData().forPath(servicePath);
            if (data == null || data.length == 0) {
                log.info("No service found for: {}", rpcServiceName);
                return null;
            }
            List<String> addresses = objectMapper.readValue(data, List.class);
            if (addresses.isEmpty()) {
                log.info("No service found for: {}", rpcServiceName);
                return null;
            }
            // 随机选择一个地址
            String address = addresses.get(random.nextInt(addresses.size()));
            String[] addressParts = address.split(":");
            String host = addressParts[0];
            int port = Integer.parseInt(addressParts[1]);
            log.info("Service found: {} -> {}", rpcServiceName, address);
            return new InetSocketAddress(host, port);
        } catch (Exception e) {
            log.info("Failed to lookup service: {}", rpcServiceName);
            e.printStackTrace();
            return null;
        }
    }
}