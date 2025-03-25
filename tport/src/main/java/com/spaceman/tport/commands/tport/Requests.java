package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.requests.Accept;
import com.spaceman.tport.commands.tport.requests.Reject;
import com.spaceman.tport.commands.tport.requests.Revoke;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.tpEvents.TPRequest;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.runCommand;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class Requests extends SubCommand {
    
    public Requests() {
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.requests.commandDescription"));
        
        addAction(empty);
        addAction(new Accept());
        addAction(new Reject());
        addAction(new Revoke());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport requests
        // tport requests accept [player...]
        // tport requests reject [player...]
        // tport requests revoke
        
        if (args.length == 1) {
            TPRequest sendRequest = TPRequest.getRequest(player.getUniqueId());
            ArrayList<TPRequest> requests = TPRequest.getRequestsToYou(player.getUniqueId());
            
            if (sendRequest == null && requests.isEmpty()) {
                sendInfoTranslation(player, "tport.command.requests.noRequests");
                return;
            }
            
            if (sendRequest != null) {
                sendInfoTranslation(player, "tport.command.requests.sendRequest", sendRequest.toInfo()); //todo show safety check info (maybe inventory?)
                if (sendRequest.isTPortRequest()) {
                    TPort tport = TPortManager.getTPort(sendRequest.getRequestToUUID(), sendRequest.getTPortUUID());
                    if (tport != null) {
//                        sendInfoTranslation(player, "", SafetyCheck.isSafe(tport.getLocation()));
                    }
                } else {
                    Player p = Bukkit.getPlayer(sendRequest.getRequestToUUID());
                    if (p != null) {
//                        sendInfoTranslation(player, "", SafetyCheck.isSafe(p.getLocation()));
                    }
                }
            }
            if (!requests.isEmpty()) {
                
                Message list = new Message();
                int size = requests.size();
                boolean color = true;
                for (int i = 0; i < size; i++) {
                    TPRequest request = requests.get(i);
                    Player requester = Bukkit.getPlayer(request.getRequesterUUID());
                    assert requester != null;
                    
                    if (color) {
                        TextComponent accept = textComponent("tport.command.requests.accept", varInfoColor)
                                .setType(TextType.TRANSLATE)
                                .addTextEvent(hoverEvent("/tport requests accept " + requester.getName(), infoColor))
                                .addTextEvent(runCommand("/tport requests accept " + requester.getName()))
                                .setInsertion("/tport requests accept " + requester.getName());
                        TextComponent reject = textComponent("tport.command.requests.reject", varInfoColor)
                                .setType(TextType.TRANSLATE)
                                .addTextEvent(hoverEvent("/tport requests reject " + requester.getName(), infoColor))
                                .addTextEvent(runCommand("/tport requests reject " + requester.getName()))
                                .setInsertion("/tport requests reject " + requester.getName());
                        
                        list.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.requests.listElement", asPlayer(requester), request.toInfo(), accept, reject));
                    } else {
                        TextComponent accept = textComponent("tport.command.requests.accept", varInfo2Color)
                                .setType(TextType.TRANSLATE)
                                .addTextEvent(hoverEvent("/tport requests accept " + requester.getName(), infoColor))
                                .addTextEvent(runCommand("/tport requests accept " + requester.getName()))
                                .setInsertion("/tport requests accept " + requester.getName());
                        TextComponent reject = textComponent("tport.command.requests.reject", varInfo2Color)
                                .setType(TextType.TRANSLATE)
                                .addTextEvent(hoverEvent("/tport requests reject " + requester.getName(), infoColor))
                                .addTextEvent(runCommand("/tport requests reject " + requester.getName()))
                                .setInsertion("/tport requests reject " + requester.getName());
                        
                        list.addMessage(formatTranslation(infoColor, varInfo2Color, "tport.command.requests.listElement", asPlayer(requester), request.toInfo2(), accept, reject));
                    }
                    
                    if (i + 2 == size) list.addMessage(formatInfoTranslation("tport.command.requests.lastDelimiter"));
                    else               list.addMessage(formatInfoTranslation("tport.command.requests.delimiter"));
                    
                    color = !color;
                }
                list.removeLast();
                
                sendInfoTranslation(player, "tport.command.requests.receiveRequests", list);
            }
            
        } else {
            if (!runCommands(getActions(), args[1], args, player)) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport requests " + CommandTemplate.convertToArgs(getActions(), true));
            }
        }
    }
}
