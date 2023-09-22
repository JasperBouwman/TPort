package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.transfer.*;
import com.spaceman.tport.inventories.SettingsInventories;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Transfer extends SubCommand {
    
    public Transfer() {
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.transfer.commandDescription"));
        addAction(empty);
        
        addAction(new Offer());
        addAction(new Revoke());
        addAction(Accept.getInstance());
        addAction(new Reject());
        addAction(new List());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport transfer
        // tport transfer offer <player> <TPort name>
        // tport transfer revoke <TPort name>
        // tport transfer accept <player> <TPort name>
        // tport transfer reject <player> <TPort name>
        // tport transfer list
        
        if (args.length == 1) {
            SettingsInventories.openTransferGUI(player, 0, null);
        } else if (args.length > 1) {
            if (CommandTemplate.runCommands(getActions(), args[1], args, player)) {
                return;
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport transfer <offer|accept|reject|list>");
        }
    }
}
