package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.Main;
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

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Add extends SubCommand {
    private final EmptyCommand emptyPlayer;
    
    public Add() {
        emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(formatInfoTranslation("tport.command.edit.whitelist.add.commandDescription"));
        emptyPlayer.setTabRunnable((args, player) -> {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport != null) {
                List<String> list = Main.getPlayerNames();
                list.remove(player.getName());
                tport.getWhitelist().stream().map(PlayerUUID::getPlayerName).filter(Objects::nonNull).forEach(list::remove);
                list.removeAll(Arrays.asList(args).subList(4, args.length));
                return list;
            } else {
                return new ArrayList<>();
            }
        });
        emptyPlayer.setLooped(true);
        emptyPlayer.setPermissions("TPort.edit.whitelist.add", "TPort.basic");
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getActions().get(0).tabList(player, args);
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport edit <TPort name> whitelist add <player...>
        
        if (!emptyPlayer.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> whitelist add <player...>");
            return;
        }
        
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
            return;
        }
        if (tport.isOffered()) {
            sendErrorTranslation(player, "tport.command.edit.whitelist.add.isOffered",
                    tport, asPlayer(tport.getOfferedTo()));
            return;
        }
        for (int i = 4; i < args.length; i++) {
            
            String newPlayerName = args[i];
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
            if (newPlayerUUID == null || !getFile("TPortData").getConfig().contains("tport." + newPlayerUUID)) {
                sendErrorTranslation(player, "tport.command.playerNotFound", newPlayerName);
                return;
            }
            
            if (newPlayerUUID.equals(player.getUniqueId())) {
                sendErrorTranslation(player, "tport.command.edit.whitelist.add.addYourself");
                continue;
            }
            Player newPlayer = Bukkit.getPlayer(newPlayerUUID);
            if (tport.addWhitelist(newPlayerUUID)) {
                sendSuccessTranslation(player, "tport.command.edit.whitelist.add.succeeded", asPlayer(newPlayer, newPlayerUUID), tport);
            } else {
                sendErrorTranslation(player, "tport.command.edit.whitelist.add.alreadyInList", asPlayer(newPlayer, newPlayerUUID), tport);
                continue;
            }
    
            sendInfoTranslation(newPlayer, "tport.command.edit.whitelist.add.succeededOtherPlayer", player, tport);
        }
        tport.save();
    }
}
