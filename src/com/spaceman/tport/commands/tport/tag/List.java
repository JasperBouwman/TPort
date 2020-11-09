package com.spaceman.tport.commands.tport.tag;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class List extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to list all the available tags", infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport tag list
        
        if (args.length == 2) {
            Message message = new Message();
            message.addText(textComponent("Available tags: ", infoColor));
            
            message.addText(textComponent(""));
            for (String tag : Tag.getTags()) {
                message.addText(textComponent(tag, varInfoColor,
                        new HoverEvent(textComponent("Permission: ", infoColor), textComponent("TPort.tags.type." + tag, varInfoColor))));
                message.addText(textComponent(", ", infoColor));
            }
            message.removeLast();
            
            message.sendMessage(player);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport tag list");
        }
    }
}
