package com.example.seckill.common;

public record Result<T>(int code, String message, T data) {
    public static <T> Result<T> success(T data) {

        return new Result<>(200, "成功", data);
    }

    public static Result<Void> error(int code, String message) {

        return new Result<>(code, message, null);
    }
}
