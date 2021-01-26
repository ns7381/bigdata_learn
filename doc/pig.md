### 1. grunt命令
pig -x mapreduce

### 加载数据
student = LOAD 'hdfs://cb032-62-master-0.novalocal:8020/pig_data/student_data.txt'
   USING PigStorage(',')
   as ( id:int, firstname:chararray, lastname:chararray, phone:chararray,
   city:chararray );

### 存储数据
STORE student INTO ' hdfs://cb032-62-master-0.novalocal:8020/pig_Output/ ' USING PigStorage (',');

### Dump 运算符用于运行Pig Latin语句，并在屏幕上显示结果，它通常用于调试目的。
dump student

### describe 运算符用于查看关系的模式
describe student

### explain 运算符用于显示关系的逻辑，物理和MapReduce执行计划。
explain student

https://www.w3cschool.cn/apache_pig/apache_pig_overview.html