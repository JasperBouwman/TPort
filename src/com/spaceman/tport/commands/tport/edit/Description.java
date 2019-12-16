package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Description extends SubCommand {
    
    public Description() {
        EmptyCommand emptySetDescription = new EmptyCommand();
        emptySetDescription.setCommandName("description...", ArgumentType.REQUIRED);
        emptySetDescription.setCommandDescription(textComponent("This command is used to edit the description of the given TPort. With ", infoColor),
                textComponent("\\\\n", ColorTheme.ColorType.varInfoColor),
                textComponent(" you can add a new line. With the character ", infoColor),
                textComponent("&", ColorTheme.ColorType.varInfoColor),
                textComponent(" and a color code you can add colors to your description", infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.edit.description", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        EmptyCommand emptySet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "set";
            }
        };
        emptySet.setCommandName("set", ArgumentType.FIXED);
        emptySet.addAction(emptySetDescription);
        
        EmptyCommand emptyRemove = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "remove";
            }
        };
        emptyRemove.setCommandName("remove", ArgumentType.FIXED);
        emptyRemove.setCommandDescription(textComponent("This command is used to remove the description of the given TPort", infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.edit.description", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        
        EmptyCommand emptyGet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "get";
            }
        };
        emptyGet.setCommandName("get", ArgumentType.FIXED);
        emptyGet.setCommandDescription(textComponent("This command is used to get the description of the given TPort", infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.edit.description", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        addAction(emptySet);
        addAction(emptyRemove);
        addAction(emptyGet);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> description set <description...>
        // tport edit <TPort name> description remove
        // tport edit <TPort name> description get
    
        if (!hasPermission(player, true, true, "TPort.edit.description", "TPort.basic")) {
            return;
        }
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        if (tport == null) {
            sendErrorTheme(player, "No TPort found called %s", args[1]);
            return;
        }
    
        if (args.length == 3) {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> description <set|remove|get> [description...]");
            return;
        }
        if (args[3].equalsIgnoreCase("set")) {
            if (args.length < 5) {
                sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> description set <description...>");
                return;
            }
            if (tport.isOffered()) {
                sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                return;
            }
        
            tport.setDescription(String.join(" ", Arrays.asList(args).subList(4, args.length)));
            tport.save();
            sendSuccessTheme(player, "Successfully set the description of TPort %s to: ", tport.getName());
            for (String s : tport.getDescription().split("\\\\n")) {
                sendSuccessTheme(player, "%s", ChatColor.BLUE + s);
            }
        } else if (args[3].equalsIgnoreCase("remove")) {
            if (args.length == 4) {
                if (tport.isOffered()) {
                    sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                    return;
                }
                tport.setDescription(null);
                tport.save();
                sendSuccessTheme(player, "Successfully removed the description of TPort %s", tport.getName());
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport edit <Tport name> description remove");
            }
        } else if (args[3].equalsIgnoreCase("get")) {
            if (args.length == 4) {
                if (tport.hasDescription()) {
                    sendInfoTheme(player, "The description of TPort %s is:", tport.getName());
                    for (String s : tport.getDescription().split("\\\\n")) {
                        sendInfoTheme(player, "%s", ChatColor.BLUE + s);
                    }
                } else {
                    sendInfoTheme(player, "TPort %s does not have a description", tport.getName());
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport edit <Tport name> description get");
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> description <set|remove> [description...]");
        }
    }
}
