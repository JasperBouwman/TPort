package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static com.spaceman.tport.events.CompassEvents.giveCompass;

public class Compass extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {
        //tport compass [player] [TPort name]

        if (args.length == 1) {
            giveCompass(player);
            sendMessage(player);
        } else if (args.length == 2) {
            giveCompass(player, args[1]);
            sendMessage(player);
        } else if (args.length == 3) {
            giveCompass(player, args[1], args[2]);
            sendMessage(player);
        } else {
            player.sendMessage(ChatColor.RED + "Use: /tport compass [player] [TPort name]");
        }
    }

    private void sendMessage(Player player) {
        player.sendMessage(ChatColor.BLUE + "Right-click with the with compass to activate it\n" +
                "When sneaking you can open chests and other blocks");
    }
}
