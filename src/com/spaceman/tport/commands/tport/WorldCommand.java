package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.events.InventoryClick.requestTeleportPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class WorldCommand extends SubCommand {
    
    EmptyCommand emptyWorld = new EmptyCommand();
    
    public WorldCommand() {
        emptyWorld.setCommandName("world", ArgumentType.REQUIRED);
        emptyWorld.setCommandDescription(textComponent("This command is used to teleport to the spawn of the given world", infoColor));
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
                Location l = FeatureTP.FeatureType.safeYSetter().setY(world, world.getSpawnLocation().getBlockX(), world.getSpawnLocation().getBlockZ());
                l.add(0.5, 0.1, 0.5);
                requestTeleportPlayer(player, l, () -> sendSuccessTheme(player, "Successfully teleported to world %s", world.getName()));
                
                int delay = Delay.delayTime(player);
                if (delay == 0) {
                    sendSuccessTheme(player, "Successfully teleported to world %s", world.getName());
                } else {
                    sendSuccessTheme(player, "Successfully requested teleportation to world %s, delay time is %s ticks", world.getName(), delay);
                }
            } else {
                sendErrorTheme(player, "World %s does not exist", args[1]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport world <world>");
        }
    }
}
