package com.spaceman.tport.commands.tport.edit.tag;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Remove extends SubCommand {
    
    public Remove() {
        EmptyCommand emptyTag = new EmptyCommand();
        emptyTag.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTag.setCommandDescription(textComponent("This command is used to remove the given tag from the give TPort", infoColor));
        addAction(emptyTag);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        if (tport != null) {
            return tport.getTags();
        }
        return Collections.emptyList();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> tag remove <tag>
        
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
            
            if (tport.removeTag(tag)) {
                tport.save();
                sendSuccessTheme(player, "Successfully removed the tag %s from TPort %s", tag, tport.getName());
            } else {
                sendErrorTheme(player, "Tag %s is not assigned to TPort %s", tag, tport.getName());
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> tag remove <tag>");
        }
    }
}