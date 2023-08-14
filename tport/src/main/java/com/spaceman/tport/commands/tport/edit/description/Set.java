package com.spaceman.tport.commands.tport.edit.description;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Set extends SubCommand {
    
    private final EmptyCommand emptySetDescription;
    
    public Set() {
        emptySetDescription = new EmptyCommand();
        emptySetDescription.setCommandName("description", ArgumentType.REQUIRED);
        emptySetDescription.setCommandDescription(formatInfoTranslation("tport.command.edit.description.set.commandDescription",
                "\\n", "&", "0-9,a-f,k-o,r", "#123456", "$RRR$GGG$BBB"));
        emptySetDescription.setPermissions("TPort.edit.description", "TPort.basic");
        emptySetDescription.setLooped(true);
        
        addAction(emptySetDescription);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> description set <description...>
        if (args.length > 4) {
            if (!emptySetDescription.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.description.set.isOffered",
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
            String newDescription = StringUtils.join(args, " ", 4, args.length);
            tport.setDescription(newDescription);
            tport.save();
            
            Message description = tport.getDescription();
            String textDescription = tport.getRawDescription();
            Message hoverText = formatInfoTranslation("tport.command.edit.description.set.literal", textDescription);
            description.getText().forEach(m -> m.addTextEvent(new HoverEvent(hoverText)).setInsertion(textDescription));
            sendSuccessTranslation(player, "tport.command.edit.description.set.succeeded", tport, description);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> description set <description...>");
        }
    }
}
