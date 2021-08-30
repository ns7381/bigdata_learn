package com.nathan.bigdata.mr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Compute {
    public static void main(String[] args) {

    }


    public File[] map(File file) throws IOException {
        BufferedWriter[] writers = new BufferedWriter[2000];
        File[] mapOutFiles = new File[2000];
        for (int i = 0; i < 2000; i++) {
            File mapOutFile = new File("map.out." + i);
            mapOutFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(mapOutFile));
            writers[i] = writer;
            mapOutFiles[i] = mapOutFile;
        }


        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            for (String word : line.split(" ")) {
                writers[word.hashCode() % 2000].write("line" + " 1\n");
            }
        }

        return null;
    }

}
