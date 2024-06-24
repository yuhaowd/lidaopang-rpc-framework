package com.zshs.rpcframeworksimple.config;


import com.zshs.rpcframeworksimple.properties.RpcRegistryZkProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class ZookeeperConfig {

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;


    @Resource
    private RpcRegistryZkProperties rpcRegistryZkProperties;


    @Bean
    public CuratorFramework getZkClient() {
        // Retry strategy. Retry 3 times, and will increase the sleep time between retries.
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // the server to connect to (can be a server list)
                .connectString(rpcRegistryZkProperties.getAddress())
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        return zkClient;
    }


}
