package com.zshs.rpcframeworksimple.spitest;

/**
 * @ClassName SecondImplementation
 * @Description
 * @Author lidaopang
 * @Date 2024/7/5 下午4:21
 * @Version 1.0
 */
public class SecondImplementation implements MyServiceInterface {
    @Override
    public void execute() {
        System.out.println("SecondImplementation executed");
    }
}
