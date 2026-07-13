package com.example.springbootdemo.entity;

public class User {
    private Long id;
    private String username;
    private Integer age;

    public User(Long id,String username,Integer age){
        this.id = id;
        this.username = username;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    public Integer getAge() {
        return age;
    }
}
