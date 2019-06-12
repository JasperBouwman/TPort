package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Private extends SubCommand {

    @Override
    public List<String> tabList(Player player, String[] args) {
        return new ArrayList<>(Arrays.asList("on", "off", "online"));
    }

    @Override
    public void run(String[] args, Player player) {
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
            if (name.equalsIgnoreCase(args[1])) {

                if (!Permissions.hasPermission(player, "TPort.command.edit.private")) {
                    return;
                }

                if (args.length != 4) {
                    if (args.length == 3) {
                        switch (tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".private.statement")) {
                            case "off":
                                player.sendMessage(ChatColor.DARK_AQUA + "The TPort " + ChatColor.BLUE +
                                        tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name") + ChatColor.DARK_AQUA + " is " + ChatColor.RED + "open");
                                return;
                            case "on":
                                player.sendMessage(ChatColor.DARK_AQUA + "The TPort " + ChatColor.BLUE +
                                        tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name") + ChatColor.DARK_AQUA + " is " + ChatColor.GREEN + "private");
                                return;
                            case "online":
                                player.sendMessage(ChatColor.DARK_AQUA + "The TPort " + ChatColor.BLUE +
                                        tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name") + ChatColor.DARK_AQUA + " is " + ChatColor.YELLOW + "online");
                                return;
                        }
                    }
                    player.sendMessage("§cUse: §4/tport edit <TPort name> private [on:off:online]");
                    return;
                }
                if (args[3].equalsIgnoreCase("on")) {
                    tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.statement", "on");
                    tportData.saveConfig();
                    player.sendMessage("§3TPort " + ChatColor.BLUE + name + ChatColor.DARK_AQUA + " is now private");
                } else if (args[3].equalsIgnoreCase("off")) {
                    tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.statement", "off");
                    tportData.saveConfig();
                    player.sendMessage("§3TPort " + ChatColor.BLUE + name + ChatColor.DARK_AQUA + " is now open");
                } else if (args[3].equalsIgnoreCase("online")) {
                    tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.statement", "online");
                    tportData.saveConfig();
                    player.sendMessage("§3TPort " + ChatColor.BLUE + name + ChatColor.DARK_AQUA + " is now open only if you are online");
                } else {
                    player.sendMessage("§cUse: §4/tport edit <TPort name> private [on:off:online]");
                }

                return;
            }
        }
        player.sendMessage("§cNo TPort found called §4" + args[1]);
    }
}
