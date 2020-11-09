package com.spaceman.tport.commands.tport.transfer;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
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

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Offer extends SubCommand {
    
    private final EmptyCommand emptyPlayerTPort;
    
    public Offer() {
        emptyPlayerTPort = new EmptyCommand();
        emptyPlayerTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyPlayerTPort.setCommandDescription(textComponent("This command is used to offer one of your own TPorts to someone else", ColorTheme.ColorType.infoColor));
        emptyPlayerTPort.setPermissions("TPort.transfer.offer", "TPort.basic");
        
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setTabRunnable((args, player) -> {
            Player p = Bukkit.getPlayer(args[2]);
            if (p == null || !Accept.emptyPlayerTPort.hasPermissionToRun(p, false)) {
                return new ArrayList<>();
            }
            List<String> list = TPortManager.getTPortList(player.getUniqueId()).stream().filter(tport -> !tport.isOffered()).map(TPort::getName).collect(Collectors.toList());
            TPortManager.getTPortList(p.getUniqueId()).stream().map(TPort::getName).forEach(s -> list.removeIf(ss -> ss.equalsIgnoreCase(s))); //remove name duplicates (case insensitive)
            return list;
        });
        emptyPlayer.addAction(emptyPlayerTPort);
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.getName().equalsIgnoreCase(player.getName()))
                .filter(p -> Accept.emptyPlayerTPort.hasPermissionToRun(p, false))
                .map(Player::getName)
                .collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport transfer offer <player> <TPort name>
        
        if (!emptyPlayerTPort.hasPermissionToRun(player, true)) {
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
                    sendErrorTheme(player, "TPort %s is already being offered to player %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
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
