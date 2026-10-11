package com.example.seckill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(min = 2, max = 20, message = "用户名长度必须为2到20个字符")
        String username,
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 20, message = "密码长度必须为6到20个字符")
        String password,
        @Size(max = 20, message = "手机号长度不能超过20个字符")
        String phone) {
}
