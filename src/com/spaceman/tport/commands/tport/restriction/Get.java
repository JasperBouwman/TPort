package com.spaceman.tport.commands.tport.restriction;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tpEvents.TPEManager;
import com.spaceman.tport.tpEvents.TPRestriction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Get extends SubCommand {
    
    private final EmptyCommand emptyGetPlayer;
    
    public Get() {
        emptyGetPlayer = new EmptyCommand();
        emptyGetPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyGetPlayer.setCommandDescription(formatInfoTranslation("tport.command.restriction.get.player.commandDescription"));
        emptyGetPlayer.setPermissions("TPort.restriction.get.all", "TPort.admin.restriction");
        addAction(emptyGetPlayer);
        
        setPermissions("TPort.restriction.get.own");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.restriction.get.commandDescription");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport restriction get [player]
        if (args.length == 2) {
            if (hasPermissionToRun(player, true))
                sendInfoTranslation(player, "tport.command.restriction.get.succeeded", TPEManager.getTPRestriction(player.getUniqueId()));
        } else if (args.length == 3) {
            if (emptyGetPlayer.hasPermissionToRun(player, true)) {
                Files tportData = GettingFiles.getFile("TPortData");
                UUID newPlayerUUID = PlayerUUID.getPlayerUUID(args[2]);
                
                if (newPlayerUUID == null || !tportData.getConfig().contains("tport." + newPlayerUUID)) {
                    sendErrorTranslation(player, "tport.command.playerNotFound", args[2]);
                    return;
                }
                TPRestriction restriction = TPEManager.getTPRestriction(newPlayerUUID);
                sendInfoTranslation(player, "tport.command.restriction.get.player.succeeded", asPlayer(newPlayerUUID), restriction);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport restriction get [player]");
        }
    }
}
