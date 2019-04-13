package com.nathan.bigdata.zookeeper;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKConnection {
    private ZooKeeper zoo;
    final CountDownLatch connectionLatch = new CountDownLatch(1);

    public ZKConnection() {
    }

    public ZooKeeper connect(String host) throws IOException, InterruptedException {
        zoo = new ZooKeeper(host, 2000, we -> {
            if (we.getState() == KeeperState.SyncConnected) {
                connectionLatch.countDown();
            }
        });
        connectionLatch.await();
        return zoo;
    }

    public void close() throws InterruptedException {
        zoo.close();
    }
}