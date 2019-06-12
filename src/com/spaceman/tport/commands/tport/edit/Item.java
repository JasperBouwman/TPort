package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Item extends SubCommand {

    @Override
    public void run(String[] args, Player player) {
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
            if (name.equalsIgnoreCase(args[1])) {
                ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item");
                ItemMeta meta = item.getItemMeta();

                if (!Permissions.hasPermission(player, "TPort.command.edit.item", false)) {
                    if (!Permissions.hasPermission(player, "TPort.basic", false)) {
                        Permissions.sendNoPermMessage(player, "TPort.command.edit.item", "TPort.basic");
                        return;
                    }
                }

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
                } else {
                    player.sendMessage("§cUse: §4/tport edit <TPort name> item");
                }

                return;
            }
        }
        player.sendMessage("§cNo TPort found called §4" + args[1]);
    }
}
