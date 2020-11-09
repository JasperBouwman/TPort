package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Add extends SubCommand {
    
    private final EmptyCommand emptyTPortDescription;
    
    public Add() {
        emptyTPortDescription = new EmptyCommand(){
            @Override
            public Message permissionsHover() {
                Message message = new Message();
                message.addText(
                        textComponent("Permissions: (", infoColor),
                        textComponent("TPort.add.[X]", varInfoColor),
                        textComponent(" and ", infoColor),
                        textComponent("TPort.edit.description", varInfoColor),
                        textComponent(") or ", infoColor),
                        textComponent("TPort.basic", varInfoColor),
                        textComponent(". Permission '", infoColor),
                        textComponent("TPort.add.<X>", varInfoColor),
                        textComponent("' overrules all other permissions", infoColor));
                return message;
            }
        };
        emptyTPortDescription.setCommandName("description...", ArgumentType.OPTIONAL);
        emptyTPortDescription.setCommandDescription(textComponent("This command is used to add a TPort to your saved TPorts list, " +
                "and all arguments after the name are the description of that TPort. With ", infoColor),
                textComponent("\\\\n", varInfoColor),
                textComponent(" you can add a new line. With the character ", infoColor),
                textComponent("&", varInfoColor),
                textComponent(" and a color code you can add colors to your description", infoColor));
        emptyTPortDescription.setPermissions("TPort.add.[X]", "TPort.edit.description", "TPort.basic");
    
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(textComponent("This command is used to add a TPort to your saved TPorts list", infoColor));
        emptyTPort.addAction(emptyTPortDescription);
        emptyTPort.setPermissions("TPort.add.[X]", "TPort.basic");
        
        addAction(emptyTPort);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport add <TPort name> [description...]
        
        if (args.length == 1) {
            sendErrorTheme(player, "Usage: %s", "/tport add <TPort name> [description...]");
            sendErrorTheme(player, "Name is one word, and the description can be more");
            return;
        }
        
        ItemStack item = new ItemStack(player.getInventory().getItemInMainHand());
        if (item.getType().equals(Material.AIR)) {
            sendErrorTheme(player, "You must hold an item in your main hand");
            return;
        }
        TPort newTPort = new TPort(player.getUniqueId(), args[1], player.getLocation(), item);
        if (emptyTPortDescription.hasPermissionToRun(player, false)) {
            if (args.length > 2) {
                newTPort.setDescription(StringUtils.join(args, " ", 2, args.length));
            }
        } else {
            sendErrorTheme(player, "Could not add description to TPort, missing permissions: %s or %s", "TPort.edit.description", "TPort.basic");
        }
        if (TPortManager.addTPort(player, newTPort, true) != null) {
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
        }
    }
}
