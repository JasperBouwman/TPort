package com.spaceman.tport.commands.tport.edit.waypoint;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_PrettyColors;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class Color extends SubCommand {
    
    public Color() {
        EmptyCommand emptySetTypeChat = new EmptyCommand();
        emptySetTypeChat.setCommandName("chat color", ArgumentType.REQUIRED);
        emptySetTypeChat.setCommandDescription(formatInfoTranslation("tport.command.edit.waypoint.color.color.chat.commandDescription"));
        EmptyCommand emptySetTypeHex = new EmptyCommand();
        emptySetTypeHex.setCommandName("hex color", ArgumentType.REQUIRED);
        emptySetTypeHex.setCommandDescription(formatInfoTranslation("tport.command.edit.waypoint.color.color.hex.commandDescription"));
        
        addAction(emptySetTypeChat);
        addAction(emptySetTypeHex);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (args[4].startsWith("#")) {
            return args[4].length() < 8 ? Collections.singletonList(args[4] + "#ffffff".substring(args[4].length(), 7)) : Collections.singletonList(args[4].substring(0, 7));
        } else {
            List<String> list = Arrays.stream(ChatColor.values()).map(Enum::name).collect(Collectors.toList());
            list.add("#ffffff");
            list.add("#000000");
            list.add("#");
            return list;
        }
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort> waypoint color [chat color]
        // tport edit <TPort> waypoint color [hex color]
        
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            
            Message thisMessage = formatTranslation(tport.getWaypointColor(), tport.getWaypointColor(), "tport.command.edit.waypoint.color.this");
            thisMessage.getText().forEach(t -> t.setInsertion(tport.getWaypointColor().getColorAsValue()));
            
            sendInfoTranslation(player, "tport.command.edit.waypoint.color.succeeded", asTPort(tport), thisMessage);
        } else if (args.length == 5) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.waypoint.color.color.isOffered",
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
            
            
            if (Arrays.stream(ChatColor.values()).map(ChatColor::name).anyMatch(c -> c.equalsIgnoreCase(args[4]))) { // tport edit <TPort> waypoint color [chat color]
                tport.setWaypointColor(new MultiColor(ChatColor.valueOf(args[4].toUpperCase())));
                tport.save();
                
                Message thisMessage = formatTranslation(tport.getWaypointColor(), tport.getWaypointColor(), "tport.command.edit.waypoint.color.color.this");
                thisMessage.getText().forEach(t -> t.setInsertion(tport.getWaypointColor().getColorAsValue()));
                sendSuccessTranslation(player, "tport.command.edit.waypoint.color.color.chat.this", asTPort(tport), thisMessage);
                Advancement_PrettyColors.grant(player);
            } else if (args[4].matches("#[0-9a-fA-F]{6}")) {// tport edit <TPort> waypoint color [hex color]
                tport.setWaypointColor(new MultiColor(args[4]));
                tport.save();
                
                Message thisMessage = formatTranslation(tport.getWaypointColor(), tport.getWaypointColor(), "tport.command.edit.waypoint.color.color.this");
                thisMessage.getText().forEach(t -> t.setInsertion(tport.getWaypointColor().getColorAsValue()));
                sendSuccessTranslation(player, "tport.command.edit.waypoint.color.color.hex.this", asTPort(tport), thisMessage);
                Advancement_PrettyColors.grant(player);
            } else {
                sendErrorTranslation(player, "tport.colorTheme.set.type.colorNotFound", args[4]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> waypoint color [chat color|hex color]");
        }
    }
}
