package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.Pair;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Read extends SubCommand {
    
    public Read() {
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyPlayer.setCommandDescription(textComponent("This command is used to filter the TPort log of the given TPort for the given player", ColorTheme.ColorType.infoColor));
        
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(textComponent("This command is used to read the TPort log of the given TPort, the maximum log size is ", ColorTheme.ColorType.infoColor),
                textComponent(String.valueOf(LogSize.getLogSize()), ColorTheme.ColorType.varInfoColor));
        emptyTPort.setTabRunnable((args, player) -> {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                return tport.getLogBook().stream().map(p -> PlayerUUID.getPlayerName(p.getRight())).collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
        emptyTPort.addAction(emptyPlayer);
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
                ArrayList<Pair<Calendar, UUID>> log = tport.getLogBook();
                if (log.isEmpty()) {
                    sendInfoTheme(player, "The log of TPort %s is empty", tport.getName());
                } else {
                    sendInfoTheme(player, "Log of TPort %s:", tport.getName());
                    
                    String format = getFile("TPortData").getConfig().getString("tport." + player.getUniqueId() + ".timeFormat", "EEE MMM dd HH:mm:ss zzz yyyy");
                    //noinspection ConstantConditions
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone(
                            getFile("TPortData").getConfig().getString("tport." + player.getUniqueId() + ".timeZone", TimeZone.getDefault().getID())));
                    for (Pair<Calendar, UUID> pair : log) {
                        Calendar time = pair.getLeft();
                        sendInfoTheme(player, "%s -> %s", sdf.format(time.getTime()), PlayerUUID.getPlayerName(pair.getRight()));
                    }
                }
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                ArrayList<Pair<Calendar, UUID>> log = tport.getLogBook();
                if (log.isEmpty()) {
                    sendInfoTheme(player, "The log of TPort %s is empty", tport.getName());
                } else {
                    Message message = new Message();
                    message.addText(textComponent("Filtered results of TPort log ", ColorTheme.ColorType.infoColor));
                    message.addText(textComponent(tport.getName(), ColorTheme.ColorType.varInfoColor));
                    message.addText(textComponent(" for player ", ColorTheme.ColorType.infoColor));
                    message.addText(textComponent(args[3], ColorTheme.ColorType.varInfoColor));
                    message.addText(textComponent(":", ColorTheme.ColorType.infoColor));
                    message.addText(textComponent("\n"));
                    String format = getFile("TPortData").getConfig().getString("tport." + player.getUniqueId() + ".timeFormat", "EEE MMM dd HH:mm:ss zzz yyyy");
                    //noinspection ConstantConditions
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone(
                            getFile("TPortData").getConfig().getString("tport." + player.getUniqueId() + ".timeZone", TimeZone.getDefault().getID())));
                    for (Pair<Calendar, UUID> pair : log) {
                        if (PlayerUUID.getPlayerName(pair.getRight()).equalsIgnoreCase(args[3])) {
                            Calendar time = pair.getLeft();
                            message.addText(textComponent(sdf.format(time.getTime()), ColorTheme.ColorType.varInfoColor));
                            message.addText(textComponent(" -> ", ColorTheme.ColorType.infoColor));
                            message.addText(textComponent(PlayerUUID.getPlayerName(pair.getRight()), ColorTheme.ColorType.varInfoColor));
                            message.addText(textComponent(",\n", ColorTheme.ColorType.infoColor));
                        }
                    }
                    message.removeLast();
                    if (message.getText().size() < 6) {
                        sendInfoTheme(player, "Player %s was not found in the log of TPort %s", args[3], tport.getName());
                    } else {
                        message.sendMessage(player);
                    }
                }
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport log read <TPort name>");
        }
    }
}
