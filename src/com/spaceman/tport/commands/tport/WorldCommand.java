package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class WorldCommand extends SubCommand {
    
    EmptyCommand emptyWorld = new EmptyCommand();
    
    public WorldCommand() {
        emptyWorld.setCommandName("world", ArgumentType.REQUIRED);
        emptyWorld.setCommandDescription(formatInfoTranslation("tport.command.worldCommand.world.commandDescription"));
        emptyWorld.setPermissions("TPort.world.tp");
        addAction(emptyWorld);
    }
    
    @Override
    public String getName(String arg) {
        return "world";
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport world <world>
        
        if (!emptyWorld.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 2) {
            World world = Bukkit.getWorld(args[1]);
            if (world != null) {
                Location l = FeatureTP.setSafeY(world, world.getSpawnLocation().getBlockX(), world.getSpawnLocation().getBlockZ());
                if (l == null) {
                    sendErrorTranslation(player, "tport.command.worldCommand.world.notSafe", world);
                    return;
                }
                l.add(0.5, 0.1, 0.5);
                requestTeleportPlayer(player, l, () -> sendSuccessTranslation(player, "tport.command.worldCommand.world.succeeded", world),
                        (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.worldCommand.world.tpRequested", world, delay, tickMessage, seconds, secondMessage));
            } else {
                sendErrorTranslation(player, "tport.command.worldCommand.world.worldNotExist", args[1]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport world <world>");
        }
    }
}
