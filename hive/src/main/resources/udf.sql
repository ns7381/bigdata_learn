ADD jar /var/lib/hive/hive-1.0-SNAPSHOT.jar;
list jars;
CREATE TEMPORARY FUNCTION STRIP AS 'com.nathan.bigdata.hive.Strip';
select strip('   hadoop') from t1;
show functions;