package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.edit.dynmap.Icon;
import com.spaceman.tport.commands.tport.edit.dynmap.Show;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.commands.tport.DynmapCommand.checkDynmapState;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Dynmap extends SubCommand {
    
    public Dynmap() {
        addAction(new Show());
        addAction(new Icon());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> dynmap show [state]
        // tport edit <TPort name> dynmap icon [icon]
        
        if (!checkDynmapState(player))  {
            return;
        }
        
        if (args.length > 3) {
            if (runCommands(getActions(), args[3], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> dynmap <show|icon>");
    }
}
