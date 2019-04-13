package com.nathan.bigdata.rpc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;

public class PublishService {
    /**
     * 构建RPC服务协议,分别set服务地址、端口号、服务协议、服务实现
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        RPC.Builder builder = new RPC.Builder(new Configuration());
        builder.setBindAddress("localhost")
                .setPort(5678)
                .setProtocol(NameNodeProtocol.class)
                .setInstance(new NameNode());
        RPC.Server server = builder.build();
        server.start();
    }
}
