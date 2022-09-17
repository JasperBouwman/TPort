package com.spaceman.tport.commands.tport;

import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class WorldCommand extends SubCommand {
    
    private final EmptyCommand emptyWorld;
    
    public WorldCommand() {
        emptyWorld = new EmptyCommand();
        emptyWorld.setCommandName("world", ArgumentType.OPTIONAL);
        emptyWorld.setCommandDescription(formatInfoTranslation("tport.command.worldCommand.world.world.commandDescription"));
        emptyWorld.setPermissions("TPort.world.tp");
        addAction(emptyWorld);
    }
    
    @Override
    public String getName(String arg) {
        return "world";
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (emptyWorld.hasPermissionToRun(player, false)) {
            return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.worldCommand.world.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport world [world]
        
        if (args.length == 1) {
            TPortInventories.openWorldTP(player);
        } else if (args.length == 2) {
            if (!emptyWorld.hasPermissionToRun(player, true)) {
                return;
            }
            
            World world = Bukkit.getWorld(args[1]);
            if (world == null) {
                sendErrorTranslation(player, "tport.command.worldCommand.world.world.worldNotExist", args[1]);
                return;
            }
            
            Location l = FeatureTP.setSafeY(world, world.getSpawnLocation().getBlockX(), world.getSpawnLocation().getBlockZ());
            if (l == null) {
                sendErrorTranslation(player, "tport.command.worldCommand.world.world.notSafe", world);
                return;
            }
            l.add(0.5, 0.1, 0.5);
            requestTeleportPlayer(player, l, () -> sendSuccessTranslation(player, "tport.command.worldCommand.world.world.succeeded", world),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.worldCommand.world.world.tpRequested", world, delay, tickMessage, seconds, secondMessage));
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport world [world]");
        }
    }
}
