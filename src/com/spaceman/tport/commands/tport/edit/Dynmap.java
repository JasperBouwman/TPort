package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.edit.dynmap.Icon;
import com.spaceman.tport.commands.tport.edit.dynmap.Show;
import com.spaceman.tport.dynmap.DynmapHandler;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Dynmap extends SubCommand {
    
    public Dynmap() {
        addAction(new Show());
        addAction(new Icon());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> dynmap show [state]
        // tport edit <TPort name> dynmap icon [icon]
        
        if (!DynmapHandler.isEnabled()) {
            DynmapHandler.sendDisableError(player);
            return;
        }
        
        if (args.length > 3) {
            if (runCommands(getActions(), args[3], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> dynmap <show|icon>");
    }
}
