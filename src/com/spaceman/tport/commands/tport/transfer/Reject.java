package com.spaceman.tport.commands.tport.transfer;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Reject extends SubCommand {
    
    public Reject() {
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.transfer.reject.player.tportName.commandDescription"));
        
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setTabRunnable((args, player) -> {
            UUID newUUID = PlayerUUID.getPlayerUUID(args[2]);
            if (newUUID == null) {
                return Collections.emptyList();
            }
            return TPortManager.getTPortList(newUUID).
                    stream().filter(tport -> player.getUniqueId().equals(tport.getOfferedTo())).map(TPort::getName).collect(Collectors.toList());
        });
        emptyPlayer.addAction(emptyTPort);
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        for (String uuidString : tportData.getKeys("tport")) {
            UUID uuid = UUID.fromString(uuidString);
            for (TPort tport : TPortManager.getTPortList(uuid)) {
                if (player.getUniqueId().equals(tport.getOfferedTo())) {
                    list.add(PlayerUUID.getPlayerName(uuid));
                }
            }
        }
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport transfer reject <player> <TPort name>
    
        if (args.length != 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport transfer reject <player> <TPort name>");
            return;
        }
        
        UUID newPlayerUUID = PlayerUUID.getPlayerUUID(args[2], player);
        if (newPlayerUUID == null) {
            return;
        }
        
        TPort tport = TPortManager.getTPort(newPlayerUUID, args[3]);
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[3]);
            return;
        }
        
        if (!player.getUniqueId().equals(tport.getOfferedTo())) {
            sendErrorTranslation(player, "tport.command.transfer.reject.player.tportName.notOffered", asTPort(tport));
            return;
        }
        
        tport.setOfferedTo(null);
        tport.save();
        
        Player oldPlayer = Bukkit.getPlayer(tport.getOwner());
        sendSuccessTranslation(player, "tport.command.transfer.reject.player.tportName.succeeded", asTPort(tport),
                asPlayer(oldPlayer, tport.getOwner()));
        
        sendInfoTranslation(oldPlayer, "tport.command.transfer.reject.player.tportName.succeededOtherPlayer", asPlayer(player), asTPort(tport));
    }
}
