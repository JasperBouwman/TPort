package com.spaceman.tport.commands.tport.delay;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.Delay.delayTime;
import static com.spaceman.tport.commands.tport.Delay.isPermissionBased;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Get extends SubCommand {
    
    private final EmptyCommand emptyPlayer;
    
    public Get() {
        emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyPlayer.setCommandDescription(formatInfoTranslation("tport.command.delay.get.player.commandDescription", ColorTheme.ColorType.infoColor));
        emptyPlayer.setPermissions("TPort.delay.get.all", "TPort.admin.delay");
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyPlayer.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return isPermissionBased() ? Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()) : PlayerUUID.getPlayerNames();
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.delay.get.commandDescription", ColorTheme.ColorType.infoColor);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport delay get [player]
        
        if (args.length == 2) {
            int delay = delayTime(player);
            double seconds = delay / 20D;
            Message secondMessage;
            if (seconds == 1) secondMessage = formatSuccessTranslation("tport.command.second");
            else secondMessage = formatSuccessTranslation("tport.command.seconds");
            Message tickMessage;
            if (delay == 1) tickMessage = formatSuccessTranslation("tport.command.minecraftTick");
            else tickMessage = formatSuccessTranslation("tport.command.minecraftTicks");
        
            sendInfoTranslation(player, "tport.command.delay.get.player.succeeded", asPlayer(player), delay, tickMessage, seconds, secondMessage);
        } else if (args.length == 3) {
            if (!emptyPlayer.hasPermissionToRun(player, true)) {
                return;
            }
    
            UUID newUUID = PlayerUUID.getPlayerUUID(args[2], player);
            if (newUUID == null) {
                return;
            }
            
            int delay;
            Player newPlayer = null;
            if (isPermissionBased()) {
                newPlayer = Bukkit.getPlayer(newUUID);
                if (newPlayer == null) {
                    sendErrorTranslation(player, "tport.command.playerNotOnline", asPlayer(newUUID));
                    return;
                }
                delay = delayTime(player);
            } else {
                delay = delayTime(newUUID);
            }
            
            double seconds = delay / 20D;
            Message secondMessage;
            if (seconds == 1) secondMessage = formatSuccessTranslation("tport.command.second");
            else secondMessage = formatSuccessTranslation("tport.command.seconds");
            Message tickMessage;
            if (delay == 1) tickMessage = formatSuccessTranslation("tport.command.minecraftTick");
            else tickMessage = formatSuccessTranslation("tport.command.minecraftTicks");
        
            sendInfoTranslation(player, "tport.command.delay.get.player.succeeded", asPlayer(newPlayer, newUUID), delay, tickMessage, seconds, secondMessage);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport delay get [player]");
        }
    }
}
