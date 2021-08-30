package com.nathan.bigdata.hive.serde;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde.Constants;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeUtils;
import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.lazy.LazyPrimitive;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe.SerDeParameters;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory.ObjectInspectorOptions;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerdeTest {

    /**
     * Test the LazySimpleSerDe class.
     */
    @Test
    public void testLazySimpleSerDe() throws Throwable {
        try {
            // Create the SerDe
            LazySimpleSerDe serDe = new LazySimpleSerDe();
            Configuration conf = new Configuration();
            Properties tbl = createProperties();
            //用Properties初始化serDe
            serDe.initialize(conf, tbl);

            // Data
            Text t = new Text("123\t456\t789\t1000\t5.3\thive and hadoop\t1.\tNULL");
            String s = "123\t456\t789\t1000\t5.3\thive and hadoop\tNULL\tNULL";
            Object[] expectedFieldsData = {new ByteWritable((byte) 123),
                    new ShortWritable((short) 456), new IntWritable(789),
                    new LongWritable(1000), new DoubleWritable(5.3),
                    new Text("hive and hadoop"), null, null};

            // Test
            deserializeAndSerialize(serDe, t, s, expectedFieldsData);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void deserializeAndSerialize(LazySimpleSerDe serDe, Text t, String s,
                                         Object[] expectedFieldsData) throws SerDeException {
        // Get the row ObjectInspector
        StructObjectInspector oi = (StructObjectInspector) serDe
                .getObjectInspector();
        // 获取列信息
        List<? extends StructField> fieldRefs = oi.getAllStructFieldRefs();
        assertEquals(8, fieldRefs.size());

        // Deserialize
        Object row = serDe.deserialize(t);
        for (int i = 0; i < fieldRefs.size(); i++) {
            Object fieldData = oi.getStructFieldData(row, fieldRefs.get(i));
            if (fieldData != null) {
                fieldData = ((LazyPrimitive) fieldData).getWritableObject();
            }
            assertEquals("Field " + i, expectedFieldsData[i], fieldData);
        }
        // Serialize
        assertEquals(Text.class, serDe.getSerializedClass());
        Text serializedText = (Text) serDe.serialize(row, oi);
        assertEquals("Serialized data", s, serializedText.toString());
    }

    //创建schema，保存在Properties中
    private Properties createProperties() {
        Properties tbl = new Properties();

        // Set the configuration parameters
        tbl.setProperty(Constants.SERIALIZATION_FORMAT, "9");
        tbl.setProperty("columns",
                "abyte,ashort,aint,along,adouble,astring,anullint,anullstring");
        tbl.setProperty("columns.types",
                "tinyint:smallint:int:bigint:double:string:int:string");
        tbl.setProperty(Constants.SERIALIZATION_NULL_FORMAT, "NULL");
        return tbl;
    }

}
