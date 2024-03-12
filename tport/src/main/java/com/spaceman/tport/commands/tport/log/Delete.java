package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class Delete extends SubCommand {
    
    public Delete() {
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.log.delete.tportName.commandDescription"));
        
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return TPortManager.getTPortList(player.getUniqueId()).stream().filter(TPort::isLogged).map(TPort::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport log delete <TPort name>
        
        if (args.length == 3) {
            
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
                return;
            }
            
            for (UUID playerUUID : tport.getLogged()) {
                String playerName = PlayerUUID.getPlayerName(playerUUID);
                Remove.run(player, tport, playerName, false);
            }
            tport.setDefaultLogMode(TPort.LogMode.NONE);
            tport.save();
            sendSuccessTranslation(player, "tport.command.log.delete.tportName.succeeded", asTPort(tport));
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log delete <TPort name>");
        }
    }
}
