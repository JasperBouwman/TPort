package com.spaceman.tport.commands.tport.edit.tag;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Remove extends SubCommand {
    
    public Remove() {
        EmptyCommand emptyTag = new EmptyCommand();
        emptyTag.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTag.setCommandDescription(formatInfoTranslation("tport.command.edit.tag.remove.commandDescription"));
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
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.tag.remove.isOffered",
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
            
            String tag = Tag.getTag(args[4]);
            
            if (tag == null) {
                sendErrorTranslation(player, "tport.command.edit.tag.remove.tagNotFound", args[4]);
                return;
            }
            
            if (tport.removeTag(tag)) {
                tport.save();
                sendSuccessTranslation(player, "tport.command.edit.tag.remove.succeeded", tag, tport);
            } else {
                sendErrorTranslation(player, "tport.command.edit.tag.remove.hasNotTag", tag, tport);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> tag remove <tag>");
        }
    }
}