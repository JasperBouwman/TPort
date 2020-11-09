package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.pa.List;
import com.spaceman.tport.commands.tport.pa.New;
import com.spaceman.tport.commands.tport.pa.Old;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;

public class ParticleAnimationCommand extends SubCommand {
    
    public ParticleAnimationCommand() {
        addAction(new New());
        addAction(new Old());
        addAction(new List());
    }
    
    @Override
    public String getName(String arg) {
        return "particleAnimation";
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport particleAnimation new set <particle> [data...]
        // tport particleAnimation new edit <data...>
        // tport particleAnimation new test
        // tport particleAnimation new enable [state]
        // tport particleAnimation old set <particle> [data...]
        // tport particleAnimation old edit <data...>
        // tport particleAnimation old test
        // tport particleAnimation old enable [state]
        // tport particleAnimation list
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage %s", "/tport particleAnimation <new|old|list>");
    }
}
