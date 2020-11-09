package com.spaceman.tport.commands.tport.pa;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class List extends SubCommand {
    
    public List() {
        setPermissions("TPort.particleAnimation.list");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get all the available particle animations", infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport particleAnimation list
        
        if (!hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 2) {
            Message message = new Message();
            
            message.addText(textComponent("The available Particle Animations are: ", infoColor));
            boolean color = true;
            
            for (String pa : ParticleAnimation.getAnimations()) {
                ParticleAnimation particleAnimation = ParticleAnimation.getNewAnimation(pa);
                HoverEvent hEvent = new HoverEvent();
                hEvent.addMessage(particleAnimation.getDescription());
                message.addText(textComponent(pa, color ? varInfoColor : varInfo2Color, hEvent));
                message.addText(textComponent(", ", infoColor));
                color = !color;
            }
            message.removeLast();
            
            message.sendMessage(player);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport particleAnimation list");
        }
    }
}
