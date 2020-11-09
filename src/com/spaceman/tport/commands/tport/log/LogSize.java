package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class LogSize extends SubCommand {
    
    private final EmptyCommand emptySize;
    
    public LogSize() {
        emptySize = new EmptyCommand();
        emptySize.setCommandName("size", ArgumentType.OPTIONAL);
        emptySize.setCommandDescription(textComponent("This command is used to set the size of a TPort log", ColorType.infoColor));
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
        return new Message(textComponent("This command is used to get the size of a TPort log", ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log logSize [size]
    
        if (args.length == 2) {
            sendInfoTheme(player, "The size of the log entries is set to %s", String.valueOf(getLogSize()));
        } else if (args.length == 3) {
            if (emptySize.hasPermissionToRun(player, true)) {
                try {
                    setLogSize(Integer.parseInt(args[2]));
                    sendSuccessTheme(player, "Successfully set the log entry size to %s", args[2]);
                } catch (NumberFormatException nfe) {
                    sendErrorTheme(player, "%s is not a valid number", args[2]);
                }
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport log logSize [size]");
        }
    }
}
