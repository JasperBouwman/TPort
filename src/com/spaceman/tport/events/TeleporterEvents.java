package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
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

import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class TeleporterEvents implements Listener {
    
    public static boolean isTeleporter(ItemStack is) {
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            PersistentDataContainer dataContainer = im.getPersistentDataContainer();
            NamespacedKey keyCommand = new NamespacedKey(Main.getInstance(), "teleporterCommand");
            return dataContainer.has(keyCommand, STRING);
        }
        return false;
    }
    
    private boolean useTeleporter(Player player, ItemStack is) {
        if (isTeleporter(is)) {
            if (hasPermission(player, true, true, "TPort.teleporter.use")) {
                ItemMeta im = is.getItemMeta();
                if (im != null) {
                    PersistentDataContainer dataContainer = im.getPersistentDataContainer();
                    NamespacedKey keyCommand = new NamespacedKey(Main.getInstance(), "teleporterCommand");
                    NamespacedKey keyTPortUUID = new NamespacedKey(Main.getInstance(), "teleporterTPortUUID");
                    NamespacedKey keyPlayerUUID = new NamespacedKey(Main.getInstance(), "teleporterPlayerUUID");
                    if (dataContainer.has(keyCommand, STRING)) {
                        if (dataContainer.has(keyTPortUUID, STRING)) {
                            String uuid = dataContainer.get(keyTPortUUID, STRING);
                            assert uuid != null;
                            TPort tport = TPortManager.getTPort(UUID.fromString(uuid));
                            
                            if (tport != null) {
                                String command = dataContainer.get(keyCommand, STRING);
                                assert command != null;
                                if (command.contains(" ")) command = "open";
                                command += " " + PlayerUUID.getPlayerName(tport.getOwner());
                                TPortCommand.executeInternal(player, command + " " + tport.getName());
                            } else {
                                sendErrorTranslation(player, "tport.events.teleporterEvents.TPortNotExistAnymore", uuid);
                            }
                        } else if (dataContainer.has(keyPlayerUUID, STRING)) {
                            String command = dataContainer.get(keyCommand, STRING);
                            command += " " + PlayerUUID.getPlayerName(dataContainer.get(keyPlayerUUID, STRING));
                            TPortCommand.executeInternal(player, command);
                        } else {
                            TPortCommand.executeInternal(player, dataContainer.get(keyCommand, STRING));
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void interactEvent(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        
        if (entity instanceof ItemFrame itemFrame) {
    
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
    
        return switch (block.getType()) {
            case ENCHANTING_TABLE, CRAFTING_TABLE, ENDER_CHEST, ANVIL, DAMAGED_ANVIL,
                    CHIPPED_ANVIL, BEACON, BREWING_STAND, COMMAND_BLOCK, CHAIN_COMMAND_BLOCK,
                    REPEATING_COMMAND_BLOCK, FURNACE, HOPPER, CHEST, TRAPPED_CHEST, NOTE_BLOCK,
                    STRUCTURE_BLOCK, COMPARATOR, REPEATER, LEVER, DISPENSER, DROPPER, JUKEBOX,
                    DAYLIGHT_DETECTOR, BLAST_FURNACE, LOOM, SMOKER, CARTOGRAPHY_TABLE, STONECUTTER,
                    GRINDSTONE, BARREL, JIGSAW, LECTERN, SMITHING_TABLE, FLETCHING_TABLE -> true;
            default -> block.getType().toString().endsWith("DOOR")
                    || block.getType().toString().endsWith("BED")
                    || block.getType().toString().endsWith("TRAPDOOR")
                    || block.getType().toString().endsWith("BUTTON")
                    || block.getType().toString().endsWith("SHULKER_BOX");
        };
    
    }
}
