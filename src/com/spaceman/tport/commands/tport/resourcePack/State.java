package com.spaceman.tport.commands.tport.resourcePack;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.ResourcePack;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.commands.tport.ResourcePack.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.openUrl;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class State extends SubCommand {
    
    public State() {
        EmptyCommand emptyStateState = new EmptyCommand();
        emptyStateState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyStateState.setCommandDescription(formatInfoTranslation("tport.command.resourcePack.state.state.commandDescription"));
        
        addAction(emptyStateState);
        
        setCommandDescription(formatInfoTranslation("tport.command.resourcePack.state.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport resourcePack state [state]
        
        if (args.length == 2) {
            boolean state = getResourcePackState(player.getUniqueId());
            
            Message stateMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.resourcePack.state." + (state ? "enabled" : "disabled") );
            final String releasePath = "https://github.com/JasperBouwman/TPort/releases/tag/TPort%20" + Main.getInstance().getDescription().getVersion();
            Message hereMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.resourcePack.state.here");
            hereMessage.getText().forEach(t -> t
                    .setInsertion(releasePath)
                    .addTextEvent(hoverEvent(textComponent(releasePath, ColorType.infoColor)))
                    .addTextEvent(openUrl(releasePath)));
            
            sendInfoTranslation(player, "tport.command.resourcePack.state.succeeded", stateMessage, hereMessage);
        } else if (args.length == 3) {
            Boolean newState = Main.toBoolean(args[2]);
            if (newState == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport resourcePack state [true|false]");
                return;
            }
            
            boolean currentState = ResourcePack.getResourcePackState(player.getUniqueId());
            if (newState == currentState) {
                Message stateMessage = formatTranslation(ColorType.varErrorColor, ColorType.varError2Color, "tport.command.resourcePack.state.state." + (newState ? "enabled" : "disabled") );
                sendErrorTranslation(player, "tport.command.resourcePack.state.state.alreadyInState", stateMessage);
                return;
            }
            
            setResourcePackState(player.getUniqueId(), newState);
            updateResourcePack(player);
            
            if (newState) {
                Message stateMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.resourcePack.state.state.enabled");
                sendInfoTranslation(player, "tport.command.resourcePack.state.state.succeeded.enabled", stateMessage);
            } else {
                Message stateMessage = formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.resourcePack.state.state.disabled");
                sendInfoTranslation(player, "tport.command.resourcePack.state.state.succeeded.disabled", stateMessage);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport resourcePack state [state]");
        }
    }
}
