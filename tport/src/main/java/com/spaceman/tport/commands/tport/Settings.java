package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.inventories.SettingsInventories;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Settings extends SubCommand {
    
    public Settings() {
        this.setCommandDescription(formatInfoTranslation("tport.command.settings.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport settings
        
        if (args.length == 1) {
            SettingsInventories.openSettingsGUI(player, 0, null);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport settings");
        }
        
    }
}
