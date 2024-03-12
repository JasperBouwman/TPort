package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class Remove extends SubCommand {
    
    public Remove() {
        EmptyCommand emptyTPortPlayer = new EmptyCommand();
        emptyTPortPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyTPortPlayer.setCommandDescription(formatInfoTranslation("tport.command.log.remove.tportName.player.commandDescription"));
        emptyTPortPlayer.setTabRunnable((args, player) -> {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport == null) {
                return Collections.emptyList();
            }
            List<String> list = tport.getLogged().stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
            list.removeAll(Arrays.asList(args).subList(3, args.length));
            return list;
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
        return TPortManager.getTPortList(player.getUniqueId()).stream().filter(TPort::isLogged).map(TPort::getName).collect(Collectors.toList());
    }
    
    public static void run(Player player, TPort tport, String playerName, boolean sendError) {
        UUID playerUUID = PlayerUUID.getPlayerUUID(playerName, player);
        if (playerUUID == null) {
            return;
        }
        if (tport.removeLogged(playerUUID)) {
            sendSuccessTranslation(player, "tport.command.log.remove.tportName.player.succeeded", asPlayer(playerUUID));
            sendInfoTranslation(Bukkit.getPlayer(playerUUID), "tport.command.log.remove.tportName.player.succeededOtherPlayer", asPlayer(player), asTPort(tport));
        } else {
            if (sendError) sendErrorTranslation(player, "tport.command.log.remove.tportName.player.playerNotLogged", asPlayer(playerUUID));
        }
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log remove <TPort name> <player...>
    
        if (args.length <= 3) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log remove <TPort name> <player...>");
            return;
        }
        
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
            return;
        }
        
        for (int i = 3; i < args.length; i++) {
            String playerName = args[i];
            run(player, tport, playerName, true);
        }
        tport.save();
    }
}
