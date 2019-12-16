package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.commands.tport.pltp.Consent.pltpConsentMap;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class Accept extends SubCommand {
    
    public Accept() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("player", ArgumentType.OPTIONAL);
        emptyCommand.setCommandDescription(textComponent("This command is used to accept the PLTP request of the given player(s)", ColorTheme.ColorType.infoColor));
        emptyCommand.setTabRunnable((args, player) -> pltpConsentMap.getOrDefault(player.getUniqueId(), new ArrayList<>())
                .stream().map(PlayerUUID::getPlayerName).filter(Objects::nonNull).collect(Collectors.toList()));
        emptyCommand.setLooped(true);
        addAction(emptyCommand);
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to accept the PLTP request of your only have 1 request", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getActions().get(0).tabList(player, args);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP accept [player...]
        
        if (args.length == 2) {
            ArrayList<UUID> list = pltpConsentMap.getOrDefault(player.getUniqueId(), new ArrayList<>());
            
            if (list.size() == 1) {
                Player tpPlayer = Bukkit.getPlayer(list.get(0));
                list.remove(list.get(0));
                if (tpPlayer == null) {
                    String playerName = PlayerUUID.getPlayerName(list.get(0));
                    if (playerName != null) {
                        sendErrorTheme(player, "Could not find a player named %s", playerName);
                    }
                    return;
                }
                sendInfoTheme(tpPlayer, "Player %s has accepted your PLTP request", player.getName());
                if (!TP.tp(tpPlayer, player)) {
                    sendErrorTheme(player, "Player %s cooldown was not cooled", tpPlayer.getName());
                }
            } else if (list.isEmpty()) {
                sendErrorTheme(player, "No PLTP teleports pending");
            } else {
                sendErrorTheme(player, "More than 1 PLTP teleports pending, please select one");
            }
        } else if (args.length > 2) {
            ArrayList<UUID> list = pltpConsentMap.getOrDefault(player.getUniqueId(), new ArrayList<>());
            for (int i = 2; i < args.length; i++) {
                UUID tpUUID = PlayerUUID.getPlayerUUID(args[i]);
                if (tpUUID != null) {
                    if (list.contains(tpUUID)) {
                        Player tpPlayer = Bukkit.getPlayer(tpUUID);
                        list.remove(list.get(0));
                        if (tpPlayer == null) {
                            sendErrorTheme(player, "Player %s is not online anymore, request removed", args[i]);
                            return;
                        }
                        sendInfoTheme(tpPlayer, "Player %s has accepted your PLTP request", player.getName());
                        if (!TP.tp(tpPlayer, player)) {
                            sendErrorTheme(player, "Player %s cooldown was not cooled", tpPlayer.getName());
                        }
                    } else {
                        sendErrorTheme(player, "Player %s has not requested to teleport to you", args[i]);
                    }
                } else {
                    sendErrorTheme(player, "Could not find a player named %s", args[i]);
                }
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport PLTP accept [player...]");
        }
    }
}
