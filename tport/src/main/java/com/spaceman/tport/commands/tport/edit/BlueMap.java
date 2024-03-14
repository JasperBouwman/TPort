package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.BlueMapCommand;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.edit.blueMap.Show;
import com.spaceman.tport.webMaps.BlueMapHandler;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class BlueMap extends SubCommand {
    
    public BlueMap() {
        addAction(new Show());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> blueMap show [state]
        
        if (Features.Feature.BlueMap.isDisabled()) {
            Features.Feature.BlueMap.sendDisabledMessage(player);
            return;
        }
        
        boolean blueMapState = false;
        try { blueMapState = BlueMapHandler.isEnabled(); } catch (Throwable ignored) { }
        if (!blueMapState) {
            BlueMapCommand.sendDisableError(player);
            return;
        }
        
        if (args.length > 3) {
            if (runCommands(getActions(), args[3], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> blueMap show [state]");
    }
}
