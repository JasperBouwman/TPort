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
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.tport.TPortInventories.openTPortGUI;
import static com.spaceman.tport.events.InventoryClick.tpPlayerToTPort;
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

            if (tportData.getConfig().contains("tport." + newPlayerUUID + ".items")) {
                for (String i : tportData.getConfig().getConfigurationSection("tport." + newPlayerUUID + ".items").getKeys(false)) {
                    if (tportData.getConfig().contains("tport." + newPlayerUUID + ".items." + i + ".item")) {
//                        ItemStack items = tportData.getConfig().getItemStack("tport." + newPlayerUUID + ".items." + i + ".item");
                        if (args[2].equals(tportData.getConfig().getString("tport." + newPlayerUUID + ".items." + i + ".name"))) {

                            if (!newPlayerUUID.equals(player.getUniqueId().toString())) {

                                if (tportData.getConfig().getString("tport." + newPlayerUUID + ".items." + i + ".private.statement").equals("on")) {

                                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                            .getStringList("tport." + newPlayerUUID + ".items." + i + ".private.players");
                                    if (!list.contains(player.getUniqueId().toString())) {
                                        player.sendMessage(ChatColor.RED + "You are not whitelisted to this private TPort");
                                        return;
                                    }
                                } else if (tportData.getConfig().getString("tport." + newPlayerUUID + ".items." + i + ".private.statement").equals("online")) {
                                    if (Bukkit.getPlayer(UUID.fromString(newPlayerUUID)) == null) {
                                        player.sendMessage(ChatColor.RED + "You can't teleport to this teleport, " + ChatColor.DARK_RED + newPlayerName + ChatColor.RED + " has set this TPort to 'online'");
                                        return;
                                    }
                                }
                            }

                            player.closeInventory();

                            Location l = Main.getLocation("tport." + newPlayerUUID + ".items." + i + ".location");

                            if (l == null) {
                                player.sendMessage("§cThe world for this location has not been found");
                                return;
                            }
                            tpPlayerToTPort(player, l, args[2], newPlayerUUID);

                            Message message = new Message();
                            message.addText("Teleported to ", ChatColor.DARK_AQUA);
                            message.addText(textComponent(args[2],
                                    ChatColor.BLUE, ClickEvent.runCommand("/tport open " + args[1] + " " + args[2])));
                            message.sendMessage(player);

//                        player.sendMessage("§3Teleported to §9" + items.getItemMeta().getDisplayName());
                            b = false;
                        }
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

        openTPortGUI(newPlayerName, newPlayerUUID, player);

    }
}
