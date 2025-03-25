package com.spaceman.tport.commands.tport.edit.tag;

import com.spaceman.tport.advancements.TPortAdvancement;
import com.spaceman.tport.advancements.TPortAdvancementManager;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.commands.tport.Tag.tagPermPrefix;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Add extends SubCommand {
    
    public Add() {
        EmptyCommand emptyTag = new EmptyCommand();
        emptyTag.setCommandName("tag", ArgumentType.REQUIRED);
        emptyTag.setCommandDescription(formatInfoTranslation("tport.command.edit.tag.add.commandDescription"));
        emptyTag.setPermissions(tagPermPrefix + "<tag>");
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
            String tag = Tag.getTag(args[4]);
            if (tag == null) {
                sendErrorTranslation(player, "tport.command.edit.tag.add.tagNotFound", args[4]);
                return;
            }
            if (!hasPermission(player, false, tagPermPrefix + tag)) {
                sendErrorTranslation(player, "tport.command.edit.tag.add.noPermissionToUseTag", tagPermPrefix + tag, tag);
                return;
            }
            
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.tag.add.isOffered",
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
            
            if (tport.addTag(tag)) {
                tport.save();
                TPortAdvancement.Advancement_TaggedYoureIt.grant(player);
                sendSuccessTranslation(player, "tport.command.edit.tag.add.succeeded", tag, tport);
            } else {
                sendErrorTranslation(player, "tport.command.edit.tag.add.hasAlreadyTag", tag, tport);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> tag add <tag>");
        }
    }
}
