package com.nathan.bigdata.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

public class HdfsKerberos {
    public static final String USER_KEY = "test2/hdinsight-20190418140738-94-master-0.novalocal@NOVALOCAL";
    public static final String KEY_TAB_PATH = "E:\\tmp\\test2.keytab";

    static Configuration conf = new Configuration();

    static {
        System.setProperty("java.security.krb5.conf", "E:\\tmp\\krb5.conf");
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("fs.defaultFS", "hdfs://hdinsight-20190418140738-94-master-0.novalocal:8020");

        try {
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab(USER_KEY, KEY_TAB_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        RemoteIterator<LocatedFileStatus> it = fs.listFiles(new Path("/user/"), true);
        while (it.hasNext()) {
            LocatedFileStatus next = it.next();
            System.out.println(next.getPath().getName());

        }
    }
}
