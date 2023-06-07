package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class LogData extends SubCommand {
    
    public LogData() {
        EmptyCommand emptyTPortPlayer = new EmptyCommand();
        emptyTPortPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyTPortPlayer.setCommandDescription(formatInfoTranslation("tport.command.log.logData.tportName.player.commandDescription"));
        
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.log.logData.tportName.commandDescription"));
        emptyTPort.setTabRunnable((args, player) -> {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                return tport.getLogged().stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
        emptyTPort.addAction(emptyTPortPlayer);
        addAction(emptyTPort);
        
        setCommandDescription(formatInfoTranslation("tport.command.log.logData.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return TPortManager.getTPortList(player.getUniqueId()).stream().filter(TPort::isLogged).map(TPort::getName).collect(Collectors.toList());
    }
    
    private Message getLogData(TPort tport, int limitPlayers) {
        Message loggedPlayers = new Message();
        int loggedSize = 0;
        if (tport.hasLoggedPlayers()) {
            List<UUID> logged = tport.getLogged();
            boolean color = true;
            for (int i = 0; i < (loggedSize = logged.size()); i++) {
                if (i == limitPlayers) {
                    loggedPlayers.removeLast();
                    loggedPlayers.addMessage(formatInfoTranslation("tport.command.log.logData.tportName.succeeded.limit"));
                    loggedPlayers.addWhiteSpace();
                    break;
                }
                UUID logUUID = logged.get(i);
                loggedPlayers.addMessage(formatTranslation(infoColor, (color ? varInfoColor : varInfo2Color), "%s:%s", asPlayer(logUUID), tport.getLogMode(logUUID)));
                
                if (i + 2 == loggedSize) loggedPlayers.addMessage(formatInfoTranslation("tport.command.log.logData.tportName.succeeded.lastDelimiter"));
                else                     loggedPlayers.addMessage(formatInfoTranslation("tport.command.log.logData.tportName.succeeded.delimiter"));
                
                color = !color;
            }
            loggedPlayers.removeLast();
        }
        
        String id;
        if (loggedSize == 1) id = "tport.command.log.logData.tportName.succeeded.singular";
        else if (loggedSize == 0) id = "tport.command.log.logData.tportName.succeeded.empty";
        else id = "tport.command.log.logData.tportName.succeeded.multiple";
        return formatInfoTranslation(id, tport, tport.getDefaultLogMode(), loggedPlayers);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log logData [TPort name] [player]
        
        if (args.length == 2) {
            Message tportListMessage = new Message();
            boolean color = true;
            ArrayList<TPort> tportList = TPortManager.getTPortList(player.getUniqueId());
            int loggedSize = 0;
            final int loggedMax = (int) tportList.stream().filter(TPort::isLogged).count();
            
            for (TPort tport : tportList) {
                if (tport.isLogged()) {
                    HoverEvent hEvent = new HoverEvent();
                    hEvent.addMessage(getLogData(tport, 2));
                    
                    tportListMessage.addText(textComponent(
                            tport.getName(),
                            color ? varInfoColor : varInfo2Color,
                            hEvent,
                            ClickEvent.runCommand("/tport own " + tport.getName())));
                    
                    if (loggedSize + 2 == loggedMax) tportListMessage.addMessage(formatInfoTranslation("tport.command.log.logData.succeeded.lastDelimiter"));
                    else                             tportListMessage.addMessage(formatInfoTranslation("tport.command.log.logData.succeeded.delimiter"));
                    
                    color = !color;
                    loggedSize++;
                }
            }
            tportListMessage.removeLast();
            
            if (loggedSize == 0) {
                sendInfoTranslation(player, "tport.command.log.logData.noLoggedTPorts");
            } else {
                String id;
                if (loggedSize == 1) id = "tport.command.log.logData.succeeded.singular";
                else id = "tport.command.log.logData.succeeded.multiple";
                sendInfoTranslation(player, id, tportListMessage);
            }
        }
        else if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
                return;
            }
            if (tport.isLogged()) {
                getLogData(tport, -1).sendAndTranslateMessage(player);
            } else {
                sendInfoTranslation(player, "tport.command.log.logData.tportName.tportNotLogged", asTPort(tport));
            }
        }
        else if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
                return;
            }
            if (!tport.isLogged()) {
                sendErrorTranslation(player, "tport.command.log.logData.tportName.player.tportNotLogged", asTPort(tport));
                return;
            }
            UUID uuid = PlayerUUID.getPlayerUUID(args[3], player);
            if (uuid == null) {
                return;
            }
            if (tport.getLogged().contains(uuid)) {
                sendInfoTranslation(player, "tport.command.log.logData.tportName.player.succeeded",
                        asPlayer(uuid), asTPort(tport), tport.getLogMode(uuid));
            } else {
                sendErrorTranslation(player, "tport.command.log.logData.tportName.player.notLogged",
                        asPlayer(uuid), asTPort(tport));
            }
        }
        else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log logData [TPort name] [player]");
        }
    }
}
