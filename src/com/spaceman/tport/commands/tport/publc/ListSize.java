package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class ListSize extends SubCommand {
    
    public ListSize() {
        EmptyCommand emptySize = new EmptyCommand();
        emptySize.setCommandName("size", ArgumentType.OPTIONAL);
        emptySize.setCommandDescription(textComponent("This command is used to set the maximum public TPorts", ColorType.infoColor),
                textComponent("\n\nPermissions: ", infoColor), textComponent("TPort.public.listSize", varInfoColor),
                textComponent(" or ", infoColor), textComponent("TPort.admin.public", varInfoColor));
        addAction(emptySize);
    }
    
    public static int getPublicTPortSize() {
        return getFile("TPortConfig").getConfig().getInt("public.size", 70);
    }
    
    private static void setPublicTPortSize(int size) {
        Files tportConfig = getFile("TPortConfig");
        tportConfig.getConfig().set("public.size", size);
        tportConfig.saveConfig();
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the maximum public TPorts", ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public listSize [size]
    
        if (args.length == 2) {
            sendInfoTheme(player, "The amount of maximum public TPorts are %s", String.valueOf(getPublicTPortSize()));
        } else if (args.length == 3) {
            if (hasPermission(player, true, true, "TPort.public.listSize", "TPort.admin.public")) {
                try {
                    setPublicTPortSize(Integer.parseInt(args[2]));
                    sendSuccessTheme(player, "Successfully set the maximum public TPorts to %s", args[2]);
                } catch (NumberFormatException nfe) {
                    sendErrorTheme(player, "%s is not a valid number", args[2]);
                }
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport public listSize [size]");
        }
    }
}
