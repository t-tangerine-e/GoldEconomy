package org.JavaWolf.goldeconomy.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Utils {
    public static String GetMessage(String filePath, String key) {
        try {

            InputStream input = new FileInputStream(filePath);

            Yaml yaml = new Yaml();

            Map<String, Object> yamlData = yaml.load(input);

            Object value = yamlData.get(key);

            if (value != null) {
                return value.toString();
            } else {
                return "";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "File not found";
        }
    }

    public static void SaveLog(File file, String log) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(log);
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static double roundDouble(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(value));
    }



}
