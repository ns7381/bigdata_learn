##单分区表
create table t1(
    id      int,
    name    string,
    hobby   array<string>,
    add     map<String,string>
)
partitioned by (pt_d string)
row format delimited      #分割符设置开始语句
fields terminated by ','  #设置字段与字段之间的分隔符
collection items terminated by '-'
map keys terminated by ':'
STORED AS TEXTFILE;

#示例行数据  1,xiaoming,book-TV-code,beijing:chaoyang-shagnhai:pudong
#加载数据
load data local inpath '/var/lib/hive/test' overwrite into table t1 partition (pt_d = '201701');
#显示分区
show partitions t1;
#插入另一个分区
load data local inpath '/var/lib/hive/test2' overwrite into table t1 partition ( pt_d = '201702');
查询相应分区的数据
select * from t1 where pt_d = '201701';
添加分区 增加一个分区文件
alter table t1 add partition (pt_d = '201703');
#删除分区
alter table t1 drop partition(pt_d='201703');
#修复分区 修复分区就是重新同步hdfs上的分区信息
msck repair table t1;


##多个分区字段
create table t2(
    id      int
   ,name    string
   ,hobby   array<string>
   ,add     map<String,string>
)
partitioned by (pt_d string,sex string)
row format delimited
fields terminated by ','
collection items terminated by '-'
map keys terminated by ':'
STORED AS TEXTFILE;

#加载数据
load data local inpath '/var/lib/hive/test2' overwrite into table t10 partition ( pt_d = '0',sex='male');