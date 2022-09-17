package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.events.PreviewEvents;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class Preview extends SubCommand {
    
    private final EmptyCommand emptyPreviewPlayerTPort;
    
    public Preview() {
        emptyPreviewPlayerTPort = new EmptyCommand();
        emptyPreviewPlayerTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyPreviewPlayerTPort.setCommandDescription(formatInfoTranslation("tport.command.preview.player.tportName.commandDescription"));
        emptyPreviewPlayerTPort.setPermissions("TPort.preview", "TPort.basic");
        
        EmptyCommand emptyPreviewPlayer = new EmptyCommand();
        emptyPreviewPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPreviewPlayer.setTabRunnable((args, player) -> {
            UUID argOneUUID = PlayerUUID.getPlayerUUID(args[1]);
            if (argOneUUID == null) {
                return Collections.emptyList();
            }
            return TPortManager.getTPortList(argOneUUID).stream().map(TPort::getName).collect(Collectors.toList());
        });
        emptyPreviewPlayer.addAction(emptyPreviewPlayerTPort);
        
        addAction(emptyPreviewPlayer);
        
        PreviewEvents.register();
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return PlayerUUID.getPlayerNames();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport preview <player> <TPort name>
        
        if (args.length != 3) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport preview <player> <TPort name>");
            return;
        }
        if (!emptyPreviewPlayerTPort.hasPermissionToRun(player, true)) {
            return;
        }
        
        String newPlayerName = args[1];
        UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName, player);
        if (newPlayerUUID == null) {
            return;
        }
        
        TPort tport = TPortManager.getTPort(newPlayerUUID, args[2]);
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
            return;
        }
        
        if (tport.getPreviewState() == TPort.PreviewState.OFF) {
            sendInfoTranslation(player, "tport.command.preview.player.tportName.stateOFF", tport.getPreviewState());
            return;
        }
        
        PreviewEvents.preview(player, tport);
        if (tport.getPreviewState() == TPort.PreviewState.NOTIFIED) {
            sendInfoTranslation(Bukkit.getPlayer(tport.getOwner()), "tport.command.preview.player.tportName.notifyOwner", asPlayer(player), asTPort(tport));
        }
    
    }
}
