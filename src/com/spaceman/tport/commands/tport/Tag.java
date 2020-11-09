package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.tag.Create;
import com.spaceman.tport.commands.tport.tag.Delete;
import com.spaceman.tport.commands.tport.tag.List;
import com.spaceman.tport.commands.tport.tag.Reset;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.tport.commandHander.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Tag extends SubCommand {
    
    private static java.util.List<String> tags = new ArrayList<>();
    
    public Tag() {
        addAction(new Create());
        addAction(new Delete());
        addAction(new List());
        addAction(new Reset());
    }
    
    public static void loadTags() {
        Files tportConfig = getFile("TPortConfig");
        tags = tportConfig.getConfig().getStringList("tags.list");
    }
    
    public static void saveTags() {
        Files tportConfig = getFile("TPortConfig");
        tportConfig.getConfig().set("tags.list", tags);
        tportConfig.saveConfig();
    }
    
    public static boolean createTag(String tag) {
        if (tags.stream().noneMatch(s -> s.equalsIgnoreCase(tag))) {
            tags.add(tag);
            return true;
        }
        return false;
    }
    
    public static boolean deleteTag(String tag) {
        for (String t : tags) {
            if (t.equalsIgnoreCase(tag)) {
                tags.remove(t);
                for (String uuid : GettingFiles.getFile("TPortData").getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        tport.removeTag(t);
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public static void resetTags() {
        tags = new ArrayList<>();
        createTag("Home");
        createTag("Spawner");
        createTag("Farm");
        createTag("Base");
        createTag("Mine");
        createTag("Temp");
        
        for (String uuid : GettingFiles.getFile("TPortData").getKeys("tport")) {
            for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                for (String tag : tport.getTags()) {
                    tport.removeTag(tag);
                }
            }
        }
    }
    
    public static ArrayList<String> getTags() {
        return new ArrayList<>(tags);
    }
    
    public static String getTag(String tag) {
        return tags.stream().filter(s -> s.equalsIgnoreCase(tag)).findFirst().orElse(null);
    }
    
    public static boolean exist(String tag) {
        return tags.stream().anyMatch(s -> s.equalsIgnoreCase(tag));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport tag create <tag> <permission>
        // tport tag delete <tag>
        // tport tag list
        // tport tag reset
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport tag " + convertToArgs(getActions(), false));
        
    }
}
