package com.zshs.rpcframeworksimple.remoting.transport;

import com.zshs.rpcframeworkcommon.extension.SPI;
import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;

/**
 * send RpcRequest。
 *
 * @author shuang.kou
 * @createTime 2020年05月29日 13:26:00
 */
@SPI
public interface RpcRequestTransport {
    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    RpcResponse sendRpcRequest(RpcRequest rpcRequest);
}