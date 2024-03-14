package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.webMaps.BlueMapHandler;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class BlueMapCommand extends SubCommand {
    
    public BlueMapCommand() {
        if (Features.Feature.BlueMap.isEnabled())  {
            try {
                BlueMapHandler.enable();
            } catch (Throwable ignored) {
                Main.getInstance().getLogger().log(Level.SEVERE, "Tried to enable BlueMap support, BlueMap API was not found");
            }
        } else {
            try {
                BlueMapHandler.disable();
            } catch (Throwable ignored) {}
        }
        
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.blueMapCommand.commandDescription"));
        
        addAction(empty);
    }
    
    @Override
    public String getName(String arg) {
        return "blueMap";
    }
    
    public static void sendDisableError(Player player) {
        sendErrorTranslation(player, "tport.command.blueMapCommand.disableError", "/tport features blueMap state true");
    }
    
    @Override
    public void run(String[] args, Player player) { //todo
        // tport blueMap
        // tport blueMap search <player> [TPort name]
        // tport blueMap IP [IP]
        // tport blueMap colors [color theme]
        
        sendInfoTranslation(player, "tport.command.blueMapCommand");
    }
}
