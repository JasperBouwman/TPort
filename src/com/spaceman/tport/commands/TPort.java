package com.spaceman.tport.commands;

import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.spaceman.tport.events.InventoryClick.NEXT;
import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class TPort implements CommandExecutor {

    private List<CmdHandler> actions = new ArrayList<>();
    public static Open open;

    public TPort() {
        open = new Open();

        actions.add(new Add());
        actions.add(new Edit());
        actions.add(new Extra());
        actions.add(new Help());
        actions.add(open);
        actions.add(new Remove());
        actions.add(new RemovePlayer());
        actions.add(new Compass());
        actions.add(new Own());
    }

    public static ItemStack getHead(UUID uuid) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        meta.setDisplayName(PlayerUUID.getPlayerName(uuid.toString()));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
        meta.setDisplayName(PlayerUUID.getPlayerName(player.getName()));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You have to be a player to use this command");
            return false;
        }

        Player player = (Player) commandSender;
        Files tportData = getFiles("TPortData");

        if (strings.length == 0) {
            int d = -1;
            tportData.getConfig().set("tport." + player.getUniqueId().toString() + ".gui", d);
            tportData.saveConfig();

            Set l = tportData.getConfig().getConfigurationSection("tport").getKeys(false);

            int size = 45;

            if (l.size() < 8) {
                size = 27;
            } else if (l.size() < 15) {
                size = 36;
            }

            Inventory inv = Bukkit.createInventory(null, size, "Choose a player (1)");
            int i = 10;

            for (String s : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {

                if (i >= 35) {
                    ItemStack item = new ItemStack(Material.HOPPER);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(NEXT);
                    item.setItemMeta(meta);
                    inv.setItem(44, item);

                    tportData.getConfig().set("tport." + player.getUniqueId().toString() + ".gui", d);
                    tportData.saveConfig();
                    break;
                }

                if (i == 17 || i == 26) {
                    i = i + 2;
                }
                if (!(i == 44)) {
//                    inv.setItem(i, tportData.getConfig().getItemStack("tport." + s + ".item"));
                    inv.setItem(i, getHead(UUID.fromString(s)));
                    i++;
                }
            }
            player.openInventory(inv);

        } else {

            for (CmdHandler action : this.actions) {
                if (strings[0].equalsIgnoreCase(action.getClass().getSimpleName())) {
                    action.run(strings, player);
                    return false;
                }
            }
            player.sendMessage(ChatColor.RED + "This is not a sub-command");

        }

        return false;
    }
}
