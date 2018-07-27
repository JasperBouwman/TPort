package com.spaceman.tport.events;

import com.spaceman.tport.commands.TPort;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CompassEvents implements Listener {


    public static void giveCompass(Player player) {
        ItemStack is = new ItemStack(Material.COMPASS);
        ItemMeta im = is.getItemMeta();

        im.setLore(Collections.singletonList(
                ChatColor.DARK_AQUA + "TPort Compass"));
        is.setItemMeta(im);

        player.getInventory().addItem(is);
    }

    public static void giveCompass(Player player, String tPortOwner) {
        ItemStack is = new ItemStack(Material.COMPASS);
        ItemMeta im = is.getItemMeta();

        im.setLore(Arrays.asList(
                ChatColor.DARK_AQUA + "TPort Compass",
                "",
                ChatColor.GRAY + "TPort Owner: " + tPortOwner));

        is.setItemMeta(im);

        player.getInventory().addItem(is);
    }

    public static void giveCompass(Player player, String tPortOwner, String tPort) {
        ItemStack is = new ItemStack(Material.COMPASS);
        ItemMeta im = is.getItemMeta();

        im.setLore(Arrays.asList(
                ChatColor.DARK_AQUA + "TPort Compass",
                "",
                ChatColor.GRAY + "TPort Owner: " + tPortOwner,
                ChatColor.GRAY + "TPort: " + tPort));

        is.setItemMeta(im);

        player.getInventory().addItem(is);
    }

    private boolean openCompass(Player player, ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {

            List<String> lore = is.getItemMeta().getLore();

            if (lore.isEmpty()) {
                return false;
            }

            if (!lore.get(0).equals(ChatColor.DARK_AQUA + "TPort Compass")) {
                return false;
            }

            if (lore.size() == 3) {
                TPort.open.run(new String[]{"open",
                                lore.get(2).replace(ChatColor.GRAY + "TPort Owner: ", "")},
                        player);
                return true;
            }
            if (lore.size() == 4) {
                TPort.open.run(new String[]{"open",
                                lore.get(2).replace(ChatColor.GRAY + "TPort Owner: ", ""),
                                lore.get(3).replace(ChatColor.GRAY + "TPort: ", "")},
                        player);
                return true;
            }

            new TPort().onCommand(player, null, null, new String[]{});
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

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) ||

                (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&

                        (!isSpecialBlock(e.getClickedBlock()) || e.getPlayer().isSneaking()))) {

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
                return true;
        }

        return block.getType().toString().endsWith("BED") || block.getType().toString().endsWith("TRAPDOOR") || block.getType().toString().endsWith("BUTTON");

    }

}
