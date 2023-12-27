package org.davideh.goldeconomy;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Setup {


    static public void CopyConfigYML(Plugin plugin, File configFile) {
        byte[] buffer = new byte[1024];
        int length;

        InputStream inputStream = plugin.getResource("config.yml");
        if (inputStream != null) {
            try {
                OutputStream outputStream = new FileOutputStream(configFile);
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                System.out.print(e);
            }
        }

    }

    static public void CopyMessagesYML(Plugin plugin, File configFile) {
        byte[] buffer = new byte[1024];
        int length;

        InputStream inputStream = plugin.getResource("messages.yml");
        if (inputStream != null) {
            try {
                OutputStream outputStream = new FileOutputStream(configFile);
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                System.out.print(e);
            }
        }

    }





}
