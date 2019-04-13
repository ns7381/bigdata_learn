package com.nathan.bigdata.rpc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Client {
    public static void main(String[] args) throws IOException {
        // 定义接口协议的代理对象,实现服务调用
        // 代理对象通过前置增强,后置增强,封装了socket连接,数据传输,反射调用,序列化等细节
        // 第2个参数versionID对应于接口协议的versionID
        NameNodeProtocol protocol = RPC.getProxy(NameNodeProtocol.class, 1L,
                new InetSocketAddress("localhost", 5678), new Configuration());
        System.out.println(protocol.getMetaData());
    }
}
