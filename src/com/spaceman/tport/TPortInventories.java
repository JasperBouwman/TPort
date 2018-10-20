package com.spaceman.tport;

import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

import static com.spaceman.tport.commands.TPort.getHead;
import static com.spaceman.tport.commands.tport.Back.prevTPort;
import static com.spaceman.tport.events.InventoryClick.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class TPortInventories {

    public static HashMap<UUID, Integer> mainTPortGUIPage = new HashMap<>();

    private static ItemStack toTPortItem(String path) {
        Files tportData = getFiles("TPortData");
        ItemStack is = tportData.getConfig().getItemStack(path + ".item").clone();
        ItemMeta im = is.getItemMeta();

        im.setDisplayName(tportData.getConfig().getString(path + ".name"));

        List<String> lore = new ArrayList<>();
        if (im.hasLore()) {
            for (String s : im.getLore()) {
                lore.add(ChatColor.BLUE + s);
            }
            lore.add("");
        }
        switch (tportData.getConfig().getString(path + ".private.statement")) {
            case "off":
                lore.add(ChatColor.GRAY + "Private: " + ChatColor.RED + "off");
                break;
            case "on":
                lore.add(ChatColor.GRAY + "Private: " + ChatColor.GREEN + "on");
                break;
            case "online":
                lore.add(ChatColor.GRAY + "Private: " + ChatColor.YELLOW + "online");
                break;
        }
        im.setLore(lore);

        is.setItemMeta(im);
        return is;
    }

    private static ItemStack createStack(String displayName, Material material) {
        ItemStack is = new ItemStack(material);

        ItemMeta im = is.getItemMeta();
        im.setDisplayName(displayName);
        is.setItemMeta(im);

        return is;
    }

    public static void openTPortGUI(String newPlayerName, String newPlayerUUID, Player player) {

        Validate.notNull(newPlayerName, "The newPlayerName can not be null");
        Validate.notNull(newPlayerUUID, "The newPlayerUUID can not be null");
        Validate.notNull(player, "The player can not be null");

        Files tportData = getFiles("TPortData");
        Inventory inv = Bukkit.createInventory(null, 27, "TPort: " + newPlayerName);
        int slotOffset = 0;
        for (int i = 0; i < TPortSize + 1; i++) {

            if (i == 8 || i == 17) {
                slotOffset++;
                continue;
            }

//            if (newPlayerUUID.equals(player.getUniqueId().toString())) {
//                if (tportData.getConfig().contains("tport." + newPlayerUUID + ".items." + (i - slotOffset) + ".item")) {
////                    inv.setItem(i, tportData.getConfig().getItemStack("tport." + newPlayerUUID + ".items." + (i - slotOffset) + ".item"));
//                    inv.setItem(i, toTPortItem("tport." + newPlayerUUID + ".items." + (i - slotOffset)));
//                }
//            } else if (tportData.getConfig().contains("tport." + newPlayerUUID + ".items." + (i - slotOffset))) {
//                switch (tportData.getConfig().getString("tport." + newPlayerUUID + ".items." + (i - slotOffset) + ".private.statement")) {
//                    case "off":
//                        inv.setItem(i, toTPortItem("tport." + newPlayerUUID + ".items." + (i - slotOffset)));
//                        break;
//                    case "on":
//                        ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
//                                .getStringList("tport." + newPlayerUUID + ".items." + (i - slotOffset) + ".private.players");
//                        if (list.contains(player.getUniqueId().toString())) {
//                            inv.setItem(i, toTPortItem("tport." + newPlayerUUID + ".items." + (i - slotOffset)));
//                        }
//                        break;
//                    case "online":
//                        if (Bukkit.getPlayer(UUID.fromString(newPlayerUUID)) != null) {
//                            inv.setItem(i, toTPortItem("tport." + newPlayerUUID + ".items." + (i - slotOffset)));
//                        }
//                        break;
//                }
//            }

            if (tportData.getConfig().contains("tport." + newPlayerUUID + ".items." + (i - slotOffset) + ".item")) {
                inv.setItem(i, toTPortItem("tport." + newPlayerUUID + ".items." + (i - slotOffset)));
            }

            ItemStack back = new ItemStack(Material.BARRIER);
            ItemMeta metaBack = back.getItemMeta();
            metaBack.setDisplayName(BACK);
            back.setItemMeta(metaBack);
            inv.setItem(26, back);

            ItemStack extraTP = new ItemStack(Material.ELYTRA);
            ItemMeta metaExtraTP = extraTP.getItemMeta();

            ArrayList<String> extraTPLore = new ArrayList<>();
            extraTPLore.add(ChatColor.DARK_AQUA + "Left-Click:");
            extraTPLore.add(ChatColor.BLUE + "Previous location: " +
                    (prevTPort.containsKey(player.getUniqueId()) ? (prevTPort.get(player.getUniqueId()).getL() == null ? "to " : "from ") +
                            (prevTPort.get(player.getUniqueId()).getTportName() != null
                                    ? prevTPort.get(player.getUniqueId()).getTportName() : PlayerUUID.getPlayerName(prevTPort.get(player.getUniqueId()).getToPlayerUUID())) : "Unknown"));
            extraTPLore.add("");
            extraTPLore.add(ChatColor.DARK_AQUA + "Right-Click:");
            extraTPLore.add(ChatColor.BLUE + "BiomeTP");
            extraTPLore.add("");
            extraTPLore.add(ChatColor.DARK_AQUA + "Middle-Click:");
            extraTPLore.add(ChatColor.BLUE + "FeatureTP");
            metaExtraTP.setLore(extraTPLore);
            metaExtraTP.setDisplayName(ChatColor.YELLOW + "Extra TP features");

            extraTP.setItemMeta(metaExtraTP);
            inv.setItem(17, extraTP);

            if (newPlayerUUID.equals(player.getUniqueId().toString())) {
                ItemStack warp = new ItemStack(Material.PLAYER_HEAD);

                if (tportData.getConfig().getString("tport." + newPlayerUUID + ".tp.statement").equals("off")) {
                    warp.setItemMeta(SET_TP_ON);
                } else {
                    warp.setItemMeta(SET_TP_OFF);
                }
                SkullMeta skin = (SkullMeta) warp.getItemMeta();
                skin.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(newPlayerUUID)));
                warp.setItemMeta(skin);
                inv.setItem(8, warp);
            } else {
                ItemStack warp = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skin = (SkullMeta) warp.getItemMeta();
                skin.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(newPlayerUUID)));

                if (tportData.getConfig().getString("tport." + newPlayerUUID + ".tp.statement").equals("off")) {

                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                            .getStringList("tport." + newPlayerUUID + "tp.players");

                    if (list.contains(player.getUniqueId().toString())) {
                        skin.setDisplayName(WARP + PlayerUUID.getPlayerName(newPlayerUUID));
                    } else {
                        skin.setDisplayName(TPOFF);
                    }
                } else if (Bukkit.getPlayer(UUID.fromString(newPlayerUUID)) != null) {
                    skin.setDisplayName(WARP + PlayerUUID.getPlayerName(newPlayerUUID));
                } else {
                    skin.setDisplayName(OFFLINE);
                }
                warp.setItemMeta(skin);
                inv.setItem(8, warp);
            }
        }
        player.openInventory(inv);
    }

    public static void openBiomeTP(Player player, int page) {

        int x = Biome.values().length;
        int y = 3;
        while (x % 7 != 0) {
            x++;
        }
        x += ((x + (9 - x % 9)) / 9) * 2;

        if (x > y * 7) {
            x = y * 9;
        }
        x += 18;

        Inventory inv = Bukkit.createInventory(null, x, "Select a Biome (" + (page + 1) + ")");
        inv.setItem(18, createStack(ChatColor.YELLOW + "Random", Material.ELYTRA));
        inv.setItem(26, createStack(BACK, Material.BARRIER));

        int a = page * 7;
        int i = 10;

        for (Biome biome : Biome.values()) {

            if (a != 0) {
                a--;
                continue;
            }

            if ((i + 1) % 9 == 0) {
                i += 2;
            }
            if (i >= x - 9) {
                break;
            }

            String b = biome.toString();
            if (b.contains("SNOWY")) {
                inv.setItem(i, createStack(b, Material.SNOW));
            } else if (b.contains("FROZEN")) {
                inv.setItem(i, createStack(b, Material.ICE));
            } //override biome
            else if (b.contains("BADLANDS") || b.equalsIgnoreCase("BADLANDS")) {
                inv.setItem(i, createStack(b, Material.TERRACOTTA));
            } else if (b.equalsIgnoreCase("BEACH") || b.contains("DESERT") || b.equalsIgnoreCase("DESERT")) {
                inv.setItem(i, createStack(b, Material.SAND));
            } else if (b.contains("BIRCH")) {
                inv.setItem(i, createStack(b, Material.BIRCH_LOG));
            } else if (b.contains("OCEAN") || b.equalsIgnoreCase("OCEAN") || b.equalsIgnoreCase("RIVER")) { // WARM!LUKEWARM
                if (b.contains("WARM") && !b.contains("LUKEWARM")) {
                    inv.setItem(i, createStack(b, Material.BRAIN_CORAL));
                } else {
                    inv.setItem(i, createStack(b, Material.WATER_BUCKET));
                }
            } else if (b.contains("DARK_FOREST") || b.equalsIgnoreCase("DARK_FOREST")) {
                inv.setItem(i, createStack(b, Material.DARK_OAK_LOG));
            } else if (b.contains("END")) {
                inv.setItem(i, createStack(b, Material.END_STONE));
            } else if (b.equalsIgnoreCase("SUNFLOWER_PLAINS")) {
                inv.setItem(i, createStack(b, Material.SUNFLOWER));
            } else if (b.equalsIgnoreCase("FLOWER_FOREST")) {
                inv.setItem(i, createStack(b, Material.ROSE_BUSH));
            } else if (b.equalsIgnoreCase("FOREST") || b.equalsIgnoreCase("WOODED_HILLS")) {
                inv.setItem(i, createStack(b, Material.OAK_LOG));
            } else if (b.contains("TAIGA") || b.equalsIgnoreCase("TAIGA")) {
                inv.setItem(i, createStack(b, Material.SPRUCE_LOG));
            } else if (b.contains("GRAVELLY")) {
                inv.setItem(i, createStack(b, Material.GRAVEL));
            } else if (b.contains("ICE")) {
                inv.setItem(i, createStack(b, Material.PACKED_ICE));
            } else if (b.contains("JUNGLE") || b.equalsIgnoreCase("JUNGLE")) {
                inv.setItem(i, createStack(b, Material.JUNGLE_LOG));
            } else if (b.equalsIgnoreCase("MOUNTAIN_EDGE") || b.equalsIgnoreCase("PLAINS")) {
                inv.setItem(i, createStack(b, Material.GRASS_BLOCK));
            } else if (b.contains("MUSHROOM")) {
                inv.setItem(i, createStack(b, Material.RED_MUSHROOM_BLOCK));
            } else if (b.contains("NETHER") || b.equalsIgnoreCase("NETHER")) {
                inv.setItem(i, createStack(b, Material.NETHERRACK));
            } else if (b.contains("SAVANNA") || b.equalsIgnoreCase("SAVANNA")) {
                inv.setItem(i, createStack(b, Material.ACACIA_LOG));
            } else if (b.contains("STONE") || b.equalsIgnoreCase("WOODED_MOUNTAINS") || b.equalsIgnoreCase("MOUNTAINS")) {
                inv.setItem(i, createStack(b, Material.STONE));
            } else if (b.contains("SWAMP") || b.equalsIgnoreCase("SWAMP")) {
                inv.setItem(i, createStack(b, Material.VINE));
            } else if (b.equalsIgnoreCase("THE_VOID")) {
                inv.setItem(i, createStack(b, Material.BARRIER));
            } else {
                inv.setItem(i, createStack(b, Material.DIAMOND_BLOCK));
            }

            i++;
        }

        if ((page + y) * 7 < Biome.values().length) {
            inv.setItem(x - 1, createStack(NEXT, Material.HOPPER));
        }
        if (page != 0) {
            inv.setItem(8, createStack(PREVIOUS, Material.FERN));
        }

        player.openInventory(inv);

    }

    public static void openFeatureTP(Player player, int page) {
        int x = FeaturesTypes.values().length;
        int y = 3;
        while (x % 7 != 0) {
            x++;
        }
        x += ((x + (9 - x % 9)) / 9) * 2;

        if (x > y * 7) {
            x = y * 9;
        }
        x += 18;

        Inventory inv = Bukkit.createInventory(null, x, "Select a Feature (" + (page + 1) + ")");
        inv.setItem(26, createStack(BACK, Material.BARRIER));

        int a = page * 7;
        int i = 10;

        for (FeaturesTypes feature : FeaturesTypes.values()) {

            if (a != 0) {
                a--;
                continue;
            }

            if ((i + 1) % 9 == 0) {
                i += 2;
            }
            if (i >= x - 9) {
                break;
            }

            String b = feature.toString();
            inv.setItem(i, createStack(b, feature.getItemStack().getType()));

            i++;
        }

        if ((page + y) * 7 < FeaturesTypes.values().length) {
            inv.setItem(x - 1, createStack(NEXT, Material.HOPPER));
        }
        if (page != 0) {
            inv.setItem(8, createStack(PREVIOUS, Material.FERN));
        }

        player.openInventory(inv);

    }

    public static void openMainTPortGUI(Player player, int page) {
        Files tportData = getFiles("TPortData");
        Set<String> l = tportData.getConfig().getConfigurationSection("tport").getKeys(false);

        int y = 3;
        int x = l.size();
        while (x % 7 != 0) {
            x++;
        }
        x += ((x + (9 - x % 9)) / 9) * 2;

        if (x > y * 7) {
            x = y * 9;
        }
        x += 18;

        if (page == 0) {
            l.remove(player.getUniqueId().toString());
        }

        Inventory inv = Bukkit.createInventory(null, x, "Choose a player (" + (page + 1) + ")");

        int a = page * 7;
        int i = 10;

        if (a == 0) {
            inv.setItem(i, getHead(player));
            i++;
        }

        for (String s : l) {
            if (a != 0) {
                a--;
                continue;
            }

            if ((i + 1) % 9 == 0) {
                i += 2;
            }
            if (i >= x - 9) {
                break;
            }
            inv.setItem(i, getHead(UUID.fromString(s)));
            i++;
        }

        if ((page + y) * 7 < (l.size() + 1)) {
            inv.setItem(x - 1, createStack(NEXT, Material.HOPPER));
        }
        if (page != 0) {
            inv.setItem(8, createStack(PREVIOUS, Material.FERN));
        }

        player.openInventory(inv);

    }

    public enum FeaturesTypes {
        Buried_Treasure(new ItemStack(Material.CHEST)),
        Desert_Pyramid(new ItemStack(Material.SAND)),
        EndCity(new ItemStack(Material.END_STONE)),
        Fortress(new ItemStack(Material.NETHER_BRICK)),
        Igloo(new ItemStack(Material.SNOW_BLOCK)),
        Jungle_Pyramid(new ItemStack(Material.MOSSY_COBBLESTONE)),
        Mansion(new ItemStack(Material.DARK_OAK_WOOD)),
        Minecraft(new ItemStack(Material.STONE)),
        Monument(new ItemStack(Material.PRISMARINE_BRICKS)),
        Ocean_Ruin(new ItemStack(Material.WATER_BUCKET)),
        Shipwreck(new ItemStack(Material.OAK_BOAT)),
        Stronghold(new ItemStack(Material.END_PORTAL_FRAME)),
        Swamp_Hut(new ItemStack(Material.VINE)),
        Village(new ItemStack(Material.OAK_PLANKS));

        private ItemStack itemStack;

        FeaturesTypes(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }
    }
}
