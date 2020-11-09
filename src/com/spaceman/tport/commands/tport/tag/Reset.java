package com.spaceman.tport.commands.tport.tag;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Reset extends SubCommand {
    
    public Reset() {
        setPermissions("TPort.tag.reset", "TPort.admin.tag");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to remove all tags, and create the default ones", infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport tag reset
    
        if (!hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 2) {
            Tag.resetTags();
            sendSuccessTheme(player, "Successfully reset all the tags");
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport tag reset");
        }
    }
}
