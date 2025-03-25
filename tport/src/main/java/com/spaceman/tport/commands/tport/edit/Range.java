package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.advancements.TPortAdvancementManager;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_safetyFirst;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class Range extends SubCommand {
    
    private final EmptyCommand emptyRange;
    
    public Range() {
        emptyRange = new EmptyCommand();
        emptyRange.setCommandName("range", ArgumentType.OPTIONAL);
        emptyRange.setCommandDescription(formatInfoTranslation("tport.command.edit.range.range.commandDescription", "0"));
        emptyRange.setPermissions("TPort.edit.range", "TPort.basic");
        addAction(emptyRange);
        
        setCommandDescription(formatInfoTranslation("tport.command.edit.range.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> range [range]
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            
            if (tport.hasRange()) {
                sendInfoTranslation(player, "tport.command.edit.range.succeededRange", asTPort(tport), String.valueOf(tport.getRange()));
            } else {
                sendInfoTranslation(player, "tport.command.edit.range.succeededNoRange",
                        asTPort(tport), formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.edit.range.off"));
            }
        } else if (args.length == 4) {
            if (!emptyRange.hasPermissionToRun(player, true)) {
                return;
            }
            
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.range.range.isOffered",
                        asTPort(tport), asPlayer(tport.getOfferedTo()));
                return;
            }
            
            int range;
            try {
                range = Integer.parseInt(args[3]);
            } catch (NumberFormatException nfe) {
                sendErrorTranslation(player, "tport.command.edit.range.range.notANumber", args[3]);
                return;
            }
            tport.setRange(range);
            tport.save();
            if (range == 0) {
                sendSuccessTranslation(player, "tport.command.edit.range.range.succeededNoRange",
                        asTPort(tport), formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.edit.range.off"));
            } else {
                sendSuccessTranslation(player, "tport.command.edit.range.range.succeededRange", asTPort(tport), String.valueOf(range));
            }
            
            Advancement_safetyFirst.grant(player);
            
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> range [range]");
        }
    }
}
