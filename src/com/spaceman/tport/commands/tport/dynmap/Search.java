package com.spaceman.tport.commands.tport.dynmap;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class Search extends SubCommand {
    
    private final EmptyCommand emptyPlayerTPort;
    
    public Search() {
        emptyPlayerTPort = new EmptyCommand();
        emptyPlayerTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyPlayerTPort.setCommandDescription(textComponent("This command is used to get a link to get to the given TPort marker on Dynmap", infoColor));
        emptyPlayerTPort.setPermissions("TPort.dynmap.search", "TPort.basic");
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(textComponent("This command is used to get a link to get to the given player on Dynmap", infoColor));
        emptyPlayer.setPermissions(emptyPlayerTPort.getPermissions());
        emptyPlayer.setTabRunnable((args, player) -> {
            UUID argOneUUID = PlayerUUID.getPlayerUUID(args[2]);
            if (argOneUUID == null) {
                return Collections.emptyList();
            }
            return TPortManager.getTPortList(argOneUUID).stream()
                    .filter(tport -> tport.hasAccess(player))
                    .map(TPort::getName)
                    .collect(Collectors.toList());
        });
        emptyPlayer.addAction(emptyPlayerTPort);
        addAction(emptyPlayer);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return GettingFiles.getFile("TPortData").getKeys("tport").stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport dynmap search <player> [tport name]
        
        if (!DynmapHandler.isEnabled()) {
            DynmapHandler.sendDisableError(player);
            return;
        }
        
        if (!emptyPlayerTPort.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 3) {
            String ip = IP.getIP();
            
            if (ip == null) {
                sendErrorTheme(player, "Dynmap IP is not set, use %s to set", "/tport dynmap IP <IP>");
                return;
            }
            
            Files tportData = GettingFiles.getFile("TPortData");
            
            String newPlayerName = args[2];
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
            
            if (newPlayerUUID == null || !tportData.getConfig().contains("tport." + newPlayerUUID)) {
                sendErrorTheme(player, "Could not find a player named %s", newPlayerName);
                return;
            }
            Player newPlayer = Bukkit.getPlayer(newPlayerUUID);
            if (newPlayer == null) {
                sendErrorTheme(player, "Player %s is not online", newPlayerName);
                return;
            }
            
            Message message = new Message();
            Location l = newPlayer.getLocation();
            String url = ip + String.format("?worldname=%s&mapname=flat&zoom=6&x=%s&y=%s&z=%s#", l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
            message.addText(textComponent("Click here to search player ", infoColor,
                    hoverEvent(textComponent(url, infoColor)), ClickEvent.openUrl(url)));
            message.addText(textComponent(newPlayerName, varInfoColor,
                    hoverEvent(textComponent(url, infoColor)), ClickEvent.openUrl(url)));
            message.addText(textComponent(" on Dynmap", infoColor,
                    hoverEvent(textComponent(url, infoColor)), ClickEvent.openUrl(url)));
            
            message.sendMessage(player);
            
        } else if (args.length == 4) {
            String ip = IP.getIP();
            
            if (ip == null) {
                sendErrorTheme(player, "Dynmap IP is not set, use %s to set", "/tport dynmap IP <IP>");
                return;
            }
            
            Files tportData = GettingFiles.getFile("TPortData");
            
            String newPlayerName = args[2];
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
            
            if (newPlayerUUID == null || !tportData.getConfig().contains("tport." + newPlayerUUID)) {
                sendErrorTheme(player, "Could not find a player named %s", newPlayerName);
                return;
            }
            
            TPort tport = TPortManager.getTPort(newPlayerUUID, args[3]);
            if (tport != null) {
                
                Message message = new Message();
                Location l = tport.getLocation();
                String url = ip + String.format("?worldname=%s&mapname=flat&zoom=6&x=%s&y=%s&z=%s#", l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
                message.addText(textComponent("Click here to search TPort ", infoColor,
                        hoverEvent(textComponent(url, infoColor)), ClickEvent.openUrl(url)));
                message.addText(textComponent(tport.getName(), varInfoColor,
                        hoverEvent(textComponent(url, infoColor)), ClickEvent.openUrl(url)));
                message.addText(textComponent(" on Dynmap", infoColor,
                        hoverEvent(textComponent(url, infoColor)), ClickEvent.openUrl(url)));
                
                message.sendMessage(player);
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[3]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport dynmap search <player> [TPort name]");
        }
    }
}
