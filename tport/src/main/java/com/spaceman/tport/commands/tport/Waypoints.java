package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.waypoints.Type;
import com.spaceman.tport.waypoint.WaypointShowType;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.spaceman.tport.commandHandler.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Waypoints extends SubCommand {
    
    public Waypoints() {
        addAction(new Type());
    }
    
    public static WaypointShowType getWaypointShowType(UUID uuid) {
        WaypointShowType def = WaypointShowType.Own;
        return WaypointShowType.get(tportData.getConfig().getString("tport." + uuid + ".waypoint.showType", def.name()), def);
    }
    
    public static void setWaypointShowType(UUID uuid, WaypointShowType waypointShowType) {
        tportData.getConfig().set("tport." + uuid + ".waypoint.showType", waypointShowType.name());
        tportData.saveConfig();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport waypoints type [type]
        //  - PublicTP (shows all PublicTP TPorts)
        //  - public (shows all public TPorts, using private state: open, online)
        //  - canTP (shows all TPorts you can teleport to)
        //  - own (shows only own TPorts)
        //  - all (shows all TPorts)
        //  - none (shows no TPorts, disabled)
        
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport waypoints " + convertToArgs(getActions(), false));
    }
}
