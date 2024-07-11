package com.zshs.rpcframeworksimple.remoting.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private String serviceImplName;
    private String serviceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private Class<?> returnType;
    private String version;
    private String group;

}
