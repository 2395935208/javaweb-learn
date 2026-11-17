# Redis的基础操作
SET/GET：存单个 k-v；DECR：原子减一
对应关系：DECR ≈ MySQL 的 UPDATE SET stock = stock - 1，防超卖原理都是"判断+扣减不可分割"
