package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.searchArea.Show;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class TeleporterEvents implements Listener {
    
    private boolean useTeleporter(Player player, ItemStack is) {
        if (hasPermission(player, true, true, "TPort.teleporter.use")) {
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                PersistentDataContainer dataContainer = im.getPersistentDataContainer();
                NamespacedKey keyCommand = new NamespacedKey(Main.getInstance(), "teleporterCommand");
                NamespacedKey keyTPortUUID = new NamespacedKey(Main.getInstance(), "teleporterTPortUUID");
                if (dataContainer.has(keyTPortUUID, PersistentDataType.STRING) && dataContainer.has(keyCommand, PersistentDataType.STRING)) {
                    TPort tport = TPortManager.getTPort(UUID.fromString(dataContainer.get(keyTPortUUID, PersistentDataType.STRING)));
                    if (tport != null) {
                        TPortCommand.executeInternal(player, dataContainer.get(keyCommand, PersistentDataType.STRING) + " " + tport.getName());
                    } else {
                        sendErrorTheme(player, "TPort of this teleporter does not exist anymore");
                    }
                    return true;
                }
                if (dataContainer.has(keyCommand, PersistentDataType.STRING)) {
                    TPortCommand.executeInternal(player, dataContainer.get(keyCommand, PersistentDataType.STRING));
                    return true;
                }
            }
        }
        return false;
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void interactEvent(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        
        if (entity instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame) entity;
            
            ItemStack is = itemFrame.getItem();
            if (!e.getPlayer().isSneaking()) {
                if (useTeleporter(e.getPlayer(), is)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void click(PlayerInteractEvent e) {
        if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && (e.getPlayer().isSneaking() || !isSpecialBlock(e.getClickedBlock()))))
                && EquipmentSlot.HAND.equals(e.getHand())) {
            
            ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
            if (useTeleporter(e.getPlayer(), is)) {
                e.setCancelled(true);
            } else if (is.getType().equals(Material.FILLED_MAP)) {
                MapMeta im = (MapMeta) is.getItemMeta();
                
                if (im != null) {
                    if (im.getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "PolygonMapRenderer"), PersistentDataType.STRING)) {
                        if (im.hasMapView()) {
                            Show.updatePolygonMap(is, e.getPlayer());
                            sendInfoTheme(e.getPlayer(), "Updated the map view to your SearchArea");
                        }
                    }
                }
            }
        }
    }
    
    private boolean isSpecialBlock(Block block) {
        if (block == null) {
            return false;
        }
        
        if (block.getState() instanceof InventoryHolder) {
            return true;
        }
        
        switch (block.getType()) {
            case ENCHANTING_TABLE:
            case CRAFTING_TABLE:
            case ENDER_CHEST:
            case ANVIL:
            case DAMAGED_ANVIL:
            case CHIPPED_ANVIL:
            case BEACON:
            case BREWING_STAND:
            case COMMAND_BLOCK:
            case CHAIN_COMMAND_BLOCK:
            case REPEATING_COMMAND_BLOCK:
            case FURNACE:
            case HOPPER:
            case CHEST:
            case TRAPPED_CHEST:
            case NOTE_BLOCK:
            case STRUCTURE_BLOCK:
            case COMPARATOR:
            case REPEATER:
            case LEVER:
            case DISPENSER:
            case DROPPER:
            case JUKEBOX:
            case DAYLIGHT_DETECTOR:
            case BLAST_FURNACE:
            case LOOM:
            case SMOKER:
            case CARTOGRAPHY_TABLE:
            case STONECUTTER:
            case GRINDSTONE:
            case BARREL:
            case JIGSAW:
            case LECTERN:
            case SMITHING_TABLE:
            case FLETCHING_TABLE:
                return true;
        }
        
        return block.getType().toString().endsWith("DOOR")
                || block.getType().toString().endsWith("BED")
                || block.getType().toString().endsWith("TRAPDOOR")
                || block.getType().toString().endsWith("BUTTON")
                || block.getType().toString().endsWith("SHULKER_BOX");
        
    }
}
