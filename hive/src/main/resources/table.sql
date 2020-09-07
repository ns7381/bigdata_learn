hive -n hive

create database if not exists hive;       #创建数据库
show databases;                           #查看Hive中包含数据库
describe databases;                       #查看hive数据库位置等信息
alter database hive set dbproperties;     #为hive设置键值对属性
use hive;                                 #切换到hive数据库下
drop database if exists hive;             #删除不含表的数据库
drop database if exists hive cascade;     #删除数据库和它中的表

##创建内部表(管理表)
create table if not exists hive.usr(
      name string comment 'username',
      pwd string comment 'password',
      address struct<street:string,city:string,state:string,zip:int> comment  'home address',
      identify map<int,tinyint> comment 'number,sex')
      comment 'description of the table'
     tblproperties('creator'='me','time'='2016.1.1');
#创建外部表
create external table if not exists usr2(
      name string,
      pwd string,
      address struct<street:string,city:string,state:string,zip:int>,
      identify map<int,tinyint>)
      row format delimited fields terminated by ','
      location '/warehouse/tablespace/external/hive/hive.db/usr';
show create table usr;                                      #展示表详细信息
alter table usr add columns(hobby string);                  #增加列
alter table usr replace columns(uname string);              #删除替换列
alter table usr set tblproperties('creator'='liming');      #修改表属性
alter table usr2 partition(city="beijing",state="China")    #修改存储属性
drop table if exists usr1;                                  #删除表
truncate table 表名;                                         #清空表


#索引
create index t1_index ontable t1(id) as 'compact' with deferred rebuild;


