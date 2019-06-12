package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.pltp.TP;
import com.spaceman.tport.commands.tport.pltp.Whitelist;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PLTP extends SubCommand {
    
    public PLTP() {
        addAction(new Whitelist());
        addAction(new TP());
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>(super.tabList(player, args));
        list.add("on");
        list.add("off");
        list.add("tp");
        return list;
    }
    
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public void run(String[] args, Player player) {
        
        // tport PLTP tp <player>
        // tport PLTP [on:off]
        // tport PLTP whitelist list
        // tport PLTP whitelist [add:remove] <playername>
        
        if (!Permissions.hasPermission(player, "TPort.command.pltp", false)) {
            if (!Permissions.hasPermission(player, "TPort.basic", false)) {
                Permissions.sendNoPermMessage(player, "TPort.command.pltp", "TPort.basic");
                return;
            }
        }
        if (args.length == 1) {
            player.sendMessage("§cUse: §4/tport PLTP <on:off:whitelist:tp>");
            return;
        }
        
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();
        
        if (args[1].equalsIgnoreCase("on")) {
            
            if (args.length != 2) {
                player.sendMessage("§cUse: §4/tport PLTP [on:off]");
                return;
            }
            
            if (tportData.getConfig().getString("tport." + playerUUID + ".tp.statement").equals("on")) {
                player.sendMessage("§cThis is already set PLTP to on");
                return;
            }
            
            player.sendMessage("§3Successfully set to on");
            tportData.getConfig().set("tport." + playerUUID + ".tp.statement", "on");
            tportData.saveConfig();
            
            
        } else if (args[1].equalsIgnoreCase("off")) {
            
            if (args.length != 2) {
                player.sendMessage("§cUse: §4/tport PLTP [on:off]");
                return;
            }
            
            if (tportData.getConfig().getString("tport." + playerUUID + ".tp.statement").equals("off")) {
                player.sendMessage("§cThis is already set PLTP to off");
                return;
            }
            
            player.sendMessage("§3Successfully set to off");
            tportData.getConfig().set("tport." + playerUUID + ".tp.statement", "off");
            tportData.saveConfig();
            
        } else if (args[1].equalsIgnoreCase("whitelist")) {
            this.getActions().get(0).run(args, player);
        } else if (args[1].equalsIgnoreCase("tp")) {
            this.getActions().get(1).run(args, player);
        } else {
            player.sendMessage("§cUse: §4/tport PLTP <on:off:whitelist:tp>");
        }
    }
}
