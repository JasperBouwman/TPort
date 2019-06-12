package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.edit.*;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.spaceman.tport.commandHander.HeadCommand.runCommands;

public class Edit extends SubCommand {

    private EmptyCommand emptyOwnTPort;

    public Edit() {
        emptyOwnTPort = new EmptyCommand();
        emptyOwnTPort.addAction(new Lore());
        emptyOwnTPort.addAction(new Name());
        emptyOwnTPort.addAction(new Item());
        emptyOwnTPort.addAction(new com.spaceman.tport.commands.tport.edit.Location());
        emptyOwnTPort.addAction(new Private());
        emptyOwnTPort.addAction(new Whitelist());
        emptyOwnTPort.addAction(new Move());
        addAction(emptyOwnTPort);
    }

    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        //tport own
        if (tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
                String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");

                if (tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".private.statement").equals("true")) {
                    ArrayList<String> listTmp = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");
                    if (listTmp.contains(player.getUniqueId().toString())) {
                        list.add(name);
                    }
                } else {
                    list.add(name);
                }
            }
        }
        return list;
    }

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
        // tport edit <TPort name> move <slot>

        if (args.length <= 2) {
            player.sendMessage("§cUse: §4/tport edit <TPort name> <lore:name:item:location:private:whitelist:move");
            return;
        }

        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        if (!tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            player.sendMessage("§cNo TPort found called §4" + args[1]);
            return;
        }

//        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
//
//            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
//            if (name.equalsIgnoreCase(args[1])) {

//                ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item");
//                ItemMeta meta = item.getItemMeta();

        if (!runCommands(emptyOwnTPort.getActions(), args[2], args, player)) {
            player.sendMessage("§cUse: §4/tport edit <TPort name> <lore:name:item:location:private:whitelist:move>");
        }
//        return;

