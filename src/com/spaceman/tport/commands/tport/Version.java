package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class Version extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the TPort plugin version (version: ", infoColor),
                textComponent(Main.getInstance().getDescription().getVersion(), ColorTheme.ColorType.varInfoColor),
                textComponent(")", infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        Message message = new Message();
        
        message.addText(textComponent("This server is running TPort version: ", infoColor));
        message.addText(textComponent(Main.getInstance().getDescription().getVersion(), varInfoColor));
        message.addText(textComponent("\nThis version is compatible with Bukkit versions: ", infoColor));
        for (String version : Arrays.asList("1.16.2", "1.16.3", "1.16.4")) {
            message.addText(textComponent(version, varInfoColor));
            message.addText(textComponent(", ", infoColor));
        }
        message.removeLast();
        message.addText(textComponent("\nWebsite: ", infoColor));
        message.addText(textComponent(Main.getInstance().getDescription().getWebsite(), varInfoColor, ClickEvent.openUrl(Main.getInstance().getDescription().getWebsite())));
        
        message.sendMessage(player);
    }
}
