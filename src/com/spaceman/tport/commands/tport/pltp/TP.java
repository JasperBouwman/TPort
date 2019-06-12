package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.spaceman.tport.events.InventoryClick.tpPlayerToPlayer;

public class TP extends SubCommand {
    
    @Override
    public String getName() {
        return "tp";
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        Files tportData = GettingFiles.getFile("TPortData");
        for (String s : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {
            list.add(PlayerUUID.getPlayerName(s));
        }
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport PLTP tp <player>
    
        if (args.length == 3) {
            if (!Permissions.hasPermission(player, "TPort.pltp.tp")) {
                return;
            }
        
            Player warp = Bukkit.getPlayerExact(args[2]);
            if (warp == null) {
                player.sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_RED + args[2] + ChatColor.RED + " is not online/does not exist");
                return;
            }
        
            long cooldown = CooldownManager.PlayerTP.getTime(player);
            if (cooldown / 1000 > 0) {
                player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                return;
            }
        
            tpPlayerToPlayer(player, warp);
            player.sendMessage("§3Teleported to §9" + warp.getName());
            CooldownManager.PlayerTP.update(player);
        } else {
            player.sendMessage(ChatColor.RED + "Usage: " + ChatColor.DARK_RED + "/tport PLTP tp <player>");
        }
        
    }
}
