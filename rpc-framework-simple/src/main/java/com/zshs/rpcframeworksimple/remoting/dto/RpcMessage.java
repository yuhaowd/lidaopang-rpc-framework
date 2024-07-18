package com.zshs.rpcframeworksimple.remoting.dto;


import lombok.*;

/**
 * @ClassName RpcMessage
 * @Description
 * @Author lidaopang
 * @Date 2024/7/18 上午10.43
 * @Version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    /**
     * rpc message type
     */
    private byte messageType;
    /**
     * serialization type
     */
    private byte codec;
    /**
     * compress type
     */
    private byte compress;
    /**
     * request id
     */
    private int requestId;
    /**
     * request data
     */
    private Object data;

}
