-- 仅用于原有 product 表尚未包含 description 的数据库，执行一次。
-- 已按新版 schema.sql 初始化的数据库不要重复执行。
USE seckill_mall;

ALTER TABLE product
  ADD COLUMN description TEXT NULL COMMENT '商品描述' AFTER name;
