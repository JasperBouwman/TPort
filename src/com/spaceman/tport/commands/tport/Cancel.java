package com.spaceman.tport.commands.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tpEvents.TPEManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Cancel extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to cancel your teleport request", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.cancel", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport cancel
        
        if (hasPermission(player, true, true, "TPort.cancel", "TPort.basic")) {
            if (TPEManager.cancelTP(player.getUniqueId())) {
                sendSuccessTheme(player, "Successfully canceled your teleport request");
            } else {
                sendErrorTheme(player, "You don't have a teleport request");
            }
        }
    }
}
