package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.dynmap.Enable;
import com.spaceman.tport.commands.tport.dynmap.IP;
import com.spaceman.tport.commands.tport.dynmap.Search;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHander.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class DynmapCommand extends SubCommand {
    
    public DynmapCommand() {
        EmptyCommand empty = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(textComponent("This command is used to get info about Dynmap support by TPort", infoColor));
        
        addAction(empty);
        addAction(new Enable());
        addAction(new Search());
        addAction(new IP());
    }
    
    @Override
    public String getName(String arg) {
        return "dynmap";
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport dynmap
        // tport dynmap enable [state]
        // tport dynmap search <player> [TPort name]
        // tport dynmap IP [IP]
        
        if (args.length == 1) {
            Message message = new Message();
    
            message.addText(textComponent("Dynmap support is ", infoColor));
            if (DynmapHandler.isEnabled()) {
                message.addText(textComponent("enabled.", ChatColor.GREEN));
            } else {
                message.addText(textComponent("disabled.", ChatColor.RED));
            }
            message.addText(textComponent("\nWhen Dynmap support is enabled all TPorts that are opt in are shown on Dynmap. ", infoColor));
            message.addText(textComponent("To opt out/in use '", infoColor));
            message.addText(textComponent("/tport edit <TPort name> dynmap show <state>", varInfoColor));
            message.addText(textComponent("', to change the icon use '", infoColor));
            message.addText(textComponent("/tport edit <TPort name> dynmap icon <icon>", varInfoColor));
            message.addText(textComponent("'.\nIf you want to use the '", infoColor));
            message.addText(textComponent("search", varInfoColor, hoverEvent(textComponent("/tport dynmap search <player> <TPort name>", infoColor))));
            message.addText(textComponent("' function you have to set the right IP: '", infoColor));
            message.addText(textComponent("/tport dynmap IP <ip>", varInfoColor));
            message.addText(textComponent("'.\nWhat you can do is just copy the url from your browser, but make sure its in the right format: ", infoColor));
            message.addText(textComponent("http://0.0.0.0:PORT/", varInfoColor));
            message.addText(textComponent(" or ", infoColor));
            message.addText(textComponent("http://YourWebsite.com/", varInfoColor));
            
            message.sendMessage(player);
            return;
        } else if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport dynmap " + convertToArgs(getActions(), true));
    }
}
