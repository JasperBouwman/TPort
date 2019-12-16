package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Attribute;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Add extends SubCommand {
    
    public Add() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("player:[LogMode]", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(textComponent("This command is used to set the LogMode of the given player for the given TPort, example: ", ColorTheme.ColorType.infoColor),
                textComponent("/tport log add ", ColorTheme.ColorType.varInfoColor),
                textComponent("home The_Spaceman:ONLINE", ColorTheme.ColorType.varInfoColor, Attribute.ITALIC),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.log", ColorTheme.ColorType.varInfoColor));
        emptyCommand.setTabRunnable((args, player) -> {
            if (!hasPermission(player, false, "TPort.log")) {
                return Collections.emptyList();
            }
            if (PlayerUUID.getPlayerUUID(args[args.length - 1].split(":")[0]) != null) {
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
                List<String> list = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
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
        emptyCommand.setLooped(true);
        addAction(emptyCommand);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log add <TPort name> <player[:LogMode]...>
        
        if (!hasPermission(player, true, "TPort.log")) {
            return;
        }
        
        if (args.length > 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                for (int i = 3; i < args.length; i++) {
                    String playerName = args[i];
                    TPort.LogMode logMode = TPort.LogMode.ALL;
                    if (Pattern.matches(".*:.*", playerName)) {
                        logMode = TPort.LogMode.get(playerName.split(":")[1], logMode);
                        playerName = playerName.split(":")[0];
                    }
                    UUID playerUUID = PlayerUUID.getPlayerUUID(playerName);
                    if (playerUUID == null) {
                        sendErrorTheme(player, "Could not find a player named %s", playerName);
                        continue;
                    }
                    tport.addLogged(playerUUID, logMode);
                    sendSuccessTheme(player, "Successfully started logging player %s with LogMode %s", playerName, logMode.name());
                }
                tport.save();
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport log add <TPort name> <player[:LogMode]...>");
        }
    }
}
