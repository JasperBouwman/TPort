package com.spaceman.tport.commands.tport.tag;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Tag;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Create extends SubCommand {
    
    private final EmptyCommand emptyTag;
    
    public Create() {
        emptyTag = new EmptyCommand();
        emptyTag.setCommandName("tag", ArgumentType.REQUIRED);
        emptyTag.setCommandDescription(textComponent("This command is used to create your own tag", infoColor));
        emptyTag.setPermissions("TPort.tag.create", "TPort.admin.tag");
        addAction(emptyTag);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport tag create <tag>
        
        if (!emptyTag.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 3) {
            String tag = Tag.getTag(args[2]);
            if (tag != null) {
                sendErrorTheme(player, "Tag %s already exist", tag);
                return;
            }
            Tag.createTag(args[2]);
            sendSuccessTheme(player, "Successfully created the tag %s", args[2]);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport tag create <tag>");
        }
    }
}
