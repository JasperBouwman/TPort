package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.featureTP.Mode.worldSearchString;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportConfig;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Mode extends SubCommand {
    
    public Mode() {
        EmptyCommand emptyModeMode = new EmptyCommand();
        emptyModeMode.setCommandName("mode", ArgumentType.OPTIONAL);
        emptyModeMode.setCommandDescription(formatInfoTranslation("tport.command.biomeTP.mode.mode.commandDescription"));
        emptyModeMode.setPermissions(worldSearchString);
        addAction(emptyModeMode);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.stream(com.spaceman.tport.commands.tport.featureTP.Mode.WorldSearchMode.values()).map(Enum::name).collect(Collectors.toList());
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.biomeTP.mode.commandDescription");
    }
    
    public static com.spaceman.tport.commands.tport.featureTP.Mode.WorldSearchMode getDefMode(UUID uuid) {
        return com.spaceman.tport.commands.tport.featureTP.Mode.WorldSearchMode.valueOf(tportConfig.getConfig().getString("biomeTP.defaultMode." + uuid.toString(), "CLOSEST"));
    }
    
    public static void setDefMode(UUID uuid, com.spaceman.tport.commands.tport.featureTP.Mode.WorldSearchMode mode) {
        tportConfig.getConfig().set("biomeTP.defaultMode." + uuid.toString(), mode.name());
        tportConfig.saveConfig();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP mode [mode]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.biomeTP.mode.succeeded", getDefMode(player.getUniqueId()).name());
        } else if (args.length == 3) {
            try {
                com.spaceman.tport.commands.tport.featureTP.Mode.WorldSearchMode mode = com.spaceman.tport.commands.tport.featureTP.Mode.WorldSearchMode.valueOf(args[2].toUpperCase());
                if (!hasPermission(player, true, mode.getPerm())) {
                    return;
                }
                setDefMode(player.getUniqueId(), mode);
                sendSuccessTranslation(player, "tport.command.biomeTP.mode.mode.succeeded", mode.name());
            } catch (IllegalArgumentException iae) {
                sendErrorTranslation(player, "tport.command.biomeTP.mode.mode.modeNotExist", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport biomeTP mode [mode]");
        }
    }
}