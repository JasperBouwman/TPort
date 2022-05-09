package com.spaceman.tport.commands.tport.tag;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Reset extends SubCommand {
    
    public Reset() {
        setPermissions("TPort.tag.reset", "TPort.admin.tag");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.tag.reset.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport tag reset
    
        if (!hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 2) {
            Tag.resetTags();
            sendSuccessTranslation(player, "tport.command.tag.reset.succeeded");
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport tag reset");
        }
    }
}
