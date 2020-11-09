package com.spaceman.tport.commands.tport.teleporter;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Teleporter;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Remove extends SubCommand {
    
    public Remove() {
        setPermissions("TPort.teleporter.remove");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to remove the Teleporter from the item in your main hand", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport teleporter remove
    
        if (hasPermissionToRun(player, true)) {
            ItemStack is = player.getInventory().getItemInMainHand();
            if (Teleporter.removeTeleporter(is)) {
                sendSuccessTheme(player, "Successfully removed TPort Teleporter from item");
            } else {
                sendErrorTheme(player, "Given item is not a TPort Teleporter");
            }
        }
    }
}
