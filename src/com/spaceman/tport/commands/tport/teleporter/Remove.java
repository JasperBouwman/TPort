package com.spaceman.tport.commands.tport.teleporter;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Teleporter;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Remove extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to remove the Teleporter from the item in your main hand", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.remove", ColorTheme.ColorType.varInfoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport teleporter remove
    
        if (hasPermission(player, true, true, "TPort.teleporter.remove")) {
            ItemStack is = player.getInventory().getItemInMainHand();
            if (Teleporter.removeTeleporter(is)) {
                sendSuccessTheme(player, "Successfully removed TPort Teleporter from item");
            } else {
                sendErrorTheme(player, "Given item is not a TPort Teleporter");
            }
        }
    }
}
