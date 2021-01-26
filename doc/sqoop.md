usage: sqoop COMMAND [ARGS]

Available commands:
  codegen            Generate code to interact with database records
  create-hive-table  Import a table definition into Hive
  eval               Evaluate a SQL statement and display the results
  export             Export an HDFS directory to a database table
  help               List available commands
  import             Import a table from a database to HDFS
  import-all-tables  Import tables from a database to HDFS
  import-mainframe   Import datasets from a mainframe server to HDFS
  job                Work with saved jobs
  list-databases     List available databases on a server
  list-tables        List available tables in a database
  merge              Merge results of incremental imports
  metastore          Run a standalone Sqoop metastore
  version            Display version information

See 'sqoop help COMMAND' for information on a specific command.


示例
## 导入
sqoop import \
--connect jdbc:mysql://10.110.25.71:43189/emr \
--username root \
--password 123456aB \
--table posts \
--target-dir /sqoop/emr2 \            #默认保存在/user/hdfs/table
--columns "id,content,title" \
--as-textfile \
--fields-terminated-by '\t' \
--lines-terminated-by '\n' \
--optionally-enclosed-by '\"' \
--where "id > 100000" \
--m 4                                 #mapper数量


sqoop import --connect jdbc:mysql://10.110.25.71:43189/emr \
--username root --password 123456aB  --table posts \
--hive-import --create-hive-table --hive-table posts -m 1
sqoop-import
--connect jdbc:mysql://hd162.bd/test    //指定连接的数据库
--username root        //连接数据库用户
--password 123456a?  //连接数据库密码
--table export_hbase    //要导入的源表
--hbase-table worker   //导入的目标表，hbase表
--column-falmily worker_info  //导入到的列族
--hbase-row-key id      //指定源表的某列为hbase的row key
--hbase-create-table  //如果hbase表不存在，先建表

## 导出
sqoop export \
--connect jdbc:mysql://10.110.25.71:43189/emr \
--username root \
--password 123456aB \
--table posts \
--export-dir /sqoop

## 列出数据库
sqoop list-databases \
--connect jdbc:mysql://10.110.25.71:43189/ \
--username root \
--password 123456aB

## 列出数据库表
sqoop list-tables \
--connect jdbc:mysql://10.110.25.71:43189/emr \
--username root \
--password 123456aB


## 作业
### 创建作业
sqoop job --create myjob \
-- import \
--connect jdbc:mysql://10.110.25.71:43189/emr \
--username root \
--password 123456aB \
--table posts \
--m 1
### 查看作业列表
sqoop job --list
### 查看作业
sqoop job --show myjob
### 执行作业
sqoop job --exec myjob





