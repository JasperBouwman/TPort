package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Attribute;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Pattern;

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_ImWatchingMe;
import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_ImWatchingYou;
import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

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
            Pair<String, UUID> playerProfile = PlayerUUID.getProfile(args[args.length - 1].split(":")[0], null);
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
                List<String> newTabList = PlayerUUID.getPlayerNames();
                
                for (int i = 0; i < newTabList.size(); i++) {
                    String newTabElement = newTabList.get(i);
                    
                    for (int j = 3; j < args.length - 1; j++) {
                        String oldTabElement = args[j];
                        if (oldTabElement.split(":")[0].equalsIgnoreCase(newTabElement.split(":")[0])) {
                            newTabList.remove(newTabElement);
                            i--;
                        }
                    }
                }
                return newTabList;
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
        if (!emptyTPortPlayer.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log add <TPort name> <player[:LogMode]...>
        
        if (args.length <= 3) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log add <TPort name> <player[:LogMode]...>");
            return;
        }
        if (!emptyTPortPlayer.hasPermissionToRun(player, true)) {
            return;
        }
        
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
            return;
        }
        
        for (int i = 3; i < args.length; i++) {
            String playerName = args[i];
            
            TPort.LogMode logMode = TPort.LogMode.ALL;
            if (Pattern.matches(".*:.*", playerName)) {
                String[] split = playerName.split(":");
                logMode = TPort.LogMode.get(split[1], null);
                if (logMode == null) {
                    sendErrorTranslation(player, "tport.command.log.add.tportName.player.logModeNotFound", split[1]);
                    continue;
                }
                playerName = split[0];
            }
            
            UUID playerUUID = PlayerUUID.getPlayerUUID(playerName, player);
            if (playerUUID == null) {
                continue;
            }
            
            if (playerName.equalsIgnoreCase(player.getName())) {
                if (logMode.equals(TPort.LogMode.ALL) || logMode.equals(TPort.LogMode.ONLINE)) {
                    tport.addLogged(player.getUniqueId(), TPort.LogMode.ALL);
                    sendInfoTranslation(player, "tport.command.log.add.tportName.player.addYourself");
                } else if (logMode.equals(TPort.LogMode.NONE) || logMode.equals(TPort.LogMode.OFFLINE)) {
                    tport.removeLogged(player.getUniqueId());
                    sendInfoTranslation(player, "tport.command.log.add.tportName.player.removeYourself");
                }
                Advancement_ImWatchingMe.grant(player);
                continue;
            }
            tport.addLogged(playerUUID, logMode);
            sendSuccessTranslation(player, "tport.command.log.add.tportName.player.succeeded", asPlayer(playerUUID), logMode, asTPort(tport));
            
            Advancement_ImWatchingYou.grant(player);
            
            sendInfoTranslation(Bukkit.getPlayer(playerUUID), "tport.command.log.add.tportName.player.succeededOtherPlayer", asPlayer(player), asTPort(tport), logMode);
        }
        
        tport.save();
    }
}
