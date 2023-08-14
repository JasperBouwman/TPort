package com.spaceman.tport.commands.tport.restriction;

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
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.Delay.isPermissionBased;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Get extends SubCommand {
    
    private final EmptyCommand emptyGetPlayer;
    
    public Get() {
        emptyGetPlayer = new EmptyCommand();
        emptyGetPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyGetPlayer.setCommandDescription(formatInfoTranslation("tport.command.restriction.get.player.commandDescription"));
        emptyGetPlayer.setPermissions("TPort.restriction.get.all", "TPort.admin.restriction");
        addAction(emptyGetPlayer);
        
        setCommandDescription(formatInfoTranslation("tport.command.restriction.get.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyGetPlayer.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return isPermissionBased() ? Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()) : PlayerUUID.getPlayerNames();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport restriction get [player]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.restriction.get.succeeded", TPEManager.getTPRestriction(player.getUniqueId()));
        } else if (args.length == 3) {
            if (!emptyGetPlayer.hasPermissionToRun(player, true)) {
                return;
            }
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(args[2], player);
            if (newPlayerUUID == null) {
                return;
            }
            
            TPRestriction restriction;
            if (isPermissionBased()) {
                if (Bukkit.getPlayer(newPlayerUUID) == null) {
                    sendErrorTranslation(player, "tport.command.playerNotOnline", asPlayer((newPlayerUUID)));
                    return;
                }
            }
            restriction = TPEManager.getTPRestriction(newPlayerUUID);
            
            sendInfoTranslation(player, "tport.command.restriction.get.player.succeeded", asPlayer(newPlayerUUID), restriction);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport restriction get [player]");
        }
    }
}
