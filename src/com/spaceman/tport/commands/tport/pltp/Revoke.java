package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.commands.tport.pltp.Consent.pltpConsentMap;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class Revoke extends SubCommand {
    
    public Revoke() {
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(textComponent("This command is used to revoke your PLTP request of the given player", ColorType.infoColor));
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return pltpConsentMap.keySet().stream()
                .filter(uuid -> pltpConsentMap.get(uuid).contains(player.getUniqueId()))
                .map(PlayerUUID::getPlayerName)
                .collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP revoke <player>
        
        if (args.length == 3) {
            UUID tpUUID = PlayerUUID.getPlayerUUID(args[2]);
            if (tpUUID != null) {
                ArrayList<UUID> list = pltpConsentMap.getOrDefault(tpUUID, new ArrayList<>());
                if (list.remove(player.getUniqueId())) {
                    if (list.isEmpty()) {
                        pltpConsentMap.remove(tpUUID);
                    } else {
                        pltpConsentMap.put(tpUUID, list);
                    }
                    sendSuccessTheme(player, "Successfully revoked PLTP request to %s", args[2]);
    
                    Player tpPlayer = Bukkit.getPlayer(tpUUID);
                    if (tpPlayer != null) {
                        sendInfoTheme(tpPlayer, "Player %s has revoked its PLTP request", player.getName());
                    }
                } else {
                    sendErrorTheme(player, "You are not requesting to teleport to player %s", args[2]);
                }
            } else {
                sendErrorTheme(player, "Could not find a player named %s", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport PLTP revoke <player>");
        }
    }
}
