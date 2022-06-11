package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Remove extends SubCommand {
    
    public Remove() {
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(formatInfoTranslation("tport.command.edit.whitelist.remove.commandDescription"));
        emptyPlayer.setTabRunnable((args, player) -> {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport != null) {
                List<String> list = tport.getWhitelist().stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
                list.removeAll(Arrays.asList(args).subList(4, args.length - 1));
                return list;
            } else {
                return new ArrayList<>();
            }
        });
        emptyPlayer.setLooped(true);
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getActions().get(0).tabList(player, args);
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport edit <TPort name> whitelist remove <player...>
        
        if (args.length == 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> whitelist remove <player...>");
            return;
        }
        
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
            return;
        }
        if (tport.isOffered()) {
            sendErrorTranslation(player, "tport.command.edit.whitelist.remove.isOffered",
                    tport, asPlayer(tport.getOfferedTo()));
            return;
        }
        
        for (int i = 4; i < args.length; i++) {
            String newPlayerName = args[i];
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
            if (newPlayerUUID == null || !tportData.getConfig().contains("tport." + newPlayerUUID)) {
                sendErrorTranslation(player, "tport.command.playerNotFound", newPlayerName);
                return;
            }
            Player newPlayer = Bukkit.getPlayer(newPlayerUUID);
            if (tport.removeWhitelist(newPlayerUUID)) {
                sendSuccessTranslation(player, "tport.command.edit.whitelist.remove.succeeded", asPlayer(newPlayerUUID), tport);
            } else {
                sendErrorTranslation(player, "tport.command.edit.whitelist.remove.notInList", asPlayer(newPlayerUUID), tport);
                continue;
            }
            
            sendInfoTranslation(newPlayer, "tport.command.edit.whitelist.remove.succeededOtherPlayer", player, tport);
        }
        tport.save();
    }
}
