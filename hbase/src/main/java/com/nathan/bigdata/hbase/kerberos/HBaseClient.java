package com.nathan.bigdata.hbase.kerberos;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.security.PrivilegedAction;

import static org.apache.hadoop.fs.CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import static org.apache.hadoop.fs.CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHORIZATION;


public class HBaseClient {
    public static Configuration configuration;
    public static Connection connection;
    public static Admin admin;

    public static void main(String[] args) throws IOException {
        init();
        createTable("student1", new String[]{"score"});
        insertData("student1", "zhangsan", "score", "English", "69");
        insertData("student1", "zhangsan", "score", "Math", "86");
        insertData("student1", "zhangsan", "score", "Computer", "77");
        getData("student1", "zhangsan", "score", "English");
    }

    /**
     * @param myTableName 表名
     * @param colFamily   列族数组
     * @throws Exception
     */
    public static void createTable(String myTableName, String[] colFamily) throws IOException {
        TableName tableName = TableName.valueOf(myTableName);
        if (admin.tableExists(tableName)) {
            System.out.println("table exists!");
        } else {
            HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
            for (String cf : colFamily) {
                tableDescriptor.addFamily(new HColumnDescriptor(cf));
            }
            admin.createTable(tableDescriptor);
        }
    }

    /*添加数据*/

    /**
     * @param tableName 表名
     * @param rowKey    行键
     * @param colFamily 列族
     * @param col       列限定符
     * @param val       数据
     * @throws Exception
     */
    public static void insertData(String tableName, String rowKey, String colFamily, String
            col, String val) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col), Bytes.toBytes(val));
        table.put(put);
        table.close();
    }

    /*获取某单元格数据*/

    /**
     * @param tableName 表名
     * @param rowKey    行键
     * @param colFamily 列族
     * @param col       列限定符
     * @throws IOException
     */
    public static void getData(String tableName, String rowKey, String
            colFamily, String col) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
        //获取的result数据是结果集，还需要格式化输出想要的数据才行
        Result result = table.get(get);
        System.out.println(new
                String(result.getValue(colFamily.getBytes(), col.getBytes())));
        table.close();
    }

    //建立连接
    public static void init() throws IOException {
        String princial = "xxx/job@HADOOP.COM";
        String keytab = "/export/data/xxx.keytab";
        UserGroupInformation ugi = null;
        Configuration configuration = null;
        try {
            configuration = HBaseConfiguration.create();
            configuration.set(HADOOP_SECURITY_AUTHENTICATION, UserGroupInformation.AuthenticationMethod.KERBEROS.name());
            configuration.set(HADOOP_SECURITY_AUTHORIZATION, "true");
            configuration.set("hbase.security.authentication", "kerberos");

            configuration.set("hbase.master.kerberos.principal", "hbase/_HOST@HADOOP.COM");
            configuration.set("hbase.regionserver.kerberos.principal", "hbase/_HOST@HADOOP.COM");
            configuration.set("hbase.zookeeper.quorum", "xxx:2181");
            UserGroupInformation.setConfiguration(configuration);
            ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(princial, keytab);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final Connection[] connections = {null};

        Configuration finalConfiguration = configuration;
        ugi.doAs((PrivilegedAction<Object>) () -> {
            try {
                connections[0] = ConnectionFactory.createConnection(finalConfiguration);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        connection = connections[0];
        admin = connection.getAdmin();
    }


    //关闭连接
    public static void close() {
        try {
            if (admin != null) {
                admin.close();
            }
            if (null != connection) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
