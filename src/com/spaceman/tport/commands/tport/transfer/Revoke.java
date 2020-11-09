package com.spaceman.tport.commands.tport.transfer;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Revoke extends SubCommand {
    
    public Revoke() {
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(TextComponent.textComponent("This command is used to revoke the offer of the given TPort", ColorType.infoColor));
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
            if (tport != null) {
                if (tport.isOffered()) {
                    UUID toPlayerUUID = tport.getOfferedTo();
                    tport.setOfferedTo(null);
                    tport.save();
    
                    sendSuccessTheme(player, "Successfully revoked the offer of TPort %s to player %s", tport.getName(), PlayerUUID.getPlayerName(toPlayerUUID));
                    Player toPlayer = Bukkit.getPlayer(toPlayerUUID);
                    if (toPlayer != null) {
                        sendInfoTheme(toPlayer, "Player %s has revoked the offer of TPort %s", player.getName(), tport.getName());
                    }
                } else {
                    sendErrorTheme(player, "TPort %s is not being offered", tport.getName());
                }
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport transfer revoke <TPort name>");
        }
    }
}
