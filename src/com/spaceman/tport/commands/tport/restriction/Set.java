package com.spaceman.tport.commands.tport.restriction;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tpEvents.TPEManager;
import com.spaceman.tport.tpEvents.TPRestriction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.Restriction.isPermissionBased;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varError2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varErrorColor;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Set extends SubCommand {
    
    private final EmptyCommand emptySetPlayerType;
    
    public Set() {
        emptySetPlayerType = new EmptyCommand();
        emptySetPlayerType.setCommandName("type", ArgumentType.REQUIRED);
        emptySetPlayerType.setCommandDescription(formatInfoTranslation("tport.command.restriction.set.player.type.commandDescription"));
        emptySetPlayerType.setPermissions("TPort.restriction.set", "TPort.admin.restriction");
        EmptyCommand emptySetPlayer = new EmptyCommand();
        emptySetPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptySetPlayer.setTabRunnable(((args, player) -> TPRestriction.getRestrictions()));
        emptySetPlayer.addAction(emptySetPlayerType);
        
        addAction(emptySetPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (emptySetPlayerType.hasPermissionToRun(player, false)) {
            if (!isPermissionBased()) {
                return Main.getPlayerNames();
            }
        }
        return Collections.emptyList();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport restriction set <player> <type>
        
        if (args.length != 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport restriction set <player> <type>");
            return;
        }
        if (!emptySetPlayerType.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (isPermissionBased()) {
            sendErrorTranslation(player, "tport.command.restriction.set.player.type.managedByPermissions",
                    formatTranslation(varErrorColor, varError2Color, "tport.command.restriction.type.permission"),
                    "/tport restriction handler command");
            return;
        }
        
        UUID newUUID = PlayerUUID.getPlayerUUID(args[2]);
        if (newUUID == null || !tportData.getConfig().contains("tport." + newUUID)) {
            sendErrorTranslation(player, "tport.command.playerNotFound", args[2]);
            return;
        }
        
        if (TPEManager.hasTPRequest(newUUID)) {
            sendErrorTranslation(player, "tport.command.restriction.set.player.type.isRequesting", asPlayer(newUUID));
            return;
        }
        
        TPRestriction type = TPRestriction.getNewRestriction(args[3]);
        if (type == null) {
            sendErrorTranslation(player, "tport.command.restriction.set.player.type.tpRestrictionNotExist", args[3]);
            return;
        }
        TPEManager.setTPRestriction(newUUID, type);
        
        if (player.getUniqueId().equals(newUUID)) {
            sendInfoTranslation(player, "tport.command.restriction.set.player.type.succeededOwn", type);
        } else {
            Player otherPlayer = Bukkit.getPlayer(newUUID);
            sendInfoTranslation(player, "tport.command.restriction.set.player.type.succeeded", asPlayer(otherPlayer, newUUID), type);
            sendInfoTranslation(otherPlayer, "tport.command.restriction.set.player.type.succeededOtherPlayer", player, type);
        }
    }
}
