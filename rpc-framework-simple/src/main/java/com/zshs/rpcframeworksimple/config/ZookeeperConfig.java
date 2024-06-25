package com.zshs.rpcframeworksimple.config;


import com.zshs.rpcframeworksimple.properties.RpcRegistryZkProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@Slf4j
public class ZookeeperConfig {

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;


    @Resource
    private RpcRegistryZkProperties rpcRegistryZkProperties;
    static Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {


            System.out.println("-----------------------test-------------------");
//            System.out.println("状态: " + watchedEvent.getState() + ", 类型:" + watchedEvent.getType() + ",路径" + watchedEvent.getPath());

//            try {
//                //连接信息
//                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
//                    logger.info("zookeeper connect success state: {}", watchedEvent.getState());
//                    long count = latch.getCount();
//                    if(count == 1){
//                        listenNode("/MDTService", zk, watcher);
//                    }
//                    latch.countDown();
//                }
//                if (watchedEvent.getType() == NodeDeleted) {
//                    logger.info("删除节点" + watchedEvent.getPath());
//                    String path = watchedEvent.getPath();
//                    int i = path.lastIndexOf("/");
//                    String substring = path.substring(i+1, path.length());
//                    getZookeeperData(substring);
//                } else if (watchedEvent.getType() == NodeCreated) {
//                    logger.info("创建节点" + watchedEvent.getPath());
//                } else if (watchedEvent.getType() == NodeDataChanged) {
//                    logger.info("节点数据变化" + watchedEvent.getPath());
//                } else if (watchedEvent.getType() == NodeChildrenChanged) {
//                    logger.info("子节点变化" + watchedEvent.getPath());
//                }
//                if (null != watchedEvent.getPath()) {
//                    Stat exists = zk.exists(watchedEvent.getPath(), false);
//                    //每次调用判断是否是删除节点，如果是删除节点，则不再进行监听，不删除则再次监听
//                    if (exists != null) {
//                        listenNode(watchedEvent.getPath(), zk, this);
//                        logger.info("{}：继续监听服务", watchedEvent.getPath());
//                    } else {
//                        logger.info("{}：服务停止", watchedEvent.getPath());
//                    }
//                }

//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    };


    @Bean
    public CuratorFramework getZkClient() {
        // Retry strategy. Retry 3 times, and will increase the sleep time between retries.
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // the server to connect to (can be a server list)
                .connectString(rpcRegistryZkProperties.getAddress())
                .retryPolicy(retryPolicy)
                .build();

        log.info("-----------zkClient start------------");

        zkClient.start();
        return zkClient;

    }
}