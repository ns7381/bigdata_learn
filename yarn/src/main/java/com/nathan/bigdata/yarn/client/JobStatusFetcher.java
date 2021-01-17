package com.nathan.bigdata.yarn.client;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;

import java.io.File;
import java.io.IOException;

public class JobStatusFetcher {
    private static String HADOOP_CONF_DIR = System.getenv("HADOOP_CONF_DIR");
    private static final Configuration HADOOP_CONF = new Configuration();

    static {
        if (!new File(HADOOP_CONF_DIR).exists()) {
            throw new RuntimeException("HADOOP_CONF_DIR not exist.");
        }
        HADOOP_CONF.addResource(new Path(HADOOP_CONF_DIR, "core-site.xml"));
        HADOOP_CONF.addResource(new Path(HADOOP_CONF_DIR, "hdfs-site.xml"));
        HADOOP_CONF.addResource(new Path(HADOOP_CONF_DIR, "yarn-site.xml"));
        HADOOP_CONF.addResource(new Path(HADOOP_CONF_DIR, "mapred-site.xml"));
        HADOOP_CONF.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        HADOOP_CONF.set("mapred.job.queue.name", "default");
    }

    public static void getLiveApplicationList(String applicationId) {
        YarnClient yarnClient = YarnClient.createYarnClient();
        yarnClient.init(HADOOP_CONF);
        yarnClient.start();
        ApplicationId appId = ApplicationId.newInstance(Long.parseLong(applicationId.split("_")[1]),
                Integer.parseInt(applicationId.split("_")[2]));
        ApplicationReport report = null;
        try {
            report = yarnClient.getApplicationReport(appId);
        } catch (YarnException | IOException e) {
            e.printStackTrace();
        }
        assert report != null;
        System.out.println(report.getYarnApplicationState());
        System.out.println(report.getFinalApplicationStatus());
    }

    public static void main(String[] args) {
        getLiveApplicationList("application_1591181323955_0426");
    }
}
