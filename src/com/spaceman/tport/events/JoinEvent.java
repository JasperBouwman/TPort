package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class JoinEvent implements Listener {

    private Main p;

    public JoinEvent(Main instance) {
        p = instance;
    }

    public static void setData(Main p, Player player) {

        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        if (!tportData.getConfig().contains("tport." + playerUUID)) {
            if (p.getConfig().contains("tport." + player.getName())) {
                tportData.getConfig().set("tport." + playerUUID, p.getConfig().get("tport." + player.getName()));

                for (String item : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
                    try {
                        if (tportData.getConfig().contains("tport." + playerUUID + ".items." + item + ".item")) {

                            ArrayList<String> tmpList = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".items." + item + ".private.players");
                            ArrayList<String> newList = new ArrayList<>();
                            ArrayList<String> list = new ArrayList<>();

                            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                                if (tmpList.contains(offlinePlayer.getName())) {
                                    newList.add(offlinePlayer.getUniqueId().toString());
                                } else {
                                    list.add(offlinePlayer.getName());
                                }
                            }
                            ItemStack tmpItem = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + item + ".item"); //get item
                            ItemMeta tmpMeta = tmpItem.getItemMeta();

                            if (!list.isEmpty()) {
                                if (tmpItem.hasItemMeta() && tmpItem.getItemMeta().hasDisplayName()) { //test if valid item

                                    Message message = new Message();
                                    message.addText(textComponent("The Whitelist of your TPort ", ChatColor.RED));

                                    message.addText(tmpItem.getItemMeta().getDisplayName(), ChatColor.DARK_RED);
                                    message.addText(" has some names in the  whitelist that couldn't be recognized by the server and will be removed from your whitelist. This list is as followed:");
                                    boolean b = true;
                                    for (String playerName : list) {
                                        message.addWhiteSpace();
                                        if (b) {
                                            message.addText(playerName, ChatColor.BLUE);
                                            b = false;
                                        } else {
                                            message.addText(playerName, ChatColor.DARK_BLUE);
                                            b = true;
                                        }
                                    }
                                    message.sendMessage(player);
                                }
                            }

                            tportData.getConfig().set("tport." + playerUUID + ".items." + item + ".name", tmpMeta.getDisplayName());
                            tmpMeta.setDisplayName(null);
                            tmpItem.setItemMeta(tmpMeta);
                            tportData.getConfig().set("tport." + playerUUID + ".items." + item + ".item", tmpItem);

                            if (tportData.getConfig().getBoolean("tport." + playerUUID + ".items." + item + ".private.statement")) {
                                tportData.getConfig().set("tport." + playerUUID + ".items." + item + ".private.statement", "on");
                                tportData.saveConfig();
                            } else {
                                tportData.getConfig().set("tport." + playerUUID + ".items." + item + ".private.statement", "off");
                                tportData.saveConfig();
                            }


                            tportData.getConfig().set("tport." + playerUUID + ".items." + item + ".private.players", newList);
                        }
                    } catch (Exception ignore) {}
                }


//                tportData.getConfig().set("tport." + playerUUID + ".tp.players", new ArrayList<>());
                ArrayList<String> tmpList = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".tp.players");
                ArrayList<String> newList = new ArrayList<>();
                ArrayList<String> list = new ArrayList<>();

                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (tmpList.contains(offlinePlayer.getName())) {
                        newList.add(offlinePlayer.getUniqueId().toString());
                    } else {
                        list.add(offlinePlayer.getName());
                    }
                }

                if (!list.isEmpty()) {
                    Message message = new Message();
                    message.addText("The main whitelist has some players names that couldn't be recognized by the server and will be removed from your whitelist. This list is as followed:", ChatColor.RED);
                    boolean b = true;
                    for (String playerName : list) {
                        message.addWhiteSpace();
                        if (b) {
                            message.addText(playerName, ChatColor.BLUE);
                            b = false;
                        } else {
                            message.addText(playerName, ChatColor.DARK_BLUE);
                            b = true;
                        }
                    }
                    message.sendMessage(player);
                }

                tportData.getConfig().set("tport." + playerUUID + ".tp.players", newList);

                tportData.getConfig().set("tport." + playerUUID + ".item", null);
                tportData.saveConfig();

            } else {
                tportData.getConfig().set("tport." + playerUUID + ".gui", -1);
                tportData.getConfig().set("tport." + playerUUID + ".tp.statement", "on");
                tportData.getConfig().set("tport." + playerUUID + ".tp.players", new ArrayList<>());
                tportData.saveConfig();
            }
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void Join(PlayerJoinEvent e) {

        Player player = e.getPlayer();
        setData(p, player);
    }
}