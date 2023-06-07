package com.spaceman.tport.commands.tport.pa.oldAndNew;

import com.spaceman.tport.Main;
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

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Enable extends SubCommand {
    
    private final ParticleAnimationCommand.AnimationType type;
    private final EmptyCommand emptyEnableState;
    
    public Enable(ParticleAnimationCommand.AnimationType type) {
        this.type = type;
        
        emptyEnableState = new EmptyCommand();
        emptyEnableState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyEnableState.setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand." + this.type + ".enable.state.commandDescription"));
        emptyEnableState.setPermissions("TPort.particleAnimation." + this.type + ".enable.set");
        
        addAction(emptyEnableState);
        
        setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand." + this.type + ".enable.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyEnableState.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Arrays.asList("true", "false");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport particleAnimation new|old enable [state]
        
        if (args.length == 3) {
            ParticleAnimation pa;
            if (this.type == ParticleAnimationCommand.AnimationType.NEW) {
                pa = TPEManager.getNewLocAnimation(player.getUniqueId());
            } else {
                pa = TPEManager.getOldLocAnimation(player.getUniqueId());
            }
            
            if (pa.isEnabled()) {
                sendInfoTranslation(player, "tport.command.particleAnimationCommand." + this.type + ".enable.succeeded",
                        formatTranslation(goodColor, varInfoColor, "tport.command.particleAnimationCommand." + this.type + ".enable.state.enabled"));
            } else {
                sendInfoTranslation(player, "tport.command.particleAnimationCommand." + this.type + ".enable.succeeded",
                        formatTranslation(badColor, varInfoColor, "tport.command.particleAnimationCommand." + this.type + ".enable.state.disabled"));
            }
        } else if (args.length == 4) {
            if (!emptyEnableState.hasPermissionToRun(player, true)) {
                return;
            }
            ParticleAnimation pa;
            if (this.type == ParticleAnimationCommand.AnimationType.NEW) {
                pa = TPEManager.getNewLocAnimation(player.getUniqueId());
            } else {
                pa = TPEManager.getOldLocAnimation(player.getUniqueId());
            }
            
            Boolean state = Main.toBoolean(args[3]);
            if (state == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation " + this.type + " enable [true|false]");
                return;
            }
            pa.setEnabled(state);
            if (pa.isEnabled()) {
                sendSuccessTranslation(player, "tport.command.particleAnimationCommand." + this.type + ".enable.state.succeeded",
                        formatTranslation(goodColor, varInfoColor, "tport.command.particleAnimationCommand." + this.type + ".enable.state.enabled"));
            } else {
                sendSuccessTranslation(player, "tport.command.particleAnimationCommand." + this.type + ".enable.state.succeeded",
                        formatTranslation(badColor, varInfoColor, "tport.command.particleAnimationCommand." + this.type + ".enable.state.disabled"));
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation " + this.type + " enable [state]");
        }
    }
}
