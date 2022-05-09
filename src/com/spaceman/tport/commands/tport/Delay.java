package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Delay extends SubCommand {
    
    public Delay() {
        EmptyCommand emptyDelayPermissionState = new EmptyCommand();
        emptyDelayPermissionState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyDelayPermissionState.setCommandDescription(formatInfoTranslation("tport.command.delay.permission.state.commandDescription", "true", "TPort.delay.time.<time in minecraft ticks>"));
        emptyDelayPermissionState.setPermissions("TPort.delay.permission.set", "TPort.admin.delay");
        EmptyCommand emptyDelayPermission = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyDelayPermission.setCommandName("permission", ArgumentType.FIXED);
        emptyDelayPermission.setCommandDescription(formatInfoTranslation("tport.command.delay.permission.commandDescription"));
        emptyDelayPermission.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyDelayPermission.setRunnable(((args, player) -> {
            // tport delay permission [state]
            if (args.length == 2) {
                if (emptyDelayPermission.hasPermissionToRun(player, true)) {
                    Files tportConfig = getFile("TPortConfig");
                    Message stateMessage;
                    if (tportConfig.getConfig().getBoolean("delay.permission", false)) {
                        stateMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.delay.type.permissions");
                    } else {
                        stateMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.delay.type.command");
                    }
                    sendInfoTranslation(player, "tport.command.delay.permission", stateMessage);
                }
            } else if (args.length == 3) {
                if (emptyDelayPermissionState.hasPermissionToRun(player, true)) {
                    Files tportConfig = getFile("TPortConfig");
                    Message stateMessage;
                    Boolean state = Main.toBoolean(args[2]);
                    if (state == null) {
                        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport delay permission [true|false]");
                        return;
                    }
                    tportConfig.getConfig().set("delay.permission", state);
                    tportConfig.saveConfig();
                    if (state) {
                        stateMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.delay.type.permissions");
                    } else {
                        stateMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.delay.type.command");
                    }
                    sendSuccessTranslation(player, "tport.command.delay.permission.state", stateMessage);
                }
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport delay permission [state]");
            }
        }));
        emptyDelayPermission.addAction(emptyDelayPermissionState);
        emptyDelayPermission.setPermissions("TPort.delay.permission.get", "TPort.admin.delay");
        
        EmptyCommand emptyDelaySetPlayerDelay = new EmptyCommand();
        emptyDelaySetPlayerDelay.setCommandName("delay", ArgumentType.REQUIRED);
        emptyDelaySetPlayerDelay.setCommandDescription(formatInfoTranslation("tport.command.delay.set.player.delay.commandDescription", "/tport delay permission false"));
        emptyDelaySetPlayerDelay.setPermissions("TPort.delay.set", "TPort.admin.delay");
        EmptyCommand emptyDelaySetPlayer = new EmptyCommand();
        emptyDelaySetPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyDelaySetPlayer.addAction(emptyDelaySetPlayerDelay);
        EmptyCommand emptyDelaySet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyDelaySet.setCommandName("set", ArgumentType.FIXED);
        emptyDelaySet.setTabRunnable(((args, player) -> {
            if (!getFile("TPortConfig").getConfig().getBoolean("delay.permission", false)) return Main.getPlayerNames();
            else return Collections.emptyList();
        }));
        emptyDelaySet.setRunnable(((args, player) -> {
            // tport delay set <player> <delay>
            if (args.length == 4) {
                if (emptyDelaySetPlayerDelay.hasPermissionToRun(player, true)) {
                    Files tportConfig = getFile("TPortConfig");
                    if (!tportConfig.getConfig().getBoolean("delay.permission", false)) {
                        try {
                            UUID newUUID = PlayerUUID.getPlayerUUID(args[2]);
                            if (newUUID == null || !getFile("TPortData").getConfig().contains("tport." + newUUID)) {
                                sendErrorTranslation(player, "tport.command.playerNotFound", args[2]);
                                return;
                            }
                            int delay = Integer.parseInt(args[3]);
                            delay = Math.max(0, delay);
                            tportConfig.getConfig().set("delay.time." + newUUID, delay);
                            tportConfig.saveConfig();
                            
                            double seconds = delay / 20D;
                            Message secondMessage;
                            if (seconds == 1) secondMessage = formatSuccessTranslation("tport.command.second");
                            else secondMessage = formatSuccessTranslation("tport.command.seconds");
                            Message tickMessage;
                            if (delay == 1) tickMessage = formatSuccessTranslation("tport.command.minecraftTick");
                            else tickMessage = formatSuccessTranslation("tport.command.minecraftTicks");
                            
                            sendSuccessTranslation(player, "tport.command.delay.set.player.delay.succeeded", asPlayer(newUUID), delay, tickMessage, seconds, secondMessage);
                        } catch (NumberFormatException nfe) {
                            sendErrorTranslation(player, "tport.command.delay.set.player.delay.invalidTime", args[3]);
                        }
                    } else {
                        sendErrorTranslation(player, "tport.command.delay.set.player.delay.managedByPermissions",
                                formatTranslation(ColorType.varErrorColor, ColorType.varError2Color, "tport.command.delay.type.permissions"),
                                "/tport delay permission false");
                    }
                }
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport delay set <player> <delay>");
            }
        }));
        emptyDelaySet.addAction(emptyDelaySetPlayer);
        
        EmptyCommand emptyDelayGetPlayer = new EmptyCommand();
        emptyDelayGetPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyDelayGetPlayer.setCommandDescription(formatInfoTranslation("tport.command.delay.get.player.commandDescription", ColorType.infoColor));
        emptyDelayGetPlayer.setPermissions("TPort.delay.get.all", "TPort.admin.delay");
        EmptyCommand emptyDelayGet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyDelayGet.setCommandName("get", ArgumentType.FIXED);
        emptyDelayGet.setCommandDescription(formatInfoTranslation("tport.command.delay.get.commandDescription", ColorType.infoColor));
        emptyDelayGet.setTabRunnable(((args, player) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())));
        emptyDelayGet.setRunnable(((args, player) -> {
            // tport delay get [player]
            if (args.length == 2) {
                if (emptyDelayGet.hasPermissionToRun(player, true)) {
                    int delay = delayTime(player);
                    double seconds = delay / 20D;
                    Message secondMessage;
                    if (seconds == 1) secondMessage = formatSuccessTranslation("tport.command.second");
                    else secondMessage = formatSuccessTranslation("tport.command.seconds");
                    Message tickMessage;
                    if (delay == 1) tickMessage = formatSuccessTranslation("tport.command.minecraftTick");
                    else tickMessage = formatSuccessTranslation("tport.command.minecraftTicks");
                    
                    sendInfoTranslation(player, "tport.command.delay.get.player.succeeded", player, delay, tickMessage, seconds, secondMessage);
                }
            } else if (args.length == 3) {
                if (emptyDelayGetPlayer.hasPermissionToRun(player, true)) {
                    Player newPlayer = Bukkit.getPlayer(args[2]);
                    if (newPlayer != null) {
                        int delay = delayTime(player);
                        double seconds = delay / 20D;
                        Message secondMessage;
                        if (seconds == 1) secondMessage = formatSuccessTranslation("tport.command.second");
                        else secondMessage = formatSuccessTranslation("tport.command.seconds");
                        Message tickMessage;
                        if (delay == 1) tickMessage = formatSuccessTranslation("tport.command.minecraftTick");
                        else tickMessage = formatSuccessTranslation("tport.command.minecraftTicks");
                        
                        sendInfoTranslation(player, "tport.command.delay.get.player.succeeded", newPlayer, delay, tickMessage, seconds, secondMessage);
                    } else {
                        sendErrorTranslation(player, "tport.command.delay.get.playerNotOnline");
                    }
                }
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport delay get [player]");
            }
        }));
        emptyDelayGet.addAction(emptyDelayGetPlayer);
        emptyDelayGet.setPermissions("TPort.delay.get.own");
        
        addAction(emptyDelayPermission);
        addAction(emptyDelaySet);
        addAction(emptyDelayGet);
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
            return tportConfig.getConfig().getInt("delay.time." + player.getUniqueId(), 0);
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
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport delay <permission|set|get>");
    }
}
