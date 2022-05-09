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
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Accept extends SubCommand {
    
    public static final EmptyCommand emptyPlayerTPort = new EmptyCommand();
    
    public Accept() {
        emptyPlayerTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyPlayerTPort.setCommandDescription(formatInfoTranslation("tport.command.transfer.accept.player.tportName.commandDescription"));
        emptyPlayerTPort.setPermissions("TPort.transfer.accept", "TPort.basic");
        
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setTabRunnable((args, player) -> TPortManager.getTPortList(PlayerUUID.getPlayerUUID(args[2])).
                stream().filter(tport -> player.getUniqueId().equals(tport.getOfferedTo())).map(TPort::getName).collect(Collectors.toList()));
        emptyPlayer.addAction(emptyPlayerTPort);
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (!emptyPlayerTPort.hasPermissionToRun(player, false)) {
            return list;
        }
        for (String uuidString : getFile("TPortData").getKeys("tport")) {
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
        
        if (!emptyPlayerTPort.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 4) {
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(args[2]);
            if (newPlayerUUID == null || !getFile("TPortData").getConfig().contains("tport." + newPlayerUUID)) {
                sendErrorTranslation(player, "tport.command.playerNotFound", args[2]);
                return;
            }
            
            TPort tport = TPortManager.getTPort(newPlayerUUID, args[3]);
            if (tport != null) {
                if (player.getUniqueId().equals(tport.getOfferedTo())) {
                    if (TPortManager.getTPort(player.getUniqueId(), tport.getName()) == null) {
                        UUID oldOwner = tport.getOwner();
                        if (TPortManager.addTPort(player, tport, true) != null) {
                            tport.getWhitelist().remove(player.getUniqueId());
                            if (!tport.getWhitelist().contains(oldOwner)) tport.getWhitelist().add(oldOwner);
                            tport.save();
                            Player oldPlayer = Bukkit.getPlayer(oldOwner);
                            sendSuccessTranslation(player, "tport.command.transfer.accept.player.tportName.succeeded", asTPort(tport),
                                    asPlayer(oldPlayer, oldOwner));
                            
                            if (oldPlayer != null) {
                                sendInfoTranslation(oldPlayer, "tport.command.transfer.accept.player.tportName.succeededOtherPlayer", asPlayer(player), asTPort(tport));
                            }
                        } else {
                            Player oldPlayer = Bukkit.getPlayer(oldOwner);
                            if (oldPlayer != null) {
                                sendInfoTranslation(oldPlayer, "tport.command.transfer.accept.player.tportName.couldNotAccept", asPlayer(player), asTPort(tport));
                            }
                        }
                    } else {
                        sendErrorTranslation(player, "tport.command.transfer.accept.player.tportName.alreadyHasName", asTPort(tport));
                    }
                } else {
                    sendErrorTranslation(player, "tport.command.transfer.accept.player.tportName.notOffered", asTPort(tport));
                }
            } else {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[3]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport transfer accept <player> <TPort name>");
        }
    }
}
