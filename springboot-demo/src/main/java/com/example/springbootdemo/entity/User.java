package com.example.springbootdemo.entity;

public class User {
    private Long id;
    private String username;
    private Integer age;

    public User(){

    }

    public User(Long id,String username,Integer age){
        this.id = id;
        this.username = username;
        this.age = age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
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
