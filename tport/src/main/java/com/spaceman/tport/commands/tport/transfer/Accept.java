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

public class Accept extends SubCommand {
    
    private static final Accept instance = new Accept();
    public static Accept getInstance() {
        return instance;
    }
    
    public final EmptyCommand emptyPlayerTPort = new EmptyCommand();
    
    public Accept() {
        emptyPlayerTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyPlayerTPort.setCommandDescription(formatInfoTranslation("tport.command.transfer.accept.player.tportName.commandDescription"));
        emptyPlayerTPort.setPermissions("TPort.transfer.accept", "TPort.basic");
        
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setTabRunnable((args, player) -> {
            if (!emptyPlayerTPort.hasPermissionToRun(player, false)) {
                return Collections.emptyList();
            }
            UUID newUUID = PlayerUUID.getPlayerUUID(args[2]);
            if (newUUID == null) {
                return Collections.emptyList();
            }
            return TPortManager.getTPortList(newUUID).
                    stream().filter(tport -> player.getUniqueId().equals(tport.getOfferedTo())).map(TPort::getName).collect(Collectors.toList());
        });
        emptyPlayer.addAction(emptyPlayerTPort);
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (!emptyPlayerTPort.hasPermissionToRun(player, false)) {
            return list;
        }
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
        // tport transfer accept <player> <TPort name>
        
        if (args.length != 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport transfer accept <player> <TPort name>");
            return;
        }
        
        if (!emptyPlayerTPort.hasPermissionToRun(player, true)) {
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
            sendErrorTranslation(player, "tport.command.transfer.accept.player.tportName.notOffered", asTPort(tport));
            return;
        }
        
        if (TPortManager.getTPort(player.getUniqueId(), tport.getName()) != null) {
            sendErrorTranslation(player, "tport.command.transfer.accept.player.tportName.alreadyHasName", asTPort(tport));
            return;
        }
        
        UUID oldOwner = tport.getOwner();
        if (TPortManager.addTPort(player, tport, true) != null) {
            tport.getWhitelist().remove(player.getUniqueId());
            if (!tport.getWhitelist().contains(oldOwner)) tport.getWhitelist().add(oldOwner);
            tport.save();
            
            Player oldPlayer = Bukkit.getPlayer(oldOwner);
            sendSuccessTranslation(player, "tport.command.transfer.accept.player.tportName.succeeded", asTPort(tport), asPlayer(oldPlayer, oldOwner));
            sendInfoTranslation(oldPlayer, "tport.command.transfer.accept.player.tportName.succeededOtherPlayer", asPlayer(player), asTPort(tport));
        } else {
            Player oldPlayer = Bukkit.getPlayer(oldOwner);
            sendInfoTranslation(oldPlayer, "tport.command.transfer.accept.player.tportName.couldNotAccept", asPlayer(player), asTPort(tport));
        }
    }
}