//                if (args[2].equalsIgnoreCase("lore")) {
//
//                    if (!Permissions.hasPermission(player, "TPort.command.edit.lore", false)) {
//                        if (!Permissions.hasPermission(player, "TPort.basic", false)) {
//                            Permissions.sendNoPermMessage(player, "TPort.command.edit.lore", "TPort.basic");
//                            return;
//                        }
//                    }
//
//                    if (args.length == 3) {
//                        player.sendMessage("§cUse: §4/tport edit " + args[1] + " lore set:remove <lore...>");
//                        return;
//                    }
//                    if (args[3].equalsIgnoreCase("set")) {
//                        if (args.length < 5) {
//                            player.sendMessage(ChatColor.RED + "§cUse: §4/tport edit <TPort name> lore set <lore...>");
//                            return;
//                        }
//
//                        StringBuilder lore = new StringBuilder(args[4]);
//                        for (int ii = 5; ii <= args.length - 1; ii++) {
//                            lore.append(" ").append(args[ii]);
//                        }
//                        ArrayList<String> list = new ArrayList<>(Arrays.asList(lore.toString().split("\\\\n")));
//
//                        meta.setLore(list);
//                        item.setItemMeta(meta);
//                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".item", item);
//                        tportData.saveConfig();
//                        player.sendMessage("§3Lore is set to: §9" + lore);
//                        return;
//                    } else if (args[3].equalsIgnoreCase("remove")) {
//                        if (args.length == 4) {
//                            ArrayList<String> list = new ArrayList<>();
//                            meta.setLore(list);
//                            item.setItemMeta(meta);
//                            tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".item", item);
//                            tportData.saveConfig();
//                            player.sendMessage("§3Lore is successfully removed");
//                            return;
//                        } else {
//                            player.sendMessage("§cUse: §4/tport edit <TPort name> lore remove");
//                            return;
//                        }
//                    } else {
//                        player.sendMessage("§cUse: §4/tport edit <TPort name> lore set <lore...>§c or§4 /tport edit <TPort> lore remove");
//                        return;
//                    }
//                }
//                else if (args[2].equalsIgnoreCase("name")) {
//
//                    if (!Permissions.hasPermission(player, "TPort.command.edit.name", false)) {
//                        if (!Permissions.hasPermission(player, "TPort.basic", false)) {
//                            Permissions.sendNoPermMessage(player, "TPort.command.edit.name", "TPort.basic");
//                            return;
//                        }
//                    }
//
//                    if (args.length == 4) {
//                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".name", args[3]);
//                        tportData.saveConfig();
//                        player.sendMessage("§3New name set to " + args[3]);
//                        return;
//                    } else {
//                        player.sendMessage("§cUse: §4/tport edit " + args[1] + " name <new TPort name>");
//                        return;
//                    }
//                }
//                else if (args[2].equalsIgnoreCase("item")) {
//
//                    if (!Permissions.hasPermission(player, "TPort.command.edit.item", false)) {
//                        if (!Permissions.hasPermission(player, "TPort.basic", false)) {
//                            Permissions.sendNoPermMessage(player, "TPort.command.edit.item", "TPort.basic");
//                            return;
//                        }
//                    }
//
//                    if (args.length == 3) {
//                        ItemStack newItem = new ItemStack(player.getInventory().getItemInMainHand());
//
//                        if (newItem.getItemMeta() == null) {
//                            player.sendMessage("§cYou must place an item in you main hand");
//                            return;
//                        }
//
//                        ItemMeta newMeta = newItem.getItemMeta();
//                        newMeta.setLore(meta.getLore());
//                        newItem.setItemMeta(newMeta);
//
//                        ItemStack oldStack = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item");
//                        player.getInventory().setItemInMainHand(oldStack);
//
//                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".item", newItem);
//                        tportData.saveConfig();
//
//                        Message message = new Message();
//                        message.addText(TextComponent.textComponent("§3New item for TPort "));
//                        message.addText(TextComponent.textComponent(name, ChatColor.BLUE, ClickEvent.runCommand("/tport own " + name)));
//                        message.addText(TextComponent.textComponent(" set to ", ChatColor.DARK_AQUA));
//                        message.addText(TextComponent.textComponent(newItem.getType().toString(), ChatColor.BLUE));
//                        message.sendMessage(player);
//                        return;
//                    } else {
//                        player.sendMessage("§cUse: §4/tport edit <TPort name> item");
//                        return;
//                    }
//                }
//                else if (args[2].equalsIgnoreCase("location")) {
//
//                    if (!Permissions.hasPermission(player, "TPort.command.edit.location", false)) {
//                        if (!Permissions.hasPermission(player, "TPort.basic", false)) {
//                            Permissions.sendNoPermMessage(player, "TPort.command.edit.location", "TPort.basic");
//                            return;
//                        }
//                    }
//
//                    if (args.length == 3) {
//                        Location l = player.getLocation();
////                        tportData.getConfig().set("tport." + player.getName() + ".items." + s + ".location", l);
//                        Main.saveLocation("tport." + playerUUID + ".items." + s + ".location", l);
//                        player.sendMessage("§3Successfully edited the location");
//                        return;
//                    } else {
//                        player.sendMessage("§cUse: §4/tport edit <TPort name> location");
//                        return;
//                    }
//                }
//                else if (args[2].equalsIgnoreCase("whitelist")) {
//
//                    if (!Permissions.hasPermission(player, "TPort.command.edit.whitelist")) {
//                        return;
//                    }
//
//                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");
//
//                    if (args.length == 4 && args[3].equalsIgnoreCase("list")) {
//
//                        Message message = new Message();
//                        boolean color = false;
//                        boolean first = true;
//                        message.addText("Players in the whitelist of the TPort " + ChatColor.DARK_GREEN + args[1] + ChatColor.GREEN + ": ", ChatColor.GREEN);
//                        message.addText(PlayerUUID.getPlayerName((list.size() > 0 ? list.get(0) : "")), ChatColor.BLUE);
//
//                        for (String tmp : list) {
//                            if (first) {
//                                first = false;
//                                continue;
//                            }
//
//                            message.addText(",", ChatColor.GREEN);
//
//                            if (color) {
//                                message.addText(PlayerUUID.getPlayerName(tmp), ChatColor.BLUE);
//                                color = false;
//                            } else {
//                                message.addText(PlayerUUID.getPlayerName(tmp), ChatColor.DARK_BLUE);
//                                color = true;
//                            }
//                        }
//
//                        message.sendMessage(player);
//                        return;
//
//                    } else if (args.length > 3) {
//                        if (args[3].equalsIgnoreCase("add") || args[3].equalsIgnoreCase("remove")) {
//                            if (!tportData.getConfig().contains("tport." + playerUUID + ".items")) {
//                                player.sendMessage("§cNo TPort found called §4" + args[1]);
//                                return;
//                            }
//
//                            if (args[3].equalsIgnoreCase("add")) {
//                                if (args.length == 4) {
//                                    player.sendMessage("§cUse: §4/tport whitelist <TPort name> <add> <players...>");
//                                    return;
//                                }
//
//                                for (int i = 4; i < args.length; i++) {
//
//                                    String newPlayerName = args[i];
//                                    String newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
//                                    if (newPlayerUUID == null) {
//                                        ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(newPlayerName);
//                                        if (globalNames.size() == 1) {
//                                            newPlayerUUID = globalNames.get(0);
//                                        } else if (globalNames.size() == 0) {
//                                            player.sendMessage(ChatColor.RED + "Could not find any players named " + ChatColor.DARK_RED + newPlayerName);
//                                            continue;
//                                        } else {
//                                            player.sendMessage(ChatColor.RED + "There are more players found with the name " + ChatColor.DARK_RED + newPlayerName + ChatColor.RED
//                                                    + ", please type the correct name with correct capitals");
//                                            continue;
//                                        }
//                                    }
//
//                                    if (newPlayerUUID.equals(playerUUID)) {
//                                        player.sendMessage("§cYou don't have to put yourself in your whitelist");
//                                        continue;
//                                    }
//                                    if (list.contains(newPlayerUUID)) {
//                                        player.sendMessage("§cThis player is already in you whitelist");
//                                        continue;
//                                    }
//
//                                    list.add(newPlayerUUID);
//                                    player.sendMessage("§3Successfully added " + newPlayerName);
//
//                                    Player newPlayer = Bukkit.getPlayer(UUID.fromString(newPlayerUUID));
//                                    if (newPlayer != null) {
//                                        newPlayer.sendMessage("§3You are in the whitelist of §9" + player.getName() + " §3in the item §9" + name);
//                                    }
//                                }
//                                tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.players", list);
//                                tportData.saveConfig();
//                                return;
//
//                            } else if (args[3].equalsIgnoreCase("remove")) {
//                                if (args.length == 4) {
//                                    player.sendMessage("§cUse: §4/tport whitelist <TPort name> <remove> <players...>");
//                                    return;
//                                }
//
//                                for (int i = 4; i < args.length; i++) {
//
//                                    String newPlayerName = args[i];
//                                    String newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
//                                    if (newPlayerUUID == null) {
//                                        ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(newPlayerName);
//                                        if (globalNames.size() == 1) {
//                                            newPlayerUUID = globalNames.get(0);
//                                        } else if (globalNames.size() == 0) {
//                                            player.sendMessage(ChatColor.RED + "Could not find any players named " + ChatColor.DARK_RED + newPlayerName);
//                                            continue;
//                                        } else {
//                                            player.sendMessage(ChatColor.RED + "There are more players found with the name " + ChatColor.DARK_RED + newPlayerName + ChatColor.RED
//                                                    + ", please type the correct name with correct capitals");
//                                            continue;
//                                        }
//                                    }
//                                    if (!list.contains(newPlayerUUID)) {
//                                        player.sendMessage("§cThis player is not in your whitelist");
//                                        continue;
//                                    }
//                                    if (newPlayerUUID.equals(playerUUID)) {
//                                        player.sendMessage(ChatColor.RED + "You can't remove yourself from your whitelist");
//                                        continue;
//                                    }
//
//                                    list.remove(newPlayerUUID);
//                                    player.sendMessage("§3Successfully removed " + newPlayerName);
//
//                                    Player newPlayer = Bukkit.getPlayer(UUID.fromString(newPlayerUUID));
//                                    if (newPlayer != null) {
//                                        newPlayer.sendMessage("§3You are removed in the whitelist of §9" + player.getName() + " §3in the item §9" + name);
//                                    }
//                                }
//                                tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.players", list);
//                                tportData.saveConfig();
//                                return;
//                            }
//
//
//                        } else {
//                            player.sendMessage("§cUse: §4/tport whitelist <TPort name> <add:remove:list>");
//                            return;
//                        }
//                    }
//
//                    player.sendMessage("§cUse: §4/tport whitelist <TPort name> <add:remove:list>");
//                    return;
//
//                }
//                else if (args[2].equalsIgnoreCase("private")) {
//
//                    if (!Permissions.hasPermission(player, "TPort.command.edit.private")) {
//                        return;
//                    }
//
//                    if (args.length != 4) {
//                        if (args.length == 3) {
//                            switch (tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".private.statement")) {
//                                case "off":
//                                    player.sendMessage(ChatColor.DARK_AQUA + "The TPort " + ChatColor.BLUE +
//                                            tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name") + ChatColor.DARK_AQUA + " is " + ChatColor.RED + "open");
//                                    return;
//                                case "on":
//                                    player.sendMessage(ChatColor.DARK_AQUA + "The TPort " + ChatColor.BLUE +
//                                            tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name") + ChatColor.DARK_AQUA + " is " + ChatColor.GREEN + "private");
//                                    return;
//                                case "online":
//                                    player.sendMessage(ChatColor.DARK_AQUA + "The TPort " + ChatColor.BLUE +
//                                            tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name") + ChatColor.DARK_AQUA + " is " + ChatColor.YELLOW + "online");
//                                    return;
//                            }
//                        }
//                        player.sendMessage("§cUse: §4/tport edit <TPort name> private <on:off:online>");
//                        return;
//                    }
//                    if (args[3].equalsIgnoreCase("on")) {
//                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.statement", "on");
//                        tportData.saveConfig();
//                        player.sendMessage("§3TPort " + ChatColor.BLUE + name + ChatColor.DARK_AQUA + " is now private");
//                        return;
//                    } else if (args[3].equalsIgnoreCase("off")) {
//                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.statement", "off");
//                        tportData.saveConfig();
//                        player.sendMessage("§3TPort " + ChatColor.BLUE + name + ChatColor.DARK_AQUA + " is now open");
//                        return;
//                    } else if (args[3].equalsIgnoreCase("online")) {
//                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.statement", "online");
//                        tportData.saveConfig();
//                        player.sendMessage("§3TPort " + ChatColor.BLUE + name + ChatColor.DARK_AQUA + " is now open only if you are online");
//                        return;
//
//                    } else {
//                        player.sendMessage("§cUse: §4/tport edit <TPort name> private <on:off:online>");
//                        return;
//                    }
//
//                }
//                else if (args[2].equalsIgnoreCase("move")) {
//
//                    if (!Permissions.hasPermission(player, "TPort.command.edit.move", false)) {
//                        if (!Permissions.hasPermission(player, "TPort.basic", false)) {
//                            Permissions.sendNoPermMessage(player, "TPort.command.edit.move", "TPort.basic");
//                            return;
//                        }
//                    }
//
//                    if (args.length == 4) {
//                        int slot;
//                        try {
//                            slot = Integer.parseInt(args[3]);
//                        } catch (NumberFormatException nfe) {
//                            player.sendMessage(ChatColor.RED + "Slot number " + ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number");
//                            return;
//                        }
//                        if (slot < 1 || slot > TPortSize) {
//                            player.sendMessage(ChatColor.RED + "Slot number must between 1 and " + TPortSize);
//                            return;
//                        }
//                        //convert to real slot number
//                        slot--;
//                        if (tportData.getConfig().contains("tport." + playerUUID + ".items." + slot)) {
//                            player.sendMessage(ChatColor.RED + "Slot " + ChatColor.DARK_RED + (slot + 1) + ChatColor.RED + " is taken, choose another one");
//                            return;
//                        }
//                        tportData.getConfig().set("tport." + playerUUID + ".items." + slot, tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items." + s));
//                        tportData.getConfig().set("tport." + playerUUID + ".items." + s, null);
//                        tportData.saveConfig();
//                        player.sendMessage(ChatColor.DARK_AQUA + "Tport has been moved to slot " + ChatColor.BLUE + (slot + 1));
//                        return;
//
//                    } else {
//                        player.sendMessage(ChatColor.RED + "Use: " + ChatColor.DARK_RED + "/tport edit <TPort name> move <slot>");
//                    }
//                }
//                else {
//                    player.sendMessage("§cUse: §4/tport edit <TPort name> <lore:name:item:location:private:whitelist:move>");
//                    return;
//                }
//            }
//        }

//        player.sendMessage("§cNo TPort found called §4" + args[1]);
    }
}
