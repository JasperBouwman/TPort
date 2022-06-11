package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportData;

public class State extends SubCommand {
    
    private final EmptyCommand emptyState;
    
    public State() {
        emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(formatInfoTranslation("tport.command.PLTP.state.state.commandDescription",
                formatTranslation(goodColor, varInfo2Color, "tport.command.PLTP.state.enabled"),
                formatTranslation(badColor, varInfo2Color, "tport.command.PLTP.state.disabled")));
        emptyState.setPermissions("TPort.PLTP.state.set", "TPort.basic");
        addAction(emptyState);
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.PLTP.state.commandDescription");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP state [state]
        
        if (args.length == 2) {
            boolean pltpState = tportData.getConfig().getBoolean("tport." + player.getUniqueId() + ".tp.statement", true);
            sendInfoTranslation(player, "tport.command.PLTP.state.succeeded", (pltpState ?
                    formatTranslation(goodColor, varInfo2Color, "tport.command.PLTP.state.enabled") :
                    formatTranslation(badColor, varInfo2Color, "tport.command.PLTP.state.disabled")));
        } else if (args.length == 3) {
            if (!emptyState.hasPermissionToRun(player, true)) {
                return;
            }
            Boolean pltpState = Main.toBoolean(args[2]);
            if (pltpState == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP state [true|false]");
                return;
            }
            boolean oldPltpState = tportData.getConfig().getBoolean("tport." + player.getUniqueId() + ".tp.statement", true);
            
            if (pltpState == oldPltpState) {
                sendErrorTranslation(player, "tport.command.PLTP.state.state.alreadyInState", (pltpState ?
                        formatTranslation(goodColor, varInfo2Color, "tport.command.PLTP.state.enabled") :
                        formatTranslation(badColor, varInfo2Color, "tport.command.PLTP.state.disabled")));
            } else {
                tportData.getConfig().set("tport." + player.getUniqueId() + ".tp.statement", pltpState);
                tportData.saveConfig();
                sendSuccessTranslation(player, "tport.command.PLTP.state.state.succeeded", (pltpState ?
                        formatTranslation(goodColor, varInfo2Color, "tport.command.PLTP.state.enabled") :
                        formatTranslation(badColor, varInfo2Color, "tport.command.PLTP.state.disabled")));
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP state [state]");
        }
    }
}
