package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Add extends SubCommand {
    
    private final EmptyCommand emptyAddTPortDescription;
    
    public Add() {
        emptyAddTPortDescription = new EmptyCommand() {
            @Override
            public Message permissionsHover() {
                return formatInfoTranslation("tport.command.add.description.permissionHover", "TPort.add.[X]", "TPort.edit.description", "TPort.basic", "TPort.add.<X>");
            }
        };
        emptyAddTPortDescription.setCommandName("description...", ArgumentType.OPTIONAL);
        emptyAddTPortDescription.setCommandDescription(formatInfoTranslation("tport.command.add.description.commandDescription", "\\n", "&"));
        emptyAddTPortDescription.setPermissions("TPort.add.[X]", "TPort.edit.description", "TPort.basic");
        
        EmptyCommand emptyAddTPort = new EmptyCommand() {
            @Override
            public Message permissionsHover() {
                return formatInfoTranslation("tport.command.add.permissionHover", "TPort.add.<X>", "TPort.add", "TPort.basic");
            }
        };
        emptyAddTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyAddTPort.setCommandDescription(formatInfoTranslation("tport.command.add.commandDescription"));
        emptyAddTPort.addAction(emptyAddTPortDescription);
        emptyAddTPort.setPermissions("TPort.add.<X>", "TPort.add", "TPort.basic");
        
        addAction(emptyAddTPort);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport add <TPort name> [description...]
        
        if (args.length == 1) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport add <TPort name> [description...]");
            sendErrorTranslation(player, "tport.command.add.wrongUsage2");
            return;
        }
        
        ItemStack item = new ItemStack(player.getInventory().getItemInMainHand());
        if (item.getType().equals(Material.AIR)) {
            sendErrorTranslation(player, "tport.command.add.noItem");
            return;
        }
        TPort newTPort = new TPort(player.getUniqueId(), args[1], player.getLocation(), item);
        
        if (args.length > 2) {
            if (emptyAddTPortDescription.hasPermissionToRun(player, false)) {
                newTPort.setDescription(StringUtils.join(args, " ", 2, args.length));
            } else {
                sendErrorTranslation(player, "tport.command.add.noDescription", "TPort.edit.description", "TPort.basic");
            }
        }
        
        if (TPortManager.addTPort(player, newTPort, true) != null) {
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
        }
    }
}
