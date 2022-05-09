package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Attribute;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Pattern;

import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Add extends SubCommand {
    
    private final EmptyCommand emptyTPortPlayer;
    
    public Add() {
        emptyTPortPlayer = new EmptyCommand();
        emptyTPortPlayer.setCommandName("player:[LogMode]", ArgumentType.REQUIRED);
        emptyTPortPlayer.setCommandDescription(formatInfoTranslation("tport.command.log.add.tportName.player.commandDescription",
                new Message(
                        textComponent("/tport log add ", varInfoColor),
                        textComponent("home The_Spaceman:ONLINE", varInfoColor, Attribute.ITALIC)
                )));
        emptyTPortPlayer.setPermissions("TPort.log");
        emptyTPortPlayer.setTabRunnable((args, player) -> {
            if (!emptyTPortPlayer.hasPermissionToRun(player, false)) {
                return Collections.emptyList();
            }
            Pair<String, UUID> playerProfile = PlayerUUID.getProfile(args[args.length - 1].split(":")[0]);
            if (playerProfile.getLeft() != null) {
                String playerName = args[args.length - 1].split(":")[0];
                
                for (String ss : Arrays.asList(args).subList(3, args.length - 1)) {
                    if (ss.split(":")[0].equalsIgnoreCase(playerName)) {
                        return Collections.emptyList();
                    }
                }
                
                ArrayList<String> list = new ArrayList<>();
                for (TPort.LogMode mode : TPort.LogMode.values()) {
                    list.add(playerName + ":" + mode.name());
                }
                return list;
            } else {
                List<String> list = Main.getPlayerNames();
//                list.remove(player.getName());
                for (int i = 0; i < list.size(); i++) {
                    String s = list.get(i);
                    for (String ss : Arrays.asList(args).subList(3, args.length)) {
                        if (ss.split(":")[0].equalsIgnoreCase(s.split(":")[0])) {
                            list.remove(s);
                        }
                    }
                }
                return list;
            }
        });
        emptyTPortPlayer.setLooped(true);
        
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setTabRunnable(emptyTPortPlayer.getTabRunnable());
        emptyTPort.addAction(emptyTPortPlayer);
        
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log add <TPort name> <player[:LogMode]...>
        
        if (args.length > 3) {
            if (!emptyTPortPlayer.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                Files tportData = GettingFiles.getFile("TPortData");
                for (int i = 3; i < args.length; i++) {
                    String playerName = args[i];
                    TPort.LogMode logMode = TPort.LogMode.ALL;
                    if (Pattern.matches(".*:.*", playerName)) {
                        logMode = TPort.LogMode.get(playerName.split(":")[1], logMode);
                        playerName = playerName.split(":")[0];
                    }
                    if (playerName.equalsIgnoreCase(player.getName())) {
                        if (logMode.equals(TPort.LogMode.ALL) || logMode.equals(TPort.LogMode.ONLINE)) {
                            tport.addLogged(player.getUniqueId(), TPort.LogMode.ALL);
                            sendInfoTranslation(player, "tport.command.log.add.tportName.player.addYourself");
                        } else if (logMode.equals(TPort.LogMode.NONE) || logMode.equals(TPort.LogMode.OFFLINE)) {
                            tport.removeLogged(player.getUniqueId());
                            sendInfoTranslation(player, "tport.command.log.add.tportName.player.removeYourself");
                        }
                        continue;
                    }
                    UUID playerUUID = PlayerUUID.getPlayerUUID(playerName);
                    if (playerUUID == null || !tportData.getConfig().contains("tport." + playerUUID)) {
                        sendErrorTranslation(player, "tport.command.playerNotFound", playerName);
                        return;
                    }
                    tport.addLogged(playerUUID, logMode);
                    sendSuccessTranslation(player, "tport.command.log.add.tportName.player.succeeded", asPlayer(playerUUID), logMode, tport);
    
                    sendInfoTranslation(Bukkit.getPlayer(playerUUID), "tport.command.log.add.tportName.player.succeededOtherPlayer", player, tport, logMode);
                }
                tport.save();
            } else {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log add <TPort name> <player[:LogMode]...>");
        }
    }
}
