package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.dynmap.Colors;
import com.spaceman.tport.commands.tport.dynmap.IP;
import com.spaceman.tport.commands.tport.dynmap.Search;
import com.spaceman.tport.webMaps.DynmapHandler;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.bukkit.entity.Player;

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_ICanSeeItAll;
import static com.spaceman.tport.commandHandler.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class DynmapCommand extends SubCommand {
    
    public DynmapCommand() {
        if (Features.Feature.Dynmap.isEnabled())  {
            DynmapHandler.enable();
        } else {
            DynmapHandler.disable();
        }
        
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.dynmapCommand.commandDescription"));
        
        addAction(empty);
        addAction(new Search());
        addAction(new IP());
        addAction(new Colors());
    }
    
    @Override
    public String getName(String arg) {
        return "dynmap";
    }
    
    public static boolean checkDynmapState(Player player) {
        if (Features.Feature.Dynmap.isDisabled()) {
            Features.Feature.Dynmap.sendDisabledMessage(player);
            return false;
        }
        
        if (!DynmapHandler.isEnabled()) {
            sendErrorTranslation(player, "tport.command.dynmapCommand.disableError", "/tport features dynmap state true");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport dynmap
        // tport dynmap search <player> [TPort name]
        // tport dynmap IP [IP]
        // tport dynmap colors [color theme]
        
        if (!checkDynmapState(player))  {
            return;
        }
        
        Advancement_ICanSeeItAll.grant(player);
        
        if (args.length == 1) {
            //When disabled this command can't be executed
            
            boolean enableBool = DynmapHandler.isEnabled();
            if (!enableBool) {
                Message enabledMessage = formatTranslation(varErrorColor, varError2Color, "tport.command.dynmapCommand.disabled");
                Message shouldEnableMessage = formatTranslation(varErrorColor, varError2Color, "tport.command.dynmapCommand.enabled");
                sendErrorTranslation(player, "tport.command.dynmapCommand.couldNotEnable", enabledMessage, shouldEnableMessage);
                return;
            }
            
            Message enabledMessage = formatTranslation(goodColor, varInfo2Color, "tport.command.dynmapCommand.enabled");
            Message shouldEnableMessage = formatTranslation(goodColor, varInfo2Color, "tport.command.dynmapCommand.enabled");
            
            Message searchMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.dynmapCommand.searchAsText");
            searchMessage.getText().forEach(textComponent -> textComponent.setInsertion("/tport dynmap search <player> [tport name]")
                    .addTextEvent(new HoverEvent(formatInfoTranslation("tport.command.dynmapCommand.searchAsHover",
                            "/tport dynmap search <player> [tport name]"))));
            
            sendInfoTranslation(player, "tport.command.dynmapCommand.getInfo",
                    shouldEnableMessage,
                    enabledMessage,
                    "/tport edit <TPort name> dynmap show <state>",
                    "/tport edit <TPort name> dynmap icon <icon>",
                    searchMessage,
                    "/tport dynmap IP <ip>",
                    "http://0.0.0.0:PORT/",
                    "http://example.com/");
            return;
        } else if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport dynmap " + convertToArgs(getActions(), true));
    }
}
