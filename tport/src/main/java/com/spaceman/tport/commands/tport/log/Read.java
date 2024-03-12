package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.inventories.QuickEditInventories;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.log.TimeFormat.defaultTimeFormat;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Read extends SubCommand {
    
    public Read() {
        EmptyCommand emptyTPortPlayer = new EmptyCommand();
        emptyTPortPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyTPortPlayer.setCommandDescription(formatInfoTranslation("tport.command.log.read.tportName.player.commandDescription"));
        
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.log.read.tportName.commandDescription", LogSize.getLogSize()));
        emptyTPort.setTabRunnable((args, player) -> {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                return tport.getLogBook().stream().map(p -> PlayerUUID.getPlayerName(p.teleportedUUID())).collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
        emptyTPort.addAction(emptyTPortPlayer);
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return TPortManager.getTPortList(player.getUniqueId()).stream().filter(tport -> !tport.isLogBookEmpty()).map(TPort::getName).collect(Collectors.toList());
    }
    
    public static void readLog_chat(Player player, TPort tport) {
        ArrayList<TPort.LogEntry> log = tport.getLogBook();
        if (log.isEmpty()) {
            sendInfoTranslation(player, "tport.command.log.read.tportName.isEmpty", asTPort(tport));
            return;
        }
        String format = TimeFormat.getTimeFormat(player);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(com.spaceman.tport.commands.tport.log.TimeZone.getTimeZone(player));
        
        Message logMessage = new Message();
        boolean color = true;
        for (int i = 0, logSize = log.size(); i < logSize; i++) {
            TPort.LogEntry logEntry = log.get(i);
            Object playerRepresentation = asPlayer(logEntry.teleportedUUID());
            
            
            if (color) {
                TextComponent ownerOnlineComponent = new TextComponent(String.valueOf(logEntry.ownerOnline()), varInfoColor);
                ownerOnlineComponent.addTextEvent(new HoverEvent(formatInfoTranslation("tport.tport.tport.logEntry." + logEntry.ownerOnline() + ".description")));
                logMessage.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.log.read.tportName.listElement",
                        sdf.format(logEntry.timeOfTeleport().getTime()),
                        playerRepresentation,
                        logEntry.loggedMode() == null ? "???" : logEntry.loggedMode(),
                        logEntry.ownerOnline() == null ? "???" : ownerOnlineComponent));
            } else {
                TextComponent ownerOnlineComponent = new TextComponent(String.valueOf(logEntry.ownerOnline()), varInfo2Color);
                ownerOnlineComponent.addTextEvent(new HoverEvent(formatInfoTranslation("tport.tport.tport.logEntry." + logEntry.ownerOnline() + ".description")));
                logMessage.addMessage(formatTranslation(infoColor, varInfo2Color, "tport.command.log.read.tportName.listElement",
                        sdf.format(logEntry.timeOfTeleport().getTime()),
                        playerRepresentation,
                        logEntry.loggedMode() == null ? "???" : logEntry.loggedMode(),
                        logEntry.ownerOnline() == null ? "???" : ownerOnlineComponent));
            }
            
            if (i + 2 == logSize) logMessage.addMessage(formatInfoTranslation("tport.command.log.read.tportName.lastDelimiter"));
            else                  logMessage.addMessage(formatInfoTranslation("tport.command.log.read.tportName.delimiter"));
            
            color = !color;
        }
        logMessage.removeLast();
        
        sendInfoTranslation(player, "tport.command.log.read.tportName.succeeded", asTPort(tport), logMessage);
    }
    public static void readLog_chat(Player player, TPort tport, @Nullable UUID filterUUID) {
        if (filterUUID == null) {
            readLog_chat(player, tport);
            return;
        }
        
        ArrayList<TPort.LogEntry> log = tport.getLogBook();
        if (log.isEmpty()) {
            sendInfoTranslation(player, "tport.command.log.read.tportName.isEmpty", asTPort(tport));
            return;
        }
        String format = tportData.getConfig().getString("tport." + player.getUniqueId() + ".timeFormat", defaultTimeFormat);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone(
                tportData.getConfig().getString("tport." + player.getUniqueId() + ".timeZone", TimeZone.getDefault().getID())));
        
        int size = 0;
        Message logMessage = new Message();
        boolean color = true;
        for (int i = 0, logSize = log.size(); i < logSize; i++) {
            TPort.LogEntry logEntry = log.get(i);
            if (!logEntry.teleportedUUID().equals(filterUUID)) {
                continue;
            }
            Object playerRepresentation = asPlayer(logEntry.teleportedUUID());
            
            if (color)
                logMessage.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.log.read.tportName.player.listElement",
                        sdf.format(logEntry.timeOfTeleport().getTime()),
                        playerRepresentation,
                        logEntry.loggedMode() == null ? "???" : logEntry.loggedMode(),
                        logEntry.ownerOnline() == null ? "???" : logEntry.ownerOnline()));
            else
                logMessage.addMessage(formatTranslation(infoColor, varInfo2Color, "tport.command.log.read.tportName.player.listElement",
                        sdf.format(logEntry.timeOfTeleport().getTime()),
                        playerRepresentation,
                        logEntry.loggedMode() == null ? "???" : logEntry.loggedMode(),
                        logEntry.ownerOnline() == null ? "???" : logEntry.ownerOnline()));
            
            if (i + 2 == logSize)
                logMessage.addMessage(formatInfoTranslation("tport.command.log.read.tportName.player.lastDelimiter"));
            else logMessage.addMessage(formatInfoTranslation("tport.command.log.read.tportName.player.delimiter"));
            
            color = !color;
            size++;
        }
        logMessage.removeLast();
        
        if (size == 0) {
            sendErrorTranslation(player, "tport.command.log.read.tportName.player.playerNotInLog", asPlayer(filterUUID), asTPort(tport));
        } else {
            sendInfoTranslation(player, "tport.command.log.read.tportName.player.succeeded", asTPort(tport), asPlayer(filterUUID), logMessage);
        }
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log read <TPort name> [player]
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
                return;
            }
            if (!tport.isLogged()) {
                sendErrorTranslation(player, "tport.command.log.read.tportName.notLogged", asTPort(tport));
                return;
            }
            QuickEditInventories.openTPortLogReadGUI(player, tport);
        }
        else if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
                return;
            }
            if (!tport.isLogged()) {
                sendErrorTranslation(player, "tport.command.log.read.tportName.player.notLogged", asTPort(tport));
                return;
            }
            UUID uuid = PlayerUUID.getPlayerUUID(args[3], player);
            if (uuid == null) {
                return;
            }
            QuickEditInventories.openTPortLogReadGUI(player, tport, uuid);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log read <TPort name>");
        }
    }
}
