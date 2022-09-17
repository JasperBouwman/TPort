package com.spaceman.tport.commands.tport.teleporter.create;

import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.teleporter.Create.createTeleporter;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class TPort extends SubCommand {
    
    public TPort() {
        EmptyCommand emptyTPortPlayerTPort = new EmptyCommand();
        emptyTPortPlayerTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyTPortPlayerTPort.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.tport.player.tportName.commandDescription"));
        emptyTPortPlayerTPort.setPermissions("TPort.teleporter.create");
        EmptyCommand emptyTPortPlayer = new EmptyCommand();
        emptyTPortPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyTPortPlayer.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.tport.player.commandDescription"));
        emptyTPortPlayer.setTabRunnable(((args, player) -> {
            UUID otherUUID = PlayerUUID.getPlayerUUID(args[3]);
            if (otherUUID == null) {
                return Collections.emptyList();
            }
            return TPortManager.getTPortList(otherUUID).stream()
                    .map(com.spaceman.tport.tport.TPort::getName)
                    .collect(Collectors.toList());
        }));
        emptyTPortPlayer.addAction(emptyTPortPlayerTPort);
        emptyTPortPlayer.setPermissions("TPort.teleporter.create");
        
        addAction(emptyTPortPlayer);
        
        setPermissions("TPort.teleporter.create");
    }
    
    @Override
    public String getName(String arg) {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return PlayerUUID.getPlayerNames();
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.teleporter.create.tport.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport teleporter create TPort [player] [TPort name]
        
        if (args.length > 2 && args.length < 6) {
            if (!this.hasPermissionToRun(player, true)) {
                return;
            }
            
            ArrayList<Message> addedLore = new ArrayList<>();
            
            String newPlayerName = null;
            UUID newPlayerUUID = null;
            String tportUUID = null;
            if (args.length > 3) {
                Pair<String, UUID> profile = PlayerUUID.getProfile(args[3], player);
                newPlayerName = profile.getLeft();
                newPlayerUUID = profile.getRight();
                if (newPlayerUUID == null) {
                    return;
                }
                addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.tport.player", newPlayerName));
            }
            if (args.length > 4) {
                com.spaceman.tport.tport.TPort tport = TPortManager.getTPort(newPlayerUUID, args[4]);
                if (tport == null) {
                    sendErrorTranslation(player, "tport.command.noTPortFound", args[4]);
                    return;
                }
                String tportName = tport.getName();
                tportUUID = tport.getTportID().toString();
                addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.tport.player", tportName));
            }
            String newPlayerUUIDString = (newPlayerUUID == null ? null : newPlayerUUID.toString());
            
            createTeleporter(player, "TPort",
                    newPlayerName == null ? "" : "open",
                    List.of(new Pair<>("teleporterTPortUUID", tportUUID), new Pair<>("teleporterPlayerUUID", newPlayerUUIDString)),
                    addedLore);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create TPort [player] [TPort name]");
        }
    }
}
