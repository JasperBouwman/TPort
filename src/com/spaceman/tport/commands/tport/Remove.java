package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.List;

import static com.spaceman.tport.commands.tport.Features.Feature.TPortTakesItem;
import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.commands.tport.publc.Remove.removePublicTPort;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Remove extends SubCommand {
    
    public Remove() {
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.remove.tportName.commandDescription"));
        addAction(emptyTPort);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport remove <TPort name>
        
        if (args.length != 2) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport remove <TPort name>");
            return;
        }
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
            return;
        }
        if (tport.isOffered()) {
            sendErrorTranslation(player, "tport.command.remove.tportName.isOffered", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
            return;
        }
        removePublicTPort(tport.getName(), player, true);
        TPortManager.removeTPort(tport);
        sendSuccessTranslation(player, "tport.command.remove.tportName.succeeded", tport.getName());
        if (tport.shouldReturnItem()) {
            Main.giveItems(player, tport.getItem());
        } else if (TPortTakesItem.isEnabled()) {
            sendInfoTranslation(player, "tport.command.remove.tportName.noItemReturn", TPortTakesItem);
        }
    }
}
