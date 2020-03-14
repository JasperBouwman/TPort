package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.TPortCommand;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class TeleporterEvents implements Listener {
    
    private boolean useTeleporter(Player player, ItemStack is) {
        if (hasPermission(player, true, true, "TPort.teleporter.use")) {
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                PersistentDataContainer dataContainer = im.getPersistentDataContainer();
                NamespacedKey keyCommand = new NamespacedKey(Main.getInstance(), "teleporterCommand");
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
        
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && (e.getPlayer().isSneaking() || !isSpecialBlock(e.getClickedBlock())))
                && EquipmentSlot.OFF_HAND.equals(e.getHand())) {
            
            ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
            if (useTeleporter(e.getPlayer(), is)) {
                e.setCancelled(true);
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
            case OAK_DOOR:
            case BIRCH_DOOR:
            case ACACIA_DOOR:
            case JUNGLE_DOOR:
            case SPRUCE_DOOR:
            case DARK_OAK_DOOR:
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
                //since 1.14
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
        
        return block.getType().toString().endsWith("BED") || block.getType().toString().endsWith("TRAPDOOR") || block.getType().toString().endsWith("BUTTON") || block.getType().toString().endsWith("SHULKER_BOX");
        
    }
}
