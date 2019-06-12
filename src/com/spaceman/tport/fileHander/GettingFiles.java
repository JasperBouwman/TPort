package com.spaceman.tport.fileHander;

import com.spaceman.tport.Main;

import java.util.Collection;
import java.util.HashMap;

public class GettingFiles {

    private static HashMap<String, Files> list = new HashMap<>();

    public static void loadFiles() {

        list = new HashMap<>();

        list.put("TPortData", new Files(Main.getInstance(), "TPortData.yml"));
        list.put("TPortConfig", new Files(Main.getInstance(), "TPortConfig.yml"));
    }

    public static Files getFile(String file) {
        return list.getOrDefault(file.replace(".yml", ""), null);
    }

    public static Collection<Files> getFiles() {
        return list.values();
    }
}
