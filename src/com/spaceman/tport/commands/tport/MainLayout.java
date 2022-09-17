package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.mainLayout.Players;
import com.spaceman.tport.commands.tport.mainLayout.TPorts;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportData;

public class MainLayout extends SubCommand {
    
    public MainLayout() {
        addAction(Players.getInstance());
        addAction(TPorts.getInstance());
    }
    
    public static boolean showPlayers(Player player) {
        return tportData.getConfig().getBoolean("tport." + player.getUniqueId() + ".mainLayout.players", true);
    }
    
    public static boolean showTPorts(Player player) {
        return tportData.getConfig().getBoolean("tport." + player.getUniqueId() + ".mainLayout.tports", false);
    }
    
    public static void showPlayers(Player player, boolean state) {
        tportData.getConfig().set("tport." + player.getUniqueId() + ".mainLayout.players", state);
        tportData.saveConfig();
    }
    
    public static void showTPorts(Player player, boolean state) {
        tportData.getConfig().set("tport." + player.getUniqueId() + ".mainLayout.tports", state);
        tportData.saveConfig();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport mainLayout players [state]
        // tport mainLayout TPorts [state]
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport mainLayout " + CommandTemplate.convertToArgs(getActions(), false));
    }
}
