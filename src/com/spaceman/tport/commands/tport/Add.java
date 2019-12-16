package com.spaceman.tport.commands.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Add extends SubCommand {
    
    public Add() {
        EmptyCommand emptyCommand1 = new EmptyCommand();
        emptyCommand1.setCommandName("description...", ArgumentType.OPTIONAL);
        emptyCommand1.setCommandDescription(textComponent("This command is used to add a TPort to your saved TPorts list, " +
                "and all arguments after the name are the description of that TPort. With ", ColorTheme.ColorType.infoColor),
                textComponent("\\\\n", ColorTheme.ColorType.varInfoColor),
                textComponent(" you can add a new line. With the character ", ColorTheme.ColorType.infoColor),
                textComponent("&", ColorTheme.ColorType.varInfoColor),
                textComponent(" and a color code you can add colors to your description", ColorTheme.ColorType.infoColor),
                
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.add.[X]", ColorTheme.ColorType.varInfoColor),
                textComponent(" and ", ColorTheme.ColorType.infoColor), textComponent("TPort.edit.description", ColorTheme.ColorType.varInfoColor),
                textComponent(", or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(textComponent("This command is used to add a TPort to your saved TPorts list", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.add.[X]", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        emptyCommand.addAction(emptyCommand1);
        
        addAction(emptyCommand);
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
        if (hasPermission(player, false, true, "TPort.edit.description", "TPort.basic")) {
            if (args.length > 2) {
                newTPort.setDescription(String.join(" ", Arrays.asList(args).subList(2, args.length)));
            }
        } else {
            sendErrorTheme(player, "Could not add description to TPort, missing permissions: %s or %s", "TPort.edit.description", "TPort.basic");
        }
        if (TPortManager.addTPort(player, newTPort, true) != null) {
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
        }
    }
}
