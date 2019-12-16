package com.spaceman.tport.commands.tport.transfer;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Offer extends SubCommand {
    
    public Offer() {
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(textComponent("This command is used to offer one of your own TPorts to someone else", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.transfer.offer", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
    
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setTabRunnable((args, player) -> {
            if (!hasPermission(player, false, true, "TPort.transfer.accept", "TPort.basic")) {
                return new ArrayList<>();
            }
            List<String> list = TPortManager.getTPortList(player.getUniqueId()).stream().filter(tport -> !tport.isOffered()).map(TPort::getName).collect(Collectors.toList());
            list.removeAll(TPortManager.getTPortList(PlayerUUID.getPlayerUUID(args[2])).stream().map(TPort::getName).collect(Collectors.toList()));
            return list;
        });
        emptyPlayer.addAction(emptyTPort);
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!hasPermission(player, false, true, "TPort.transfer.accept", "TPort.basic")) {
            return new ArrayList<>();
        }
        List<String> list = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        list.remove(player.getName());
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport transfer offer <player> <TPort name>
        
        if (!hasPermission(player, "TPort.transfer.offer", "TPort.basic")) {
            return;
        }
        
        if (args.length == 4) {
            
            Player toPlayer = Bukkit.getPlayer(PlayerUUID.getPlayerUUID(args[2]));
            if (toPlayer == null) {
                sendErrorTheme(player, "Player %s must be online", args[2]);
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[3]);
            if (tport != null) {
                if (tport.isPublicTPort()) {
                    sendErrorTheme(player, "TPort %s is a Public TPort, and can't be offered to other players. Please make it private first", tport.getName());
                    return;
                }
                
                if (tport.isOffered()) {
                    sendErrorTheme(player, "TPort %s is already being promised to player %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                    return;
                }
                
                if (TPortManager.getTPort(toPlayer.getUniqueId(), tport.getName()) != null) {
                    sendErrorTheme(player, "Player %s has already a TPort named %s", toPlayer.getName(), tport.getName());
                    return;
                }
                tport.setOfferedTo(toPlayer.getUniqueId());
                tport.save();
                Message message = new Message();
                ColorTheme theme = ColorTheme.getTheme(toPlayer);
                message.addText(textComponent("You have been offered TPort ", theme.getInfoColor()));
                message.addText(textComponent(tport.getName(), theme.getVarInfoColor()));
                message.addText(textComponent(" from player ", theme.getInfoColor()));
                message.addText(textComponent(player.getName(), theme.getVarInfoColor()));
                message.addText(textComponent(", to accept the offer use ", theme.getInfoColor()));
                message.addText(textComponent("/tport transfer accept " + player.getName() + " " + tport.getName(), theme.getVarInfoColor(),
                        new HoverEvent(textComponent("/tport transfer accept " + player.getName() + " " + tport.getName(), theme.getInfoColor())),
                        ClickEvent.runCommand("/tport transfer accept " + player.getName() + " " + tport.getName())));
                message.addText(textComponent(", to reject the offer use ", theme.getInfoColor()));
                message.addText(textComponent("/tport transfer reject " + player.getName() + " " + tport.getName(), theme.getVarInfoColor(),
                        new HoverEvent(textComponent("/tport transfer reject " + player.getName() + " " + tport.getName(), theme.getInfoColor())),
                        ClickEvent.runCommand("/tport transfer reject " + player.getName() + " " + tport.getName())));
                message.sendMessage(toPlayer);
                
                Message ownMessage = new Message();
                ColorTheme ownTheme = ColorTheme.getTheme(player);
                ownMessage.addText(textComponent("Successfully offered TPort ", ownTheme.getSuccessColor()));
                ownMessage.addText(textComponent(tport.getName(), ownTheme.getVarSuccessColor()));
                ownMessage.addText(textComponent(" to player ", ownTheme.getSuccessColor()));
                ownMessage.addText(textComponent(toPlayer.getName(), ownTheme.getVarSuccessColor()));
                ownMessage.addText(textComponent(", to revoke your offer use ", ownTheme.getSuccessColor()));
                ownMessage.addText(textComponent("/tport transfer revoke " + tport.getName(), ownTheme.getVarSuccessColor(),
                        new HoverEvent(textComponent("/tport transfer revoke " + tport.getName(), ownTheme.getSuccessColor())),
                        ClickEvent.runCommand("/tport transfer revoke " + tport.getName())));
                ownMessage.sendMessage(player);
                
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[3]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport transfer offer <player> <TPort name>");
        }
    }
}
