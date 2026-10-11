package com.example.seckill.dto;

import java.util.List;

public record PageResult<T>(List<T> list, long total, int page, int size) {
}
