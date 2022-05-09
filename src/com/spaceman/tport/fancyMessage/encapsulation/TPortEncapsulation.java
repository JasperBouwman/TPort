package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPortManager;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class TPortEncapsulation extends Encapsulation {
    
    private com.spaceman.tport.tport.TPort tport = null;
    private String tportName = null;
    
    public static TPortEncapsulation asTPort(UUID tportUUID) {
        return new TPortEncapsulation(tportUUID, null);
    }
    
    public static TPortEncapsulation asTPort(UUID tportUUID, UUID ownerUUID) {
        return new TPortEncapsulation(tportUUID, ownerUUID);
    }
    
    public static TPortEncapsulation asTPort(com.spaceman.tport.tport.TPort tport) {
        return new TPortEncapsulation(tport);
    }
    
    public static TPortEncapsulation asTPort(com.spaceman.tport.tport.TPort tport, String tportName) {
        return new TPortEncapsulation(tport, tportName);
    }
    
    public static TPortEncapsulation asTPort(com.spaceman.tport.tport.TPort tport, UUID tportUUID, UUID ownerUUID) {
        return new TPortEncapsulation(tport, tportUUID, ownerUUID);
    }
    
    public static TPortEncapsulation asTPort(com.spaceman.tport.tport.TPort tport, UUID tportUUID, UUID ownerUUID, String tportName) {
        return new TPortEncapsulation(tport, tportUUID, ownerUUID, tportName);
    }
    
    public TPortEncapsulation(UUID tportUUID, @Nullable UUID ownerUUID) {
        if (ownerUUID != null) this.tport = TPortManager.getTPort(ownerUUID, tportUUID);
        else                   this.tport = TPortManager.getTPort(tportUUID);
    }
    
    public TPortEncapsulation(com.spaceman.tport.tport.TPort tport) {
        this.tport = tport;
    }
    
    public TPortEncapsulation(com.spaceman.tport.tport.TPort tport, String tportName) {
        if (tport == null) this.tportName = tportName;
        else               this.tport = tport;
    }
    
    public TPortEncapsulation(com.spaceman.tport.tport.TPort tport, UUID tportUUID, UUID ownerUUID) {
        if (tport != null) this.tport = tport;
        else this.tport = TPortManager.getTPort(tportUUID, ownerUUID);
    }
    
    public TPortEncapsulation(com.spaceman.tport.tport.TPort tport, UUID tportUUID, UUID ownerUUID, String tportName) {
        if (tport != null) this.tport = tport;
        else {
            this.tport = TPortManager.getTPort(tportUUID, ownerUUID);
            if (this.tport == null) this.tportName = tportName;
        }
    }
    
    @Override
    public String asString() {
        if (tport == null) return tportName;
        return tport.getName();
    }
    
    private String getCommand() {
        String command;
        if (tport.parseAsPublic()) command = "/tport public open " + tport.getName();
        else command = "/tport open " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName();
        return command;
    }
    
    @Override
    public HoverEvent getHoverEvent() {
        if (tport == null) return null;
        HoverEvent hEvent = new HoverEvent();
        hEvent.addText(textComponent(getCommand(), ColorTheme.ColorType.infoColor));
        hEvent.addText(TextComponent.NEW_LINE);
        for (Message message : tport.getHoverData(true)) {
            hEvent.addText(TextComponent.NEW_LINE);
            hEvent.addMessage(message);
        }
        
        return hEvent;
    }
    
    @Override
    public ClickEvent getClickEvent() {
        if (tport == null) return null;
        return ClickEvent.runCommand(getCommand());
    }
    
    @Override
    public String getInsertion() {
        if (tport == null) return tportName;
        return tport.getName();
    }
}
