package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class LogData extends SubCommand {
    
    public LogData() {
        EmptyCommand emptyTPortPlayer = new EmptyCommand();
        emptyTPortPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyTPortPlayer.setCommandDescription(textComponent("This command is used to get the LogMode of the given player in the given TPort", infoColor));
        
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyTPort.setCommandDescription(textComponent("This command is used to get all logged players of the given TPort", infoColor));
        emptyTPort.setTabRunnable((args, player) -> {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                return tport.getLogged().stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
        emptyTPort.addAction(emptyTPortPlayer);
        addAction(emptyTPort);
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get all the logged TPorts", infoColor));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return TPortManager.getTPortList(player.getUniqueId()).stream().filter(TPort::isLogged).map(TPort::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log logData [TPort name] [player]
        
        if (args.length == 2) {
            Message message = new Message();
            message.addText(textComponent("All your logged TPorts: ", infoColor));
            boolean color = true;
            for (TPort tport : TPortManager.getTPortList(player.getUniqueId())) {
                if (tport.isLogged()) {
                    HoverEvent hEvent = new HoverEvent();
                    hEvent.addText(textComponent("Default log mode: ", infoColor));
                    hEvent.addText(textComponent(tport.getDefaultLogMode().name(), varInfoColor));
                    
                    if (tport.hasLoggedPlayers()) {
                        hEvent.addText(textComponent("\nLogged players: ", infoColor));
                        hEvent.addText(textComponent("", infoColor));
                        for (int i = 0; i < tport.getLogged().size() && i <= 10; i++) {
                            if (i == 10) {
                                hEvent.addText(textComponent(", and more", infoColor));
                                hEvent.addText(textComponent(""));
                                break;
                            }
                            UUID logUUID = tport.getLogged().get(i);
                            hEvent.addText(textComponent(PlayerUUID.getPlayerName(logUUID), varInfoColor));
                            hEvent.addText(textComponent(":", infoColor));
                            hEvent.addText(textComponent(tport.getLogMode(logUUID).name(), varInfoColor));
                            hEvent.addText(textComponent(", ", infoColor));
                        }
                        hEvent.removeLast();
                    }
                    
                    message.addText(textComponent(
                            tport.getName(),
                            color ? varInfoColor : varInfo2Color,
                            hEvent,
                            ClickEvent.runCommand("/tport own " + tport.getName())));
                    color = !color;
                    message.addText(textComponent(", ", infoColor));
                }
            }
            message.removeLast();
            if (message.isEmpty()) {
                sendInfoTheme(player, "You don't have any logged TPorts");
            } else {
                message.sendMessage(player);
            }
        }
        else if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                
                if (tport.isLogged()) {
                    Message message = new Message();
                    message.addText(textComponent("Log list of TPort ", infoColor));
                    message.addText(textComponent(tport.getName(), varInfoColor, ClickEvent.runCommand("/tport own " + tport.getName())));
                    message.addText(textComponent("\nDefault log mode: ", infoColor));
                    message.addText(textComponent(tport.getDefaultLogMode().name(), varInfoColor));
                    
                    if (tport.hasLoggedPlayers()) {
                        message.addText(textComponent("\nLogged players: ", infoColor));
                        message.addText(textComponent(""));
                        for (UUID logUUID : tport.getLogged()) {
                            message.addText(textComponent(PlayerUUID.getPlayerName(logUUID), varInfoColor));
                            message.addText(textComponent(":", infoColor));
                            message.addText(textComponent(tport.getLogMode(logUUID).name(), varInfoColor));
                            message.addText(textComponent(", ", infoColor));
                        }
                        message.removeLast();
                    }
                    message.sendMessage(player);
                } else {
                    sendInfoTheme(player, "TPort %s is not logged", tport.getName());
                }
                
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        }
        else if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                UUID uuid = PlayerUUID.getPlayerUUID(args[3]);
                if (uuid == null) {
                    sendErrorTheme(player, "Could not find a player named %s", args[3]);
                    return;
                }
                if (tport.getLogged().contains(uuid)) {
                    sendInfoTheme(player, "Log mode of player %s in TPort %s is %s", args[3], tport.getName(), tport.getLogMode(uuid).name());
                } else {
                    sendErrorTheme(player, "Player %s is not logged", args[3]);
                }
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        }
        else {
            sendErrorTheme(player, "Usage: %s", "/tport log logData [TPort name] [player]");
        }
    }
}
