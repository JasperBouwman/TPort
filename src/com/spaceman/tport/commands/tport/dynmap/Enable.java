package com.spaceman.tport.commands.tport.dynmap;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendInfoTheme;

public class Enable extends SubCommand {
    
    private final EmptyCommand emptyState;
    
    public Enable() {
        emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(textComponent("This command is used to enable/disable Dynmap support", ColorTheme.ColorType.infoColor));
        emptyState.setPermissions("TPort.dynmap.enable", "TPort.admin.dynmap");
        addAction(emptyState);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get if Dynmap support is enabled or not", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport dynmap enable [state]
        
        if (args.length == 2) {
            sendInfoTheme(player, "Dynmap support is %s", (DynmapHandler.isEnabled() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
        } else if (args.length == 3) {
            if (!emptyState.hasPermissionToRun(player, true)) {
                return;
            }
            if (args[2].equalsIgnoreCase("true")) {
                if (DynmapHandler.isEnabled()) {
                    sendErrorTheme(player, "Dynmap support is already %s", "enabled");
                    return;
                }
                DynmapHandler.setShouldEnabled(true);
                if (!DynmapHandler.isEnabled()) {
                    sendErrorTheme(player, "Could not enable Dynmap support, Dynmap not found");
                } else {
                    sendInfoTheme(player, "Successfully %s dynmap support", "enabled");
                }
            } else if (args[2].equalsIgnoreCase("false")) {
                if (!DynmapHandler.shouldEnable()) {
                    sendErrorTheme(player, "Dynmap support is already %s", "disabled");
                    return;
                }
                DynmapHandler.setShouldEnabled(false);
                sendInfoTheme(player, "Successfully %s dynmap support", "disabled");
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport metrics enable [state]");
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport metrics enable [state]");
        }
    }
}
