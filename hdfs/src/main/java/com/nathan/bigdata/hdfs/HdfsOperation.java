package com.nathan.bigdata.hdfs;

import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

public class HdfsOperation {
    private static FileSystem fileSystem;

    private static void initFileSystem(String hdfsUri) {
        // Init HDFS File System Object
        Configuration conf = new Configuration();
        // Set FileSystem URI
        conf.set("fs.defaultFS", hdfsUri);
        // Because of Maven
        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        // Set HADOOP user
        System.setProperty("HADOOP_USER_NAME", "hdfs");
        System.setProperty("hadoop.home.dir", "/");
        try {
            fileSystem = FileSystem.get(URI.create(hdfsUri), conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void mkdir(String path) throws IOException {
        Path newFolderPath= new Path(path);
        if(!fileSystem.exists(newFolderPath)) {
            fileSystem.mkdirs(newFolderPath);
        }
    }

    private static void writeFile(String path, String fileContent) throws IOException {
        Path hdfswritepath = new Path(path);
        FSDataOutputStream outputStream=fileSystem.create(hdfswritepath);

        outputStream.writeBytes(fileContent);
        outputStream.flush();
        outputStream.close();
        fileSystem.close();
    }

    private static void readFile(String path) throws IOException {
        Path hdfsreadpath = new Path(path);
        FSDataInputStream inputStream = fileSystem.open(hdfsreadpath);
        String out= org.apache.commons.io.IOUtils.toString(inputStream, "UTF-8");
        System.out.println(out);
        inputStream.close();
        fileSystem.close();
    }

    private static void readBlock(String path) throws IOException {
        Path hdfsreadpath = new Path(path);
        final RemoteIterator<LocatedFileStatus> files = fileSystem.listFiles(hdfsreadpath, false);
        final BlockLocation[] blockLocations = files.next().getBlockLocations();
        for (BlockLocation blockLocation : blockLocations) {
            System.out.println(blockLocation.getHosts());
            System.out.println(blockLocation.getOffset());
            System.out.println(blockLocation.getLength());
            System.out.println(blockLocation.getTopologyPaths());
            System.out.println(blockLocation.getNames());
            System.out.println(blockLocation.getCachedHosts());
        }
        final FSDataInputStream in = fileSystem.open(hdfsreadpath);
        in.seek(blockLocations[1].getOffset());
        FileOutputStream out = new FileOutputStream(new File("g:/files/hadoop_block2.tar.gz"));

        IOUtils.copyBytes(in, out, 4096, true);
    }

    public static void main(String[] args) throws IOException {
        initFileSystem("hdfs://10.110.26.228:8020");
        mkdir("/nathan");
//        writeFile("/nathan/haha.txt", "hello world.");
        readFile("/nathan/test");
    }
}
