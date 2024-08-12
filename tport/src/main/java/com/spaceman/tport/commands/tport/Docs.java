package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Docs extends SubCommand {
    
    // String:              markdown file name
    // ArrayList<String>:   markdown chapter names
    // ArrayList<Message>:  markdown chapters
    private final HashMap<String, Pair<ArrayList<String>, ArrayList<Message>>> docs = new HashMap<>();
    
    public Docs(CommandTemplate mainTemplate) {
        loadMD("quickStart.md", "/docs/quickStart.md", mainTemplate);
        loadMD("readme.md", "/docs/readme.md", mainTemplate);
        loadMD("changelog.md", "/docs/changelog.md", mainTemplate);
    }
    
    private void loadMD(String name, String mdFile, CommandTemplate mainTemplate) {
        try (InputStream md = Main.class.getResourceAsStream(mdFile)) {
            if (md != null) {
                String mdText = IOUtils.toString(md, StandardCharsets.UTF_8);
                docs.put(name, MessageUtils.fromSplitMarkdown(mdText, mainTemplate));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return docs.keySet();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport docs <markdown file> [chapter]
        
        if (args.length == 1) {
            // todo open docs GUI
        } else if (args.length == 2) {
            
            Pair<ArrayList<String>, ArrayList<Message>> chapters = docs.getOrDefault(args[1], null);
            if (chapters == null) {
                player.sendMessage("no chapters found");
            } else {
                //todo
                // list chapters (table of content)
                //  or
                // start at first chapter, and add navigation
//                chapters.forEach(message -> message.sendMessage(player));
                chapters.getLeft().forEach(player::sendMessage);
            }
            
        } else {
            Pair<ArrayList<String>, ArrayList<Message>> chapters = docs.getOrDefault(args[1], null);
            if (chapters == null) {
                player.sendMessage("no chapters found");
            } else {
                String chapterInput = StringUtils.join(args, " ", 2, args.length);
                
                ArrayList<String> left = chapters.getLeft();
                for (int i = 0; i < left.size(); i++) {
                    String chapterName = left.get(i);
                    if (chapterName.equalsIgnoreCase(chapterInput)) {
                    
                    }
                }
                
            }
        }
        
    }
}
