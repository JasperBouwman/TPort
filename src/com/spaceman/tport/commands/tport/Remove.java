package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.List;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.commands.tport.publc.Remove.removePublicTPort;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class Remove extends SubCommand {
    
    public Remove() {
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(textComponent("This command is used to remove the given TPort from your TPorts", ColorTheme.ColorType.infoColor));
        addAction(emptyTPort);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport remove <TPort name>
        
        if (args.length == 1) {
            sendErrorTheme(player, "Usage: %s", "/tport remove <TPort name>");
            return;
        }
        if (args.length == 2) {
            
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport != null) {
                if (tport.isOffered()) {
                    sendErrorTheme(player, "You can't remove TPort %s while its offered to player %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                    return;
                }
                removePublicTPort(tport.getName(), player, true);
                tport = TPortManager.removeTPort(player.getUniqueId(), args[1]);
                if (tport != null) {
                    sendSuccessTheme(player, "Successfully removed TPort %s", tport.getName());
                    Main.giveItems(player, tport.getItem());
                } else {
                    sendErrorTheme(player, "Could not remove TPort %s", args[1]);
                }
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport remove <TPort name>");
        }
    }
}
