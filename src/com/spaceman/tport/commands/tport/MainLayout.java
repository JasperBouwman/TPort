package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.CommandTemplate;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.spaceman.tport.commandHander.ArgumentType.FIXED;
import static com.spaceman.tport.commandHander.ArgumentType.OPTIONAL;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class MainLayout extends SubCommand {
    
    public MainLayout() {
        EmptyCommand emptyPlayersState = new EmptyCommand();
        emptyPlayersState.setCommandName("state", OPTIONAL);
        emptyPlayersState.setCommandDescription(textComponent("This command is used to set if Players are hidden in your Main TPort GUI", infoColor));
        emptyPlayersState.setPermissions("TPort.mainLayout.players");
        EmptyCommand emptyPlayers = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyPlayers.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyPlayers.setCommandName("players", FIXED);
        emptyPlayers.setCommandDescription(textComponent("This command is used to get if Players are hidden in your Main TPort GUI", infoColor));
        emptyPlayers.setRunnable(((args, player) -> {
            if (args.length == 2) {
                sendInfoTheme(player, "Players are %s for your Main TPort GUI layout", (showPlayers(player) ? "shown" : "hidden"));
            } else if (args.length == 3) {
                if (emptyPlayersState.hasPermissionToRun(player, true)) {
                    if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                        boolean state = Boolean.parseBoolean(args[2]);
                        showPlayers(player, state);
                        sendSuccessTheme(player, "Successfully %s players in your Main TPort GUI layout", (state ? "shown" : "hidden"));
                    } else {
                        sendErrorTheme(player, "Usage: %s", "/tport mainLayout players [true|false]");
                    }
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport mainLayout players [state]");
            }
        }));
        emptyPlayers.addAction(emptyPlayersState);
    
        EmptyCommand emptyTPortsState = new EmptyCommand();
        emptyTPortsState.setCommandName("state", OPTIONAL);
        emptyTPortsState.setCommandDescription(textComponent("This command is used to set if TPorts are hidden in your Main TPort GUI", infoColor));
        emptyTPortsState.setPermissions("TPort.mainLayout.TPorts");
        EmptyCommand emptyTPorts = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyTPorts.setCommandName("TPorts", FIXED);
        emptyTPorts.setCommandDescription(textComponent("This command is used to get if TPorts are hidden in your Main TPort GUI", infoColor));
        emptyTPorts.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyTPorts.setRunnable(((args, player) -> {
            if (args.length == 2) {
                sendInfoTheme(player, "TPorts are %s for your Main TPort GUI layout", (showTPorts(player) ? "shown" : "hidden"));
            } else if (args.length == 3) {
                if (emptyTPortsState.hasPermissionToRun(player, true)) {
                    if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                        boolean state = Boolean.parseBoolean(args[2]);
                        showTPorts(player, state);
                        sendSuccessTheme(player, "Successfully %s TPorts in your Main TPort GUI layout", (state ? "shown" : "hidden"));
                    } else {
                        sendErrorTheme(player, "Usage: %s", "/tport mainLayout TPorts [true|false]");
                    }
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport mainLayout TPorts [state]");
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
        sendErrorTheme(player, "Usage: %s", "/tport mainLayout " + CommandTemplate.convertToArgs(getActions(), false));
    }
}
