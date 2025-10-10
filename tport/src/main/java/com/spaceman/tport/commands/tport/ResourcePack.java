package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.advancements.TPortAdvancement;
import com.spaceman.tport.advancements.TPortAdvancementManager;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.resourcePack.ResolutionCommand;
import com.spaceman.tport.commands.tport.resourcePack.State;
import com.spaceman.tport.events.JoinEvent;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.spaceman.tport.commandHandler.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.openUrl;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public class ResourcePack extends SubCommand {
    
    public ResourcePack() {
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.resourcePack.commandDescription"));
        
        addAction(empty);
        addAction(new State());
        addAction(new ResolutionCommand());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport resourcePack
        // tport resourcePack state [state]
        // tport resourcePack resolution [resolution]
        
        if (args.length == 1) {
            boolean state = tportConfig.getConfig().getBoolean("resourcePack." + player.getUniqueId() + ".state", false);
            
            Message stateMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.resourcePack." + (state ? "enabled" : "disabled") );
            final String releasePath = "https://github.com/JasperBouwman/TPort/releases/tag/TPort%20" + Main.getInstance().getDescription().getVersion();
            Message hereMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.resourcePack.here");
            hereMessage.getText().forEach(t -> t
                    .setInsertion(releasePath)
                    .addTextEvent(hoverEvent(textComponent(releasePath, ColorType.infoColor)))
                    .addTextEvent(openUrl(releasePath)));
            
            sendInfoTranslation(player, "tport.command.resourcePack.succeeded", stateMessage, getResourcePackResolution(player.getUniqueId()), hereMessage);
        } else if (args.length > 1) {
            if (!runCommands(this.getActions(), args[1], args, player)) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport resourcePack " + convertToArgs(this.getActions(), false));
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport resourcePack " + convertToArgs(this.getActions(), false));
        }
    }
    
    public static boolean getResourcePackState(UUID uuid) {
        return tportConfig.getConfig().getBoolean("resourcePack." + uuid + ".state", false);
    }
    public static void setResourcePackState(UUID uuid, boolean state) {
        tportConfig.getConfig().set("resourcePack." + uuid + ".state", state);
        tportConfig.saveConfig();
    }
    public static ResolutionCommand.Resolution getResourcePackResolution(UUID uuid) {
        return ResolutionCommand.Resolution.getResolution(tportConfig.getConfig().getString("resourcePack." + uuid + ".resolution"), "x16");
    }
    public static void setResourcePackResolution(UUID uuid, ResolutionCommand.Resolution resolution) {
        tportConfig.getConfig().set("resourcePack." + uuid + ".resolution", resolution.getName());
        tportConfig.saveConfig();
    }
    
    public static void updateResourcePack(Player player, boolean delayAdvancement) {
        if (getResourcePackState(player.getUniqueId())) {
            ResolutionCommand.Resolution res = getResourcePackResolution(player.getUniqueId());
            String resourcePath = res.getUrl();
            if (resourcePath != null) {
                player.setResourcePack(resourcePath, null, "For a best experience with TPort you should enable the TPort Resource Pack", false);
                JoinEvent.playerResourceList.add(player.getUniqueId());
            }
        }
        
        if (TPortAdvancement.isActive()) {
            int time = delayAdvancement ? 50 : 0;
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                TPortAdvancement.Advancement_OhNoMyButtons.grant(player);
                TPortAdvancementManager.reInitAdvancements(player);
            }, time);
        }
    }
    
}
