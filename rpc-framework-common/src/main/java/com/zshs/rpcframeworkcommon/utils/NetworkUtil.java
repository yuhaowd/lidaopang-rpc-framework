package com.zshs.rpcframeworkcommon.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtil {

    public static String getLocalIpAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to get local IP address", e);
        }
    }
}
