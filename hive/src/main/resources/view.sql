create table test(id int,name string);
CREATE VIEW test_view(
id,
name_length
)
AS SELECT id,length(name) FROM test;
insert into table test values (1, "nathan");