package com.zshs.rpcframeworksimple.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.List;

public class ZookeeperUtil {


    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static void createNode(CuratorFramework client, String path, byte[] data) throws Exception {
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path, data);
    }

    public static void deleteNode(CuratorFramework client, String path, String address) throws Exception {

        byte[] data = getNodeData(client, path);
        List<String> addresses;

        if (data != null && data.length > 0) {
            addresses = objectMapper.readValue(data, List.class);
        } else {
            addresses = new ArrayList<>();
        }

        // 从addresses列表中，删除address
        addresses.remove(address);

        byte[] newData = objectMapper.writeValueAsBytes(addresses);
        setNodeData(client, path, newData);
    }

    public static byte[] getNodeData(CuratorFramework client, String path) throws Exception {
        return client.getData().forPath(path);
    }

    public static void setNodeData(CuratorFramework client, String path, byte[] data) throws Exception {
        client.setData().forPath(path, data);
    }

    public static void addAddressToNode(CuratorFramework client, String path, String address) throws Exception {
        byte[] data = getNodeData(client, path);
        List<String> addresses;

        if (data != null && data.length > 0) {
            addresses = objectMapper.readValue(data, List.class);
        } else {
            addresses = new ArrayList<>();
        }

        if (!addresses.contains(address)) {
            addresses.add(address);
        }

        byte[] newData = objectMapper.writeValueAsBytes(addresses);
        setNodeData(client, path, newData);
    }
}