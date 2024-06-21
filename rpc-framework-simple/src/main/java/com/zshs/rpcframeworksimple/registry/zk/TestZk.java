package com.zshs.rpcframeworksimple.registry.zk;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class TestZk {


    public static void main(String[] args) {

        int BASE_SLEEP_TIME = 1000;
        int MAX_RETRIES = 3;

        // Retry strategy. Retry 3 times, and will increase the sleep time between retries.

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // the server to connect to (can be a server list)
                .connectString("192.168.170.129:2181")
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();

        try {
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/node1122/00001");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
