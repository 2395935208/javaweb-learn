package com.example.springbootdemo.dto;

import jakarta.validation.constraints.*;

public class CreateUserRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度 2-20")
    private String username;

    @NotNull(message = "年龄必填")
    @Min(value = 1, message = "年龄最小 1")
    @Max(value = 120, message = "年龄最大 120")
    private Integer age;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
