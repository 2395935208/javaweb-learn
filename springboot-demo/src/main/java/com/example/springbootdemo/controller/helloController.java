package com.example.springbootdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//第一个注解作用：controller表明这是一个控制器，ResponseBody表明返回的是一个json数据，不是页面
@RestController
//第二个注解设置请求路径，访问路径为/hello
@RequestMapping("/hello")
public class helloController {
    //第三个注解设置请求方法为GET
    @GetMapping
    public String hello(){
        return"Hello Spring Boot!";
    }
}
