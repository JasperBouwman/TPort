package com.spaceman.tport.commands.tport.pa;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.ParticleAnimationCommand;
import com.spaceman.tport.commands.tport.pa.oldAndNew.Edit;
import com.spaceman.tport.commands.tport.pa.oldAndNew.Enable;
import com.spaceman.tport.commands.tport.pa.oldAndNew.Set;
import com.spaceman.tport.commands.tport.pa.oldAndNew.Test;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class New extends SubCommand {
    
    public New() {
        addAction(new Set(ParticleAnimationCommand.AnimationType.NEW));
        addAction(new Edit(ParticleAnimationCommand.AnimationType.NEW));
        addAction(new Test(ParticleAnimationCommand.AnimationType.NEW));
        addAction(new Enable(ParticleAnimationCommand.AnimationType.NEW));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport particleAnimation new set <particleAnimation> [data...]
        // tport particleAnimation new edit <data...>
        // tport particleAnimation new test
        // tport particleAnimation new enable [state]
        
        if (args.length > 2) {
            if (runCommands(getActions(), args[2], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation new <set|edit|test|enable>");
    }
}
