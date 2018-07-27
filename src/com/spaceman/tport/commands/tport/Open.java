package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.CmdHandler;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.tport.events.InventoryClick.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class Open extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        // tport open <playername> [TPort name]

        if (args.length == 1) {
            player.sendMessage("§cUse: §4/tport open <playername> [TPort name]");
            return;
        }
        Files tportData = getFiles("TPortData");

        String newPlayerName = args[1];
        String newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);

        if (newPlayerUUID == null) {

            ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(newPlayerName);

            if (globalNames.size() == 1) {
                newPlayerUUID = globalNames.get(0);
            } else if (globalNames.size() == 0) {
                player.sendMessage(ChatColor.RED + "Could not find any players named " + ChatColor.DARK_RED + newPlayerName);
                return;
            } else {
                player.sendMessage(ChatColor.RED + "There are more players found with the name " + ChatColor.DARK_RED + newPlayerName + ChatColor.RED
                        + ", please type the correct name with correct capitals");
                return;
            }
        }

        if (!tportData.getConfig().contains("tport." + newPlayerUUID)) {
            player.sendMessage("§cThis isn't a player that has ever been online");
            return;
        }
        if (args.length == 3) {
            boolean b = true;
            for (int i = 0; i < 7; i++) {
                if (tportData.getConfig().contains("tport." + newPlayerUUID + ".items." + i + ".item")) {
                    ItemStack items = tportData.getConfig().getItemStack("tport." + newPlayerUUID + ".items." + i + ".item");
                    if (args[2].equals(items.getItemMeta().getDisplayName())) {

                        if (tportData.getConfig().getString("tport." + newPlayerUUID + ".items." + i + ".private.statement").equals("true")) {

                            ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                    .getStringList("tport." + newPlayerUUID + ".items." + i + ".private.players");
                            if (!list.contains(player.getUniqueId().toString())) {
                                player.sendMessage(ChatColor.RED + "You are not whitelisted to this private TPort");
                                return;
                            }
                        }

                        player.closeInventory();

                        Location l = Main.getLocation("tport." + newPlayerUUID + ".items." + i + ".location");

                        if (l == null) {
                            player.sendMessage("§cThe world for this location has not been found");
                            return;
                        }
                        teleportPlayer(player, l);

                        Message message = new Message();
                        message.addText("Teleported to ", ChatColor.DARK_AQUA);
                        message.addText(textComponent(items.getItemMeta().getDisplayName(),
                                ChatColor.BLUE, ClickEvent.runCommand("/tport open " + args[1] + " " + items.getItemMeta().getDisplayName())));
                        message.sendMessage(player);

//                        player.sendMessage("§3Teleported to §9" + items.getItemMeta().getDisplayName());
                        b = false;
                    }
                }
            }
            if (b) {
                player.sendMessage("§cThe TPort §4" + args[2] + "§c doesn't exist");
            }
            return;
        }
        if (args.length > 3) {
            player.sendMessage("§cUse: §4/tport open <playername> [TPort name]");
            return;
        }

        Inventory invc = Bukkit.createInventory(null, 9, "TPort: " + newPlayerName);
        for (int i = 0; i < 7; i++) {

            if (newPlayerUUID.equals(player.getUniqueId().toString())) {
                invc.setItem(i, tportData.getConfig().getItemStack("tport." + newPlayerUUID + ".items." + i + ".item"));
            } else if (tportData.getConfig().contains("tport." + newPlayerUUID + ".items." + i)) {
                if (tportData.getConfig().getString("tport." + newPlayerUUID + ".items." + i + ".private.statement").equals("false")) {

                    invc.setItem(i, tportData.getConfig().getItemStack("tport." + newPlayerUUID + ".items." + i + ".item"));

                } else if (tportData.getConfig().getString("tport." + newPlayerUUID + ".items." + i + ".private.statement").equals("true")) {

                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                            .getStringList("tport." + newPlayerUUID + ".items." + i + ".private.players");
                    if (list.contains(player.getUniqueId().toString())) {
                        invc.setItem(i, tportData.getConfig().getItemStack("tport." + newPlayerUUID + ".items." + i + ".item"));
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
            invc.setItem(7, warp);
        }
        player.openInventory(invc);

    }
}
