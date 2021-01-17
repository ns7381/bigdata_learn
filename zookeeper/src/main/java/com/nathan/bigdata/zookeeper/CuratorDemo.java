package com.nathan.bigdata.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class CuratorDemo {
    public static void main(String[] args) throws Exception {
        CuratorDemo demo = new CuratorDemo();
        CuratorFramework client = demo.create("localhost:2181");
        demo.testNode(client);
    }

    public CuratorFramework create(String connectionInfo) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client =
                CuratorFrameworkFactory.builder()
                        .connectString(connectionInfo)
                        .sessionTimeoutMs(5000)
                        .connectionTimeoutMs(5000)
                        .retryPolicy(retryPolicy)
                        .namespace("app1")
                        .build();
        client.start();
        return client;
    }

    public void testNode(CuratorFramework client) throws Exception {
        client.create().forPath("/path1");
        client.create().forPath("/path2","init".getBytes());
        client.create().withMode(CreateMode.EPHEMERAL).forPath("/path3");
        client.create().withMode(CreateMode.EPHEMERAL).forPath("/path4","init".getBytes());
        client.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath("/path5","init".getBytes());
        client.delete().forPath("/path1");

        client.delete().deletingChildrenIfNeeded().forPath("/path1");
        client.delete().withVersion(10086).forPath("/path5");
        client.delete().guaranteed().forPath("/path");
        client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(10086).forPath("path");
        client.getData().forPath("/path");
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/path");
    }
}
