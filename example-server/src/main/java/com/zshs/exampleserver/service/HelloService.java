package com.zshs.exampleserver.service;


import com.zshs.rpcframeworksimple.annotation.RpcService;


public interface HelloService {


    String sayHello(String name);

    String sayHello(String name, int age);

}
