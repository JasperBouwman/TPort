package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.pltp.whitelist.Add;
import com.spaceman.tport.commands.tport.pltp.whitelist.Remove;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Whitelist extends SubCommand {

    public Whitelist() {
        addAction(new Add());
        addAction(new Remove());
    }

    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("list");
        list.add("add");
        list.add("remove");
        return list;
    }

    @Override
    public void run(String[] args, Player player) {
        // tport PLTP whitelist list
        // tport PLTP whitelist [add:remove] <playername>

        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        if (args.length == 3) {

            if (args[2].equalsIgnoreCase("list")) {
                ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                        .getStringList("tport." + playerUUID + ".tp.players");
                int i = 0;
                player.sendMessage("§3Players in your whitelist:");
                for (String ss : list) {
                    if (i == 0) {
                        player.sendMessage("§9" + PlayerUUID.getPlayerName(ss));
                        i++;
                    } else {
                        player.sendMessage("§3" + PlayerUUID.getPlayerName(ss));
                        i = 0;
                    }

                }
                return;
            }

        }

        if (args.length != 4) {
            player.sendMessage("§cUse: §4/tport PLTP whitelist [add:remove] <playername> §cor §4/tport PLTP whitelist list");
            return;
        }

        if (args[2].equalsIgnoreCase("add")) {
            getActions().get(0).run(args, player);
        } else if (args[2].equalsIgnoreCase("remove")) {
            getActions().get(1).run(args, player);
        } else {
            player.sendMessage("§cUse: §4/tport PLTP whitelist [add:remove] <playername> §cor §4/tport PLTP whitelist list");
        }

    }
}
