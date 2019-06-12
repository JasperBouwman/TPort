package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.edit.whitelist.Add;
import com.spaceman.tport.commands.tport.edit.whitelist.Remove;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.spaceman.tport.commandHander.HeadCommand.runCommands;

public class Whitelist extends SubCommand {

    public Whitelist() {
        addAction(new Add());
        addAction(new Remove());
        addAction(new com.spaceman.tport.commands.tport.edit.whitelist.List());
    }

    @Override
    public List<String> tabList(Player player, String[] args) {
        return new ArrayList<>(Arrays.asList("add", "remove", "list"));
    }

    @Override
    public void run(String[] args, Player player) {
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
            if (name.equalsIgnoreCase(args[1])) {

                if (!Permissions.hasPermission(player, "TPort.command.edit.whitelist")) {
                    return;
                }

                ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");

//                if (args.length == 4 && args[3].equalsIgnoreCase("list")) {
//
//                    Message message = new Message();
//                    boolean color = false;
//                    boolean first = true;
//                    message.addText("Players in the whitelist of the TPort " + ChatColor.DARK_GREEN + args[1] + ChatColor.GREEN + ": ", ChatColor.GREEN);
//                    message.addText(PlayerUUID.getPlayerName((list.size() > 0 ? list.get(0) : "")), ChatColor.BLUE);
//
//                    for (String tmp : list) {
//                        if (first) {
//                            first = false;
//                            continue;
//                        }
//
//                        message.addText(",", ChatColor.GREEN);
//
//                        if (color) {
//                            message.addText(PlayerUUID.getPlayerName(tmp), ChatColor.BLUE);
//                            color = false;
//                        } else {
//                            message.addText(PlayerUUID.getPlayerName(tmp), ChatColor.DARK_BLUE);
//                            color = true;
//                        }
//                    }
//
//                    message.sendMessage(player);
//                    return;
//
//                }
//                else
                if (args.length > 3) {
//                    if (args[3].equalsIgnoreCase("add") || args[3].equalsIgnoreCase("remove")) {

//                        if (args[3].equalsIgnoreCase("add")) {
//                            if (args.length == 4) {
//                                player.sendMessage("§cUse: §4/tport whitelist <TPort name> <add> <players...>");
//                                return;
//                            }
//
//                            for (int i = 4; i < args.length; i++) {
//
//                                String newPlayerName = args[i];
//                                String newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
//                                if (newPlayerUUID == null) {
//                                    ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(newPlayerName);
//                                    if (globalNames.size() == 1) {
//                                        newPlayerUUID = globalNames.get(0);
//                                    } else if (globalNames.size() == 0) {
//                                        player.sendMessage(ChatColor.RED + "Could not find any players named " + ChatColor.DARK_RED + newPlayerName);
//                                        continue;
//                                    } else {
//                                        player.sendMessage(ChatColor.RED + "There are more players found with the name " + ChatColor.DARK_RED + newPlayerName + ChatColor.RED
//                                                + ", please type the correct name with correct capitals");
//                                        continue;
//                                    }
//                                }
//
//                                if (newPlayerUUID.equals(playerUUID)) {
//                                    player.sendMessage("§cYou don't have to put yourself in your whitelist");
//                                    continue;
//                                }
//                                if (list.contains(newPlayerUUID)) {
//                                    player.sendMessage("§cThis player is already in you whitelist");
//                                    continue;
//                                }
//
//                                list.add(newPlayerUUID);
//                                player.sendMessage("§3Successfully added " + newPlayerName);
//
//                                Player newPlayer = Bukkit.getPlayer(UUID.fromString(newPlayerUUID));
//                                if (newPlayer != null) {
//                                    newPlayer.sendMessage("§3You are in the whitelist of §9" + player.getName() + " §3in the item §9" + name);
//                                }
//                            }
//                            tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.players", list);
//                            tportData.saveConfig();
//                            return;
//
//                        }
//                        else if (args[3].equalsIgnoreCase("remove")) {
//                            if (args.length == 4) {
//                                player.sendMessage("§cUse: §4/tport whitelist <TPort name> <remove> <players...>");
//                                return;
//                            }
//
//                            for (int i = 4; i < args.length; i++) {
//
//                                String newPlayerName = args[i];
//                                String newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
//                                if (newPlayerUUID == null) {
//                                    ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(newPlayerName);
//                                    if (globalNames.size() == 1) {
//                                        newPlayerUUID = globalNames.get(0);
//                                    } else if (globalNames.size() == 0) {
//                                        player.sendMessage(ChatColor.RED + "Could not find any players named " + ChatColor.DARK_RED + newPlayerName);
//                                        continue;
//                                    } else {
//                                        player.sendMessage(ChatColor.RED + "There are more players found with the name " + ChatColor.DARK_RED + newPlayerName + ChatColor.RED
//                                                + ", please type the correct name with correct capitals");
//                                        continue;
//                                    }
//                                }
//                                if (!list.contains(newPlayerUUID)) {
//                                    player.sendMessage("§cThis player is not in your whitelist");
//                                    continue;
//                                }
//                                if (newPlayerUUID.equals(playerUUID)) {
//                                    player.sendMessage(ChatColor.RED + "You can't remove yourself from your whitelist");
//                                    continue;
//                                }
//
//                                list.remove(newPlayerUUID);
//                                player.sendMessage("§3Successfully removed " + newPlayerName);
//
//                                Player newPlayer = Bukkit.getPlayer(UUID.fromString(newPlayerUUID));
//                                if (newPlayer != null) {
//                                    newPlayer.sendMessage("§3You are removed in the whitelist of §9" + player.getName() + " §3in the item §9" + name);
//                                }
//                            }
//                            tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.players", list);
//                            tportData.saveConfig();
//                            return;
//                        }

                       if ( !runCommands(getActions(), args[3], args, player)) {
                           player.sendMessage("§cUse: §4/tport whitelist <TPort name> <add:remove:list>");
                       }
                    return;
//                    }
                }

                player.sendMessage("§cUse: §4/tport whitelist <TPort name> <add:remove:list>");
                return;

            }
        }
        player.sendMessage("§cNo TPort found called §4" + args[1]);
    }
}
