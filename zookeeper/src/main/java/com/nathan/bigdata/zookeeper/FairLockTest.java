package com.nathan.bigdata.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FairLockTest {

    private String zkQurom = "localhost:2181";

    private String lockName = "/mylock";

    private String lockZnode = null;

    private ZooKeeper zk;

    final CountDownLatch connectionLatch = new CountDownLatch(1);
    final CountDownLatch waitLatch = new CountDownLatch(1);

    public FairLockTest() {
        try {
            zk = new ZooKeeper(zkQurom, 6000000, watchedEvent -> {
                System.out.println("Receive event " + watchedEvent);
                if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                    connectionLatch.countDown();
                    System.out.println("connection is established...");
                }
            });
            connectionLatch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void ensureRootPath() {
        try {
            if (zk.exists(lockName, true) == null) {
                zk.create(lockName, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取锁
     */
    public void lock() {
        String path = null;
        ensureRootPath();
        try {
            path = zk.create(lockName + "/mylock_", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            lockZnode = path;
            List<String> minPath = zk.getChildren(lockName, false);
            Collections.sort(minPath);
            if (!"".equals(path) && !"".equals(minPath.get(0)) && path.equals(lockName + "/" + minPath.get(0))) {
                System.out.println(Thread.currentThread().getName() + "  get Lock...");
                return;
            }
            String watchNode = null;
            for (int i = minPath.size() - 1; i >= 0; i--) {
                if (minPath.get(i).compareTo(path.substring(path.lastIndexOf("/") + 1)) < 0) {
                    watchNode = minPath.get(i);
                    break;
                }
            }

            if (watchNode != null) {
                Stat stat = zk.exists(lockName + "/" + watchNode, new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                            waitLatch.countDown();
                        }
                    }

                });
                if (stat != null) {
                    System.out.println("Thread " + Thread.currentThread().getId() + " waiting for " + lockName + "/" + watchNode);
                }
            }
            try {
                waitLatch.await();
            } catch (InterruptedException ex) {
                System.out.println(Thread.currentThread().getName() + " notify");
                System.out.println(Thread.currentThread().getName() + "  get Lock...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放锁
     */
    public void unlock() {
        try {
            System.out.println(Thread.currentThread().getName() + "release Lock...");
            zk.delete(lockZnode, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }


    public static void main(String args[]) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 4; i++) {
            service.execute(() -> {
                FairLockTest test = new FairLockTest();
                try {
                    test.lock();
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                test.unlock();
            });
        }
        service.shutdown();
    }

}