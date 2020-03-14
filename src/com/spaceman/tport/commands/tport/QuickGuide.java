package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.SubCommand;
import org.bukkit.entity.Player;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;

public class QuickGuide extends SubCommand {
    
    @Override
    public void run(String[] args, Player player) {
        // tport quickGuide [guide]
        sendErrorTheme(player, "Feature not done yet");
    }
}
