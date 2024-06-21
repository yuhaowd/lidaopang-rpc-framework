package com.zshs.exampleserver;


import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class TestServer {

    @Test
    void contextLoads() {
    }



    @Resource
    private CuratorFramework zkClient;


    @Test
    void testZkClient() {

        try {
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node2/00001");
            Stat stat = zkClient.checkExists().forPath("/node2/00001");//不为null的话，说明节点创建成功
            if (stat != null) {
                System.out.println("节点创建成功");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}