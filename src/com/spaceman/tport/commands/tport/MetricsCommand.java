package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Attribute;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.runCommand;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class MetricsCommand extends SubCommand {
    
    private final EmptyCommand emptyEnableState;
    
    public MetricsCommand() {
        emptyEnableState = new EmptyCommand();
        emptyEnableState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyEnableState.setCommandDescription(textComponent("This command is used to enable/disable metrics", infoColor));
        emptyEnableState.setPermissions("TPort.metrics.enable", "TPort.admin.metrics");
        EmptyCommand emptyEnable = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyEnable.setCommandName("enable", ArgumentType.FIXED);
        emptyEnable.setTabRunnable((args, player) -> Arrays.asList("true", "false"));
        emptyEnable.setCommandDescription(textComponent("This command is used to get if metrics is enabled or not", infoColor));
        emptyEnable.addAction(emptyEnableState);
        
        EmptyCommand emptyViewStats = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyViewStats.setCommandName("viewStats", ArgumentType.FIXED);
        emptyViewStats.setCommandDescription(textComponent("This command is used to get a link to view the metrics stats online", infoColor));
        
        addAction(emptyEnable);
        addAction(emptyViewStats);
    }
    
    @Override
    public String getName(String arg) {
        return "metrics";
    }
    
    public static void setEnabled(boolean enabled) {
        Files tportConfig = getFile("TPortConfig");
        tportConfig.getConfig().set("metrics.enabled", enabled);
        tportConfig.saveConfig();
    }
    
    public static boolean isEnabled() {
        Files tportConfig = getFile("TPortConfig");
        if (!tportConfig.getConfig().contains("metrics.enabled")) {
            tportConfig.getConfig().set("metrics.enabled", true);
            tportConfig.saveConfig();
        }
        return tportConfig.getConfig().getBoolean("metrics.enabled", true);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport metrics enable [state]
        // tport metrics viewStats
        
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("enable")) {
                if (args.length == 2) {
                    sendInfoTheme(player, "Metrics is %s", (isEnabled() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                } else if (args.length == 3) {
                    if (emptyEnableState.hasPermissionToRun(player, true)) {
                        if (args[2].equalsIgnoreCase("true")) {
                            setEnabled(true);
                            sendInfoTheme(player, "Successfully %s metrics, to have effect reload/restart the server", "enabled");
                        } else if (args[2].equalsIgnoreCase("false")) {
                            setEnabled(false);
                            sendInfoTheme(player, "Successfully %s metrics, to have effect reload/restart the server", "disabled");
                        } else {
                            sendErrorTheme(player, "Usage: %s", "/tport metrics enable [state]");
                        }
                    }
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport metrics enable [state]");
                }
            } else if (args[1].equalsIgnoreCase("viewStats")) {
                if (!isEnabled()) {
                    Message message = new Message();
                    message.addText(textComponent("To view stats you must enable Metrics, to enable use ", infoColor));
                    message.addText(textComponent("/tport metrics enable true", varInfoColor, runCommand("/tport metrics enable true")));
                    message.sendMessage(player);
                    return;
                }
                if (args.length == 2) {
                    Message message = new Message();
    
                    message.addText(textComponent("To view the metric stats for TPort click ", infoColor));
                    message.addText(textComponent("here", varInfoColor,
                            ClickEvent.openUrl("https://bstats.org/plugin/bukkit/TPort/8061"),
                            hoverEvent(textComponent("https://bstats.org/plugin/bukkit/TPort/8061", infoColor))));
                    message.addText(textComponent(", powered by ", infoColor));
                    message.addText(textComponent("bStats", varInfoColor,
                            Arrays.asList(ClickEvent.openUrl("https://bstats.org/"), hoverEvent(textComponent("https://bstats.org/", infoColor))),
                            Collections.singletonList(Attribute.ITALIC)));
    
                    message.sendMessage(player);
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport metrics viewStats");
                }
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport metrics <enable|viewStats>");
        }
        
    }
}
