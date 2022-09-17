package com.spaceman.tport.commands.tport.transfer;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class Offer extends SubCommand {
    
    private final EmptyCommand emptyPlayerTPort;
    
    public Offer() {
        emptyPlayerTPort = new EmptyCommand();
        emptyPlayerTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyPlayerTPort.setCommandDescription(formatInfoTranslation("tport.command.transfer.accept.player.tportName.commandDescription"));
        emptyPlayerTPort.setPermissions("TPort.transfer.offer", "TPort.basic");
        
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setTabRunnable((args, player) -> {
            if (!emptyPlayerTPort.hasPermissionToRun(player, false)) {
                return Collections.emptyList();
            }
            Player p = Bukkit.getPlayer(args[2]);
            if (p == null || !Accept.getInstance().emptyPlayerTPort.hasPermissionToRun(p, false)) {
                return new ArrayList<>();
            }
            List<String> list = TPortManager.getTPortList(player.getUniqueId()).stream().filter(tport -> !tport.isOffered()).map(TPort::getName).collect(Collectors.toList());
            TPortManager.getTPortList(p.getUniqueId()).stream().map(TPort::getName).forEach(s -> list.removeIf(ss -> ss.equalsIgnoreCase(s))); //remove name duplicates (case-insensitive)
            return list;
        });
        emptyPlayer.addAction(emptyPlayerTPort);
        addAction(emptyPlayer);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyPlayerTPort.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.getName().equalsIgnoreCase(player.getName()))
                .filter(p -> Accept.getInstance().emptyPlayerTPort.hasPermissionToRun(p, false))
                .map(Player::getName)
                .collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport transfer offer <player> <TPort name>
        
        if (args.length != 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport transfer offer <player> <TPort name>");
            return;
        }
        
        if (!emptyPlayerTPort.hasPermissionToRun(player, true)) {
            return;
        }
        
        UUID newPlayerUUID = PlayerUUID.getPlayerUUID(args[2], player);
        if (newPlayerUUID == null) {
            return;
        }
        Player toPlayer = Bukkit.getPlayer(newPlayerUUID);
        if (toPlayer == null) {
            sendErrorTranslation(player, "tport.command.playerNotOnline", asPlayer(newPlayerUUID));
            return;
        }
        
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[3]);
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[3]);
            return;
        }
        
        if (tport.isPublicTPort()) {
            sendErrorTranslation(player, "tport.command.transfer.offer.player.tportName.isPublic", asTPort(tport));
            return;
        }
        
        if (tport.isOffered()) {
            sendErrorTranslation(player, "tport.command.transfer.offer.player.tportName.isOffered",
                    asTPort(tport), asPlayer(tport.getOfferedTo()));
            return;
        }
        
        if (TPortManager.getTPort(toPlayer.getUniqueId(), tport.getName()) != null) {
            sendErrorTranslation(player, "tport.command.transfer.offer.player.tportName.alreadyHasName", asPlayer(toPlayer), asTPort(tport));
            return;
        }
        
        tport.setOfferedTo(toPlayer.getUniqueId());
        tport.save();
        
        ColorTheme theme = ColorTheme.getTheme(toPlayer);
        sendInfoTranslation(toPlayer, "tport.command.transfer.offer.player.tportName.succeededOtherPlayer",
                asTPort(tport),
                asPlayer(player),
                textComponent("/tport transfer accept " + player.getName() + " " + tport.getName(), theme.getVarInfoColor(),
                        new HoverEvent(textComponent("/tport transfer accept " + player.getName() + " " + tport.getName(), theme.getInfoColor())),
                        ClickEvent.runCommand("/tport transfer accept " + player.getName() + " " + tport.getName())),
                textComponent("/tport transfer reject " + player.getName() + " " + tport.getName(), theme.getVarInfoColor(),
                        new HoverEvent(textComponent("/tport transfer reject " + player.getName() + " " + tport.getName(), theme.getInfoColor())),
                        ClickEvent.runCommand("/tport transfer reject " + player.getName() + " " + tport.getName())));
        
        ColorTheme ownTheme = ColorTheme.getTheme(player);
        sendSuccessTranslation(player, "tport.command.transfer.offer.player.tportName.succeeded",
                asTPort(tport),
                asPlayer(toPlayer),
                textComponent("/tport transfer revoke " + tport.getName(), ownTheme.getVarSuccessColor(),
                        new HoverEvent(textComponent("/tport transfer revoke " + tport.getName(), ownTheme.getSuccessColor())),
                        ClickEvent.runCommand("/tport transfer revoke " + tport.getName())));
    }
}
