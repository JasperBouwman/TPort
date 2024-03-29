package com.spaceman.tport.tpEvents.restrictions;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tpEvents.TPRestriction;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.SubCommand.lowerCaseFirst;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;

public class NoneRestriction extends TPRestriction {
    
    @Override
    public String getRestrictionName() {
        return lowerCaseFirst(this.getClass().getSimpleName());
    }
    
    @Override
    public void start(Player player, int taskID) {
    
    }
    
    @Override
    public boolean shouldTeleport(Player player) {
        return true;
    }
    
    @Override
    public void cancel() {
    
    }
    
    @Override
    public Message getDescription() {
        return formatInfoTranslation("tport.tpEvents.restrictions.noneRestriction.description");
    }
}
