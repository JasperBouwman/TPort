package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class LogSize extends SubCommand {
    
    private final EmptyCommand emptySize;
    
    public LogSize() {
        emptySize = new EmptyCommand();
        emptySize.setCommandName("size", ArgumentType.OPTIONAL);
        emptySize.setCommandDescription(formatInfoTranslation("tport.command.log.logSize.size.commandDescription"));
        emptySize.setPermissions("TPort.log.logSize", "TPort.admin.log");
        addAction(emptySize);
    }
    
    public static int getLogSize() {
        return getFile("TPortConfig").getConfig().getInt("logbook.size", 50);
    }
    
    private static void setLogSize(int size) {
        Files tportConfig = getFile("TPortConfig");
        tportConfig.getConfig().set("logbook.size", size);
        tportConfig.saveConfig();
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.log.logSize.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log logSize [size]
    
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.log.logSize.succeeded", getLogSize());
        } else if (args.length == 3) {
            if (emptySize.hasPermissionToRun(player, true)) {
                try {
                    int logSize = Integer.parseInt(args[2]);
                    setLogSize(logSize);
                    sendSuccessTranslation(player, "tport.command.log.logSize.size.succeeded", logSize);
                } catch (NumberFormatException nfe) {
                    sendErrorTranslation(player, "tport.command.log.logSize.size.notValidNumber", args[2]);
                }
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log logSize [size]");
        }
    }
}
