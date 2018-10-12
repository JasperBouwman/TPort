package com.spaceman.tport.fileHander;

import com.spaceman.tport.Main;

import java.util.Collection;
import java.util.HashMap;

public class GettingFiles {

    private static HashMap<String, Files> list;
    public static Main main;

    public GettingFiles(Main main) {
        GettingFiles.main = main;

        list = new HashMap<>();

        list.put("TPortData", new Files(main, "TPortData.yml"));
        list.put("TPortConfig", new Files(main, "TPortConfig.yml"));
    }

    public static Files getFiles(String file) {
        return list.getOrDefault(file.replace(".yml", ""), null);
    }

    public static Collection<Files> getFiles() {
        return list.values();
    }
}
