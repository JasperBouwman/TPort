package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.CmdHandler;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.spaceman.tport.Main.Cooldown.cooldownBack;
import static com.spaceman.tport.Main.Cooldown.updateBackCooldown;
import static com.spaceman.tport.events.InventoryClick.teleportPlayer;
import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class Back extends CmdHandler {
    public static HashMap<UUID, PrevTPort> prevTPort = new HashMap<>();

    public static int tpBack(Player player) {
        /*return codes:
         * 0= no location known
         * 1= teleported
         * 2= player not online
         * 3= player not whitelisted
         * 4= invalid prevTPort value
         * 5= tport set to private*/

        if (!prevTPort.containsKey(player.getUniqueId())) {
            return 0;
        }

        PrevTPort prev = prevTPort.get(player.getUniqueId());
        Files tportData = getFiles("TPortData");
        if (prev.getL() != null) {

            prevTPort.put(player.getUniqueId(), new PrevTPort(prev.getTportName(), null, prev.getToPlayerUUID()));
            teleportPlayer(player, prev.getL());
            return 1;

        } else if (prev.getTportName() != null) {
            for (String i : tportData.getConfig().getConfigurationSection("tport." + prev.getToPlayerUUID() + ".items").getKeys(false)) {
                String name = tportData.getConfig().getString("tport." + prev.getToPlayerUUID() + ".items." + i + ".name");
                if (prev.getTportName().equals(name)) {

                    if (!prev.getToPlayerUUID().equals(player.getUniqueId().toString())) {

                        if (tportData.getConfig().getString("tport." + prev.getToPlayerUUID() + ".items." + i + ".private.statement").equals("on")) {

                            ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                    .getStringList("tport." + prev.getToPlayerUUID() + ".items." + i + ".private.players");
                            if (!list.contains(player.getUniqueId().toString())) {
                                player.sendMessage(ChatColor.RED + "You are not whitelisted to this private TPort");
                                return 5;
                            }
                        } else if (tportData.getConfig().getString("tport." + prev.getToPlayerUUID() + ".items." + i + ".private.statement").equals("online")) {
                            if (Bukkit.getPlayer(UUID.fromString(prev.getToPlayerUUID())) == null) {
                                player.sendMessage(ChatColor.RED + "You can't teleport to this teleport, " + ChatColor.DARK_RED + prev.getToPlayerUUID() + ChatColor.RED + " has set this TPort to 'online'");
                                return 5;
                            }
                        }
                    }


                    prevTPort.put(player.getUniqueId(), new PrevTPort(prev.getTportName(), player.getLocation(), prev.getToPlayerUUID()));
                    teleportPlayer(player, Main.getLocation("tport." + prev.getToPlayerUUID() + ".items." + i + ".location"));
                    return 1;
                }
            }

        } else if (prev.getToPlayerUUID() != null) {
            Player toPlayer = Bukkit.getPlayer(UUID.fromString(prev.getToPlayerUUID()));
            if (toPlayer != null) {
                if (tportData.getConfig().getString("tport." + prev.getToPlayerUUID() + ".tp.statement").equals("off")) {
                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                            .getStringList("tport." + prev.getToPlayerUUID() + "tp.players");
                    if (!list.contains(player.getUniqueId().toString())) {
                        return 3;
                    }
                }
                prevTPort.put(player.getUniqueId(), new PrevTPort(null, player.getLocation(), prev.getToPlayerUUID()));
                teleportPlayer(player, toPlayer.getLocation());
                return 1;
            } else {
                return 2;
            }
        }
        return 4;
    }

    @Override
    public void run(String[] args, Player player) {
        //tport back

        long cooldown = cooldownBack(player);
        if (cooldown / 1000 > 0) {
            player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
            return;
        }

        switch (tpBack(player)) {
            case 1:
                player.sendMessage(ChatColor.DARK_AQUA + "Teleported back");
                updateBackCooldown(player);
                return;
            case 2:
                player.sendMessage(ChatColor.RED + "Player not online anymore");
                return;
            case 3:
                player.sendMessage(ChatColor.RED + "You are not whitelisted anymore");
                return;
            case 4:
                player.sendMessage(ChatColor.RED + "Could not teleport you back");
                return;
            case 0:
                player.sendMessage(ChatColor.RED + "No previous location known");
                return;
            default:
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class PrevTPort {
        private String tportName;
        private Location l;
        private String toPlayerUUID;

        public PrevTPort(String tportName, Location l, String toPlayerUUID) {
            this.tportName = tportName;
            this.l = l;
            this.toPlayerUUID = toPlayerUUID;
        }

        public Location getL() {
            return l;
        }

        public String getToPlayerUUID() {
            return toPlayerUUID;
        }

        public String getTportName() {
            return tportName;
        }
    }
}
