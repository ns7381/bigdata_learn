package com.nathan.bigdata.rpc;

import org.apache.hadoop.ipc.ProtocolInfo;

/**
 * 模拟定义namenode获取元数据的接口协议
 */
@ProtocolInfo(protocolName = "NameNode", protocolVersion = 1L)
public interface NameNodeProtocol {
    // hadoop的RPC框架必须定义versionID
    // 通过RPC.getProtocolVersion方法获取versionID
    // 使用类注解ProtocolInfo或者直接定义值
    public static final long versionID = 1L;

    public String getMetaData();
}
