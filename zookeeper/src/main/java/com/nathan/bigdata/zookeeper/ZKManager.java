package com.nathan.bigdata.zookeeper;

import org.apache.zookeeper.KeeperException;

import java.util.List;

public interface ZKManager {
    String createNode(String node, boolean watch, boolean ephimeral);

    boolean watchNode(String node, boolean watch);

    List<String> getChildren(String node, boolean watch);

    /**
     * Create a Znode and save some data
     *
     * @param path
     * @param data
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void create(String path, byte[] data) throws KeeperException, InterruptedException;

    /**
     * Get ZNode Data
     *
     * @param path
     * @param watchFlag
     * @throws KeeperException
     * @throws InterruptedException
     */
    public Object getZNodeData(String path, boolean watchFlag);

    /**
     * Update the ZNode Data
     *
     * @param path
     * @param data
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void update(String path, byte[] data) throws KeeperException, InterruptedException, KeeperException;
}