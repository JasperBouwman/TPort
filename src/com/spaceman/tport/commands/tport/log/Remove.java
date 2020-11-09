package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Remove extends SubCommand {
    
    public Remove() {
        EmptyCommand emptyTPortPlayer = new EmptyCommand();
        emptyTPortPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyTPortPlayer.setCommandDescription(TextComponent.textComponent("This command is used to remove the player form the TPort log", ColorTheme.ColorType.infoColor));
        emptyTPortPlayer.setTabRunnable((args, player) -> {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                List<String> list = tport.getLogged().stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
                list.removeAll(Arrays.asList(args).subList(3, args.length));
                return list;
            } else {
                return Collections.emptyList();
            }
        });
        emptyTPortPlayer.setLooped(true);
        
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.addAction(emptyTPortPlayer);
        
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return TPortManager.getTPortList(player.getUniqueId()).stream().filter(TPort::isLogged).map(TPort::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log remove <TPort name> <player...>
        
        if (args.length > 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                for (int i = 3; i < args.length; i++) {
                    String playerName = args[i];
                    UUID playerUUID = PlayerUUID.getPlayerUUID(playerName);
                    if (playerUUID == null) {
                        sendErrorTheme(player, "Could not find a player named %s", playerName);
                        continue;
                    }
                    if (tport.removeLogged(playerUUID)) {
                        sendSuccessTheme(player, "Successfully stopped logging player %s", playerName);
                    } else {
                        sendErrorTheme(player, "Player %s was nog logged", playerName);
                    }
                }
                tport.save();
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport log remove <TPort name> <player...>");
        }
    }
}
