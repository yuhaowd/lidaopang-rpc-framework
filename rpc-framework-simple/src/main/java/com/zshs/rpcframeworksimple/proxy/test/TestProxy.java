package com.zshs.rpcframeworksimple.proxy.test;

public class TestProxy {


    public static void main(String[] args) {

        SmsService smsService = (SmsService) ProxyFactory.getProxy(new SmsServiceImpl());
        smsService.send("12345678901", "hello world");
    }
}
