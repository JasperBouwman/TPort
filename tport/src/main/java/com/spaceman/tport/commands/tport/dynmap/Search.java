package com.spaceman.tport.commands.tport.dynmap;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.DynmapCommand;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class Search extends SubCommand {
    
    private final EmptyCommand emptyPlayerTPort;
    
    public Search() {
        emptyPlayerTPort = new EmptyCommand();
        emptyPlayerTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyPlayerTPort.setCommandDescription(formatInfoTranslation("tport.command.dynmapCommand.search.player.tport.commandDescription", infoColor));
        emptyPlayerTPort.setPermissions("TPort.dynmap.search", "TPort.basic");
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(formatInfoTranslation("tport.command.dynmapCommand.search.player.commandDescription", infoColor));
        emptyPlayer.setPermissions(emptyPlayerTPort.getPermissions());
        emptyPlayer.setTabRunnable((args, player) -> {
            if (emptyPlayerTPort.hasPermissionToRun(player, false)) {
                return Collections.emptyList();
            }
            UUID argOneUUID = PlayerUUID.getPlayerUUID(args[2]);
            if (argOneUUID == null) {
                return Collections.emptyList();
            }
            List<String> list = new ArrayList<>();
            for (TPort tport : TPortManager.getTPortList(argOneUUID)) {
                Boolean access = tport.hasAccess(player);
                if (access == null || access) {
                    list.add(tport.getName());
                }
            }
            return list;
        });
        emptyPlayer.addAction(emptyPlayerTPort);
        addAction(emptyPlayer);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        if (!emptyPlayerTPort.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return PlayerUUID.getPlayerNames();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport dynmap search <player> [tport name]
        
        if (!DynmapHandler.isEnabled()) {
            DynmapCommand.sendDisableError(player);
            return;
        }
        
        if (args.length == 3) {
            if (!emptyPlayerTPort.hasPermissionToRun(player, true)) {
                return;
            }
            
            String ip = IP.getIP();
            if (ip == null) {
                sendErrorTranslation(player, "tport.command.dynmapCommand.search.player.ipNotSet", "/tport dynmap IP <IP>");
                return;
            }
            
            String newPlayerName = args[2];
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName, player);
            if (newPlayerUUID == null) {
                return;
            }
            Player newPlayer = Bukkit.getPlayer(newPlayerUUID);
            if (newPlayer == null) {
                sendErrorTranslation(player, "tport.command.playerNotOnline", asPlayer(newPlayerUUID));
                return;
            }
            
            Location location = newPlayer.getLocation();
            String url = ip + String.format("?worldname=%s&mapname=flat&zoom=6&x=%s&y=%s&z=%s#", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
            
            Message message = formatInfoTranslation("tport.command.dynmapCommand.search.player.succeeded", asPlayer(newPlayer));
            message.getText().forEach(textComponent -> textComponent
                    .setInsertion(url)
                    .addTextEvent(hoverEvent(textComponent(url, infoColor)))
                    .addTextEvent(ClickEvent.openUrl(url))
            );
            message.sendAndTranslateMessage(player);
        } else if (args.length == 4) {
            if (!emptyPlayerTPort.hasPermissionToRun(player, true)) {
                return;
            }
            
            String ip = IP.getIP();
            if (ip == null) {
                sendErrorTranslation(player, "tport.command.dynmapCommand.search.player.ipNotSet", "/tport dynmap IP <IP>");
                return;
            }
            
            String newPlayerName = args[2];
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName, player);
            if (newPlayerUUID == null) {
                return;
            }
            
            TPort tport = TPortManager.getTPort(newPlayerUUID, args[3]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[3]);
                return;
            }
            
            Location location = tport.getLocation();
            String url = ip + String.format("?worldname=%s&mapname=flat&zoom=6&x=%s&y=%s&z=%s#", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
            
            Message message = formatInfoTranslation("tport.command.dynmapCommand.search.player.tport.succeeded", asTPort(tport));
            message.getText().forEach(textComponent -> textComponent
                    .setInsertion(url)
                    .addTextEvent(hoverEvent(textComponent(url, infoColor)))
                    .addTextEvent(ClickEvent.openUrl(url))
            );
            message.sendAndTranslateMessage(player);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport dynmap search <player> [TPort name]");
        }
    }
}
