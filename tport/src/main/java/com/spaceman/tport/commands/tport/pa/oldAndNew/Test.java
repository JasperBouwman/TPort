package com.spaceman.tport.commands.tport.pa.oldAndNew;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.ParticleAnimationCommand;
import com.spaceman.tport.tpEvents.TPEManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Test extends SubCommand {
    
    private final ParticleAnimationCommand.AnimationType type;
    
    public Test(ParticleAnimationCommand.AnimationType type) {
        this.type = type;
        
        this.setPermissions("TPort.particleAnimation." + this.type + ".test");
        this.setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand." + this.type + ".test"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport particleAnimation new|old test
        
        if (args.length == 4) {
            if (!this.hasPermissionToRun(player, true)) {
                return;
            }
            
            if (this.type == ParticleAnimationCommand.AnimationType.NEW) {
                TPEManager.getNewLocAnimation(player.getUniqueId()).show(player, player.getLocation());
            } else {
                TPEManager.getOldLocAnimation(player.getUniqueId()).show(player, player.getLocation());
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation " + this.type + " test");
        }
        
    }
}
