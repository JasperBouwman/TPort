package com.spaceman.tport.commands.tport.teleporter;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Teleporter;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Remove extends SubCommand {
    
    public Remove() {
        setPermissions("TPort.teleporter.remove");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.teleporter.remove.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport teleporter remove
        
        if (args.length == 2) {
            if (hasPermissionToRun(player, true)) {
                ItemStack is = player.getInventory().getItemInMainHand();
                if (Teleporter.removeTeleporter(is)) {
                    sendSuccessTranslation(player, "tport.command.teleporter.remove.succeeded");
                } else {
                    sendErrorTranslation(player, "tport.command.teleporter.remove.isNotTeleporter");
                }
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter remove");
        }
    }
}
