package com.spaceman.tport.commands.tport.tag;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Tag;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Delete extends SubCommand {
    
    private final EmptyCommand emptyTag;
    
    public Delete() {
        emptyTag = new EmptyCommand();
        emptyTag.setCommandName("tag", ArgumentType.REQUIRED);
        emptyTag.setCommandDescription(formatInfoTranslation("tport.command.tag.delete.tag.commandDescription"));
        emptyTag.setPermissions("TPort.tag.delete", "TPort.admin.tag");
        addAction(emptyTag);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyTag.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Tag.getTags();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport tag delete <tag>
        
        if (args.length == 3) {
            if (!emptyTag.hasPermissionToRun(player, true)) {
                return;
            }
            String tag = Tag.getTag(args[2]);
            if (tag == null) {
                sendErrorTranslation(player, "tport.command.tag.delete.tag.notExist", args[2]);
                return;
            }
            Tag.deleteTag(tag);
            sendSuccessTranslation(player, "tport.command.tag.delete.tag.succeeded", tag);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport tag delete <tag>");
        }
    }
}
