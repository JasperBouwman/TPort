package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;

public class Description extends SubCommand {
    
    public Description() {
        EmptyCommand emptySetDescription = new EmptyCommand();
        emptySetDescription.setCommandName("description...", ArgumentType.REQUIRED);
        emptySetDescription.setCommandDescription(textComponent("This command is used to edit the description of the given TPort. With ", infoColor),
                textComponent("\\n", ColorTheme.ColorType.varInfoColor),
                textComponent(" you can add a new line. With the character ", infoColor),
                textComponent("&", ColorTheme.ColorType.varInfoColor),
                textComponent(" and a color code (0-9,a-f, example ", infoColor),
                textComponent("&2", varInfoColor),
                textComponent(") you can add colors to your description, ", infoColor),
                textComponent("another way to add colors is using the HEX notation: ", infoColor),
                textComponent("#123456", varInfoColor),
                textComponent(" or the RGB notation: ", infoColor),
                textComponent("$RRR$GGG$BBB", varInfoColor),
                textComponent(" the value can go from 0 to 255", infoColor));
        emptySetDescription.setPermissions("TPort.edit.description", "TPort.basic");
        EmptyCommand emptySet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "set";
            }
        };
        emptySet.setCommandName("set", ArgumentType.FIXED);
        emptySet.addAction(emptySetDescription);
        emptySet.setRunnable(((args, player) -> {
            if (!emptySetDescription.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            if (args.length < 5) {
                sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> description set <description...>");
                return;
            }
            if (tport.isOffered()) {
                sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                return;
            }
            
            tport.setDescription(StringUtils.join(args, " ", 4, args.length));
            tport.save();
            sendSuccessTheme(player, "Successfully set the description of TPort %s to: ", tport.getName());
            for (String s : tport.getDescription().split("\\\\n")) {
                sendSuccessTheme(player, "%s", ChatColor.BLUE + s);
            }
        }));
        
        EmptyCommand emptyRemove = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "remove";
            }
        };
        emptyRemove.setCommandName("remove", ArgumentType.FIXED);
        emptyRemove.setCommandDescription(textComponent("This command is used to remove the description of the given TPort", infoColor));
        emptyRemove.setRunnable(((args, player) -> {
            if (args.length == 4) {
                TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
                if (tport == null) {
                    sendErrorTheme(player, "No TPort found called %s", args[1]);
                    return;
                }
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
        }));
        
        EmptyCommand emptyGet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "get";
            }
        };
        emptyGet.setCommandName("get", ArgumentType.FIXED);
        emptyGet.setCommandDescription(textComponent("This command is used to get the description of the given TPort", infoColor));
        emptyGet.setRunnable(((args, player) -> {
            if (args.length == 4) {
                TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
                if (tport == null) {
                    sendErrorTheme(player, "No TPort found called %s", args[1]);
                    return;
                }
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
        }));
        addAction(emptySet);
        addAction(emptyRemove);
        addAction(emptyGet);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> description set <description...>
        // tport edit <TPort name> description remove
        // tport edit <TPort name> description get
        
        if (args.length > 3) {
            if (runCommands(getActions(), args[3], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> description <set|remove|get> [description...]");
    }
}
