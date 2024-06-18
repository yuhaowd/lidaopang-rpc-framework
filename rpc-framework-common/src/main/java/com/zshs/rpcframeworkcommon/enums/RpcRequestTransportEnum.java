package com.zshs.rpcframeworkcommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author xiaobiaoxu
 * @Date 2023年02月24日 15:30
 */
@AllArgsConstructor
@Getter
public enum RpcRequestTransportEnum {

    NETTY("netty"),
    SOCKET("socket");

    private final String name;
}
