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

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Remove extends SubCommand {
    
    public Remove() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("player", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(textComponent("This command is used to remove players from the whitelist of the given TPort", ColorType.infoColor));
        emptyCommand.setTabRunnable((args, player) -> {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport != null) {
                List<String> list = tport.getWhitelist().stream().map(PlayerUUID::getPlayerName).filter(Objects::nonNull).collect(Collectors.toList());
                list.removeAll(Arrays.asList(args).subList(4, args.length));
                return list;
            } else {
                return new ArrayList<>();
            }
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
        //tport edit <TPort name> whitelist remove <player...>
        
        if (args.length == 4) {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> whitelist remove <player...>");
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
            if (tport.removeWhitelist(newPlayerUUID)) {
                sendSuccessTheme(player, "Successfully removed %s", newPlayerName);
            } else {
                sendErrorTheme(player, "Player %s is not in your whitelist", newPlayerName);
                continue;
            }
            
            Player newPlayer = Bukkit.getPlayer(newPlayerUUID);
            if (newPlayer != null) {
                sendInfoTheme(newPlayer, "You have been removed from the whitelist of %s in the TPort %s", player.getName(), tport.getName());
            }
        }
        tport.save();
    }
}
