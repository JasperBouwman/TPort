package com.spaceman.tport.commands.tport.pa;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import org.bukkit.entity.Player;

import java.util.Set;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;

public class List extends SubCommand {
    
    public List() {
        setPermissions("TPort.particleAnimation.list");
        setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand.list.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport particleAnimation list
        
        if (args.length == 2) {
            if (!hasPermissionToRun(player, true)) {
                return;
            }
            Message animationsMessage = new Message();
            
            boolean color = true;
            
            Set<String> particleAnimations = ParticleAnimation.getAnimations();
            int i = particleAnimations.size();
            for (String pa : particleAnimations) {
                ParticleAnimation particleAnimation = ParticleAnimation.getNewAnimation(pa);
                HoverEvent hEvent = new HoverEvent(particleAnimation.getDescription());
                animationsMessage.addText(textComponent(pa, color ? varInfoColor : varInfo2Color, hEvent));
                
                if (i == 2) animationsMessage.addMessage(formatInfoTranslation("tport.command.particleAnimationCommand.list.lastDelimiter"));
                else        animationsMessage.addMessage(formatInfoTranslation("tport.command.particleAnimationCommand.list.delimiter"));
                
                i--;
                color = !color;
            }
            animationsMessage.removeLast();
            
            sendInfoTranslation(player, "tport.command.particleAnimationCommand.list.succeeded", animationsMessage);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation list");
        }
    }
}
