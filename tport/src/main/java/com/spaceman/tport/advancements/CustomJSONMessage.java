package com.spaceman.tport.advancements;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import eu.endercentral.crazy_advancements.JSONMessage;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.craftbukkit.v1_21_R6.util.CraftChatMessage;

public class CustomJSONMessage extends JSONMessage {
    
    private final Message message;
    private final ColorTheme colorTheme;
    
    public CustomJSONMessage(Message message, ColorTheme colorTheme) {
        super(new TextComponent());
        
        this.message = message;
        this.colorTheme = colorTheme;
    }
    
    @Override
    public net.minecraft.network.chat.IChatBaseComponent getBaseComponent() {
        return CraftChatMessage.fromJSON(message.translateJSON(colorTheme));
    }
    
}
