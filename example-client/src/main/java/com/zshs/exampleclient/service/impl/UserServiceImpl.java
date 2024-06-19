package com.zshs.exampleclient.service.impl;

import com.zshs.exampleclient.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {


    @Override
    public String sayHello(String name) {
        String res = "hello:" +name;
        return res;
    }
}