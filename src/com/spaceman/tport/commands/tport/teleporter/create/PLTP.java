package com.spaceman.tport.commands.tport.teleporter.create;

import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.teleporter.Create.createTeleporter;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class PLTP extends SubCommand {
    
    private final EmptyCommand emptyPLTPPlayer;
    
    public PLTP() {
        emptyPLTPPlayer = new EmptyCommand();
        emptyPLTPPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPLTPPlayer.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.PLTP.player.commandDescription"));
        emptyPLTPPlayer.setPermissions("TPort.teleporter.create");
        
        addAction(emptyPLTPPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return PlayerUUID.getPlayerNames();
    }
    
    @Override
    public String getName(String arg) {
        return "PLTP";
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport teleporter create PLTP <player>
        
        if (args.length == 4) {
            if (emptyPLTPPlayer.hasPermissionToRun(player, true)) {
                Pair<String, UUID> profile = PlayerUUID.getProfile(args[3], player);
                String newPlayerName = profile.getLeft();
                if (newPlayerName == null) {
                    return;
                }
                
                createTeleporter(player, "PLTP", "PLTP tp " + newPlayerName,
                        List.of(formatInfoTranslation("tport.command.teleporter.create.format.data.pltp.player", newPlayerName)));
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create PLTP <player>");
        }
    }
}
