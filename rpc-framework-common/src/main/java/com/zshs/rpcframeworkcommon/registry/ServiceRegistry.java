package com.zshs.rpcframeworkcommon.registry;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {
    private static final Map<String, Object> serviceMap = new HashMap<>();

    public static void register(String interfaceName, Object service) {
        serviceMap.put(interfaceName, service);
    }

    public static Object getService(String interfaceName) {
        return serviceMap.get(interfaceName);
    }
}
