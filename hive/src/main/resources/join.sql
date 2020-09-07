create table if not exists hive.stu(id int,name string)
row format delimited fields terminated by ',' STORED AS TEXTFILE;
create table if not exists hive.course(cid int,sid int)
row format delimited fields terminated by ',' STORED AS TEXTFILE;

load data local inpath '/var/lib/hive/stu.txt' overwrite into table stu;


#内连接使用比较运算符根据每个表共有的列的值匹配两个表中的行
select stu.*, course.* from stu join course on(stu .id=course .sid);
#左连接的结果集包括"LEFT OUTER"子句中指定的左表的所有行, 而不仅仅是连接列所匹配的行.如果左表的某行在右表中没有匹配行, 则在相关联的结果集中右表的所有选择列均为空值
select stu.*, course.* from stu left outer join course on(stu .id=course .sid);
#右连接是左向外连接的反向连接,将返回右表的所有行 如果右表的某行在左表中没有匹配行,则将为左表返回空值
select stu.*, course.* from stu right outer join course on(stu .id=course .sid);
#全连接返回左表和右表中的所有行
select stu.*, course.* from stu full outer join course on(stu .id=course .sid);
#半连接是 Hive 所特有的, Hive 不支持 in 操作,但是拥有替代的方案; left semi join, 称为半连接, 需要注意的是连接的表不能在查询的列中,只能出现在 on 子句中
select stu.* from stu left semi join course on(stu .id=course .sid);

