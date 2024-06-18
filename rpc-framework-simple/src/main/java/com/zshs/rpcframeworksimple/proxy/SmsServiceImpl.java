package com.zshs.rpcframeworksimple.proxy;


public class SmsServiceImpl implements SmsService{
    @Override
    public String send(String phone, String message) {
        String result = phone + ":" + message;
        return result;
    }
}
