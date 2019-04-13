package com.nathan.bigdata.zookeeper;

import com.nathan.bigdata.zookeeper.leader.ProcessNode;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

public class ZKManagerImpl implements ZKManager {
    private static ZooKeeper zkeeper;
    private static ZKConnection zkConnection;

    public ZKManagerImpl() {
        initialize();
    }

    public ZKManagerImpl(final String url, final ProcessNode.ProcessNodeWatcher processNodeWatcher) throws IOException {
        zkeeper = new ZooKeeper(url, 120000, processNodeWatcher);
    }

    /**
     * Initialize connection
     */
    private void initialize() {
        try {
            zkConnection = new ZKConnection();
            zkeeper = zkConnection.connect("117.73.3.115:2181");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            zkConnection.close();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String createNode(final String node, final boolean watch, final boolean ephimeral) {
        String createdNodePath = null;
        try {

            final Stat nodeStat =  zkeeper.exists(node, watch);

            if(nodeStat == null) {
                createdNodePath = zkeeper.create(node, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, (ephimeral ?  CreateMode.EPHEMERAL_SEQUENTIAL : CreateMode.PERSISTENT));
            } else {
                createdNodePath = node;
            }

        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return createdNodePath;
    }

    @Override
    public boolean watchNode(final String node, final boolean watch) {

        boolean watched = false;
        try {
            final Stat nodeStat =  zkeeper.exists(node, watch);

            if(nodeStat != null) {
                watched = true;
            }

        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return watched;
    }

    @Override
    public List<String> getChildren(final String node, final boolean watch) {

        List<String> childNodes = null;

        try {
            childNodes = zkeeper.getChildren(node, watch);
        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return childNodes;
    }

    public void create(String path, byte[] data) throws KeeperException, InterruptedException {
        Stat exists = zkeeper.exists(path, null);
        zkeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public Object getZNodeData(String path, boolean watchFlag) {
        try {
            byte[] b = null;
            b = zkeeper.getData(path, null, null);
            String data = new String(b, "UTF-8");
            System.out.println(data);
            return data;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void update(String path, byte[] data) throws KeeperException, InterruptedException {
        int version = zkeeper.exists(path, true).getVersion();
        zkeeper.setData(path, data, version);
    }

    public static void main(String[] args) throws KeeperException, InterruptedException {
        new ZKManagerImpl().create("/test", "test".getBytes());

    }
}
