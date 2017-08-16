package com.spaceman.tport.commands;

import com.spaceman.tport.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.spaceman.tport.commands.tport.*;
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

public class TPort implements CommandExecutor {

    private Main p;

    private List<CmdHandler> actions = new ArrayList<>();

    public TPort(Main main) {
        p = main;

        actions.add(new Add());
        actions.add(new Edit());
        actions.add(new Extra());
        actions.add(new Help());
        actions.add(new Open());
        actions.add(new Remove());
        actions.add(new Whitelist());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("you have to be a player to use this command");
            return false;
        }

        Player player = (Player) commandSender;

        if (strings.length == 0) {
            int d = -1;
            p.getConfig().set("tport." + player.getName() + ".gui", d);
            p.saveConfig();

            Set l = p.getConfig().getConfigurationSection("tport").getKeys(false);

            int size = 45;

            if (l.size() < 8) {
                size = 27;
            }
            else if (l.size() < 15) {
                size = 36;
            }

            Inventory inv = Bukkit.createInventory(null, size, "choose a player (1)");
            int i = 10;

            for (String s : p.getConfig().getConfigurationSection("tport").getKeys(false)) {

                if (i >= 35) {
                    ItemStack item = new ItemStack(Material.HOPPER);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.DARK_AQUA + "next");
                    item.setItemMeta(meta);
                    inv.setItem(44, item);

                    p.getConfig().set("tport." + player.getName() + ".gui", d);
                    p.saveConfig();
                    break;
                }

                if (i == 17 || i == 26) {
                    i = i + 2;
                }
                if (!(i == 44)) {
                    inv.setItem(i, p.getConfig().getItemStack("tport." + s + ".item"));
                    i++;
                }
            }
            player.openInventory(inv);

        } else {

            for (CmdHandler action : this.actions) {
                if (strings[0].equalsIgnoreCase(action.getClass().getSimpleName())) {
                    action.setMain(p);
                    action.run(strings, player);
                    return false;
                }
            }
            player.sendMessage("this is not a sub-command");

        }

        return false;
    }
}
