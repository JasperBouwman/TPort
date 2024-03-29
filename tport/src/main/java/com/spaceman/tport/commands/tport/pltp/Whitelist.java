package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.pltp.whitelist.Add;
import com.spaceman.tport.commands.tport.pltp.whitelist.List;
import com.spaceman.tport.commands.tport.pltp.whitelist.Remove;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Whitelist extends SubCommand {
    
    public Whitelist() {
        addAction(new Add());
        addAction(new Remove());
        addAction(new List());
    }
    
    public static ArrayList<String> getPLTPWhitelist(Player player) {
        return getPLTPWhitelist(player.getUniqueId());
    }
    public static ArrayList<String> getPLTPWhitelist(UUID uuid) {
        return (ArrayList<String>) tportData.getConfig().getStringList("tport." + uuid + ".tp.players");
    }
    
    public static void setPLTPWhitelist(Player player, java.util.List<String> whitelist) {
        tportData.getConfig().set("tport." + player.getUniqueId() + ".tp.players", whitelist);
        tportData.saveConfig();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP whitelist list
        // tport PLTP whitelist <add|remove> <player...>
        
        if (args.length > 2) {
            if (runCommands(getActions(), args[2], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "tport PLTP whitelist <add|remove|list>");
    }
}
