package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class List extends SubCommand {
    
    public List() {
        EmptyCommand emptyOwn = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "Own";
            }
        };
        emptyOwn.setCommandName("own", ArgumentType.FIXED);
        emptyOwn.setCommandDescription(formatInfoTranslation("tport.command.public.list.own.commandDescription"));
        EmptyCommand emptyAll = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "All";
            }
        };
        emptyAll.setCommandName("all", ArgumentType.FIXED);
        emptyAll.setCommandDescription(formatInfoTranslation("tport.command.public.list.all.commandDescription"));
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.public.list.all.commandDescription"));
        addAction(empty);
        addAction(emptyOwn);
        addAction(emptyAll);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public list [own|all]
        
        if (args.length == 2) {
            sendPublicTPorts(player, false);
        } else if (args.length == 3) {
            if (args[2].equalsIgnoreCase("all")) {
                sendPublicTPorts(player, false);
            } else if (args[2].equalsIgnoreCase("own")) {
                sendPublicTPorts(player, true);
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport public list [own|all]");
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport public list [own|all]");
        }
    }
    
    private void sendPublicTPorts(Player player, boolean filterOwn) {
        Message publicTPortList = new Message();
        boolean hasPublicTPorts = false;
        boolean color = true;
        
        Collection<String> keys = tportData.getKeys("public.tports");
        int lastDelimiterIndex = keys.size() - 1;
        Message delimiter = formatInfoTranslation("tport.command.public.list.delimiter");
        int index = 0;
        
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport != null) {
                if (!filterOwn || tport.getOwner().equals(player.getUniqueId())) {
                    hasPublicTPorts = true;
                    if (color) publicTPortList.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s", asTPort(tport.parseAsPublic(true))));
                    else       publicTPortList.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s", asTPort(tport.parseAsPublic(true))));
                    color = !color;
                    
                    if (index + 1 == lastDelimiterIndex) publicTPortList.addMessage(formatInfoTranslation("tport.command.public.list.lastDelimiter"));
                    else                                 publicTPortList.addMessage(delimiter);
                }
            }
            index++;
        }
        publicTPortList.removeLast();
        
        String t = (filterOwn ? "own" : "all");
        if (!hasPublicTPorts) sendInfoTranslation(player, "tport.command.public.list." + t + ".hasNone");
        else sendInfoTranslation(player, "tport.command.public.list." + t + ".succeeded", publicTPortList);
    }
}
