##### 操作Hive

```
hive> use dbtaobao; -- 使用dbtaobao数据库
hive> show tables; -- 显示数据库中所有表。
hive> show create table user_log; -- 查看user_log表的各种属性；
hive> desc user_log;
```

###### 简单查询分析

```
hive> select brand_id from user_log limit 10; -- 查看日志前10个交易日志的商品品牌
hive> select month,day,cat_id from user_log limit 20; -- 查询前20个交易日志中购买商品时的时间和商品的种类
```

###### 查询条数统计分析

```
hive> select count(*) from user_log; -- 用聚合函数count()计算出表内有多少条行数据
hive> select count(distinct user_id) from user_log; -- 在函数内部加上distinct，查出user_id不重复的数据有多少条
hive> select count() from (select user_id,item_id,cat_id,merchant_id,brand_id,month,day,action from user_log group by user_id,item_id,cat_id,merchant_id,brand_id,month,day,action having count()=1)a; --查询不重复的数据有多少条(为了排除客户刷单情况)
```

###### 关键字条件查询分析 

```
hive> select count(distinct user_id) from user_log where action='2'; --查询双11那天有多少人购买了商品
hive> select count(*) from user_log where action='2' and brand_id=2661; --取给定时间和给定品牌，求当天购买的此品牌商品的数量
```

###### 用户实时查询分析

```
不同的品牌的浏览次数
hive> create table scan(brand_id INT,scan INT) COMMENT 'This is the search of bigdatataobao' ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE; -- 创建新的数据表进行存储
hive> insert overwrite table scan select brand_id,count(action) from user_log where action='2' group by brand_id; --导入数据
hive> select * from scan; -- 显示结果
```

