package com.spaceman.tport.commands.tport.tag;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Tag;
import org.bukkit.entity.Player;

import java.util.Collection;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Delete extends SubCommand {
    
    private final EmptyCommand emptyTag;
    
    public Delete() {
        emptyTag = new EmptyCommand();
        emptyTag.setCommandName("tag", ArgumentType.REQUIRED);
        emptyTag.setCommandDescription(textComponent("This command is used to delete the given tag, the tag will also be removed from all TPorts", infoColor));
        emptyTag.setPermissions("TPort.tag.delete", "TPort.admin.tag");
        addAction(emptyTag);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Tag.getTags();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport tag delete <tag>
        
        if (!emptyTag.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 3) {
            String tag = Tag.getTag(args[2]);
            if (tag == null) {
                sendErrorTheme(player, "Tag %s does not exist", args[2]);
                return;
            }
            Tag.deleteTag(tag);
            sendSuccessTheme(player, "Successfully deleted the tag %s", tag);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport tag delete <tag>");
        }
    }
}
