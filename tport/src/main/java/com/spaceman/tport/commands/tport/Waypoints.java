package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.waypoint.WaypointManager;
import com.spaceman.tport.waypoint.WaypointShowType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Waypoints extends SubCommand {
    
    private final EmptyCommand emptyTypeType;
    
    public Waypoints() {
        emptyTypeType = new EmptyCommand();
        emptyTypeType.setCommandName("type", ArgumentType.OPTIONAL);
        emptyTypeType.setCommandDescription(formatInfoTranslation("tport.command.waypoints.type.type.commandDescription"));
        emptyTypeType.setPermissions("tport.waypoints.<type>");
        
        EmptyCommand emptyType = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                return this.getCommandName();
            }
        };
        emptyType.setCommandName("type", ArgumentType.FIXED);
        emptyType.setCommandDescription(formatInfoTranslation("tport.command.waypoints.type.commandDescription"));
        emptyType.setTabRunnable((args, player) -> {
            if (!emptyTypeType.hasPermissionToRun(player, false)) {
                return Collections.emptyList();
            }
            return Arrays.stream(WaypointShowType.values()).map(Enum::name).toList();
        });
        emptyType.addAction(emptyTypeType);
        
        addAction(emptyType);
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
        // tport waypoints showAll <state>
        // tport waypoints type [type]
        //  - PublicTP (shows all PublicTP TPorts)
        //  - public (shows all public TPorts, using private state: open, online)
        //  - canTP (shows all TPorts you can teleport to)
        //  - own (shows only own TPorts)
        //  - all (shows all TPorts)
        //  - none (shows no TPorts, disabled)
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.waypoints.type.succeeded", getWaypointShowType(player.getUniqueId()));
        } else if (args.length == 3) {
//          if (emptyTypeType.hasPermissionToRun(player, true)) {
//              return; //todo fix
//          }
          
          WaypointShowType showType = WaypointShowType.get(args[2], null);
          
          if (showType == null) {
              sendErrorTranslation(player, "tport.command.waypoints.type.type.showTypeNotExist", args[2]);
              return;
          }
          
          setWaypointShowType(player.getUniqueId(), showType);
          sendSuccessTranslation(player, "tport.command.waypoints.type.type.succeeded", showType);
          
          WaypointManager.removeFromWorld(player, player.getWorld().getName(), player.getWorld().getName());
          
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport waypoints type [type]");
        }
        
    }
    
}
