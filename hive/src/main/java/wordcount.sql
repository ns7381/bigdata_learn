create table docs(line string) STORED AS TEXTFILE;
load data local inpath '/var/lib/hive/test2' overwrite into table docs;
create table word_count as
select word, count(1) as count from
(select explode(split(line,' '))as word from docs) w
group by word
order by word;
select * from word_count;