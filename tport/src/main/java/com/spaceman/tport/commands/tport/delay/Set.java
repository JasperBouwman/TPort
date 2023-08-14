package com.spaceman.tport.commands.tport.delay;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.Delay.isPermissionBased;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varError2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varErrorColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public class Set extends SubCommand {
    
    private final EmptyCommand emptyPlayerDelay;
    
    public Set() {
        emptyPlayerDelay = new EmptyCommand();
        emptyPlayerDelay.setCommandName("delay", ArgumentType.REQUIRED);
        emptyPlayerDelay.setCommandDescription(formatInfoTranslation("tport.command.delay.set.player.delay.commandDescription", "/tport delay permission command"));
        emptyPlayerDelay.setPermissions("TPort.delay.set", "TPort.admin.delay");
        
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.addAction(emptyPlayerDelay);
        
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (emptyPlayerDelay.hasPermissionToRun(player, false)) {
            if (!isPermissionBased()) {
                return PlayerUUID.getPlayerNames();
            }
        }
        return Collections.emptyList();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport delay set <player> <delay>
        
        if (args.length != 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport delay set <player> <delay>");
            return;
        }
        if (!emptyPlayerDelay.hasPermissionToRun(player, true)) {
            return;
        }
        if (isPermissionBased()) {
            sendErrorTranslation(player, "tport.command.delay.set.player.delay.managedByPermissions",
                    formatTranslation(varErrorColor, varError2Color, "tport.command.delay.type.permissions"),
                    "/tport delay handler command");
            return;
        }
        
        UUID newUUID = PlayerUUID.getPlayerUUID(args[2], player);
        if (newUUID == null) {
            return;
        }
        
        int delay;
        try {
            delay = Integer.parseInt(args[3]);
            delay = Math.max(0, delay);
        } catch (NumberFormatException nfe) {
            sendErrorTranslation(player, "tport.command.delay.set.player.delay.invalidTime", args[3]);
            return;
        }
        
        tportConfig.getConfig().set("delay.time." + newUUID, delay);
        tportConfig.saveConfig();
        
        double seconds = delay / 20D;
        Message secondMessage;
        if (seconds == 1) secondMessage = formatSuccessTranslation("tport.command.second");
        else secondMessage = formatSuccessTranslation("tport.command.seconds");
        Message tickMessage;
        if (delay == 1) tickMessage = formatSuccessTranslation("tport.command.minecraftTick");
        else tickMessage = formatSuccessTranslation("tport.command.minecraftTicks");
        
        if (player.getUniqueId().equals(newUUID)) {
            sendSuccessTranslation(player, "tport.command.delay.set.player.delay.succeededOwn", delay, tickMessage, seconds, secondMessage);
        } else {
            Player otherPlayer = Bukkit.getPlayer(newUUID);
            sendSuccessTranslation(player, "tport.command.delay.set.player.delay.succeeded", asPlayer(otherPlayer, newUUID), delay, tickMessage, seconds, secondMessage);
            sendInfoTranslation(otherPlayer, "tport.command.delay.set.player.delay.succeededOtherPlayer", asPlayer(player), delay, tickMessage, seconds, secondMessage);
        }
    }
}
