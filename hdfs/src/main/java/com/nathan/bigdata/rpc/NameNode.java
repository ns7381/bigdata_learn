package com.nathan.bigdata.rpc;

public class NameNode implements NameNodeProtocol {
    /**
     * 实现获取元数据接口协议
     * @return
     */
    @Override
    public String getMetaData() {
        return "{block1:{D1,D3,D4}, block2:{D2,D4,D5}}";
    }
}
