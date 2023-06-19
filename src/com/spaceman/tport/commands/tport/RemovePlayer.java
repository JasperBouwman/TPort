package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportData;

public class RemovePlayer extends SubCommand {
    
    private static final EmptyCommand emptyPlayer = new EmptyCommand();
    
    public RemovePlayer() {
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(formatInfoTranslation("tport.command.removePlayer.player.commandDescription"));
        emptyPlayer.setPermissions("TPort.admin.removePlayer");
        addAction(emptyPlayer);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        List<String> list = new ArrayList<>();
        if (emptyPlayer.hasPermissionToRun(player, false)) {
            list = tportData.getKeys("tport").stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
        }
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport removePlayer <player>
        
        if (args.length != 2) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport removePlayer <player>");
            return;
        }
        removePlayer(player, args[1]);
    }
    
    public static void removePlayer(Player player, String toRemoveName) {
        if (!emptyPlayer.hasPermissionToRun(player, true)) {
            return;
        }
        
        UUID newPlayerUUID;
        try {
            newPlayerUUID = UUID.fromString(toRemoveName);
        } catch (IllegalArgumentException lae) {
            newPlayerUUID = PlayerUUID.getPlayerUUID_OLD2(toRemoveName); //find ALL players (even not registered at TPort)
            if (newPlayerUUID == null) {
                sendErrorTranslation(player, "tport.command.playerNotFound", toRemoveName);
                return;
            }
        }
        
        Player newPlayer = Bukkit.getPlayer(newPlayerUUID);
        if (newPlayer != null) {
            sendErrorTranslation(player, "tport.command.removePlayer.player.isOnline", newPlayer);
            return;
        }
        
        if (tportData.getConfig().contains("tport." + newPlayerUUID)) {
            tportData.getConfig().set("tport." + newPlayerUUID, null);
            tportData.saveConfig();
            sendSuccessTranslation(player, "tport.command.removePlayer.player.succeeded", toRemoveName);
        } else {
            sendInfoTranslation(player, "tport.command.removePlayer.player.alreadyRemoved", toRemoveName);
        }
    }
}
