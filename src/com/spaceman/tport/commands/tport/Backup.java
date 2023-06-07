package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.commands.tport.backup.Load;
import com.spaceman.tport.commands.tport.backup.Save;
import com.spaceman.tport.inventories.SettingsInventories;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Backup extends SubCommand {
    
    public Backup() {
        
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.backup.commandDescription"));
        
        addAction(empty);
        addAction(new Save());
        addAction(new Load());
        addAction(new Auto());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport backup
        // tport backup save <name>
        // tport backup load <name>
        // tport backup auto [state|count]
        
        if (args.length == 1) {
            SettingsInventories.openTPortBackupGUI(player);
        } else {
            if (!runCommands(getActions(), args[1], args, player)) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport backup <save|load|auto> <name>|[state|count]");
            }
        }
    }
}
