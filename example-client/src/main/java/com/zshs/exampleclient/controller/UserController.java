package com.zshs.exampleclient.controller;

import com.zshs.exampleserver.entity.Student;
import com.zshs.exampleserver.service.HelloService;
import com.zshs.exampleserver.service.StudentService;
import com.zshs.rpcframeworksimple.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @RpcReference
    private HelloService helloService;

//    @RpcReference(implementation = "com.zshs.exampleserver.service.impl.HiServiceImpl", group = "lidaopang", version = "1.0.0")
    private HelloService hiService;

    @RpcReference(implementation = "com.zshs.exampleserver.service.impl.StudentServiceImpl", group = "lidaopang", version = "1.0.0")
    private StudentService studentService;


    @GetMapping("/hello")
    public String sayHello(String name) {
        String s = helloService.sayHello(name);
        log.info("receive from server result: {}", s);

        return s;
    }

    @GetMapping("/hi")
    public String sayHi(String name) {

        String s = hiService.sayHello(name);
        log.info("receive from server result: {}", s);

        return s;
    }

    @GetMapping("/hello2")
    public String sayHello2(String name, Integer age) {

        String s = helloService.sayHello(name, age);
        log.info("receive from server result: {}", s);

        return s;
    }

    @GetMapping("/stu")
    public Student getStudent() {
        Student student = studentService.get();
        log.info("receive from server result:{}", student);

        return student;
    }
}