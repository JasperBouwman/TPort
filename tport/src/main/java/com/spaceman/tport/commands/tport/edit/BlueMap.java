package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.edit.blueMap.Icon;
import com.spaceman.tport.commands.tport.edit.blueMap.Show;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.commands.tport.BlueMapCommand.checkBlueMapState;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class BlueMap extends SubCommand {
    
    public BlueMap() {
        addAction(new Show());
        addAction(new Icon());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> blueMap show [state]
        // tport edit <TPort name> blueMap icon [icon]
        
        if (!checkBlueMapState(player))  {
            return;
        }
        
        if (args.length > 3) {
            if (runCommands(getActions(), args[3], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> blueMap <show|icon>");
    }
}
