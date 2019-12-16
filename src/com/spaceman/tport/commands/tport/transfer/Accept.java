package com.spaceman.tport.commands.tport.transfer;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Accept extends SubCommand {
    
    public Accept() {
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(TextComponent.textComponent("This command is used to accept the offer of the given TPort", ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.transfer.accept", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setTabRunnable((args, player) -> TPortManager.getTPortList(PlayerUUID.getPlayerUUID(args[2])).
                stream().filter(tport -> player.getUniqueId().equals(tport.getOfferedTo())).map(TPort::getName).collect(Collectors.toList()));
        emptyPlayer.addAction(emptyTPort);
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (!hasPermission(player, false, true, "TPort.transfer.accept", "TPort.basic")) {
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
    
        if (!hasPermission(player, "TPort.transfer.accept", "TPort.basic")) {
            return;
        }
        
        if (args.length == 4) {
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(args[2]);
            if (newPlayerUUID == null || !getFile("TPortData").getConfig().contains("tport." + newPlayerUUID)) {
                sendErrorTheme(player, "Could not find a player named %s", args[2]);
                return;
            }
            
            TPort tport = TPortManager.getTPort(newPlayerUUID, args[3]);
            if (tport != null) {
                if (player.getUniqueId().equals(tport.getOfferedTo())) {
                    if (TPortManager.getTPort(player.getUniqueId(), tport.getName()) == null) {
                        UUID oldOwner = tport.getOwner();
                        if (TPortManager.addTPort(player, tport, true) != null) {
                            TPortManager.removeTPort(oldOwner, tport);
                            tport.getWhitelist().remove(player.getUniqueId());
                            tport.save();
                            sendSuccessTheme(player, "Successfully accepted the offer of TPort %s", tport.getName());
                            Player oldPlayer = Bukkit.getPlayer(oldOwner);
                            if (oldPlayer != null) {
                                sendInfoTheme(oldPlayer, "Player %s has accepted your offer of TPort %s", player.getName(), tport.getName());
                            }
                        } else {
                            Player oldPlayer = Bukkit.getPlayer(oldOwner);
                            if (oldPlayer != null) {
                                sendInfoTheme(oldPlayer, "Player %s could not accepted your offer of TPort %s", player.getName(), tport.getName());
                            }
                        }
                    } else {
                        sendErrorTheme(player, "You already have a TPort named %s", tport.getName());
                    }
                } else {
                    sendErrorTheme(player, "TPort %s is not offered to you", tport.getName());
                }
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[3]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport transfer accept <player> <TPort name>");
        }
    }
}
