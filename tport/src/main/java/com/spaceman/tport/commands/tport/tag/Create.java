package com.spaceman.tport.commands.tport.tag;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Tag;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Create extends SubCommand {
    
    private final EmptyCommand emptyTag;
    
    public Create() {
        emptyTag = new EmptyCommand();
        emptyTag.setCommandName("tag", ArgumentType.REQUIRED);
        emptyTag.setCommandDescription(formatInfoTranslation("tport.command.tag.create.tag.commandDescription"));
        emptyTag.setPermissions("TPort.tag.create", "TPort.admin.tag");
        addAction(emptyTag);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport tag create <tag>
        
        if (args.length == 3) {
            if (!emptyTag.hasPermissionToRun(player, true)) {
                return;
            }
            String tag = Tag.getTag(args[2]);
            if (tag != null) {
                sendErrorTranslation(player, "tport.command.tag.create.tag.alreadyExist", tag);
                return;
            }
            Tag.createTag(args[2]);
            sendSuccessTranslation(player, "tport.command.tag.create.tag.succeeded", args[2]);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport tag create <tag>");
        }
    }
}
