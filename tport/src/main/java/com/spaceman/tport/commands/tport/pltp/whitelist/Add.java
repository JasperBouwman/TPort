package com.spaceman.tport.commands.tport.pltp.whitelist;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.pltp.Whitelist;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.*;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Add extends SubCommand {
    
    private final EmptyCommand emptyPlayer;
    
    public Add() {
        emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(formatInfoTranslation("tport.command.PLTP.whitelist.add.players.commandDescription"));
        emptyPlayer.setTabRunnable((args, player) -> {
            if (emptyPlayer.hasPermissionToRun(player, false)) {
                return Collections.emptyList();
            }
            List<String> list;
            
            list = PlayerUUID.getPlayerNames();
            list.remove(player.getName());
            
            Whitelist.getPLTPWhitelist(player).stream()
                    .map(PlayerUUID::getPlayerName).filter(Objects::nonNull).forEach(list::remove);
            list.removeAll(Arrays.asList(args).subList(3, args.length));
            return list;
        });
        emptyPlayer.setLooped(true);
        emptyPlayer.setPermissions("TPort.PLTP.edit", "TPort.basic");
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyPlayer.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return emptyPlayer.tabList(player, args);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP whitelist add <player...>
        
        if (args.length < 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP whitelist add <player...>");
            return;
        }
        if (!emptyPlayer.hasPermissionToRun(player, true)) {
            return;
        }
        
        ArrayList<String> list = Whitelist.getPLTPWhitelist(player);
        
        for (int i = 3; i < args.length; i++) {
            String addPlayerName = args[i];
            UUID addPlayerUUID = PlayerUUID.getPlayerUUID(addPlayerName, player);
            if (addPlayerUUID == null) {
                return;
            }
            
            if (addPlayerUUID.equals(player.getUniqueId())) {
                sendErrorTranslation(player, "tport.command.PLTP.whitelist.add.players.yourself");
                continue;
            }
            
            if (list.contains(addPlayerUUID.toString())) {
                sendErrorTranslation(player, "tport.command.PLTP.whitelist.add.players.alreadyInList",
                       asPlayer(addPlayerUUID));
                continue;
            }
            
            list.add(addPlayerUUID.toString());
            Whitelist.setPLTPWhitelist(player, list);
            sendSuccessTranslation(player, "tport.command.PLTP.whitelist.add.players.succeeded",
                    asPlayer(addPlayerUUID));
            
            sendInfoTranslation(Bukkit.getPlayer(addPlayerUUID), "tport.command.PLTP.whitelist.add.players.succeededOtherPlayer", asPlayer(player));
        }
    }
}
