package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.commands.tport.backup.Load;
import com.spaceman.tport.commands.tport.backup.Save;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;

public class Backup extends SubCommand {
    
    public Backup() {
        addAction(new Save());
        addAction(new Load());
        addAction(new Auto());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport backup save <name>
        // tport backup load <name>
        // tport backup auto [state|count]
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport backup <save|load|auto> <name>|[state|count]");
    }
}
