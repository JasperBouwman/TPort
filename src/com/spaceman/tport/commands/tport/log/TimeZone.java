package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportData;

public class TimeZone extends SubCommand {
    
    public TimeZone() {
        EmptyCommand emptyTimeZone = new EmptyCommand();
        emptyTimeZone.setCommandName("TimeZone", ArgumentType.OPTIONAL);
        emptyTimeZone.setCommandDescription(formatInfoTranslation("tport.command.log.timeZone.timeZone.commandDescription"));
        addAction(emptyTimeZone);
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.log.timeZone.commandDescription");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList(java.util.TimeZone.getAvailableIDs());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log TimeZone [TimeZone]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.log.timeZone.succeeded",
                    java.util.TimeZone.getTimeZone(tportData.getConfig().getString(
                            "tport." + player.getUniqueId() + ".timeZone",
                            java.util.TimeZone.getDefault().getID())
                    ).getDisplayName());
        } else if (args.length == 3) {
            if (Arrays.asList(java.util.TimeZone.getAvailableIDs()).contains(args[2])) {
                java.util.TimeZone zone = java.util.TimeZone.getTimeZone(args[2]);
                tportData.getConfig().set("tport." + player.getUniqueId() + ".timeZone", zone.getID());
                tportData.saveConfig();
                sendSuccessTranslation(player, "tport.command.log.timeZone.timeZone.succeeded", zone.getDisplayName());
            } else {
                sendErrorTranslation(player, "tport.command.log.timeZone.timeZone.timeZoneNotExist", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log TimeZone [TimeZone]");
        }
    }
}
