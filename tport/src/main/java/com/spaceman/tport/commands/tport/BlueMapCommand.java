package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.blueMap.Colors;
import com.spaceman.tport.commands.tport.blueMap.IP;
import com.spaceman.tport.commands.tport.blueMap.Search;
import com.spaceman.tport.webMaps.BlueMapHandler;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class BlueMapCommand extends SubCommand {
    
    public BlueMapCommand() {
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.blueMapCommand.commandDescription"));
        
        addAction(empty);
        addAction(new Search());
        addAction(new IP());
        addAction(new Colors());
    }
    
    @Override
    public String getName(String arg) {
        return "blueMap";
    }
    
    public static boolean checkBlueMapState(Player player) {
        if (Features.Feature.BlueMap.isDisabled()) {
            Features.Feature.BlueMap.sendDisabledMessage(player);
            return false;
        }
        
        boolean blueMapState = false;
        try { blueMapState = BlueMapHandler.isEnabled(); } catch (Throwable ignored) { }
        if (!blueMapState) {
            sendErrorTranslation(player, "tport.command.blueMapCommand.disableError", "/tport features blueMap state true");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void run(String[] args, Player player) { //todo
        // tport blueMap
        // tport blueMap search <player> [TPort name]
        // tport blueMap IP [IP]
        // tport blueMap colors [color theme]
        // tport blueMap icons
        
        if (!checkBlueMapState(player))  {
            return;
        }
        
        if (args.length == 1) {
            sendInfoTranslation(player, "tport.command.blueMapCommand");
        } else if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport blueMap " + convertToArgs(getActions(), true));
        
    }
}
