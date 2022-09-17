package com.spaceman.tport.commands.tport.transfer;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class Revoke extends SubCommand {
    
    public Revoke() {
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.transfer.revoke.tportName.commandDescription"));
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return TPortManager.getTPortList(player.getUniqueId()).stream().filter(TPort::isOffered).map(TPort::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport transfer revoke <TPort name>
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
                return;
            }
            if (!tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.transfer.revoke.tportName.notOffered", asTPort(tport));
                return;
            }
            
            UUID toPlayerUUID = tport.getOfferedTo();
            tport.setOfferedTo(null);
            tport.save();
    
            sendSuccessTranslation(player, "tport.command.transfer.revoke.tportName.succeeded",
                    asTPort(tport), asPlayer(toPlayerUUID));
    
            Player toPlayer = Bukkit.getPlayer(toPlayerUUID);
            sendInfoTranslation(toPlayer, "tport.command.transfer.revoke.tportName.succeededOtherPlayer", asPlayer(player), asTPort(tport));
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport transfer revoke <TPort name>");
        }
    }
}
