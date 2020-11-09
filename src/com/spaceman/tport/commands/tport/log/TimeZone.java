package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class TimeZone extends SubCommand {
    
    public TimeZone() {
        EmptyCommand emptyTimeZone = new EmptyCommand();
        emptyTimeZone.setCommandName("TimeZone", ArgumentType.OPTIONAL);
        emptyTimeZone.setCommandDescription(textComponent("This command is used to set to your time zone, the time zone is used for the read command", ColorType.infoColor));
        addAction(emptyTimeZone);
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the set time zone, the time zone is used for the read command", ColorType.infoColor));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList(java.util.TimeZone.getAvailableIDs());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log TimeZone [TimeZone]
        
        if (args.length == 2) {
            sendInfoTheme(player, "Your time zone: %s",
                    java.util.TimeZone.getTimeZone(getFile("TPortData").getConfig().getString(
                            "tport." + player.getUniqueId() + ".timeZone",
                            java.util.TimeZone.getDefault().getID())
                    ).getDisplayName());
        } else if (args.length == 3) {
            if (Arrays.asList(java.util.TimeZone.getAvailableIDs()).contains(args[2])) {
                java.util.TimeZone zone = java.util.TimeZone.getTimeZone(args[2]);
                Files tportData = getFile("TPortData");
                tportData.getConfig().set("tport." + player.getUniqueId() + ".timeZone", zone.getID());
                tportData.saveConfig();
                sendSuccessTheme(player, "Successfully set your time zone to %s", zone.getDisplayName());
            } else {
                sendErrorTheme(player, "Time zone %s does not exist", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport log TimeZone [TimeZone]");
        }
    }
}
