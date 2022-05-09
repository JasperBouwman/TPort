package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Description extends SubCommand {
    
    public Description() {
        EmptyCommand emptySetDescription = new EmptyCommand();
        emptySetDescription.setCommandName("description...", ArgumentType.REQUIRED);
        emptySetDescription.setCommandDescription(formatInfoTranslation("tport.command.edit.description.set.commandDescription",
                "\\n", "&", "0-9,a-f,k-o,r", "#123456", "$RRR$GGG$BBB"));
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
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (args.length < 5) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> description set <description...>");
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.description.set.isOffered",
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
            
            tport.setDescription(ChatColor.BLUE + StringUtils.join(args, " ", 4, args.length));
            tport.save();
            sendSuccessTranslation(player, "tport.command.edit.description.set.succeeded", tport, tport.getDescription());
        }));
        
        EmptyCommand emptyRemove = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "remove";
            }
        };
        emptyRemove.setCommandName("remove", ArgumentType.FIXED);
        emptyRemove.setCommandDescription(formatInfoTranslation("tport.command.edit.description.remove.commandDescription"));
        emptyRemove.setRunnable(((args, player) -> {
            if (args.length == 4) {
                TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
                if (tport == null) {
                    sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                    return;
                }
                if (tport.isOffered()) {
                    sendErrorTranslation(player, "tport.command.edit.description.remove.isOffered",
                            tport, asPlayer(tport.getOfferedTo()));
                    return;
                }
                tport.setDescription(null);
                tport.save();
                sendSuccessTranslation(player, "tport.command.edit.description.remove.succeeded", tport);
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <Tport name> description remove");
            }
        }));
        
        EmptyCommand emptyGet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "get";
            }
        };
        emptyGet.setCommandName("get", ArgumentType.FIXED);
        emptyGet.setCommandDescription(formatInfoTranslation("tport.command.edit.description.get.commandDescription"));
        emptyGet.setRunnable(((args, player) -> {
            if (args.length == 4) {
                TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
                if (tport == null) {
                    sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                    return;
                }
                if (tport.hasDescription()) {
                    sendInfoTranslation(player, "tport.command.edit.description.get.succeeded", tport, tport.getDescription());
                } else {
                    sendInfoTranslation(player, "tport.command.edit.description.get.noDescription", tport);
                }
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <Tport name> description get");
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
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> description <set|remove|get> [description...]");
    }
}
