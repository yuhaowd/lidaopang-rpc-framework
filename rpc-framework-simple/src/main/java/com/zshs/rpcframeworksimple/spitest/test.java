package com.zshs.rpcframeworksimple.spitest;

import java.util.ServiceLoader;

/**
 * @ClassName test
 * @Description
 * @Author lidaopang
 * @Date 2024/7/5 下午4:22
 * @Version 1.0
 */

public class test {


    public static void main(String[] args) {

        ServiceLoader<MyServiceInterface> loader = ServiceLoader.load(MyServiceInterface.class);
        for (MyServiceInterface service : loader) {
            service.execute();
        }
    }
}