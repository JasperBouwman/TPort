package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.spaceman.tport.commandHandler.ArgumentType.FIXED;
import static com.spaceman.tport.commandHandler.ArgumentType.OPTIONAL;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class MainLayout extends SubCommand {
    
    public MainLayout() {
        EmptyCommand emptyPlayersState = new EmptyCommand();
        emptyPlayersState.setCommandName("state", OPTIONAL);
        emptyPlayersState.setCommandDescription(formatInfoTranslation("tport.command.mainLayout.players.state.commandDescription"));
        emptyPlayersState.setPermissions("TPort.mainLayout.players");
        EmptyCommand emptyPlayers = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyPlayers.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyPlayers.setCommandName("players", FIXED);
        emptyPlayers.setCommandDescription(formatInfoTranslation("tport.command.mainLayout.players.commandDescription"));
        emptyPlayers.setRunnable(((args, player) -> {
            if (args.length == 2) {
                sendInfoTranslation(player, "tport.command.mainLayout.players.succeeded",
                        formatTranslation(varInfoColor, varInfo2Color, "tport.command.mainLayout." + (showPlayers(player) ? "show" : "hide")));
            } else if (args.length == 3) {
                if (emptyPlayersState.hasPermissionToRun(player, true)) {
                    Boolean state = Main.toBoolean(args[2]);
                    if (state == null) {
                        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport mainLayout players [true|false]");
                        return;
                    }
                    showPlayers(player, state);
                    sendSuccessTranslation(player, "tport.command.mainLayout.players.state.succeeded",
                            formatTranslation(varInfoColor, varInfo2Color, "tport.command.mainLayout." + (state ? "show" : "hide")));
                }
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport mainLayout players [state]");
            }
        }));
        emptyPlayers.addAction(emptyPlayersState);
        
        EmptyCommand emptyTPortsState = new EmptyCommand();
        emptyTPortsState.setCommandName("state", OPTIONAL);
        emptyTPortsState.setCommandDescription(formatInfoTranslation("tport.command.mainLayout.tports.state.commandDescription"));
        emptyTPortsState.setPermissions("TPort.mainLayout.TPorts");
        EmptyCommand emptyTPorts = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyTPorts.setCommandName("TPorts", FIXED);
        emptyTPorts.setCommandDescription(formatInfoTranslation("tport.command.mainLayout.tports.commandDescription"));
        emptyTPorts.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyTPorts.setRunnable(((args, player) -> {
            if (args.length == 2) {
                sendInfoTranslation(player, "tport.command.mainLayout.tports.succeeded",
                        formatTranslation(varInfoColor, varInfo2Color, "tport.command.mainLayout." + (showTPorts(player) ? "show" : "hide")));
            } else if (args.length == 3) {
                if (emptyTPortsState.hasPermissionToRun(player, true)) {
                    Boolean state = Main.toBoolean(args[2]);
                    if (state == null) {
                        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport mainLayout TPorts [true|false]");
                        return;
                    }
                    showTPorts(player, state);
                    sendSuccessTranslation(player, "tport.command.mainLayout.tports.state.succeeded",
                            formatTranslation(varInfoColor, varInfo2Color, "tport.command.mainLayout." + (state ? "show" : "hide")));
                }
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport mainLayout TPorts [state]");
            }
        }));
        emptyTPorts.addAction(emptyTPortsState);
        
        addAction(emptyPlayers);
        addAction(emptyTPorts);
    }
    
    public static boolean showPlayers(Player player) {
        return getFile("TPortData").getConfig().getBoolean("tport." + player.getUniqueId() + ".mainLayout.players", true);
    }
    
    public static boolean showTPorts(Player player) {
        return getFile("TPortData").getConfig().getBoolean("tport." + player.getUniqueId() + ".mainLayout.tports", false);
    }
    
    public static void showPlayers(Player player, boolean state) {
        Files tportData = getFile("TPortData");
        tportData.getConfig().set("tport." + player.getUniqueId() + ".mainLayout.players", state);
        tportData.saveConfig();
    }
    
    public static void showTPorts(Player player, boolean state) {
        Files tportData = getFile("TPortData");
        tportData.getConfig().set("tport." + player.getUniqueId() + ".mainLayout.tports", state);
        tportData.saveConfig();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport mainLayout players [state]
        // tport mainLayout TPorts [state]
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport mainLayout " + CommandTemplate.convertToArgs(getActions(), false));
    }
}
