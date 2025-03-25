package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.history.TeleportHistory;
import com.spaceman.tport.history.locationSource.LookLocationSource;
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

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_LookMeInTheEyes;
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
            
            Entity entity;
            RayTraceResult entityTrace = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 100, predicate);
            if (entityTrace != null) {
                entity = entityTrace.getHitEntity();
            } else {
                entity = null;
            }
            
            Block block;
            RayTraceResult blockTrace = player.rayTraceBlocks(100, FluidCollisionMode.ALWAYS);
            if (blockTrace != null) {
                block = blockTrace.getHitBlock();
            } else {
                block = null;
            }
            
            if (entity == null && block == null) {
                sendErrorTranslation(player, "tport.command.look.notLooking");
                return;
            }
            
            
            double entityDistance = Double.POSITIVE_INFINITY;
            if (entity != null) {
                entityDistance = player.getLocation().distance(entity.getLocation());
            }
            double blockDistance = Double.POSITIVE_INFINITY;
            if (block != null) {
                blockDistance = player.getLocation().distance(block.getLocation());
            }
            
            if (entityDistance < blockDistance) { //entity TP
                if (entity.getType().equals(EntityType.PLAYER)) {
                    Player p = (Player) entity;
                    sendInfoTranslation(player, "tport.command.look.pltp");
                    TPortCommand.executeTPortCommand(player, new String[]{"PLTP", "tp", p.getName()});
                    Advancement_LookMeInTheEyes.grant(player);
                } else {
                    TeleportHistory.setLocationSource(player.getUniqueId(), new LookLocationSource(entity.getType()));
                    TPEManager.requestTeleportPlayer(player, entity.getLocation(), () -> {
                                sendSuccessTranslation(player, "tport.command.look.entityTP", entity.getType().toString());
                                Advancement_LookMeInTheEyes.grant(player);
                            },
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.entityTP.tpRequested", entity.getType().toString(), delay, tickMessage, seconds, secondMessage));
                }
            } else { //block TP
                if (!block.getType().equals(Material.WATER) && !block.getType().equals(Material.LAVA)) { //block TP
                    Location blockLocation = block.getLocation();
                    if (blockLocation.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                        blockLocation.add(blockTrace.getHitBlockFace().getDirection());
                    } else if (blockLocation.getBlock().getType().isSolid()) {
                        blockLocation.add(0, 1, 0);
                    }
                    blockLocation.add(0.5, 0.1, 0.5);
                    blockLocation.setPitch(player.getLocation().getPitch());
                    blockLocation.setYaw(player.getLocation().getYaw());
                    
                    TeleportHistory.setLocationSource(player.getUniqueId(), new LookLocationSource(block.getType()));
                    TPEManager.requestTeleportPlayer(player, blockLocation, () -> sendSuccessTranslation(player, "tport.command.look.blockTP", block.getType().toString()),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.blockTP.tpRequested", block.getType().toString(), delay, tickMessage, seconds, secondMessage));
                } else { //fluid TP
                    Location location = block.getLocation().add(0.5, 1.1, 0.5);
                    location.setPitch(player.getLocation().getPitch());
                    location.setYaw(player.getLocation().getYaw());
                    
                    TeleportHistory.setLocationSource(player.getUniqueId(), new LookLocationSource(block.getType()));
                    TPEManager.requestTeleportPlayer(player, location, () -> sendSuccessTranslation(player, "tport.command.look.fluidTP", block.getType().toString()),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.fluidTP.tpRequested", block.getType().toString(), delay, tickMessage, seconds, secondMessage));
                }
            }
            
        });
        
        Look.registerLookType("entity", player -> {
            Predicate<Entity> predicate = (entity) -> !entity.equals(player) && !entity.equals(player.getVehicle());
            
            RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 100, predicate);
            if (rayTraceResult == null) {
                sendErrorTranslation(player, "tport.command.look.type.entity.notFound");
                return;
            }
            Entity entity = rayTraceResult.getHitEntity();
            if (entity == null) {
                sendErrorTranslation(player, "tport.command.look.type.entity.notFound");
                return;
            }
            
            if (entity.getType().equals(EntityType.PLAYER)) {
                Player lookedAtPlayer = (Player) entity;
                sendInfoTranslation(player, "tport.command.look.type.entity.pltp");
                TPortCommand.executeTPortCommand(player, new String[]{"PLTP", "tp", lookedAtPlayer.getName()});
                Advancement_LookMeInTheEyes.grant(player);
            } else {
                TeleportHistory.setLocationSource(player.getUniqueId(), new LookLocationSource(entity.getType()));
                TPEManager.requestTeleportPlayer(player, entity.getLocation(), () -> {
                            sendSuccessTranslation(player, "tport.command.look.type.entity.succeeded", entity.getType().toString());
                            Advancement_LookMeInTheEyes.grant(player);
                        },
                        (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.type.entity.tpRequested", entity.getType().toString(), delay, tickMessage, seconds, secondMessage));
            }
        });
        
        Look.registerLookType("block", (player -> {
            RayTraceResult rayTraceResult = player.rayTraceBlocks(100, FluidCollisionMode.NEVER);
            if (rayTraceResult == null) {
                sendErrorTranslation(player, "tport.command.look.type.block.notFound");
                return;
            }
            Block block = rayTraceResult.getHitBlock();
            if (block == null) {
                sendErrorTranslation(player, "tport.command.look.type.block.notFound");
                return;
            }
            
            Location blockLocation = block.getLocation();
            if (blockLocation.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                blockLocation.add(rayTraceResult.getHitBlockFace().getDirection());
            } else if (blockLocation.getBlock().getType().isSolid()) {
                blockLocation.add(0, 1, 0);
            }
            blockLocation.add(0.5, 0.1, 0.5);
            blockLocation.setPitch(player.getLocation().getPitch());
            blockLocation.setYaw(player.getLocation().getYaw());
            
            TeleportHistory.setLocationSource(player.getUniqueId(), new LookLocationSource(block.getType()));
            TPEManager.requestTeleportPlayer(player, blockLocation, () -> sendSuccessTranslation(player, "tport.command.look.type.block.succeeded", block.getType().toString()),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.type.block.tpRequested", block.getType().toString(), delay, tickMessage, seconds, secondMessage));
        }));
        
        Look.registerLookType("fluid", (player -> {
            RayTraceResult rayTraceResult = player.rayTraceBlocks(100, FluidCollisionMode.ALWAYS);
            if (rayTraceResult == null) {
                sendErrorTranslation(player, "tport.command.look.type.fluid.notFound");
                return;
            }
            Block fluid = rayTraceResult.getHitBlock();
            if (fluid == null) {
                sendErrorTranslation(player, "tport.command.look.type.fluid.notFound");
                return;
            }
            if (!fluid.getType().equals(Material.WATER) && !fluid.getType().equals(Material.LAVA)) {
                sendErrorTranslation(player, "tport.command.look.type.fluid.notFound");
                return;
            }
            
            Location location = fluid.getLocation().add(0.5, 1.1, 0.5);
            location.setPitch(player.getLocation().getPitch());
            location.setYaw(player.getLocation().getYaw());
            TeleportHistory.setLocationSource(player.getUniqueId(), new LookLocationSource(fluid.getType()));
            TPEManager.requestTeleportPlayer(player, location, () -> sendSuccessTranslation(player, "tport.command.look.type.fluid.succeeded", fluid.getType().toString()),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.look.type.fluid.tpRequested", fluid.getType().toString(), delay, tickMessage, seconds, secondMessage));
        }));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport look [type]
        
        //types: 
        // - entity
        // - block
        // - fluid
        
        if (Features.Feature.LookTP.isDisabled())  {
            Features.Feature.LookTP.sendDisabledMessage(player);
            return;
        }
        
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
