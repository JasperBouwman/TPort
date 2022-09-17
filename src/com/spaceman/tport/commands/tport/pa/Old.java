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

public class Old extends SubCommand {
    
    public Old() {
        addAction(new Set(ParticleAnimationCommand.AnimationType.OLD));
        addAction(new Edit(ParticleAnimationCommand.AnimationType.OLD));
        addAction(new Test(ParticleAnimationCommand.AnimationType.OLD));
        addAction(new Enable(ParticleAnimationCommand.AnimationType.OLD));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport particleAnimation old set <particleAnimation> [data...]
        // tport particleAnimation old edit <data...>
        // tport particleAnimation old test
        // tport particleAnimation old enable [state]
        
        if (args.length > 2) {
            if (runCommands(getActions(), args[2], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation old <set|edit|test|enable>");
    }
}
