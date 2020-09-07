#Hive 中 table 可以拆分成 Partition table 和 桶(BUCKET),对于Table或者Partition, Hive可以进一步组织成桶,也就是说桶Bucket是更为细粒度的数据范围划分.Bucket是对指定列进行hash,然后根据hash值除以桶的个数进行求余,决定该条记录存放在哪个桶中.桶操作是通过 Partition 的 CLUSTERED BY 实现的,BUCKET 中的数据可以通过 SORT BY 排序.
#
#优点①:获得更高的查询处理效率.桶为表加上了额外的结构,Hive 在处理有些查询时能利用这个结构.具体而言,连接两个在相同列上划分了桶的表,可以使用 Map-side Join 的高效实现.
#
#优点②:抽样(sampling)可以在全体数据上进行采样,这样效率自然就低,它还是要去访问所有数据.而如果一个表已经对某一列制作了bucket,就可以采样所有桶中指定序号的某个桶,这就减少了访问量.

create table bucket_table(id string) clustered by(id) into 4 buckets;