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

public class Edit extends SubCommand {
    
    private final ParticleAnimationCommand.AnimationType type;
    private final EmptyCommand emptyEditData;
    
    public Edit(ParticleAnimationCommand.AnimationType type) {
        this.type = type;
        
        emptyEditData = new EmptyCommand();
        emptyEditData.setCommandName("data", ArgumentType.REQUIRED);
        emptyEditData.setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand." + this.type + ".edit.data.commandDescription"));
        emptyEditData.setTabRunnable((args, player) -> {
            if (!emptyEditData.hasPermissionToRun(player, false)) {
                return Collections.emptyList();
            }
            if (this.type == ParticleAnimationCommand.AnimationType.NEW) {
                return TPEManager.getNewLocAnimation(player.getUniqueId()).tabList(player, Arrays.copyOfRange(args, 3, args.length));
            } else {
                return TPEManager.getOldLocAnimation(player.getUniqueId()).tabList(player, Arrays.copyOfRange(args, 3, args.length));
            }
        });
        emptyEditData.setLooped(true);
        emptyEditData.setPermissions("TPort.particleAnimation." + this.type + ".edit");
        
        addAction(emptyEditData);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyEditData.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return emptyEditData.tabList(player, args);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport particleAnimation new|old edit <data...>
    
        if (args.length < 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation " + this.type + " edit <data...>");
            return;
        }
        if (!emptyEditData.hasPermissionToRun(player, true)) {
            return;
        }
        
        ParticleAnimation pa;
        if (this.type == ParticleAnimationCommand.AnimationType.NEW) {
            pa = TPEManager.getNewLocAnimation(player.getUniqueId());
        } else {
            pa = TPEManager.getOldLocAnimation(player.getUniqueId());
        }
        
        String[] data = Arrays.copyOfRange(args, 3, args.length);
        if (pa.edit(player, data)) {
            sendSuccessTranslation(player, "tport.command.particleAnimationCommand." + this.type + ".edit.data.succeeded", String.join(" ", data));
        }
    }
}
