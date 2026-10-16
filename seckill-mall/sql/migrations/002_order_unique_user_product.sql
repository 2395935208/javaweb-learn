-- 防一人多单：同一用户对同一秒杀商品只能有一个订单，执行一次。
-- 已按新版 schema.sql 初始化的数据库不要重复执行。
USE seckill_mall;

ALTER TABLE `order` ADD UNIQUE KEY uk_user_product (user_id, product_id);
