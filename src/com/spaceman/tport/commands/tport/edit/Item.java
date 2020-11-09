package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Item extends SubCommand {
    
    public Item() {
        setPermissions("TPort.edit.item", "TPort.basic");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to edit the item of the given TPort, the item in your main hand will become the new item," +
                " and you will get the old item back", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> item
    
        if (!hasPermissionToRun(player, true)) {
            return;
        }
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
    
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                return;
            }
            ItemStack newItem = player.getInventory().getItemInMainHand();
            if (newItem.getType().equals(Material.AIR)) {
                sendErrorTheme(player, "You must place an item in your main hand");
                return;
            }
            ItemStack oldItem = tport.getItem();
            tport.setItem(newItem);
            tport.save();
            player.getInventory().setItemInMainHand(oldItem);
            
            Message message = new Message();
            ColorTheme theme = ColorTheme.getTheme(player);
            message.addText(textComponent("Successfully set item for TPort ", theme.getSuccessColor()));
            message.addText(textComponent(tport.getName(), theme.getVarSuccessColor(), ClickEvent.runCommand("/tport own " + tport.getName())));
            message.addText(textComponent(" to ", theme.getSuccessColor()));
            message.addText(textComponent(newItem.getType().toString(), theme.getVarSuccessColor()));
            message.sendMessage(player);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> item");
        }
    }
}
