package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.Back;
import com.spaceman.tport.commands.tport.Delay;
import com.spaceman.tport.commands.tport.edit.Move;
import com.spaceman.tport.commands.tport.pltp.Offset;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tpEvents.TPEManager;
import com.spaceman.tport.tpEvents.TPRestriction;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.*;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.TPortInventories.*;
import static com.spaceman.tport.commands.TPortCommand.executeInternal;
import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.tpEvents.TPEManager.registerTP;

public class InventoryClick implements Listener {
    
    public static final int TPortSize = 24;
    public static String NEXT = ChatColor.YELLOW + "Next";
    public static String PREVIOUS = ChatColor.YELLOW + "Previous";
    public static String BACK = ChatColor.YELLOW + "Back";
    public static String TPOFF = ChatColor.YELLOW + "Player tp is off";
    public static String WARP = ChatColor.YELLOW + "Warp to ";
    public static String OFFLINE = ChatColor.YELLOW + "Player is not online";
    
    public static void tpPlayerToPlayer(Player player, Player toPlayer, Runnable postMessage) {
        prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.PLAYER, "playerUUID", toPlayer.getUniqueId().toString(), "prevLoc", player.getLocation()));
        requestTeleportPlayer(player, Offset.getPLTPOffset(toPlayer).applyOffset(toPlayer.getLocation()), postMessage);
    }
    
    public static void tpPlayerToTPort(Player player, Location location, UUID tportUUID, String ownerUUID, Runnable postMessage) {
        prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.TPORT, "tportUUID", tportUUID.toString(), "tportOwner", ownerUUID,
                "prevLoc", player.getLocation()));
        requestTeleportPlayer(player, location, postMessage);
    }
    
    public static void requestTeleportPlayer(Player player, Location l, Runnable postMessage) {
        if (TPEManager.hasTPRequest(player.getUniqueId())) {
            Message message = new Message();
            message.addText(textComponent("You already have a tp request, click ", ColorTheme.ColorType.errorColor));
            message.addText(textComponent("here", ColorTheme.ColorType.varErrorColor,
                    new HoverEvent(textComponent("/tport cancel", ColorTheme.ColorType.varInfoColor)), ClickEvent.runCommand("/tport cancel")));
            message.addText(textComponent(" to cancel it", ColorTheme.ColorType.errorColor));
            message.sendMessage(player);
            return;
        }
        int delay = Delay.delayTime(player);
        if (delay == 0) {
            teleportPlayer(player, l);
        } else {
            TPRestriction tpRestriction = TPEManager.getTPRestriction(player.getUniqueId());
            if (tpRestriction == null) {
                registerTP(player.getUniqueId(),
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> teleportPlayer(Bukkit.getPlayer(player.getUniqueId()), l), delay).getTaskId());
            } else {
                tpRestriction.start(player, registerTP(player.getUniqueId(),
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                            if (tpRestriction.shouldTeleport(player)) {
                                teleportPlayer(Bukkit.getPlayer(player.getUniqueId()), l);
                                postMessage.run();
                            }
                        }, delay).getTaskId())
                );
            }
        }
    }
    
    private static void teleportPlayer(@Nullable Player player, Location l) {
        if (player == null) return;
        
        ArrayList<LivingEntity> slaves = new ArrayList<>();
        for (Entity e : player.getWorld().getEntities()) {
            if (e instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) e;
                if (livingEntity.isLeashed()) {
                    if (livingEntity.getLeashHolder() instanceof Player) {
                        if (livingEntity.getLeashHolder().getUniqueId().equals(player.getUniqueId())) {
                            slaves.add(livingEntity);
                            livingEntity.setLeashHolder(null);
                        }
                    }
                }
            }
        }
        
        LivingEntity horse = null;
        TreeSpecies boatType = null;
        Entity sailor = null;
        if (player.getVehicle() instanceof LivingEntity) {
            horse = (LivingEntity) player.getVehicle();
        } else if (player.getVehicle() instanceof Boat) {
            Boat b = (Boat) player.getVehicle();
            boatType = b.getWoodType();
            if (b.getPassengers().size() > 1) {
                sailor = b.getPassengers().get(1);
                sailor.leaveVehicle();
                sailor.teleport(l);
            }
            b.remove();
        }
        
        TPEManager.getOldLocAnimation(player.getUniqueId()).showIfEnabled(player, player.getLocation().clone());
        if (!player.getWorld().equals(l.getWorld())) {
            player.teleport(l);
        }
        player.teleport(l);
        TPEManager.removeTP(player.getUniqueId());
        TPEManager.getNewLocAnimation(player.getUniqueId()).showIfEnabled(player, l.clone());
        
        try {
            if (horse != null) {
                horse.teleport(player);
                horse.addPassenger(player);
            } else if (boatType != null) {
                Boat b = player.getWorld().spawn(player.getLocation(), Boat.class);
                b.setWoodType(boatType);
                b.teleport(player);
                b.addPassenger(player);
                if (sailor != null) {
                    sailor.teleport(player);
                    b.addPassenger(sailor);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        for (LivingEntity e : slaves) {
            try {
                e.teleport(player);
                e.setLeashHolder(player);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @SuppressWarnings("unused")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        
        if (!(inv.getHolder() instanceof TPortInventories)) {
            return;
        }
        TPortInventories tportInventories = (TPortInventories) inv.getHolder();
        String addendum = tportInventories.getAddendum();
        InventoryType inventoryType = tportInventories.getType();
        
        if (e.getRawSlot() > inv.getSize()) {
            return;
        }
        
        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        
        NamespacedKey runKey = new NamespacedKey(Main.getInstance(), Action.RIGHT_CLICK.getCode());
        if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {//right lick
            runKey = new NamespacedKey(Main.getInstance(), Action.RIGHT_CLICK.getCode());
        } else if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {//left click
            runKey = new NamespacedKey(Main.getInstance(), Action.LEFT_CLICK.getCode());
        } else if (e.getAction().equals(InventoryAction.CLONE_STACK) || e.getAction().equals(InventoryAction.UNKNOWN)) {//middle click
            runKey = new NamespacedKey(Main.getInstance(), Action.MIDDLE_CLICK.getCode());
        }
        if (pdc.has(runKey, PersistentDataType.STRING)) {
            e.setCancelled(true);
            TPortCommand.executeInternal(player, pdc.get(runKey, PersistentDataType.STRING));
    
            runKey = new NamespacedKey(Main.getInstance(), Action.SECONDARY_CLICK.getCode());
            if (pdc.has(runKey, PersistentDataType.STRING)) {
                TPortCommand.executeInternal(player, pdc.get(runKey, PersistentDataType.STRING));
            }
            return;
        }
        
        NamespacedKey pageKey = new NamespacedKey(Main.getInstance(), "PageNumber");
        if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {//right click
            pageKey = new NamespacedKey(Main.getInstance(), "PageNumberSkip");
        } else if (e.getAction().equals(InventoryAction.CLONE_STACK) || e.getAction().equals(InventoryAction.UNKNOWN)) {//middle click
            pageKey = new NamespacedKey(Main.getInstance(), "PageNumberEnd");
        }
        
        if (inventoryType.equals(InventoryType.MAIN)) {
            e.setCancelled(true);
            if (pdc.has(pageKey, PersistentDataType.INTEGER)) {
                List<ItemStack> list = tportInventories.getContent();
                //noinspection ConstantConditions
                openMainTPortGUI(player, pdc.get(pageKey, PersistentDataType.INTEGER), list, false);
            }
        }
        else if (inventoryType.equals(InventoryType.TPORT)) {
            e.setCancelled(true);
            
            UUID ownerUUID = PlayerUUID.getPlayerUUID(addendum);
    
            //quick edit
            if (player.getUniqueId().equals(ownerUUID)) {
                NamespacedKey tportUUIDKey = new NamespacedKey(Main.getInstance(), "TPortUUID");
                if (pdc.has(tportUUIDKey, PersistentDataType.STRING)) {
                    //noinspection ConstantConditions
                    TPort tport = TPortManager.getTPort(ownerUUID, UUID.fromString(pdc.get(tportUUIDKey, PersistentDataType.STRING)));
                    if (tport != null) {
                        Files tportData = getFile("TPortData");
            
                        if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                            QuickEditType.get(tportData.getConfig().getString("tport." + player.getUniqueId() + ".editState")).edit(tport, player);
                            openTPortGUI(player.getUniqueId(), player);
                        } else if (e.getAction().equals(InventoryAction.CLONE_STACK) || e.getAction().equals(InventoryAction.UNKNOWN)) {
                            QuickEditType type = QuickEditType.get(tportData.getConfig().getString("tport." + player.getUniqueId() + ".editState")).getNext();
                            QuickEditType.clearData(player.getUniqueId());
                            tportData.getConfig().set("tport." + player.getUniqueId() + ".editState", type.name());
                            tportData.saveConfig();
                            openTPortGUI(ownerUUID, player);
                        }
                    }
                }
            }
        }
        else if (inventoryType.equals(InventoryType.BIOME_TP)) {
            e.setCancelled(true);
            if (pdc.has(pageKey, PersistentDataType.INTEGER)) {
                //noinspection ConstantConditions
                openBiomeTP(player, pdc.get(pageKey, PersistentDataType.INTEGER));
            }
        }
        else if (inventoryType.equals(InventoryType.BIOME_TP_PRESETS)) {
            e.setCancelled(true);
            if (pdc.has(pageKey, PersistentDataType.INTEGER)) {
                //noinspection ConstantConditions
                openBiomeTPPreset(player, pdc.get(pageKey, PersistentDataType.INTEGER));
            }
        }
        else if (inventoryType.equals(InventoryType.FEATURE_TP)) {
            e.setCancelled(true);
            if (pdc.has(pageKey, PersistentDataType.INTEGER)) {
                //noinspection ConstantConditions
                openFeatureTP(player, pdc.get(pageKey, PersistentDataType.INTEGER));
            }
        }
        else if (inventoryType.equals(InventoryType.PUBLIC)) {
            e.setCancelled(true);
            
            if (pdc.has(pageKey, PersistentDataType.INTEGER)) {
                //noinspection ConstantConditions
                openPublicTPortGUI(player, pdc.get(pageKey, PersistentDataType.INTEGER));
                return;
            }
            
            NamespacedKey keyUUID = new NamespacedKey(Main.getInstance(), "TPortUUID");
            if (pdc.has(keyUUID, PersistentDataType.STRING) && e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                //noinspection ConstantConditions
                TPort tport = TPortManager.getTPort(UUID.fromString(pdc.get(keyUUID, PersistentDataType.STRING)));
                if (tport != null) {
                    if (quickEditPublicMoveList.containsKey(player.getUniqueId())) {
                        UUID otherTPortID = quickEditPublicMoveList.get(player.getUniqueId());
                        quickEditPublicMoveList.remove(player.getUniqueId());
                        if (!otherTPortID.equals(tport.getTportID())) {
                            TPort tmpTPort = TPortManager.getTPort(otherTPortID);
                            if (tmpTPort != null) {
                                executeInternal(player, new String[]{"public", "move", tmpTPort.getName(), tport.getName()});
                            }
                        }
                    } else {
                        if (!Move.emptySlot.hasPermissionToRun(player, false)) {
                            return;
                        }
                        quickEditPublicMoveList.put(player.getUniqueId(), tport.getTportID());
                    }
                    openPublicTPortGUI(player, tportInventories.getPage());
                }
            }
        }
        else if (inventoryType.equals(InventoryType.SEARCH)) {
            e.setCancelled(true);
            if (pdc.has(pageKey, PersistentDataType.INTEGER)) {
                //noinspection ConstantConditions
                openSearchGUI(player, pdc.get(pageKey, PersistentDataType.INTEGER), tportInventories);
            }
        }
    }
}
