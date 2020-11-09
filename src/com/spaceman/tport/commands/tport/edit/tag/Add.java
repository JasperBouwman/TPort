package com.spaceman.tport.commands.tport.edit.tag;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Add extends SubCommand {
    
    public Add() {
        EmptyCommand emptyTag = new EmptyCommand();
        emptyTag.setCommandName("tag", ArgumentType.REQUIRED);
        emptyTag.setCommandDescription(textComponent("This command is used to add the given tag to the given TPort", infoColor));
        emptyTag.setPermissions("TPort.edit.tag.add.<tag>");
        addAction(emptyTag);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = Tag.getTags();
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        if (tport != null) {
            list.removeAll(tport.getTags());
            return list;
        }
        return Collections.emptyList();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> tag add <tag>
        
        if (args.length == 5) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                return;
            }
    
            String tag = Tag.getTag(args[3]);
    
            if (tag == null) {
                sendErrorTheme(player, "Tag %s does not exist", args[4]);
                return;
            }
            
            if (!hasPermission(player, true, "TPort.tag.add." + tag)) {
                return;
            }
            
            if (tport.addTag(tag)) {
                tport.save();
                sendSuccessTheme(player, "Successfully added the tag %s to TPort %s", tag, tport.getName());
            } else {
                sendErrorTheme(player, "Tag %s is already assigned to TPort %s", tag, tport.getName());
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> tag add <tag>");
        }
    }
}
