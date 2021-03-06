package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tpEvents.TPEManager;
import com.spaceman.tport.tpEvents.TPRestriction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Restriction extends SubCommand {
    
    public Restriction() {
        EmptyCommand emptyPermissionState = new EmptyCommand();
        emptyPermissionState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyPermissionState.setCommandDescription(textComponent("This command is used to set if the tp restrictions are managed by permissions or by TPort self, if ", ColorType.infoColor),
                textComponent("true", ColorType.varInfoColor),
                textComponent(" its managed by permissions.", ColorType.infoColor),
                textComponent("\nThe permission is: ", ColorType.infoColor),
                textComponent("TPort.restriction.type.<restriction name>", ColorType.varInfoColor));
        emptyPermissionState.setPermissions("TPort.restriction.permission.set", "TPort.admin.restriction");
        EmptyCommand emptyPermission = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyPermission.setCommandName("permission", ArgumentType.FIXED);
        emptyPermission.setCommandDescription(textComponent("This command is used to get if the tp restrictions are managed by permissions or by TPort self", ColorType.infoColor));
        emptyPermission.addAction(emptyPermissionState);
        emptyPermission.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyPermission.setRunnable(((args, player) -> {
            if (args.length == 2) {
                if (emptyPermission.hasPermissionToRun(player, true)) {
                    Files tportConfig = getFile("TPortConfig");
                    if (tportConfig.getConfig().getBoolean("restriction.permission", false)) {
                        sendInfoTheme(player, "The tp restriction type is defined by %s", "permissions");
                    } else {
                        sendInfoTheme(player, "The tp restriction type is defined by %s", "command");
                    }
                }
            } else if (args.length == 3) {
                if (emptyPermissionState.hasPermissionToRun(player, true)) {
                    Files tportConfig = getFile("TPortConfig");
                    tportConfig.getConfig().set("restriction.permission", Boolean.parseBoolean(args[2]));
                    tportConfig.saveConfig();
                    if (Boolean.parseBoolean(args[2])) {
                        sendSuccessTheme(player, "Successfully set tp restriction type to %s", "permissions");
                    } else {
                        sendSuccessTheme(player, "Successfully set tp restriction type to %s", "command");
                    }
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport restriction permission [state]");
            }
        }));
        emptyPermission.setPermissions("TPort.restriction.permission.get", "TPort.admin.restriction");
        
        EmptyCommand emptySetPlayerType = new EmptyCommand();
        emptySetPlayerType.setCommandName("type", ArgumentType.REQUIRED);
        emptySetPlayerType.setCommandDescription(textComponent("This command is used to set the tp restriction type of the given player, " +
                        "this will ony have an impact when the tp restrictions are managed by command, use ", ColorType.infoColor),
                textComponent("/tport restriction permission [state]", ColorType.varInfoColor),
                textComponent(" to change this", ColorType.infoColor));
        emptySetPlayerType.setPermissions("TPort.restriction.set", "TPort.admin.restriction");
        EmptyCommand emptySetPlayer = new EmptyCommand();
        emptySetPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptySetPlayer.setTabRunnable(((args, player) -> TPRestriction.getRestrictions()));
        emptySetPlayer.addAction(emptySetPlayerType);
        EmptyCommand emptySet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptySet.setCommandName("set", ArgumentType.FIXED);
        emptySet.setTabRunnable(((args, player) -> {
            if (!getFile("TPortConfig").getConfig().getBoolean("restriction.permission", false)) return Main.getPlayerNames();
            else return Collections.emptyList();
        }));
        emptySet.setRunnable(((args, player) -> {
            if (args.length == 4) {
                if (emptySetPlayerType.hasPermissionToRun(player, true)) {
                    Files tportConfig = getFile("TPortConfig");
                    if (!tportConfig.getConfig().getBoolean("restriction.permission", false)) {
                        try {
                            UUID newUUID = PlayerUUID.getPlayerUUID(args[2]);
                            if (newUUID == null || !getFile("TPortData").getConfig().contains("tport." + newUUID)) {
                                sendErrorTheme(player, "Could not find a player named %s", args[2]);
                                return;
                            }
                        
                            if (TPEManager.hasTPRequest(newUUID)) {
                                sendErrorTheme(player, "Player %s has a tp request, can only edit tp restriction type when not requesting", args[2]);
                                return;
                            }
                        
                            TPRestriction type = TPRestriction.getNewRestriction(args[3]);
                            if (type == null) {
                                sendErrorTheme(player, "TP restriction type %s does not exist", args[3]);
                                return;
                            }
                            TPEManager.setTPRestriction(newUUID, type);
                        
                            Message message = new Message();
                            message.addText(textComponent("Successfully set the tp restriction type for player ", ColorType.successColor));
                            message.addText(textComponent(args[2], ColorType.varSuccessColor));
                            message.addText(textComponent(" to ", ColorType.successColor));
                            HoverEvent he = new HoverEvent();
                            he.addMessage(type.getDescription());
                            message.addText(textComponent(type.getRestrictionName(), ColorType.varSuccessColor, he));
                            message.sendMessage(player);
                        
                        } catch (NumberFormatException nfe) {
                            sendErrorTheme(player, "%s is not a valid tp restriction", args[3]);
                        }
                    } else {
                        sendErrorTheme(player, "Restrictions are permission based, can't edit type this way");
                    }
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport restriction set <player> <type>");
            }
        }));
        emptySet.addAction(emptySetPlayer);
    
        EmptyCommand emptyGetPlayer = new EmptyCommand();
        emptyGetPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyGetPlayer.setCommandDescription(textComponent("This command is used to get the tp restriction of the given player", ColorType.infoColor));
        emptyGetPlayer.setPermissions("TPort.restriction.get.all", "TPort.admin.restriction");
        EmptyCommand emptyGet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyGet.setCommandName("get", ArgumentType.FIXED);
        emptyGet.setCommandDescription(textComponent("This command is used to get your tp restriction", ColorType.infoColor));
        emptyGet.setTabRunnable(((args, player) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())));
        emptyGet.setRunnable(((args, player) -> {
            if (args.length == 2) {
                if (emptyGet.hasPermissionToRun(player, true))
                    sendInfoTheme(player, "You have the tp restriction of %s", TPEManager.getTPRestriction(player.getUniqueId()).getRestrictionName());
            } else if (args.length == 3) {
                if (emptyGetPlayer.hasPermissionToRun(player, true)) {
                    Player newPlayer = Bukkit.getPlayer(args[2]);
                    if (newPlayer != null) {
                        sendInfoTheme(player, "Player %s has the tp restriction of %s", newPlayer.getName(), TPEManager.getTPRestriction(newPlayer.getUniqueId()).getRestrictionName());
                    } else {
                        sendErrorTheme(player, "Player %s was not found, player must be online");
                    }
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport restriction get [player]");
            }
        }));
        emptyGet.addAction(emptyGetPlayer);
        emptyGet.setPermissions("TPort.restriction.get.own");
        
        addAction(emptyPermission);
        addAction(emptySet);
        addAction(emptyGet);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport restriction permission [state]
        // tport restriction set <player> <type>
        // tport restriction get [player]
        
        /*
         * movement restriction type is defined with the permission: TPort.restriction.type.<restriction name>
         * */
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport restriction <permission|set|get>");
    }
}
