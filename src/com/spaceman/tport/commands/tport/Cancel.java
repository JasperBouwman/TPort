package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.tpEvents.TPEManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Cancel extends SubCommand {
    
    public Cancel() {
        setPermissions("TPort.cancel", "TPort.basic");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to cancel your teleport request", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport cancel
        
        if (hasPermissionToRun(player, true)) {
            if (TPEManager.cancelTP(player.getUniqueId())) {
                sendSuccessTheme(player, "Successfully canceled your teleport request");
            } else {
                sendErrorTheme(player, "You don't have a teleport request");
            }
        }
    }
}
