package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static com.spaceman.tport.commands.TPort.getHead;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class InventoryClick implements Listener {

    public static String BACK = "Back";
    public static String NEXT = ChatColor.DARK_AQUA + "Next";
    public static String TPOFF = "Player tp is off";
    public static String WARP = "Warp to ";
    public static String OFFLINE = "Player is not online";
    private static String PREVIOUS = ChatColor.DARK_AQUA + "Previous";

    @SuppressWarnings("deprecation")
    public static void teleportPlayer(Player player, Location l) {
        if (player.getVehicle() instanceof Horse) {
            Horse horseEntity = (Horse) player.getVehicle();
            player.teleport(l);
            horseEntity.teleport(l);
            horseEntity.setPassenger(player);
            return;
        }
        if (player.getVehicle() instanceof SkeletonHorse) {
            SkeletonHorse horseEntity = (SkeletonHorse) player.getVehicle();
            player.teleport(l);
            horseEntity.teleport(l);
            horseEntity.setPassenger(player);
            return;
        }
        if (player.getVehicle() instanceof ZombieHorse) {
            ZombieHorse horseEntity = (ZombieHorse) player.getVehicle();
            player.teleport(l);
            horseEntity.teleport(l);
            horseEntity.setPassenger(player);
            return;
        }
        if (player.getVehicle() instanceof Pig) {
            Pig horseEntity = (Pig) player.getVehicle();
            player.teleport(l);
            horseEntity.teleport(l);
            horseEntity.setPassenger(player);
            return;
        }
        if (player.getVehicle() instanceof Llama) {
            Llama horseEntity = (Llama) player.getVehicle();
            player.teleport(l);
            horseEntity.teleport(l);
            horseEntity.setPassenger(player);
            return;
        }
//        if (player.getVehicle() instanceof Boat) { //todo
//            Boat horseEntity = (Boat) player.getVehicle();
//            player.teleport(l);
//            horseEntity.teleport(l);
//            horseEntity.setPassenger(player);
//            return;
//        }


        player.teleport(l);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();

        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }
        if (meta.getDisplayName() == null) {
            return;
        }

        Files tportData = getFiles("TPortData");

        if (inv.getTitle().startsWith("Choose a player")) {

            if (e.getRawSlot() > inv.getSize()) {
                return;
            }

            String playerUUID = player.getUniqueId().toString();

            if (meta.getDisplayName().equals(NEXT)) {

                int b = tportData.getConfig().getInt("tport." + playerUUID + ".gui");
                int c = 0;
                b = b + 7;
                int i = 10;

                tportData.getConfig().set("tport." + playerUUID + ".gui", b);
                tportData.saveConfig();

                Inventory inve = Bukkit.createInventory(null, 45, "Choose a player (" + (b + 8) / 7 + ")");

                for (String ss : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {

                    if (!(c <= b)) {

                        tportData.getConfig().set("tport." + playerUUID + ".gui", b);

                        if (i >= 35) {
                            ItemStack items = new ItemStack(Material.HOPPER);
                            ItemMeta metas = items.getItemMeta();
                            metas.setDisplayName(NEXT);
                            items.setItemMeta(metas);
                            inve.setItem(44, items);

                            tportData.getConfig().set("tport." + playerUUID + ".gui", b);
                            tportData.saveConfig();
                            break;
                        }

                        if (i == 17 || i == 26) {
                            i = i + 2;
                        }
                        if (!(i == 44)) {
//                            inve.setItem(i, tportData.getConfig().getItemStack("tport." + ss + ".item"));
                            inve.setItem(i, getHead(UUID.fromString(ss)));
                            i++;

                            ItemStack itemsa = new ItemStack(Material.TALL_GRASS, 1, (byte) 2);
                            ItemMeta metasa = itemsa.getItemMeta();
                            metasa.setDisplayName(PREVIOUS);
                            itemsa.setItemMeta(metasa);
                            inve.setItem(8, itemsa);

                        }
                    } else {
                        c++;
                    }
                }
                player.openInventory(inve);

            }

            if (meta.getDisplayName().equals(PREVIOUS)) {
                int b = tportData.getConfig().getInt("tport." + playerUUID + ".gui");
                int c = 0;
                b = b - 7;
                int i = 10;
                Inventory inve = Bukkit.createInventory(null, 45, "Choose a player (" + (b + 8) / 7 + ")");

                for (String ss : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {

                    if (!(c <= b)) {

                        if (!(b == -1)) {
                            ItemStack itemsa = new ItemStack(Material.TALL_GRASS, 1, (byte) 2);
                            ItemMeta metasa = itemsa.getItemMeta();
                            metasa.setDisplayName(PREVIOUS);
                            itemsa.setItemMeta(metasa);
                            inve.setItem(8, itemsa);
                        }

                        if (i >= 35) {
                            ItemStack items = new ItemStack(Material.HOPPER);
                            ItemMeta metas = items.getItemMeta();
                            metas.setDisplayName(NEXT);
                            items.setItemMeta(metas);
                            inve.setItem(44, items);

                            tportData.getConfig().set("tport." + playerUUID + ".gui", b);
                            tportData.saveConfig();
                            break;
                        }

                        if (i == 17 || i == 26) {
                            i = i + 2;
                        }
                        if (!(i == 44)) {
//                            inve.setItem(i, tportData.getConfig().getItemStack("tport." + ss + ".item"));
                            inve.setItem(i, getHead(UUID.fromString(ss)));
                            i++;

                        }
                    } else {
                        c++;
                    }
                }
                player.openInventory(inve);

            }

            Inventory invc = Bukkit.createInventory(null, 9, "TPort: " + meta.getDisplayName());

            for (String s : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {

                if (meta.getDisplayName().equals(PlayerUUID.getPlayerName(s))) {

                    for (int i = 0; i < 7; i++) {

                        if (s.equals(playerUUID)) {
                            invc.setItem(i, tportData.getConfig().getItemStack("tport." + s + ".items." + i + ".item"));
                        } else if (tportData.getConfig().contains("tport." + s + ".items." + i)) {

                            if (tportData.getConfig().getString("tport." + s + ".items." + i + ".private.statement")
                                    .equals("false")) {

                                invc.setItem(i, tportData.getConfig().getItemStack("tport." + s + ".items." + i + ".item"));

                            } else if (tportData.getConfig().getString("tport." + s + ".items." + i + ".private.statement")
                                    .equals("true")) {

                                ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                        .getStringList("tport." + s + ".items." + i + ".private.players");
                                if (list.contains(player.getName())) {
                                    invc.setItem(i, tportData.getConfig().getItemStack("tport." + s + ".items." + i + ".item"));
                                }
                            }
                        }
                        ItemStack back = new ItemStack(Material.BARRIER);
                        ItemMeta metaback = back.getItemMeta();
                        metaback.setDisplayName(BACK);
                        back.setItemMeta(metaback);
                        invc.setItem(8, back);

                        ItemStack warp = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta skin = (SkullMeta) warp.getItemMeta();
                        skin.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(s)));
                        String newPlayerName = PlayerUUID.getPlayerName(s);

                        if (tportData.getConfig().getString("tport." + s + ".tp.statement").equals("off")) {

                            ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                    .getStringList("tport." + s + ".tp.players");

                            if (!list.contains(playerUUID)) {
                                skin.setDisplayName(TPOFF);
                            } else {
                                skin.setDisplayName(WARP + PlayerUUID.getPlayerName(s));
                            }
                        } else if (Bukkit.getPlayer(UUID.fromString(s)) != null) {
                            skin.setDisplayName(WARP + PlayerUUID.getPlayerName(s));
                        } else {
                            skin.setDisplayName(OFFLINE);
                        }
                        warp.setItemMeta(skin);
                        invc.setItem(7, warp);

                    }
                    e.setCancelled(true);
                    player.openInventory(invc);

                } else {
                    e.setCancelled(true);
                }
            }

        } else if (inv.getTitle().startsWith("TPort: ")) {

            for (String s : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {

                if (inv.getTitle().equals("TPort: " + PlayerUUID.getPlayerName(s))) {

                    if (e.getRawSlot() > inv.getSize()) {
                        return;
                    }

                    String playerUUID = player.getUniqueId().toString();

                    if (item.getType().equals(Material.BARRIER)) {
                        if (meta.getDisplayName().equals(BACK)) {
                            if (e.getSlot() == 8) {

                                int b = tportData.getConfig().getInt("tport." + playerUUID + ".gui");
                                int c = 0;
                                int i = 10;

                                int size = 45;
                                Set l = tportData.getConfig().getConfigurationSection("tport").getKeys(false);
                                if (l.size() < 8) {
                                    size = 27;
                                } else if (l.size() < 15) {
                                    size = 36;
                                }

                                Inventory inve = Bukkit.createInventory(null, size,
                                        "Choose a player (" + (b + 8) / 7 + ")");

                                for (String ss : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {

                                    if (!(c <= b)) {

                                        if (i >= 35) {
                                            ItemStack items = new ItemStack(Material.HOPPER);
                                            ItemMeta metas = items.getItemMeta();
                                            metas.setDisplayName(NEXT);
                                            items.setItemMeta(metas);
                                            inve.setItem(44, items);

                                            tportData.getConfig().set("tport." + playerUUID + ".gui", b);
                                            tportData.saveConfig();
                                            break;
                                        }

                                        if (i == 17 || i == 26) {
                                            i = i + 2;
                                        }
                                        if (!(i == 44)) {
//                                            inve.setItem(i, tportData.getConfig().getItemStack("tport." + ss + ".item"));
                                            inve.setItem(i, getHead(UUID.fromString(ss)));
                                            i++;

                                            if (!tportData.getConfig().get("tport." + playerUUID + ".gui").equals(-1)) {

                                                ItemStack itemsa = new ItemStack(Material.TALL_GRASS, 1, (byte) 2);
                                                ItemMeta metasa = itemsa.getItemMeta();
                                                metasa.setDisplayName(PREVIOUS);
                                                itemsa.setItemMeta(metasa);
                                                inve.setItem(8, itemsa);
                                            }
                                        }
                                    } else {
                                        c++;
                                    }
                                }
                                player.openInventory(inve);

                            } else {
                                e.setCancelled(true);
                            }
                        }
                    }

                    if (item.getType().equals(Material.PLAYER_HEAD)) {
                        if (meta.getDisplayName().equals(OFFLINE) || meta.getDisplayName().equals(TPOFF)) {

                            if (Bukkit.getPlayerExact(inv.getTitle().replaceAll("TPort:", "").trim()) != null) {
                                Player warp = Bukkit.getPlayerExact(inv.getTitle().replaceAll("TPort:", "").trim());
//                                player.teleport(warp.getLocation());
                                teleportPlayer(player, warp.getLocation());
                                player.sendMessage("§3Teleported to §9" + warp.getName());
                            }

                            e.setCancelled(true);
                        } else if (meta.getDisplayName().equals(WARP + PlayerUUID.getPlayerName(s))) {
                            if (Bukkit.getPlayer(UUID.fromString(s)) == null) {

                                Inventory invc = Bukkit.createInventory(null, 9, "Tport: " + PlayerUUID.getPlayerName(s));
                                for (int i = 0; i < 7; i++) {

                                    if (s.equals(playerUUID)) {
                                        invc.setItem(i,
                                                tportData.getConfig().getItemStack("tport." + s + ".items." + i + ".item"));
                                    } else if (tportData.getConfig().contains("tport." + s + ".items." + i)) {

                                        if (tportData.getConfig().getString("tport." + s + ".items." + i + ".private.statement")
                                                .equals("false")) {

                                            invc.setItem(i,
                                                    tportData.getConfig().getItemStack("tport." + s + ".items." + i + ".item"));

                                        } else if (tportData.getConfig()
                                                .getString("tport." + s + ".items." + i + ".private.statement")
                                                .equals("true")) {

                                            ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                                    .getStringList("tport." + s + ".items." + i + ".private.players");
                                            if (list.contains(player.getName())) {
                                                invc.setItem(i, tportData.getConfig()
                                                        .getItemStack("tport." + s + ".items." + i + ".item"));
                                            }
                                        }
                                    }
                                    ItemStack back = new ItemStack(Material.BARRIER);
                                    ItemMeta metaback = back.getItemMeta();
                                    metaback.setDisplayName(BACK);
                                    back.setItemMeta(metaback);
                                    invc.setItem(8, back);

                                    ItemStack warp = new ItemStack(Material.PLAYER_HEAD);
                                    SkullMeta skin = (SkullMeta) warp.getItemMeta();
                                    skin.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(s)));

                                    if (Bukkit.getPlayerExact(s) != null) {
                                        skin.setDisplayName(WARP + PlayerUUID.getPlayerName(s));
                                    } else {
                                        skin.setDisplayName(OFFLINE);
                                    }
                                    warp.setItemMeta(skin);
                                    invc.setItem(7, warp);

                                }
                                e.setCancelled(true);
                                player.openInventory(invc);

                            } else {
                                Player warp = Bukkit.getPlayer(UUID.fromString(s));
                                e.setCancelled(true);
//                                player.teleport(warp.getLocation());
                                teleportPlayer(player, warp.getLocation());
                                player.sendMessage("§3Teleported to §9" + warp.getName());
                            }
                        }
                    }

                    for (int i = 0; i < 7; i++) {
                        if (tportData.getConfig().contains("tport." + s + ".items." + i + ".item")) {
                            ItemStack items = tportData.getConfig().getItemStack("tport." + s + ".items." + i + ".item");
                            if (meta.getDisplayName().equals(items.getItemMeta().getDisplayName())) {
                                if (e.getSlot() == 0 || e.getSlot() == 1 || e.getSlot() == 2 || e.getSlot() == 3
                                        || e.getSlot() == 4 || e.getSlot() == 5 || e.getSlot() == 6) {
                                    e.setCancelled(true);
                                    player.closeInventory();

                                    Location l = Main.getLocation("tport." + s + ".items." + i + ".location");

                                    if (l == null) {
                                        player.sendMessage("§cThe world for this location is not found");
                                        return;
                                    }
                                    teleportPlayer(player, l);

                                    Message message = new Message();
                                    message.addText("Teleported to ", ChatColor.DARK_AQUA);
                                    message.addText(textComponent(items.getItemMeta().getDisplayName(),
                                            ChatColor.BLUE, ClickEvent.runCommand("/tport open " + PlayerUUID.getPlayerName(s) + " " + items.getItemMeta().getDisplayName())));
                                    message.sendMessage(player);
                                } else {
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}