package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Delay extends SubCommand {
    
    public Delay() {
        EmptyCommand emptyPermissionState = new EmptyCommand();
        emptyPermissionState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyPermissionState.setCommandDescription(textComponent("This command is used to set if the delay is managed by permissions or by TPort self, if ", ColorType.infoColor),
                textComponent("true", ColorType.varInfoColor),
                textComponent(" its managed by permissions.", ColorType.infoColor),
                textComponent("\nThe permission is: ", ColorType.infoColor),
                textComponent("TPort.delay.time.<time in minecraft ticks>", ColorType.varInfoColor));
        emptyPermissionState.setPermissions("TPort.delay.permission.set", "TPort.admin.delay");
        EmptyCommand emptyPermission = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyPermission.setCommandName("permission", ArgumentType.FIXED);
        emptyPermission.setCommandDescription(textComponent("This command is used to get if the delay is managed by permissions or by TPort self", ColorType.infoColor));
        emptyPermission.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyPermission.setRunnable(((args, player) -> {
            if (args.length == 2) {
                if (emptyPermission.hasPermissionToRun(player, true)) {
                    Files tportConfig = getFile("TPortConfig");
                    if (tportConfig.getConfig().getBoolean("delay.permission", false)) {
                        sendInfoTheme(player, "The delay time is defined by %s", "permissions");
                    } else {
                        sendInfoTheme(player, "The delay time is defined by %s", "command");
                    }
                }
            } else if (args.length == 3) {
                if (emptyPermissionState.hasPermissionToRun(player, true)) {
                    Files tportConfig = getFile("TPortConfig");
                    tportConfig.getConfig().set("delay.permission", Boolean.parseBoolean(args[2]));
                    tportConfig.saveConfig();
                    if (Boolean.parseBoolean(args[2])) {
                        sendSuccessTheme(player, "Successfully set delay time to %s", "permissions");
                    } else {
                        sendSuccessTheme(player, "Successfully set delay time to %s", "command");
                    }
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport delay permission [state]");
            }
        }));
        emptyPermission.addAction(emptyPermissionState);
        emptyPermission.setPermissions("TPort.delay.permission.get", "TPort.admin.delay");
        
        EmptyCommand emptySetPlayerDelay = new EmptyCommand();
        emptySetPlayerDelay.setCommandName("delay", ArgumentType.REQUIRED);
        emptySetPlayerDelay.setCommandDescription(textComponent("This command is used to set the delay of the given player, " +
                        "this will ony have an impact when the delay is managed by command, use ", ColorType.infoColor),
                textComponent("/tport delay permission false", ColorType.varInfoColor),
                textComponent(" to change this", ColorType.infoColor));
        emptySetPlayerDelay.setPermissions("TPort.delay.set", "TPort.admin.delay");
        EmptyCommand emptySetPlayer = new EmptyCommand();
        emptySetPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptySetPlayer.addAction(emptySetPlayerDelay);
        EmptyCommand emptySet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptySet.setCommandName("set", ArgumentType.FIXED);
        emptySet.setTabRunnable(((args, player) -> {
            if (!getFile("TPortConfig").getConfig().getBoolean("delay.permission", false)) return Main.getPlayerNames();
            else return Collections.emptyList();
        }));
        emptySet.setRunnable(((args, player) -> {
            if (args.length == 4) {
                if (emptySetPlayerDelay.hasPermissionToRun(player, true)) {
                    Files tportConfig = getFile("TPortConfig");
                    if (!tportConfig.getConfig().getBoolean("delay.permission", false)) {
                        try {
                            UUID newUUID = PlayerUUID.getPlayerUUID(args[2]);
                            if (newUUID == null || !getFile("TPortData").getConfig().contains("tport." + newUUID)) {
                                sendErrorTheme(player, "Could not find a player named %s", args[2]);
                                return;
                            }
                            int delay = Integer.parseInt(args[3]);
                            delay = Math.max(0, delay);
                            tportConfig.getConfig().set("delay.time." + newUUID.toString(), delay);
                            tportConfig.saveConfig();
                        
                            double seconds = delay / 20D;
                        
                            sendSuccessTheme(player, "Successfully set the delay time for player %s to %s ticks (%s second" + (seconds == 1 ? "" : "s") + ")", args[2], String.valueOf(delay), String.valueOf(seconds));
                        
                        } catch (NumberFormatException nfe) {
                            sendErrorTheme(player, "%s is not a valid number", args[3]);
                        }
                    }
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport delay set <player> <delay>");
            }
        }));
        emptySet.addAction(emptySetPlayer);
        
        EmptyCommand emptyGetPlayer = new EmptyCommand();
        emptyGetPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyGetPlayer.setCommandDescription(textComponent("This command is used to get the delay of the given player", ColorType.infoColor));
        emptyGetPlayer.setPermissions("TPort.delay.get.all", "TPort.admin.delay");
        EmptyCommand emptyGet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyGet.setCommandName("get", ArgumentType.FIXED);
        emptyGet.setCommandDescription(textComponent("This command is used to get your delay", ColorType.infoColor));
        emptyGet.setTabRunnable(((args, player) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())));
        emptyGet.setRunnable(((args, player) -> {
            if (args.length == 2) {
                if (emptyGet.hasPermissionToRun(player, true))
                    sendInfoTheme(player, "Your delay time is %s (in minecraft ticks)", String.valueOf(delayTime(player)));
            } else if (args.length == 3) {
                if (emptyGetPlayer.hasPermissionToRun(player, true)) {
                    Player newPlayer = Bukkit.getPlayer(args[2]);
                    if (newPlayer != null) {
                        sendInfoTheme(player, "Player %s has a delay of %s (in minecraft ticks)", newPlayer.getName(), String.valueOf(delayTime(newPlayer)));
                    } else {
                        sendErrorTheme(player, "Player %s was not found, player must be online");
                    }
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport delay get [player]");
            }
        }));
        emptyGet.addAction(emptyGetPlayer);
        emptyGet.setPermissions("TPort.delay.get.own");
        
        addAction(emptyPermission);
        addAction(emptySet);
        addAction(emptyGet);
    }
    
    public static int delayTime(Player player) {
        Files tportConfig = getFile("TPortConfig");
        if (tportConfig.getConfig().getBoolean("delay.permission", false)) {
            for (PermissionAttachmentInfo p : player.getEffectivePermissions()) {
                if (p.getPermission().toLowerCase().startsWith("tport.delay.time.")) {
                    try {
                        return Integer.parseInt(p.getPermission().toLowerCase().replace("tport.delay.time.", ""));
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
        } else {
            return tportConfig.getConfig().getInt("delay.time." + player.getUniqueId().toString(), 0);
        }
        return 0;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport delay permission [state]
        // tport delay set <player> <delay>
        // tport delay get [player]
        
        /*
         * delay time is defined with the permission: TPort.delay.time.<time in minecraft ticks>
         * */
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport delay <permission|set|get>");
    }
}
