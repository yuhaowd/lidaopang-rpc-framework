package com.zshs.exampleserver;



import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.transport.netty.client.RpcNettyClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class ExampleServerApplicationTests {

    @Resource
    private RpcNettyClient rpcNettyClient;

    @Test
    void contextLoads() {
    }

    @Resource
    private CuratorFramework zkClient;


    @Test
    public void testZkClient() {

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

    @Test
    public void testRpcNettyClient() {


        String serviceName = "lidaopang/" + "1.0.0/" + "com.zshs.exampleserver.service.impl.HelloServiceImpl";
        RpcRequest rpcRequest = RpcRequest.builder().serviceName(serviceName).build();
        rpcNettyClient.sendRpcRequest(rpcRequest);


    }

}
