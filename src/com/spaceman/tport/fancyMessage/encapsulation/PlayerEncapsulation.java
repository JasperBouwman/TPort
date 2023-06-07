package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import static com.spaceman.tport.commands.TPortCommand.getPlayerData;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class PlayerEncapsulation extends Encapsulation {
    
    private final UUID uuid;
    private final String name;
    
    public static PlayerEncapsulation asPlayer(@Nonnull UUID uuid) {
        return new PlayerEncapsulation(uuid);
    }
    
    public static PlayerEncapsulation asPlayer(@Nonnull Player player) {
        return new PlayerEncapsulation(player);
    }
    
    public static PlayerEncapsulation asPlayer(UUID uuid, String name) {
        return new PlayerEncapsulation(uuid, name);
    }
    
    public static PlayerEncapsulation asPlayer(@Nullable Player player, UUID uuid) {
        if (player != null) return new PlayerEncapsulation(player.getUniqueId(), player.getName());
        else                return new PlayerEncapsulation(uuid);
    }
    
    public PlayerEncapsulation(@Nonnull UUID uuid) {
        this.name = PlayerUUID.getPlayerName(uuid);
        this.uuid = uuid;
    }
    
    public PlayerEncapsulation(@Nonnull org.bukkit.entity.Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }
    
    public PlayerEncapsulation(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
    
    
    public PlayerEncapsulation(@Nullable org.bukkit.entity.Player player, UUID uuid, String name) {
        if (player == null) {
            this.uuid = uuid;
            this.name = name;
        } else {
            this.uuid = player.getUniqueId();
            this.name = player.getName();
        }
    }
    
    @Override
    public String asString() {
        return name;
    }
    
    @Override
    public String getInsertion() {
        return name;
    }
    
    @Override
    public ClickEvent getClickEvent() {
        return ClickEvent.runCommand("/tport pltp tp " + name);
    }
    
    @Override
    public HoverEvent getHoverEvent() {
        HoverEvent hEvent = new HoverEvent();
        hEvent.addText(textComponent("/tport pltp tp " + name, ColorTheme.ColorType.infoColor));
        hEvent.addText(TextComponent.NEW_LINE);
        
        for (Message message : getPlayerData(uuid)) {
            hEvent.addText(TextComponent.NEW_LINE);
            hEvent.addMessage(message);
        }
        
        return hEvent;
    }
    
    public String getName() {
        return name;
    }
}
