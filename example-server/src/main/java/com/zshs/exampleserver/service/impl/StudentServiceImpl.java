package com.zshs.exampleserver.service.impl;

import com.zshs.exampleserver.entity.Student;
import com.zshs.exampleserver.service.StudentService;
import com.zshs.rpcframeworksimple.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * @ClassName StudentServiceImpl
 * @Description
 * @Author lidaopang
 * @Date 2024/7/8 下午4:42
 * @Version 1.0
 */
@Service
@RpcService
public class StudentServiceImpl implements StudentService {


    @Override
    public Student get() {
        Student student = new Student();
        student.setAge(21);
        student.setName("lidaopang");
        student.setAge(1);
        return student;
    }
}
