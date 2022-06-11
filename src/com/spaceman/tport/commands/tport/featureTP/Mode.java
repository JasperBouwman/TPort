package com.spaceman.tport.commands.tport.featureTP;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportConfig;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Mode extends SubCommand {
    
    public static String worldSearchString = "TPort.worldSearch.mode.<mode>";
    
    public Mode() {
        EmptyCommand emptyModeMode = new EmptyCommand();
        emptyModeMode.setCommandName("mode", ArgumentType.OPTIONAL);
        emptyModeMode.setCommandDescription(formatInfoTranslation("tport.command.featureTP.mode.mode.commandDescription"));
        emptyModeMode.setPermissions(worldSearchString);
        addAction(emptyModeMode);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.stream(WorldSearchMode.values()).map(Enum::name).collect(Collectors.toList());
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.featureTP.mode.commandDescription");
    }
    
    public static WorldSearchMode getDefMode(UUID uuid) {
        return WorldSearchMode.valueOf(tportConfig.getConfig().getString("featureTP.defaultMode." + uuid.toString(), "CLOSEST"));
    }
    
    public static void setDefMode(UUID uuid, WorldSearchMode mode) {
        tportConfig.getConfig().set("featureTP.defaultMode." + uuid.toString(), mode.name());
        tportConfig.saveConfig();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport featureTP mode [mode]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.featureTP.mode.succeeded", getDefMode(player.getUniqueId()).name());
        } else if (args.length == 3) {
            try {
                WorldSearchMode mode = WorldSearchMode.valueOf(args[2].toUpperCase());
                if (!hasPermission(player, true, mode.getPerm())) {
                    return;
                }
                setDefMode(player.getUniqueId(), mode);
                sendSuccessTranslation(player, "tport.command.featureTP.mode.mode.succeeded", mode.name());
            } catch (IllegalArgumentException iae) {
                sendErrorTranslation(player, "tport.command.featureTP.mode.mode.modeNotExist", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport featureTP mode [mode]");
        }
    }
    
    public enum WorldSearchMode {
        RANDOM(Main::getRandomLocation),
        CLOSEST(Main::getClosestLocation);
        
        private final LocationGetter locationGetter;
        
        WorldSearchMode(LocationGetter locationGetter) {
            this.locationGetter = locationGetter;
        }
        
        public Location getLoc(Player player) {
            return locationGetter.getLoc(player);
        }
        
        public String getPerm() {
            return "TPort.worldSearch.mode." + this.name();
        }
        
        public WorldSearchMode getNext() {
            boolean next = false;
            for (WorldSearchMode mode : values()) {
                if (mode.equals(this)) {
                    next = true;
                } else if (next) {
                    return mode;
                }
            }
            return Arrays.asList(values()).get(0);
        }
        
        @Nullable
        public static WorldSearchMode getForPlayer(String name, Player player, boolean checkPerm) {
            for (WorldSearchMode mode : values()) {
                if (mode.name().equalsIgnoreCase(name)) {
                    if (!checkPerm || hasPermission(player, true, mode.getPerm())) {
                        return mode;
                    } else {
                        break;
                    }
                }
            }
            return null;
        }
        
        @FunctionalInterface
        private interface LocationGetter {
            Location getLoc(Player player);
        }
    }
}
