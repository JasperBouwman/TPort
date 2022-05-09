package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.util.*;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Consent extends SubCommand {
    
    private final EmptyCommand emptyState;
    
    public Consent() {
        emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(formatInfoTranslation("tport.command.PLTP.consent.state.commandDescription",
                formatTranslation(goodColor, varInfo2Color, "tport.command.PLTP.consent.enabled"),
                formatTranslation(badColor, varInfo2Color, "tport.command.PLTP.consent.disabled")));
        emptyState.setPermissions("TPort.PLTP.consent.set", "TPort.basic");
        addAction(emptyState);
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.PLTP.consent.commandDescription");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP consent [state]
        
        if (args.length == 2) {
            Files tportData = getFile("TPortData");
            boolean pltpState = tportData.getConfig().getBoolean("tport." + player.getUniqueId() + ".tp.consent", false);
            sendInfoTranslation(player, "tport.command.PLTP.consent.succeeded", (pltpState ?
                    formatTranslation(goodColor, varInfo2Color, "tport.command.PLTP.consent.enabled") :
                    formatTranslation(badColor, varInfo2Color, "tport.command.PLTP.consent.disabled")));
        } else if (args.length == 3) {
            if (!emptyState.hasPermissionToRun(player, true)) {
                return;
            }
            Files tportData = getFile("TPortData");
            Boolean consentState = Main.toBoolean(args[2]);
            if (consentState == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP consent [true|false]");
                return;
            }
            boolean oldConsentState = tportData.getConfig().getBoolean("tport." + player.getUniqueId() + ".tp.consent", false);
            
            if (consentState == oldConsentState) {
                sendSuccessTranslation(player, "tport.command.PLTP.consent.state.alreadyInState", (consentState ?
                        formatTranslation(goodColor, varInfo2Color, "tport.command.PLTP.consent.enabled") :
                        formatTranslation(badColor, varInfo2Color, "tport.command.PLTP.consent.disabled")));
            } else {
                tportData.getConfig().set("tport." + player.getUniqueId() + ".tp.consent", consentState);
                tportData.saveConfig();
                sendSuccessTranslation(player, "tport.command.PLTP.consent.state.succeeded", (consentState ?
                        formatTranslation(goodColor, varInfo2Color, "tport.command.PLTP.consent.enabled") :
                        formatTranslation(badColor, varInfo2Color, "tport.command.PLTP.consent.disabled")));
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP consent [state]");
        }
    }
}
