package com.nathan.bigdata.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.List;


/**
 * 用户代理模式访问hdfs
 * @author ningsheng
 */
class HadoopProxy {
    private static final Logger LOGGER = Logger.getLogger(HadoopProxy.class);
    private static final String CORE_SITE = "core-site.xml";
    private static final String HDFS_SITE = "hdfs-site.xml";
    private static final String HADOOP_CONF_DIR = System.getenv("HADOOP_CONF_DIR");
    private static Configuration conf;

    @PostConstruct
    private void initConfiguration() {
        System.setProperty("HADOOP_USER_NAME", "hdfs");
        conf = new Configuration();
        conf.set("fs.defaultFS", "viewfs://test");
        conf.set("fs.viewfs.mounttable.test.link./ns1", "hdfs://ns1/");
        conf.set("fs.viewfs.mounttable.test.link./ns2", "hdfs://ns2/");
        conf.addResource(new Path(HADOOP_CONF_DIR, CORE_SITE));
        conf.addResource(new Path(HADOOP_CONF_DIR, HDFS_SITE));
    }

    public FileStatus[] treeFiles(String dir, String proxyUserName) {
        FileSystem viewFs = null;
        try {
            viewFs = getFileSystem(proxyUserName);
            return treeFiles(dir, viewFs);
        } catch (IOException e) {
            close(null);
            LOGGER.error("HDFS list file error: ", e);
            throw new RuntimeException("HDFS list file error.");
        }
    }

    private FileStatus[] treeFiles(String dir, FileSystem viewFs) throws IOException {
        FileStatus[] fileStatuses;
        try {
            return viewFs.listStatus(new Path(dir));
        } catch (AccessControlException e) {
            LOGGER.error("HDFS list file error: ", e);
            throw new RuntimeException("无权限查看此目录，请确认输入目录是否有读权限。");
        } catch (FileNotFoundException e) {
            LOGGER.error("HDFS list file error: ", e);
            throw new RuntimeException("目录不存在，请确认输入目录。");
        }
    }

    void mkdir(String path, String proxyUserName) {
        FileSystem viewFs = null;
        try {
            viewFs = getFileSystem(proxyUserName);
            viewFs.mkdirs(new Path(path));
            close(viewFs);
            LOGGER.info(String.format("HDFS-OPERATE: mkdir %s.", path));
        } catch (IOException e) {
            close(viewFs);
            LOGGER.error(String.format("HDFS-OPERATE: mkdir %s.", path), e);
            throw new RuntimeException("HDFS mkdir file error.");
        }
    }

    void copy(List<String> sourceFiles, String targetPath, String sourceProxyUserName, String targetProxyUserName) {
        FileSystem srcFs = null;
        FileSystem dstFs = null;
        try {
            srcFs = getFileSystem(sourceProxyUserName);
            dstFs = getFileSystem(targetProxyUserName);
            for (int i = 0; i < sourceFiles.size(); i++) {
                String sourceFileName = sourceFiles.get(i);
                FileUtil.copy(srcFs, new Path(sourceFileName), dstFs, new Path(targetPath), false, false, conf);
            }
            close(srcFs);
            close(dstFs);
            LOGGER.info(String.format("HDFS-OPERATE: Form %s, TO %s.", sourceFiles.toString(), targetPath));
        } catch (IOException e) {
            close(srcFs);
            close(dstFs);
            LOGGER.error(String.format("HDFS-OPERATE: Form %s, TO %s.", sourceFiles.toString(), targetPath), e);
            throw new RuntimeException("HDFS copy file error.");
        }
    }

    private FileSystem getFileSystem(String proxyUserName) throws IOException {
        if ("hdfs".equals(proxyUserName)) {
            throw new RuntimeException("Forbidden user: hdfs");
        } else {
            LOGGER.info("HDFS-OPERATE: proxy user " + proxyUserName);
        }
        UserGroupInformation proxyUser = UserGroupInformation.createProxyUser(proxyUserName, UserGroupInformation.getCurrentUser());
        return execute(() -> FileSystem.get(conf), proxyUser);
    }

    private <T> T execute(PrivilegedExceptionAction<T> action, UserGroupInformation ugi) {
        try {
            T result = null;

            // Retry strategy applied here due to HDFS-1058. HDFS can throw random
            // IOException about retrieving block from DN if concurrent read/write
            // on specific file is performed (see details on HDFS-1058).
            int tryNumber = 0;
            boolean succeeded = false;
            do {
                tryNumber += 1;
                try {
                    result = ugi.doAs(action);
                    succeeded = true;
                } catch (IOException ex) {
                    if (!ex.getMessage().contains("Cannot obtain block length for")) {
                        throw ex;
                    }
                    if (tryNumber >= 3) {
                        throw ex;
                    }
                    LOGGER.info("HDFS threw 'IOException: Cannot obtain block length' exception. " +
                            "Retrying... Try #" + (tryNumber + 1));
                    Thread.sleep(1000);
                }
            } while (!succeeded);
            return result;
        } catch (Exception e) {
            LOGGER.error("Could not connect to HDFS", e);
            throw new RuntimeException("Could not connect to HDFS.");
        }
    }

    private void close(FileSystem fs) {
        if (null != fs) {
            try {
                fs.close();
            } catch (IOException e) {
                LOGGER.warn("Tried to close the fileSystem but failed", e);
            }
        }
    }
}
