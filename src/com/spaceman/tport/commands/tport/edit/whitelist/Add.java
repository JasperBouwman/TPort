package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Add extends SubCommand {
    private final EmptyCommand emptyPlayer;
    
    public Add() {
        emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(textComponent("This command is used to add players to the whitelist of the given TPort", ColorType.infoColor));
        emptyPlayer.setTabRunnable((args, player) -> {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport != null) {
                List<String> list = getFile("TPortData").getKeys("tport").stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
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
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> whitelist add <player...>");
            return;
        }
        
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        
        if (tport == null) {
            sendErrorTheme(player, "No TPort found called %s", args[1]);
            return;
        }
        if (tport.isOffered()) {
            sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
            return;
        }
        for (int i = 4; i < args.length; i++) {
            
            String newPlayerName = args[i];
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
            if (newPlayerUUID == null || !getFile("TPortData").getConfig().contains("tport." + newPlayerUUID)) {
                sendErrorTheme(player, "Could not find a player named %s", newPlayerName);
                return;
            }
            
            if (newPlayerUUID.equals(player.getUniqueId())) {
                sendErrorTheme(player, "You don't have to put yourself in your whitelist");
                continue;
            }
            if (tport.getWhitelist().contains(newPlayerUUID)) {
                sendErrorTheme(player, "Player %s is already in your whitelist", newPlayerName);
                continue;
            }

            sendSuccessTheme(player, "Successfully added %s", newPlayerName);
    
            Player newPlayer = Bukkit.getPlayer(newPlayerUUID);
            if (newPlayer != null) {
                sendInfoTheme(newPlayer, "You have been added in the whitelist of %s in the TPort %s", player.getName(), tport.getName());
            }
        }
        tport.save();
    }
}
