package com.zshs.exampleserver.entity;

import lombok.Data;

import javax.annotation.security.DenyAll;
import java.io.Serializable;

/**
 * @ClassName Student
 * @Description
 * @Author lidaopang
 * @Date 2024/7/8 下午4:43
 * @Version 1.0
 */

@Data
public class Student implements Serializable {



    private int age;
    private String name;
    private int sex;

}
