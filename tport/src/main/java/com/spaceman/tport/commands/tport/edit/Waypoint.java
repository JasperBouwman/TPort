package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.edit.waypoint.Color;
import com.spaceman.tport.commands.tport.edit.waypoint.Icon;
import com.spaceman.tport.commands.tport.edit.waypoint.Show;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.commands.tport.BlueMapCommand.checkBlueMapState;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Waypoint extends SubCommand {
    
    public Waypoint() {
        
        addAction(new Show());
    //    addAction(new Icon());
        addAction(new Color());
        
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort> waypoint show [state]
        // tport edit <TPort> waypoint icon [icon]
        // tport edit <TPort> waypoint color [chat color]
        // tport edit <TPort> waypoint color [hex color]
        
        if (Features.Feature.Waypoints.isDisabled())  {
            Features.Feature.Waypoints.sendDisabledMessage(player);
            return;
        }
        
        if (args.length > 3) {
            if (runCommands(getActions(), args[3], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> waypoint <show|icon|color>");
    }
}
