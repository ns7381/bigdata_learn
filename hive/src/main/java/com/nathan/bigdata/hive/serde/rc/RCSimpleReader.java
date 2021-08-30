package com.nathan.bigdata.hive.serde.rc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.RCFile;
import org.apache.hadoop.hive.serde2.columnar.BytesRefArrayWritable;
import org.apache.hadoop.hive.serde2.columnar.BytesRefWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class RCSimpleReader {
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        conf.set("hive.io.rcfile.column.number.conf", "5");
        FileSystem fs = FileSystem.get(conf);

        RCFile.Reader rcFileReader = new RCFile.Reader(fs, new Path("/tmp/rctext"), conf);
        int counter = 1;
        // Getting the Chunk of Row Groups...
        while (rcFileReader.next(new LongWritable(counter))) {
            System.out.println("READ ROW WISE - we are getting some data for ROW = " + counter);
            BytesRefArrayWritable dataRead = new BytesRefArrayWritable();
            rcFileReader.getCurrentRow(dataRead);

            // Iterate over all Rows fetched and iterate over each column
            System.out.println("Size of Data Read - " + dataRead.size());
            for (int i = 0; i < dataRead.size(); i++) {
                BytesRefWritable bytesRefread = dataRead.get(i);
                byte b1[] = bytesRefread.getData();
                Text returnData = new Text(b1);
                // This will PRINT the Data for the existing Row
                System.out.println("READ-DATA = " + returnData.toString());
            }
            System.out.println("Checking for next Iteration");

            counter++;
        }
    }
}
