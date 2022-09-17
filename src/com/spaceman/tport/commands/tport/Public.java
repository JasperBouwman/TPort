package com.spaceman.tport.commands.tport;

import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.publc.Add;
import com.spaceman.tport.commands.tport.publc.Open;
import com.spaceman.tport.commands.tport.publc.Remove;
import com.spaceman.tport.commands.tport.publc.*;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Public extends SubCommand {
    
    private final EmptyCommand empty;
    
    public Public() {
        empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.public.commandDescription"));
        empty.setPermissions("TPort.public.open", "TPort.basic");
        addAction(empty);
        addAction(new Open());
        addAction(new Add());
        addAction(Remove.getInstance());
        addAction(new List());
        addAction(Move.getInstance());
        addAction(new ListSize());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public
        // tport public open <TPort name|page>
        // tport public add <TPort name>
        // tport public remove <own TPort name|all TPort name>
        // tport public list [own|all]
        // tport public move <TPort name> <slot|TPort name>
        
        if (Features.Feature.PublicTP.isEnabled()) {
            if (args.length == 1) {
                if (empty.hasPermissionToRun(player, true)) {
                    TPortInventories.openPublicTPortGUI(player);
                }
            } else {
                if (!runCommands(getActions(), args[1], args, player)) {
                    sendErrorTranslation(player, "tport.command.wrongUsage", "/tport public " + CommandTemplate.convertToArgs(getActions(), true));
                }
            }
        } else {
            sendErrorTranslation(player, "tport.command.public.notEnabled");
        }
    }
}
