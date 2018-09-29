package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.CmdHandler;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class Edit extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        // tport edit <TPort name> lore set <lore...>
        // tport edit <TPort name> lore remove
        // tport edit <TPort name> name <new TPort name>
        // tport edit <TPort name> item
        // tport edit <TPort name> location
        // tport edit <TPort name> private
        // tport edit <TPort name> private [on:off:online]
        // tport edit <TPort name> whitelist <add:remove> <players names...>
        // tport edit <TPort name> whitelist list

        if (args.length <= 2) {
            player.sendMessage("§cUse: §4/tport edit <TPort name> <lore:name:item:location:private:whitelist>");
            return;
        }

        Files tportData = getFiles("TPortData");
        String playerUUID = player.getUniqueId().toString();

        if (!tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            player.sendMessage("§cNo TPort found called §4" + args[1]);
            return;
        }

        boolean nameb = false;

        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

            ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item");
            ItemMeta meta = item.getItemMeta();
            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
            if (name.equalsIgnoreCase(args[1])) {

                if (args[2].equalsIgnoreCase("lore")) {
                    if (args.length == 3) {
                        player.sendMessage("§cUse: §4/tport edit " + args[1] + " lore set:remove <lore...>");
                        return;
                    }
                    if (args[3].equalsIgnoreCase("set")) {
                        if (args.length < 5) {
                            player.sendMessage(ChatColor.RED + "§cUse: §4/tport edit <TPort name> lore set <lore...>");
                            return;
                        }

                        StringBuilder lore = new StringBuilder(args[4]);
                        for (int ii = 5; ii <= args.length - 1; ii++) {
                            lore.append(" ").append(args[ii]);
                        }
                        ArrayList<String> list = new ArrayList<>(Arrays.asList(lore.toString().split("\\\\n")));

                        meta.setLore(list);
                        item.setItemMeta(meta);
                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".item", item);
                        tportData.saveConfig();
                        player.sendMessage("§3Lore is set to: §9" + lore);
                        return;
                    } else if (args[3].equalsIgnoreCase("remove")) {
                        if (args.length == 4) {
                            ArrayList<String> list = new ArrayList<>();
                            meta.setLore(list);
                            item.setItemMeta(meta);
                            tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".item", item);
                            tportData.saveConfig();
                            player.sendMessage("§3Lore is successfully removed");
                            return;
                        } else {
                            player.sendMessage("§cUse: §4/tport edit <TPort name> lore remove");
                            return;
                        }
                    } else {
                        player.sendMessage("§cUse: §4/tport edit <TPort name> lore set <lore...>§c or§4 /tport edit <TPort> lore remove");
                        return;
                    }
                }
                else if (args[2].equalsIgnoreCase("name")) {
                    if (args.length == 4) {
                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".name", args[3]);
                        tportData.saveConfig();
                        player.sendMessage("§3New name set to " + args[3]);
                        return;
                    } else {
                        player.sendMessage("§cUse: §4/tport edit " + args[1] + " name <new TPort name>");
                        return;
                    }
                }
                else if (args[2].equalsIgnoreCase("item")) {
                    if (args.length == 3) {
                        ItemStack newItem = new ItemStack(player.getInventory().getItemInMainHand());

                        if (newItem.getItemMeta() == null) {
                            player.sendMessage("§cYou must place an item in you main hand");
                            return;
                        }

                        ItemMeta newMeta = newItem.getItemMeta();
                        newMeta.setLore(meta.getLore());
                        newItem.setItemMeta(newMeta);

                        ItemStack oldStack = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item");
                        player.getInventory().setItemInMainHand(oldStack);

                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".item", newItem);
                        tportData.saveConfig();

                        Message message = new Message();
                        message.addText(TextComponent.textComponent("§3New item for TPort "));
                        message.addText(TextComponent.textComponent(name, ChatColor.BLUE, ClickEvent.runCommand("/tport own " + name)));
                        message.addText(TextComponent.textComponent(" set to ", ChatColor.DARK_AQUA));
                        message.addText(TextComponent.textComponent(newItem.getType().toString(), ChatColor.BLUE));
                        message.sendMessage(player);
                        return;
                    } else {
                        player.sendMessage("§cUse: §4/tport edit <TPort name> item");
                        return;
                    }
                }
                else if (args[2].equalsIgnoreCase("location")) {
                    if (args.length == 3) {
                        Location l = player.getLocation();
//                        tportData.getConfig().set("tport." + player.getName() + ".items." + s + ".location", l);
                        Main.saveLocation("tport." + playerUUID + ".items." + s + ".location", l);
                        player.sendMessage("§3Successfully edited the location");
                        return;
                    } else {
                        player.sendMessage("§cUse: §4/tport edit <TPort name> location");
                        return;
                    }
                }
                else if (args[2].equalsIgnoreCase("whitelist")) {

                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");

                    if (args.length == 4 && args[3].equalsIgnoreCase("list")) {

                        Message message = new Message();
                        boolean color = false;
                        boolean first = true;
                        message.addText("Players in the whitelist of the TPort " + ChatColor.DARK_GREEN + args[1] + ChatColor.GREEN + ": ", ChatColor.GREEN);
                        message.addText(PlayerUUID.getPlayerName((list.size() > 0 ? list.get(0) : "")), ChatColor.BLUE);

                        for (String tmp : list) {
                            if (first) {
                                first = false;
                                continue;
                            }

                            message.addText(",", ChatColor.GREEN);

                            if (color) {
                                message.addText(PlayerUUID.getPlayerName(tmp), ChatColor.BLUE);
                                color = false;
                            } else {
                                message.addText(PlayerUUID.getPlayerName(tmp), ChatColor.DARK_BLUE);
                                color = true;
                            }
                        }

                        message.sendMessage(player);
                        return;

                    } else if (args.length > 3) {
                        if (args[3].equalsIgnoreCase("add") || args[3].equalsIgnoreCase("remove")) {
                            if (!tportData.getConfig().contains("tport." + playerUUID + ".items")) {
                                player.sendMessage("§cNo TPort found called §4" + args[1]);
                                return;
                            }

                            if (args[3].equalsIgnoreCase("add")) {
                                if (args.length == 4) {
                                    player.sendMessage("§cUse: §4/tport whitelist <TPort name> <add> <players...>");
                                    return;
                                }

                                for (int i = 4; i < args.length; i++) {

                                    String newPlayerName = args[i];
                                    String newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
                                    if (newPlayerUUID == null) {
                                        ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(newPlayerName);
                                        if (globalNames.size() == 1) {
                                            newPlayerUUID = globalNames.get(0);
                                        } else if (globalNames.size() == 0) {
                                            player.sendMessage(ChatColor.RED + "Could not find any players named " + ChatColor.DARK_RED + newPlayerName);
                                            continue;
                                        } else {
                                            player.sendMessage(ChatColor.RED + "There are more players found with the name " + ChatColor.DARK_RED + newPlayerName + ChatColor.RED
                                                    + ", please type the correct name with correct capitals");
                                            continue;
                                        }
                                    }

                                    if (newPlayerUUID.equals(playerUUID)) {
                                        player.sendMessage("§cYou don't have to put yourself in your whitelist");
                                        continue;
                                    }
                                    if (list.contains(newPlayerUUID)) {
                                        player.sendMessage("§cThis player is already in you whitelist");
                                        continue;
                                    }

                                    list.add(newPlayerUUID);
                                    player.sendMessage("§3Successfully added " + newPlayerName);

                                    Player newPlayer = Bukkit.getPlayer(UUID.fromString(newPlayerUUID));
                                    if (newPlayer != null) {
                                        newPlayer.sendMessage("§3You are in the whitelist of §9" + player.getName() + " §3in the item §9" + name);
                                    }
                                }
                                tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.players", list);
                                tportData.saveConfig();
                                return;

                            } else if (args[3].equalsIgnoreCase("remove")) {
                                if (args.length == 4) {
                                    player.sendMessage("§cUse: §4/tport whitelist <TPort name> <remove> <players...>");
                                    return;
                                }

                                for (int i = 4; i < args.length; i++) {

                                    String newPlayerName = args[i];
                                    String newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
                                    if (newPlayerUUID == null) {
                                        ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(newPlayerName);
                                        if (globalNames.size() == 1) {
                                            newPlayerUUID = globalNames.get(0);
                                        } else if (globalNames.size() == 0) {
                                            player.sendMessage(ChatColor.RED + "Could not find any players named " + ChatColor.DARK_RED + newPlayerName);
                                            continue;
                                        } else {
                                            player.sendMessage(ChatColor.RED + "There are more players found with the name " + ChatColor.DARK_RED + newPlayerName + ChatColor.RED
                                                    + ", please type the correct name with correct capitals");
                                            continue;
                                        }
                                    }
                                    if (!list.contains(newPlayerUUID)) {
                                        player.sendMessage("§cThis player is not in your whitelist");
                                        continue;
                                    }
                                    if (newPlayerUUID.equals(playerUUID)) {
                                        player.sendMessage(ChatColor.RED + "You can't remove yourself from your whitelist");
                                        continue;
                                    }

                                    list.remove(newPlayerUUID);
                                    player.sendMessage("§3Successfully removed " + newPlayerName);

                                    Player newPlayer = Bukkit.getPlayer(UUID.fromString(newPlayerUUID));
                                    if (newPlayer != null) {
                                        newPlayer.sendMessage("§3You are removed in the whitelist of §9" + player.getName() + " §3in the item §9" + name);
                                    }
                                }
                                tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.players", list);
                                tportData.saveConfig();
                                return;
                            }


                        } else {
                            player.sendMessage("§cUse: §4/tport whitelist <TPort name> <add:remove:list>");
                            return;
                        }
                    }

                    player.sendMessage("§cUse: §4/tport whitelist <TPort name> <add:remove:list>");
                    return;

                }
                else if (args[2].equalsIgnoreCase("private")) {
                    if (args.length != 4) {
                        if (args.length == 3) {
                            boolean b = tportData.getConfig().getBoolean("tport." + playerUUID + ".items." + s + ".private.statement");
                            if (!b) {
                                player.sendMessage("§3This TPort is open");
                                return;
                            } else {
                                player.sendMessage("§3This TPort is private");
                                return;
                            }
                        }
                        player.sendMessage("§cUse: §4/tport edit <TPort name> private <true:false>");
                        return;
                    }
                    if (args[3].equalsIgnoreCase("on")) {
                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.statement", "on");
                        tportData.saveConfig();
                        player.sendMessage("§3TPort " + ChatColor.BLUE + name + ChatColor.DARK_AQUA + " is now private");
                        return;
                    } else if (args[3].equalsIgnoreCase("off")) {
                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.statement", "off");
                        tportData.saveConfig();
                        player.sendMessage("§3TPort " + ChatColor.BLUE + name + ChatColor.DARK_AQUA + " is now open");
                        return;
                    } else if (args[3].equalsIgnoreCase("online")) {
                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.statement", "online");
                        tportData.saveConfig();
                        player.sendMessage("§3TPort " + ChatColor.BLUE + name + ChatColor.DARK_AQUA + " is now open only if you are online");
                        return;

                    } else {
                        player.sendMessage("§cUse: §4/tport edit <TPort name> private <on:off:online>");
                        return;
                    }

                }
                else {
                    player.sendMessage("§cUse: §4/tport edit <TPort name> <lore:name:item:location:private:whitelist>");
                    return;
                }
            } else {
                nameb = true;
            }
        }


        if (nameb) {
            player.sendMessage("§cNo TPort found called §4" + args[1]);
        }
    }
}
