package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class Item extends SubCommand {
    
    public Item() {
        setPermissions("TPort.edit.item", "TPort.basic");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.edit.item.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> item
        
        if (args.length != 3) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> item");
            return;
        }
        if (!hasPermissionToRun(player, true)) {
            return;
        }
        
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
            return;
        }
        if (tport.isOffered()) {
            sendErrorTranslation(player, "tport.command.edit.item.isOffered",
                    asTPort(tport), asPlayer(tport.getOfferedTo()));
            return;
        }
        
        ItemStack newItem = player.getInventory().getItemInMainHand();
        if (newItem.getType().equals(Material.AIR)) {
            sendErrorTranslation(player, "tport.command.edit.item.noItem");
            return;
        }
        ItemStack oldItem = tport.getItem();
        tport.setItem(newItem);
        tport.save();
        player.getInventory().setItemInMainHand(oldItem);
        
        sendSuccessTranslation(player, "tport.command.edit.item.succeeded", asTPort(tport), newItem.getType().name());
    }
}
