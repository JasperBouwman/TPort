package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.logbook.Logbook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;

public class List extends SubCommand {

    @Override
    public java.util.List<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }

    @Override
    public void run(String[] args, Player player) {
        //tport log list [TPort name]

        if (args.length == 2) {
            ArrayList<String> log = Logbook.getList(player.getUniqueId());
            Message message = new Message();
            message.addText(TextComponent.textComponent("Logged TPorts: ", ChatColor.DARK_AQUA));
            message.addText("");
            for (String loggedTPort : log) {

                HoverEvent hEvent = HoverEvent.hoverEvent("");
                int i = 0;
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (i == 4) {
                        hEvent.removeLast();
                        hEvent.addText(TextComponent.textComponent("...", ChatColor.DARK_AQUA));
                        break;
                    }
                    if (Logbook.isLogged(player.getUniqueId(), loggedTPort, offlinePlayer.getUniqueId())) {
                        hEvent.addText(TextComponent.textComponent(offlinePlayer.getName(), ChatColor.DARK_AQUA));
                        hEvent.addText(TextComponent.textComponent("," + TextComponent.NEW_LINE, ChatColor.BLUE));
                        i++;
                    }
                }
                hEvent.removeLast();

                message.addText(TextComponent.textComponent(loggedTPort, ChatColor.BLUE, hEvent));
                message.addText(TextComponent.textComponent(", ", ChatColor.DARK_AQUA));
            }
            message.removeLast();

            message.sendMessage(player);

        } else if (args.length == 3) {

            ArrayList<String> log = Logbook.getList(player.getUniqueId());
            if (log.contains(args[2])) {
                Message message = new Message();

                HoverEvent hEvent = HoverEvent.hoverEvent("");
                int i = 0;
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (i == 4) {
                        hEvent.removeLast();
                        hEvent.addText(TextComponent.textComponent("...", ChatColor.DARK_AQUA));
                        break;
                    }
                    if (Logbook.isLogged(player.getUniqueId(), args[2], offlinePlayer.getUniqueId())) {
                        hEvent.addText(TextComponent.textComponent(offlinePlayer.getName(), ChatColor.DARK_AQUA));
                        hEvent.addText(TextComponent.textComponent("," + TextComponent.NEW_LINE, ChatColor.BLUE));
                        i++;
                    }
                }
                hEvent.removeLast();
                message.addText(TextComponent.textComponent("TPort ", ChatColor.DARK_AQUA));
                message.addText(TextComponent.textComponent(args[2], ChatColor.BLUE, hEvent));
                message.addText(TextComponent.textComponent(" is logged", ChatColor.DARK_AQUA));

                message.sendMessage(player);
            } else {
                player.sendMessage("TPort " + args[2] + " is not logged");//todo colors
            }

        } else {
            player.sendMessage("§cUse: §4/tport log list [TPort name]");
        }

    }
}
