package com.spaceman.tport.commands.tport.waypoints;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.waypoint.WaypointManager;
import com.spaceman.tport.waypoint.WaypointShowType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.commands.tport.Waypoints.getWaypointShowType;
import static com.spaceman.tport.commands.tport.Waypoints.setWaypointShowType;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Type extends SubCommand {
    
    public Type() {
        EmptyCommand emptyType = new EmptyCommand();
        emptyType.setCommandName("type", ArgumentType.OPTIONAL);
        emptyType.setCommandDescription(formatInfoTranslation("tport.command.waypoints.type.type.commandDescription"));
        emptyType.setPermissions("tport.waypoints.<type>");
        
        addAction(emptyType);
        setCommandDescription(formatInfoTranslation("tport.command.waypoints.type.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.stream(WaypointShowType.values()).map(Enum::name).toList();
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
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.waypoints.type.succeeded", getWaypointShowType(player.getUniqueId()));
        } else if (args.length == 3) {

            WaypointShowType showType = WaypointShowType.get(args[2], null);

            if (showType == null) {
                sendErrorTranslation(player, "tport.command.waypoints.type.type.showTypeNotExist", args[2]);
                return;
            }
            if (!hasPermission(player, true, "tport.waypoints." + showType.name())) {
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
