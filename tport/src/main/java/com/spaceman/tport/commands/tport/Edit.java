package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.edit.Preview;
import com.spaceman.tport.commands.tport.edit.Tag;
import com.spaceman.tport.commands.tport.edit.*;
import org.bukkit.entity.Player;

import java.util.List;

import static com.spaceman.tport.commandHandler.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Edit extends SubCommand {
    
    private final EmptyCommand emptyOwnTPort;
    
    public Edit() {
        emptyOwnTPort = new EmptyCommand();
        emptyOwnTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyOwnTPort.addAction(new Description());
        emptyOwnTPort.addAction(new Name());
        emptyOwnTPort.addAction(new Item());
        emptyOwnTPort.addAction(new com.spaceman.tport.commands.tport.edit.Location());
        emptyOwnTPort.addAction(new Private());
        emptyOwnTPort.addAction(new Whitelist());
        emptyOwnTPort.addAction(Move.getInstance());
        emptyOwnTPort.addAction(new Range());
        emptyOwnTPort.addAction(new Tag());
        if (Features.Feature.Preview.isEnabled()) emptyOwnTPort.addAction(new Preview());
        if (Features.Feature.Dynmap.isEnabled()) emptyOwnTPort.addAction(new Dynmap());
        addAction(emptyOwnTPort);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return Own.getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> description set <description...>
        // tport edit <TPort name> description remove
        // tport edit <TPort name> description get
        // tport edit <TPort name> name <new TPort name>
        // tport edit <TPort name> item
        // tport edit <TPort name> location
        // tport edit <TPort name> private
        // tport edit <TPort name> private <state>
        // tport edit <TPort name> whitelist <add|remove> <players names...>
        // tport edit <TPort name> whitelist list
        // tport edit <TPort name> move <slot|TPort name>
        // tport edit <TPort name> range [range]
        // tport edit <TPort name> tag add <tag>
        // tport edit <TPort name> tag remove <tag>
        // tport edit <TPort name> dynmap show [state]
        // tport edit <TPort name> dynmap icon [icon]
        
        if (args.length > 2) {
            if (runCommands(emptyOwnTPort.getActions(), args[2], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> " + convertToArgs(getActions().get(0).getActions(), false));
    }
}
