package com.spaceman.tport.fancyMessage.colorTheme;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.commandHander.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;

public class ColorThemeCommand extends SubCommand {
    
    public ColorThemeCommand() {
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("");
    
        EmptyCommand emptySetTypeColor = new EmptyCommand();
        emptySetTypeColor.setCommandName("color", ArgumentType.REQUIRED);
        EmptyCommand emptySetTypeHex = new EmptyCommand();
        emptySetTypeHex.setCommandName("hex color", ArgumentType.REQUIRED);
        EmptyCommand emptySetType = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                if (Arrays.stream(ColorType.values()).anyMatch(type -> type.name().equalsIgnoreCase(argument)))
                    return argument;
                return "";
            }
        };
        emptySetType.setCommandName("type", ArgumentType.REQUIRED);
        emptySetType.setTabRunnable(((args, player) -> {
            if (args[3].startsWith("#")) {
                return args[3].length() < 8 ? Collections.singletonList(args[3] + "#ffffff".substring(args[3].length(), 7)) : Collections.singletonList(args[3].substring(0, 7));
            } else {
                List<String> list = Arrays.stream(ChatColor.values()).map(Enum::name).collect(Collectors.toList());
                list.add("#ffffff");
                list.add("#000000");
                list.add("#");
                return list;
            }
        }));
        emptySetType.setRunnable(((args, player) -> {
            if (args.length == 4) {
                if (ColorType.getTypes().contains(args[2])) {
                    if (Arrays.stream(ChatColor.values()).map(ChatColor::name).anyMatch(c -> c.equalsIgnoreCase(args[3]))) {
                        ColorType.valueOf(args[2]).setColor(player, new MultiColor(ChatColor.valueOf(args[3].toUpperCase())));
                        sendSuccessTheme(player, "Successfully set color type %s to " + ColorType.valueOf(args[2]).getColor(player) + "this", args[2]);
                    } else if (args[3].matches("#[0-9a-fA-F]{6}")) {
                        ColorType.valueOf(args[2]).setColor(player, new MultiColor(args[3]));
                        sendSuccessTheme(player, "Successfully set color type %s to " + ColorType.valueOf(args[2]).getColor(player) + "this", args[2]);
                    } else {
                        sendErrorTheme(player, "color %s does not exist", args[3].toUpperCase());
                    }
                } else {
                    sendErrorTheme(player, "color type %s does not exist", args[2]);
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport colorTheme set <type> <color|hex color>");
            }
        }));
        emptySetType.addAction(emptySetTypeColor);
        emptySetType.addAction(emptySetTypeHex);
        EmptyCommand emptySetTheme = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                if (ColorTheme.getDefaultThemes().contains(argument)) return argument;
                return "";
            }
        };
        emptySetTheme.setCommandName("theme", ArgumentType.REQUIRED);
        emptySetTheme.setRunnable(((args, player) -> {
            if (args.length == 3) {
                ColorTheme.setDefaultTheme(player, args[2]);
                sendSuccessTheme(player, "Successfully set color theme to %s", args[2]);
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport colorTheme set <theme>");
            }
        }));
        EmptyCommand emptySet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptySet.setCommandName("set", ArgumentType.FIXED);
        emptySet.setTabRunnable((args, player) -> Stream.concat(ColorTheme.getDefaultThemes().stream(), ColorType.getTypes().stream()).collect(Collectors.toList()));
        emptySet.setRunnable(((args, player) -> {
            if (args.length > 2 && runCommands(emptySet.getActions(), args[2], args, player)) {
                return;
            }
            sendErrorTheme(player, "Usage: %s", "/tport colorTheme set " + convertToArgs(emptySet.getActions(), false));
        }));
        emptySet.addAction(emptySetTheme);
        emptySet.addAction(emptySetType);
    
        EmptyCommand emptyGetType = new EmptyCommand();
        emptyGetType.setCommandName("type", ArgumentType.REQUIRED);
        EmptyCommand emptyGet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyGet.setCommandName("get", ArgumentType.FIXED);
        emptyGet.setTabRunnable((args, player) -> Arrays.stream(ColorTheme.ColorType.values()).map(Enum::name).collect(Collectors.toList()));
        emptyGet.setRunnable(((args, player) -> {
            if (args.length == 3) {
                if (ColorType.getTypes().contains(args[2])) {
                    sendInfoTheme(player, "The color of type %s is set to " + ColorType.valueOf(args[2]).getColor(player) + "'this'", args[2]);
                } else {
                    sendErrorTheme(player, "color type %s does not exist", args[2]);
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport colorTheme get <type>");
            }
        }));
        emptyGet.addAction(emptyGetType);
    
        addAction(empty);
        addAction(emptySet);
        addAction(emptyGet);
    }
    
    @Override
    public String getName(String arg) {
        return "colorTheme";
    }
    
    
    @Override
    public void run(String[] args, Player player) {
        //tport colorTheme
        //tport colorTheme set <theme>
        //tport colorTheme set <type> <color>
        //tport colorTheme set <type> <hex color>
        //tport colorTheme get <type>
        
        if (args.length == 1) {
            sendInfoTheme(player, "This is your %s theme", "info");
            sendSuccessTheme(player, "This is your %s theme", "success");
            sendErrorTheme(player, "This is your %s theme", "error");
        } else {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
            sendErrorTheme(player, "Usage: %s", "/tport colorTheme " + convertToArgs(getActions(), true));
        }
    }
}
