package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.openUrl;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public class ResourcePack extends SubCommand {
    
    public ResourcePack() {
        EmptyCommand emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(formatInfoTranslation("tport.command.resourcePack.state.commandDescription"));
        
        addAction(emptyState);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.resourcePack.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport resourcePack state [state]
        // tport resourcePack resolution [resolution]
        
        if (args.length == 1) {
            boolean state = tportConfig.getConfig().getBoolean("resourcePack." + player.getUniqueId() + ".state", false);
            
            Message stateMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.resourcePack." + (state ? "enabled" : "disabled") );
            final String resourcePath = "https://github.com/JasperBouwman/TPort/blob/master/resource%20pack/pack/TPort.zip";
            Message hereMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.resourcePack.here");
            hereMessage.getText().forEach(t -> t
                    .setInsertion(resourcePath)
                    .addTextEvent(hoverEvent(textComponent(resourcePath, ColorType.infoColor)))
                    .addTextEvent(openUrl(resourcePath)));
            
            sendInfoTranslation(player, "tport.command.resourcePack.succeeded", stateMessage, hereMessage);
        } else if (args.length == 2) {
            Boolean newState = Main.toBoolean(args[1]);
            if (newState == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport resourcePack <true|false>");
                return;
            }
            
            boolean state = tportConfig.getConfig().getBoolean("resourcePack." + player.getUniqueId() + ".state", false);
            if (newState == state) {
                Message stateMessage = formatTranslation(ColorType.varErrorColor, ColorType.varError2Color, "tport.command.resourcePack." + (newState ? "enabled" : "disabled") );
                sendErrorTranslation(player, "tport.command.resourcePack.state.alreadyInState", stateMessage);
                return;
            }
            
            tportConfig.getConfig().set("resourcePack." + player.getUniqueId() + ".state", newState);
            tportConfig.saveConfig();
            
            updateResourcePack(player);
            
            if (newState) {
                Message stateMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.resourcePack.enabled");
                sendInfoTranslation(player, "tport.command.resourcePack.state.succeeded.enabled", stateMessage);
            } else {
                Message stateMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.resourcePack.disabled");
                sendInfoTranslation(player, "tport.command.resourcePack.state.succeeded.disabled", stateMessage);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport resourcePack [state]");
        }
    }
    
    public static void updateResourcePack(Player player) {
        boolean state = tportConfig.getConfig().getBoolean("resourcePack." + player.getUniqueId() + ".state", false);
        if (state) {
            String resourcePath = "https://github.com/JasperBouwman/TPort/raw/master/resource%20pack/pack/TPort.zip";
            player.setResourcePack(resourcePath, null, "test", false); //todo test
        }
    }
}
