package com.spaceman.tport.commands.tport.pltp.whitelist;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Add extends SubCommand {
    
    public Add() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("player", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(textComponent("This command is used to add players to your PLTP whitelist", ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.PLTP.edit", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        emptyCommand.setTabRunnable((args, player) -> {
            List<String> list;
            Files tportData = GettingFiles.getFile("TPortData");
    
            list = GettingFiles.getFile("TPortData").getKeys("tport").stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
            list.remove(player.getName());
    
            tportData.getConfig().getStringList("tport." + player.getUniqueId() + ".tp.players").stream()
                    .map(PlayerUUID::getPlayerName).filter(Objects::nonNull).forEach(list::remove);
            list.removeAll(Arrays.asList(args).subList(3, args.length));
            return list;
        });
        emptyCommand.setLooped(true);
        addAction(emptyCommand);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getActions().get(0).tabList(player, args);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP whitelist add <player...>
        
        if (!hasPermission(player, true, "TPort.PLTP.edit", "TPort.basic")) {
            return;
        }
        
        Files tportData = GettingFiles.getFile("TPortData");
        UUID playerUUID = player.getUniqueId();
        ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".tp.players");
        
        for (int i = 3; i < args.length; i++) {
            String addPlayerName = args[i];
            UUID addPlayerUUID = PlayerUUID.getPlayerUUID(addPlayerName);
    
            if (addPlayerUUID == null || !tportData.getConfig().contains("tport." + addPlayerUUID)) {
                sendErrorTheme(player, "Could not find a player named %s", addPlayerName);
                return;
            }
    
            if (addPlayerUUID.equals(playerUUID)) {
                sendErrorTheme(player, "You don't have to put yourself in your PLTP whitelist");
                continue;
            }
    
            if (list.contains(addPlayerUUID.toString())) {
                sendErrorTheme(player, "Player %s is already in your PLTP whitelist");
                continue;
            }
    
            list.add(addPlayerUUID.toString());
            tportData.getConfig().set("tport." + playerUUID + ".tp.players", list);
            tportData.saveConfig();
//            player.sendMessage("§3Successfully added");
            sendSuccessTheme(player, "Successfully added player %s to your PLTP whitelist");
    
            Player addPlayer = Bukkit.getPlayer((addPlayerUUID));
            if (addPlayer != null) {
//                addPlayer.sendMessage("§3You are new in the main whitelist of §9" + player.getName());
                sendInfoTheme(addPlayer, "You have been added in the PLTP whitelist of player %s", player.getName());
            }
        }
    }
}
