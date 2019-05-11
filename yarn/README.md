mv simple-yarn-app-0.1.0.jar YARNAPP.jar
cp YARNAPP.jar /opt/
ll /opt/
su -s /bin/bash -c 'hdfs dfs -rm -f /apps/YARNAPP.jar' hdfs
su -s /bin/bash -c 'hdfs dfs -copyFromLocal /opt/YARNAPP.jar /apps/YARNAPP.jar' hdfs
hadoop jar YARNAPP.jar com.nathan.bigdata.yarn.Client
yarn logs -applicationId application_1554804067024_0036
