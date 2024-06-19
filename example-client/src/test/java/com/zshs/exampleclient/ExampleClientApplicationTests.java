package com.zshs.exampleclient;

import com.zshs.exampleclient.service.UserService;

import com.zshs.exampleclient.service.impl.UserServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ExampleClientApplicationTests {


    @Test
    public void contextLoads() {

    }

    @Test
    public void testRpc() {
        UserService userService = new UserServiceImpl();
        String data = userService.sayHello("lidaopang");
        System.out.println(data);
    }

}
