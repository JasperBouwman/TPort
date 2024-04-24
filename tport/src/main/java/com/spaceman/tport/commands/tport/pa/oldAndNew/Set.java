package com.spaceman.tport.commands.tport.pa.oldAndNew;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.ParticleAnimationCommand;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import com.spaceman.tport.tpEvents.TPEManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Set extends SubCommand {
    
    private final ParticleAnimationCommand.AnimationType type;
    private final EmptyCommand emptySetParticleData;
    
    public Set(ParticleAnimationCommand.AnimationType type) {
        this.type = type;
        
        emptySetParticleData = new EmptyCommand();
        emptySetParticleData.setCommandName("data", ArgumentType.OPTIONAL);
        emptySetParticleData.setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand." + this.type + ".set.particleAnimation.data.commandDescription"));
        emptySetParticleData.setTabRunnable((args, player) -> ParticleAnimation.getNewAnimation(args[3]).tabList(player, Arrays.copyOfRange(args, 4, args.length)));
        emptySetParticleData.setLooped(true);
        emptySetParticleData.setPermissions("TPort.particleAnimation." + this.type + ".set");
        
        EmptyCommand emptySetParticle = new EmptyCommand();
        emptySetParticle.setCommandName("particleAnimation", ArgumentType.REQUIRED);
        emptySetParticle.setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand." + this.type + ".set.particleAnimation.commandDescription"));
        emptySetParticle.setTabRunnable((args, player) -> emptySetParticleData.tabList(player, args));
        emptySetParticle.addAction(emptySetParticleData);
        emptySetParticle.setPermissions(emptySetParticleData.getPermissions());
        
        addAction(emptySetParticle);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptySetParticleData.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return ParticleAnimation.getAnimations();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport particleAnimation new|old set <particleAnimation> [data...]
    
        if (args.length < 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation " + this.type + ". set <particleAnimation> [data...]");
            return;
        }
        if (!emptySetParticleData.hasPermissionToRun(player, true)) {
            return;
        }
        
        ParticleAnimation pa = ParticleAnimation.getNewAnimation(args[3], Arrays.copyOfRange(args, 4, args.length), player);
        if (pa == null) {
            sendErrorTranslation(player, "tport.command.particleAnimationCommand." + this.type + ".set.particleAnimation.data.animationNotFound", args[3]);
            return;
        }
        if (this.type == ParticleAnimationCommand.AnimationType.NEW) {
            TPEManager.setNewLocAnimation(player.getUniqueId(), pa);
        } else {
            TPEManager.setOldLocAnimation(player.getUniqueId(), pa);
        }
        sendSuccessTranslation(player, "tport.command.particleAnimationCommand." + this.type + ".set.particleAnimation.data.succeeded", pa);
    }
}
