##### Hive预操作

```
创建临时表inner_user_log
hive> create table dbtaobao.inner_user_log(user_id INT,item_id INT,cat_id INT,merchant_id INT,brand_id INT,month STRING,day STRING,action INT,age_range INT,gender INT,province STRING) COMMENT 'Welcome to XMU dblab! Now create inner table inner_user_log ' ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE;
hive> INSERT OVERWRITE TABLE dbtaobao.inner_user_log select * from dbtaobao.user_log;
```

![](assets/hive-create-table-log.jpg)

#####  Mysql预操作

```
mysql> show databases; #显示所有数据库
mysql> create database dbtaobao; #创建dbtaobao数据库
mysql> use dbtaobao; #使用数据库
mysql> show variables like "char%";
mysql> CREATE TABLE `dbtaobao`.`user_log` (`user_id` varchar(20),`item_id` varchar(20),`cat_id` varchar(20),`merchant_id` varchar(20),`brand_id` varchar(20), `month` varchar(6),`day` varchar(6),`action` varchar(6),`age_range` varchar(6),`gender` varchar(6),`province` varchar(10)) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

##### 执行Sqoop操作

```
sqoop export --connect jdbc:mysql://117.73.8.128:3306/dbtaobao --username root --password 123456aB? --table user_log --export-dir '/warehouse/tablespace/managed/hive/dbtaobao.db/inner_user_log/base_0000001' --fields-terminated-by ',';
```

##### 问题记录

1.  ERROR tool.ExportTool: Error during export:

```
查看yarn日志（也可UI查看）
yarn logs -applicationId application_1554804067024_0010
发现连接mysql报错，将jdbc:mysql://localhost改为ip即可，因为nodemanager分配的不是本地。
```

