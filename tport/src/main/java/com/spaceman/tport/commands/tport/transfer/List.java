package com.spaceman.tport.commands.tport.transfer;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fileHander.Files.tportData;

public class List extends SubCommand {
    
    public List() {
        setCommandDescription(formatInfoTranslation("tport.command.transfer.list.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport transfer list
        
        if (args.length == 2) {
            
            boolean color = true;
            ArrayList<Message> offered = new ArrayList<>();
            for (TPort tport : TPortManager.getTPortList(player.getUniqueId())) {
                if (tport.isOffered()) {
                    PlayerEncapsulation otherPlayer = asPlayer(tport.getOfferedTo());
                    
                    HoverEvent hEvent = new HoverEvent();
                    hEvent.addMessage(formatInfoTranslation(
                            "tport.command.transfer.list.offeredHoverFormat",
                            tport.getName(),
                            tport,
                            otherPlayer,
                            "/tport transfer revoke " + tport.getName()
                    ));
                    
                    Message m = formatTranslation(
                            color ? varInfoColor : varInfo2Color,
                            color ? varInfoColor : varInfo2Color,
                            "tport.command.transfer.list.offeredFormat",
                            tport.getName(),
                            tport,
                            otherPlayer,
                            "/tport transfer revoke " + tport.getName()
                    );
                    m.getText().forEach(t -> t
                            .addTextEvent(hEvent)
                            .addTextEvent(ClickEvent.runCommand("/tport transfer revoke " + tport.getName()))
                    );
                    
                    offered.add(m);
                    color = !color;
                }
            }
            int offeredSize = offered.size();
            Message offeredMessage = new Message();
            for (int i = 0; i < offeredSize; i++) {
                offeredMessage.addMessage(offered.get(i));
                
                if (i + 2 == offeredSize) offeredMessage.addMessage(formatInfoTranslation("tport.command.transfer.list.lastDelimiter"));
                else                      offeredMessage.addMessage(formatInfoTranslation("tport.command.transfer.list.delimiter"));
            }
            offeredMessage.removeLast();
            
            color = true;
            ArrayList<Message> offers = new ArrayList<>();
            for (String uuidString : tportData.getKeys("tport")) {
                UUID uuid = UUID.fromString(uuidString);
                for (TPort tport : TPortManager.getTPortList(uuid)) {
                    if (player.getUniqueId().equals(tport.getOfferedTo())) {
                        
                        PlayerEncapsulation otherPlayer = asPlayer(tport.getOwner());
                        
                        HoverEvent hEvent = new HoverEvent();
                        hEvent.addMessage(formatInfoTranslation(
                                "tport.command.transfer.list.offerHoverFormat",
                                tport.getName(),
                                tport,
                                otherPlayer,
                                "/tport transfer accept " + otherPlayer.getName() + " " + tport.getName(),
                                "/tport transfer reject " + otherPlayer.getName() + " " + tport.getName()
                        ));
                        
                        Message m = formatTranslation(
                                color ? varInfoColor : varInfo2Color,
                                color ? varInfoColor : varInfo2Color,
                                "tport.command.transfer.list.offerFormat",
                                tport.getName(),
                                tport,
                                otherPlayer,
                                "/tport transfer accept " + tport.getName(),
                                "/tport transfer reject " + tport.getName()
                        );
                        m.getText().forEach(t -> t
                                .addTextEvent(hEvent)
                                .addTextEvent(ClickEvent.runCommand("/tport transfer accept " + otherPlayer.getName() + " " + tport.getName()))
                                .setInsertion("/tport transfer reject " + otherPlayer.getName() + " " + tport.getName())
                        );
                        for (TextComponent t : m.getText()) {
                            for (Message with : t.getTranslateWith()) {
                                for (TextComponent withT : with.getText()) {
                                    withT.setInsertion("/tport transfer reject " + otherPlayer.getName() + " " + tport.getName());
                                }
                            }
                        }
                        
                        offers.add(m);
                        color = !color;
                    }
                }
            }
            int offersSize = offers.size();
            Message offersMessage = new Message();
            for (int i = 0; i < offersSize; i++) {
                offersMessage.addMessage(offers.get(i));
        
                if (i + 2 == offersSize) offersMessage.addMessage(formatInfoTranslation("tport.command.transfer.list.lastDelimiter"));
                else                     offersMessage.addMessage(formatInfoTranslation("tport.command.transfer.list.delimiter"));
            }
            offersMessage.removeLast();
            
            Message offeredPrefix = switch (offeredSize) {
                case 0 -> formatInfoTranslation("tport.command.transfer.list.noOffered");
                case 1 -> formatInfoTranslation("tport.command.transfer.list.singleOffered");
                default -> formatInfoTranslation("tport.command.transfer.list.multipleOffered");
            };
            
            Message offersPrefix = switch (offersSize) {
                case 0 -> formatInfoTranslation("tport.command.transfer.list.noOffers");
                case 1 -> formatInfoTranslation("tport.command.transfer.list.singleOffer");
                default -> formatInfoTranslation("tport.command.transfer.list.multipleOffers");
            };
            
            sendInfoTranslation(player, "tport.command.transfer.list.succeeded", offeredPrefix, offeredMessage, offersPrefix, offersMessage);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport transfer list");
        }
    }
}
