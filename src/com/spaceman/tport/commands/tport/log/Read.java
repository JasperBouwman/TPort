package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

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
                return tport.getLogBook().stream().map(p -> PlayerUUID.getPlayerName(p.getRight())).collect(Collectors.toList());
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
    
    @Override
    public void run(String[] args, Player player) {
        // tport log read <TPort name> [player]
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                if (!tport.isLogged()) {
                    sendErrorTranslation(player, "tport.command.log.read.tportName.notLogged", tport);
                    return;
                }
                ArrayList<Pair<Calendar, UUID>> log = tport.getLogBook();
                if (log.isEmpty()) {
                    sendInfoTranslation(player, "tport.command.log.read.tportName.isEmpty", tport);
                } else {
                    String format = getFile("TPortData").getConfig().getString("tport." + player.getUniqueId() + ".timeFormat", "EEE MMM dd HH:mm:ss zzz yyyy");
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone(
                            getFile("TPortData").getConfig().getString("tport." + player.getUniqueId() + ".timeZone", TimeZone.getDefault().getID())));
                    
                    Message logMessage = new Message();
                    boolean color = true;
                    for (int i = 0, logSize = log.size(); i < logSize; i++) {
                        Pair<Calendar, UUID> pair = log.get(i);
                        Object playerRepresentation = asPlayer(pair.getRight());
                        
                        if (color) logMessage.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.log.read.tportName.listElement",
                                sdf.format(pair.getLeft().getTime()),
                                playerRepresentation));
                        else       logMessage.addMessage(formatTranslation(infoColor, varInfo2Color, "tport.command.log.read.tportName.listElement",
                                sdf.format(pair.getLeft().getTime()),
                                playerRepresentation));
                        
                        if (i + 2 == logSize) logMessage.addMessage(formatInfoTranslation("tport.command.log.read.tportName.lastDelimiter"));
                        else                  logMessage.addMessage(formatInfoTranslation("tport.command.log.read.tportName.delimiter"));
                        
                        color = !color;
                    }
                    logMessage.removeLast();
                    
                    sendInfoTranslation(player, "tport.command.log.read.tportName.succeeded", tport, logMessage);
                }
            } else {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
            }
        } else if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            Files tportData = GettingFiles.getFile("TPortData");
            if (tport != null) {
                if (!tport.isLogged()) {
                    sendErrorTranslation(player, "tport.command.log.read.tportName.player.notLogged", tport);
                    return;
                }
                ArrayList<Pair<Calendar, UUID>> log = tport.getLogBook();
                if (log.isEmpty()) {
                    sendInfoTranslation(player, "tport.command.log.read.tportName.player.isEmpty", tport);
                } else {
                    UUID uuid = PlayerUUID.getPlayerUUID(args[3]);
                    if (uuid == null || !tportData.getConfig().contains("tport." + uuid)) {
                        sendErrorTranslation(player, "tport.command.playerNotFound", args[3]);
                        return;
                    }
                    
                    String format = getFile("TPortData").getConfig().getString("tport." + player.getUniqueId() + ".timeFormat", "EEE MMM dd HH:mm:ss zzz yyyy");
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone(
                            getFile("TPortData").getConfig().getString("tport." + player.getUniqueId() + ".timeZone", TimeZone.getDefault().getID())));
                    
                    int size = 0;
                    Message logMessage = new Message();
                    boolean color = true;
                    for (int i = 0, logSize = log.size(); i < logSize; i++) {
                        Pair<Calendar, UUID> pair = log.get(i);
                        if (!pair.getRight().equals(uuid)) {
                            continue;
                        }
                        Object playerRepresentation = asPlayer(pair.getRight());
                        
                        if (color) logMessage.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.log.read.tportName.player.listElement",
                                sdf.format(pair.getLeft().getTime()),
                                playerRepresentation));
                        else       logMessage.addMessage(formatTranslation(infoColor, varInfo2Color, "tport.command.log.read.tportName.player.listElement",
                                sdf.format(pair.getLeft().getTime()),
                                playerRepresentation));
                        
                        if (i + 2 == logSize) logMessage.addMessage(formatInfoTranslation("tport.command.log.read.tportName.player.lastDelimiter"));
                        else                  logMessage.addMessage(formatInfoTranslation("tport.command.log.read.tportName.player.delimiter"));
                        
                        color = !color;
                        size++;
                    }
                    logMessage.removeLast();
                    
                    if (size == 0) {
                        sendErrorTranslation(player, "tport.command.log.read.tportName.player.playerNotInLog",
                                asPlayer(uuid), tport);
                    } else {
                        sendInfoTranslation(player, "tport.command.log.read.tportName.succeeded",
                                tport, asPlayer(uuid), logMessage);
                    }
                }
            } else {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log read <TPort name>");
        }
    }
}
