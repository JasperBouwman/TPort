package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.pa.List;
import com.spaceman.tport.commands.tport.pa.New;
import com.spaceman.tport.commands.tport.pa.Old;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class ParticleAnimationCommand extends SubCommand {
    
    public enum AnimationType {
        OLD,
        NEW;
        
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
    
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
        
        if (Features.Feature.ParticleAnimation.isDisabled())  {
            Features.Feature.ParticleAnimation.sendDisabledMessage(player);
            return;
        }
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation <new|old|list>");
    }
}
