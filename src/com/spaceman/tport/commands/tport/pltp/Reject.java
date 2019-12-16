package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.commands.tport.pltp.Consent.pltpConsentMap;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class Reject extends SubCommand {
    
    public Reject() {
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(textComponent("This command is used to reject the PLTP request of the given player", ColorType.infoColor));
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return pltpConsentMap.getOrDefault(player.getUniqueId(), new ArrayList<>())
                .stream().map(PlayerUUID::getPlayerName).filter(Objects::nonNull).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP reject <player>
        
        if (args.length == 3) {
            UUID tpUUID = PlayerUUID.getPlayerUUID(args[2]);
            if (tpUUID != null) {
                ArrayList<UUID> list = pltpConsentMap.getOrDefault(player.getUniqueId(), new ArrayList<>());
                if (list.remove(tpUUID)) {
                    if (list.isEmpty()) {
                        pltpConsentMap.remove(player.getUniqueId());
                    } else {
                        pltpConsentMap.put(player.getUniqueId(), list);
                    }
                    sendSuccessTheme(player, "Successfully rejected PLTP request from %s", args[2]);
                    
                    Player tpPlayer = Bukkit.getPlayer(tpUUID);
                    if (tpPlayer != null) {
                        sendInfoTheme(tpPlayer, "Player %s has rejected your PLTP request", player.getName());
                    }
                } else {
                    sendErrorTheme(player, "Player %s has not requested to teleport to you", args[2]);
                }
            } else {
                sendErrorTheme(player, "Could not find a player named %s", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport PLTP reject <player>");
        }
    }
}
