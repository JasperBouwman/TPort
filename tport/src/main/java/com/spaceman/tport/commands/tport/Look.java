package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.tpEvents.TPEManager;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Predicate;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Look extends SubCommand {
    
    public Look() {
        EmptyCommand emptyType = new EmptyCommand();
        emptyType.setCommandName("type", ArgumentType.OPTIONAL);
        emptyType.setCommandDescription(formatInfoTranslation("tport.command.look.type.commandDescription"));
        emptyType.setPermissions("TPort.lookTP");
        
        addAction(emptyType);
        
        setCommandDescription(formatInfoTranslation("tport.command.look.commandDescription"));
        setPermissions("TPort.lookTP");
        
        registerLookTypes();
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return typeMap.keySet();
    }
    
    @FunctionalInterface
    public interface LookRunnable {
        void look(Player player);
    }
    private static final HashMap<String, LookRunnable> typeMap = new HashMap<>();
    public static boolean registerLookType(String name, LookRunnable run) {
        return typeMap.putIfAbsent(name.toLowerCase(), run) != null;
    }
    
    public void registerLookTypes() {
        typeMap.clear();
        
        Look.registerLookType("", player -> {
            
            Predicate<Entity> predicate = (entity) -> !entity.equals(player) && !entity.equals(player.getVehicle());
            
            Entity e;
            RayTraceResult entityTrace = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 100, predicate);
            if (entityTrace != null) {
                e = entityTrace.getHitEntity();
            } else {
                e = null;
            }
            
            Block b;
            RayTraceResult blockTrace = player.rayTraceBlocks(100, FluidCollisionMode.ALWAYS);
            if (blockTrace != null) {
                b = blockTrace.getHitBlock();
            } else {
                b = null;
            }
            
            if (e == null && b == null) {
                sendErrorTranslation(player, "tport.command.look.notLooking");
                return;
            }
            
            
            double entityDistance = Double.POSITIVE_INFINITY;
            if (e != null) {
                entityDistance = player.getLocation().distance(e.getLocation());
            }
            double blockDistance = Double.POSITIVE_INFINITY;
            if (b != null) {
                blockDistance = player.getLocation().distance(b.getLocation());
            }
            
            if (entityDistance < blockDistance) { //entity TP
                if (e.getType().equals(EntityType.PLAYER)) {
                    Player p = (Player) e;
                    sendInfoTranslation(player, "tport.command.look.pltp");
                    TPortCommand.executeTPortCommand(player, new String[]{"PLTP", "tp", p.getName()});
                } else {
                    TPEManager.requestTeleportPlayer(player, e.getLocation(), () -> sendSuccessTranslation(player, "tport.command.look.entityTP", e.getType().toString()),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.entityTP.tpRequested", e.getType().toString(), delay, tickMessage, seconds, secondMessage));
                }
            } else { //block TP
                if (!b.getType().equals(Material.WATER) && !b.getType().equals(Material.LAVA)) { //block TP
                    Location blockLocation = b.getLocation();
                    if (blockLocation.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                        blockLocation.add(blockTrace.getHitBlockFace().getDirection());
                    } else if (blockLocation.getBlock().getType().isSolid()) {
                        blockLocation.add(0, 1, 0);
                    }
                    blockLocation.add(0.5, 0.1, 0.5);
                    blockLocation.setPitch(player.getLocation().getPitch());
                    blockLocation.setYaw(player.getLocation().getYaw());
                    
                    TPEManager.requestTeleportPlayer(player, blockLocation, () -> sendSuccessTranslation(player, "tport.command.look.blockTP", b.getType().toString()),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.blockTP.tpRequested", b.getType().toString(), delay, tickMessage, seconds, secondMessage));
                } else { //fluid TP
                    Location l = b.getLocation().add(0.5, 1.1, 0.5);
                    l.setPitch(player.getLocation().getPitch());
                    l.setYaw(player.getLocation().getYaw());
                    
                    TPEManager.requestTeleportPlayer(player, l, () -> sendSuccessTranslation(player, "tport.command.look.fluidTP", b.getType().toString()),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.fluidTP.tpRequested", b.getType().toString(), delay, tickMessage, seconds, secondMessage));
                }
            }
            
        });
        
        Look.registerLookType("entity", player -> {
            Predicate<Entity> predicate = (entity) -> !entity.equals(player) && !entity.equals(player.getVehicle());
            
            RayTraceResult r = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 100, predicate);
            if (r == null) { 
                sendErrorTranslation(player, "tport.command.look.type.entity.notFound");
                return;
            }
            Entity e = r.getHitEntity();
            if (e == null) {
                sendErrorTranslation(player, "tport.command.look.type.entity.notFound");
                return;
            }
            
            if (e.getType().equals(EntityType.PLAYER)) {
                Player p = (Player) e;
                sendInfoTranslation(player, "tport.command.look.type.entity.pltp");
                TPortCommand.executeTPortCommand(player, new String[]{"PLTP", "tp", p.getName()});
            } else {
                TPEManager.requestTeleportPlayer(player, e.getLocation(), () -> sendSuccessTranslation(player, "tport.command.look.type.entity.succeeded", e.getType().toString()),
                        (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.type.entity.tpRequested", e.getType().toString(), delay, tickMessage, seconds, secondMessage));
            }
        });
        
        Look.registerLookType("block", (player -> {
            RayTraceResult r = player.rayTraceBlocks(100, FluidCollisionMode.NEVER);
            if (r == null) { 
                sendErrorTranslation(player, "tport.command.look.type.block.notFound");
                return;
            }
            Block b = r.getHitBlock();
            if (b == null) {
                sendErrorTranslation(player, "tport.command.look.type.block.notFound");
                return;
            }
            
            Location blockLocation = b.getLocation();
            if (blockLocation.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                blockLocation.add(r.getHitBlockFace().getDirection());
            } else if (blockLocation.getBlock().getType().isSolid()) {
                blockLocation.add(0, 1, 0);
            }
            blockLocation.add(0.5, 0.1, 0.5);
            blockLocation.setPitch(player.getLocation().getPitch());
            blockLocation.setYaw(player.getLocation().getYaw());
            
            TPEManager.requestTeleportPlayer(player, blockLocation, () -> sendSuccessTranslation(player, "tport.command.look.type.block.succeeded", b.getType().toString()),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.type.block.tpRequested", b.getType().toString(), delay, tickMessage, seconds, secondMessage));
        }));
        
        Look.registerLookType("fluid", (player -> {
            RayTraceResult r = player.rayTraceBlocks(100, FluidCollisionMode.ALWAYS);
            if (r == null) {
                sendErrorTranslation(player, "tport.command.look.type.fluid.notFound");
                return;
            }
            Block f = r.getHitBlock();
            if (f == null) {
                sendErrorTranslation(player, "tport.command.look.type.fluid.notFound");
                return;
            }
            if (!f.getType().equals(Material.WATER) && !f.getType().equals(Material.LAVA)) {
                sendErrorTranslation(player, "tport.command.look.type.fluid.notFound");
                return;
            }
            
            Location l = f.getLocation().add(0.5, 1.1, 0.5);
            l.setPitch(player.getLocation().getPitch());
            l.setYaw(player.getLocation().getYaw());
            TPEManager.requestTeleportPlayer(player, l, () -> sendSuccessTranslation(player, "tport.command.look.type.fluid.succeeded", f.getType().toString()),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.type.fluid.tpRequested", f.getType().toString(), delay, tickMessage, seconds, secondMessage));
        }));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport look [type]
        
        //types: 
        // - entity
        // - block
        // - fluid
        
        if (args.length == 1) {
            if (!CooldownManager.LookTP.hasCooled(player, true)) return;
            
            LookRunnable looker = typeMap.get("");
            looker.look(player);
            
            CooldownManager.LookTP.update(player);
        } else if (args.length == 2) {
            LookRunnable looker = typeMap.get(args[1].toLowerCase());
            if (looker == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport look " + CommandTemplate.convertToArgs(this.getActions(), false));
                return;
            }
            
            if (!CooldownManager.LookTP.hasCooled(player, true)) return;
            looker.look(player);
            CooldownManager.LookTP.update(player);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport look " + CommandTemplate.convertToArgs(this.getActions(), true));
        }
        
    }
}
