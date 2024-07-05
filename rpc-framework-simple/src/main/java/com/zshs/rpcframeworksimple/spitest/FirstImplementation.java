package com.zshs.rpcframeworksimple.spitest;

public class FirstImplementation implements MyServiceInterface {
    @Override
    public void execute() {
        System.out.println("FirstImplementation executed");
    }
}