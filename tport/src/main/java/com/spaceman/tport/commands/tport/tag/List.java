package com.spaceman.tport.commands.tport.tag;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.spaceman.tport.commands.tport.Tag.tagPermPrefix;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class List extends SubCommand {
    
    public List() {
        setCommandDescription(formatInfoTranslation("tport.command.tag.list.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport tag list
        
        if (args.length == 2) {
            ArrayList<String> tags = Tag.getTags();
            int size = tags.size();
            
            if (size == 0) {
                sendInfoTranslation(player, "tport.command.tag.list.empty", "/tport tag reset", "/tport tag create <tag>");
                return;
            }
            
            Message tagsMessage = new Message();
            Message delimiter = formatInfoTranslation("tport.command.tag.list.delimiter");
            boolean color = true;
            
            for (int i = 0; i < size; i++) {
                String tag = tags.get(i);
                
                TextComponent tagMessage;
                if (color) tagMessage = textComponent(tag, varInfoColor);
                else       tagMessage = textComponent(tag, varInfo2Color);
                if (Features.Feature.Permissions.isEnabled())
                    tagMessage.addTextEvent(new HoverEvent(formatInfoTranslation("tport.command.tag.list.hoverText", tagPermPrefix + tag)));
                
                color = !color;
                tagsMessage.addText(tagMessage);
                
                if (i + 2 == size) tagsMessage.addMessage(formatInfoTranslation("tport.command.tag.list.lastDelimiter"));
                else               tagsMessage.addMessage(delimiter);
            }
            tagsMessage.removeLast();
            
            sendInfoTranslation(player, "tport.command.tag.list.succeeded", tagsMessage);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport tag list");
        }
    }
}
