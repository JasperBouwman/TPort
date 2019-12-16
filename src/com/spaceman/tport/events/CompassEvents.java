package com.spaceman.tport.events;

import com.spaceman.tport.commands.TPortCommand;
import org.bukkit.ChatColor;
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

import java.util.List;

import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class CompassEvents implements Listener {
    
    private boolean openCompass(Player player, ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            
            List<String> lore = is.getItemMeta().getLore();
            
            if (lore.isEmpty()) {
                return false;
            }

            
            if (!lore.get(0).equals(ChatColor.DARK_AQUA + "TPort Compass")) {
                return false;
            }
    
            if (!hasPermission(player, true, "TPort.compass.use")) {
                return false;
            }
            
            if (lore.size() == 3) {
                switch (lore.get(2).split("Type: ")[1]) {
                    case "biomeTP":
                        TPortCommand.executeInternal(player, "BiomeTP");
                        break;
                    case "featureTP":
                        TPortCommand.executeInternal(player, "FeatureTP");
                        break;
                    case "back":
                        TPortCommand.executeInternal(player, "back");
                        break;
                    case "home":
                        TPortCommand.executeInternal(player, "home");
                        break;
                    case "Public":
                        TPortCommand.executeInternal(player, "public");
                        break;
                    case "TPort":
                    default:
                        TPortCommand.executeInternal(player, "");
                        break;
                }
            } else if (lore.size() == 4) {
                switch (lore.get(2).split("Type: ")[1]) {
                    case "TPort":
                        TPortCommand.executeInternal(player, new String[]{"open", lore.get(3).split("Player: ")[1]});
                        break;
                    case "biomeTP":
                        TPortCommand.executeInternal(player, new String[]{"BiomeTP", lore.get(3).split("Biome: ")[1]});
                        break;
                    case "featureTP":
                        TPortCommand.executeInternal(player, new String[]{"FeatureTP", lore.get(3).split("Feature: ")[1]});
                        break;
                    case "PLTP":
                        TPortCommand.executeInternal(player, new String[]{"PLTP", "tp", lore.get(3).split("Player: ")[1]});
						break;
                    case "Public":
                        TPortCommand.executeInternal(player, new String[]{"public", "open", lore.get(3).split("Public TPort: ")[1]});
                        break;
                    default:
                        TPortCommand.executeInternal(player, "");
                        break;
                }
            } else if (lore.size() == 5) {
                if ("TPort".equals(lore.get(2).split("Type: ")[1])) {
                    TPortCommand.executeInternal(player, new String[]{"open", lore.get(3).split("Player: ")[1], lore.get(4).split("TPort: ")[1]});
                }
            }
            
            return true;
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
                if (openCompass(e.getPlayer(), is)) {
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
            if (openCompass(e.getPlayer(), is)) {
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
