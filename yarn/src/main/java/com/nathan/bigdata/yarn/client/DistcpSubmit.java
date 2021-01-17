package com.nathan.bigdata.yarn.client;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.tools.DistCp;
import org.apache.hadoop.tools.DistCpOptions;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class DistcpSubmit {
    private static final String HADOOP_CONF_DIR = System.getenv("HADOOP_CONF_DIR");
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

    public static Job runDistCp(Path src, Path dst) throws IOException {
        DistCpOptions options = new DistCpOptions(Collections.singletonList(src), dst);
        options.setSyncFolder(true);
        options.setDeleteMissing(true);
        options.setSkipCRC(true);
        options.setMaxMaps(1000);
        options.setMapBandwidth(40);
        options.setBlocking(false);
        options.preserve(DistCpOptions.FileAttribute.USER);
        options.preserve(DistCpOptions.FileAttribute.GROUP);
        options.preserve(DistCpOptions.FileAttribute.PERMISSION);
        options.preserve(DistCpOptions.FileAttribute.BLOCKSIZE);
        try {
            DistCp distcp = new DistCp(HADOOP_CONF, options);
            return distcp.execute();
        } catch (Exception e) {
            throw new IOException("Distcp execute error. ", e);
        }
    }
}
