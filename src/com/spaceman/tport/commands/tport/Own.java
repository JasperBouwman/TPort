package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.TPort;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Own extends SubCommand {

    public static List<String> getOwnTPorts(Player player) {
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
    public List<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }

    @Override
    public void run(String[] args, Player player) {
        //tport own [TPort name]

        if (!Permissions.hasPermission(player, "TPort.command.own", false)) {
            if (!Permissions.hasPermission(player, "TPort.basic", false)) {
                Permissions.sendNoPermMessage(player, "TPort.command.own", "TPort.basic");
                return;
            }
        }

        if (args.length == 1) {
            TPort.open.run(new String[]{"open", player.getName()}, player);
        } else if (args.length == 2) {
            TPort.open.run(new String[]{"open", player.getName(), args[1]}, player);
        } else {
            player.sendMessage("§cUse: §4/tport own [TPort name]");
        }
    }
}
