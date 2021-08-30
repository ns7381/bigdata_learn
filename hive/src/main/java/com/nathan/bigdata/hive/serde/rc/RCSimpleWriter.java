package com.nathan.bigdata.hive.serde.rc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.RCFile;
import org.apache.hadoop.hive.serde2.columnar.BytesRefArrayWritable;
import org.apache.hadoop.hive.serde2.columnar.BytesRefWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class RCSimpleWriter {
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        conf.set("hive.io.rcfile.column.number.conf", "5");
        FileSystem fs = FileSystem.get(conf);

        RCFile.Writer rcFileWriter = new RCFile.Writer(fs, conf, new Path("/tmp/rctext"));
        // No of Rows........
        for (int j = 0; j < 10; j++) {
            BytesRefArrayWritable dataWrite = new BytesRefArrayWritable(10);
            // Number of Column in Each Row......
            for (int i = 0; i < 5; i++) {
                BytesRefWritable bytes = new BytesRefWritable();
                Text text = new Text("ROW-NUM - " + j + ", COLUMN-NUM = " + i + "\n");
                bytes.set(text.getBytes(), 0, text.getLength());
                // ensure the if required the capacity is increased
                dataWrite.resetValid(i);
                dataWrite.set(i, bytes);
            }
            rcFileWriter.append(dataWrite);
        }
        rcFileWriter.close();
    }
}
