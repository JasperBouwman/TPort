package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportData;

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
        
        setCommandDescription(formatInfoTranslation("tport.command.PLTP.consent.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyState.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Arrays.asList("true", "false");
    }
    
    public static boolean shouldAskConsent(Player player) {
        return shouldAskConsent(player.getUniqueId());
    }
    public static boolean shouldAskConsent(UUID uuid) {
        return tportData.getConfig().getBoolean("tport." + uuid + ".tp.consent", false);
    }
    public static void shouldAskConsent(Player player, boolean consentState) {
        tportData.getConfig().set("tport." + player.getUniqueId() + ".tp.consent", consentState);
        tportData.saveConfig();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP consent [state]
        
        if (args.length == 2) {
            boolean pltpState = shouldAskConsent(player);
            sendInfoTranslation(player, "tport.command.PLTP.consent.succeeded", (pltpState ?
                    formatTranslation(goodColor, varInfo2Color, "tport.command.PLTP.consent.enabled") :
                    formatTranslation(badColor, varInfo2Color, "tport.command.PLTP.consent.disabled")));
        } else if (args.length == 3) {
            if (!emptyState.hasPermissionToRun(player, true)) {
                return;
            }
            Boolean consentState = Main.toBoolean(args[2]);
            if (consentState == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP consent [true|false]");
                return;
            }
            boolean oldConsentState = shouldAskConsent(player);
            
            if (consentState == oldConsentState) {
                sendSuccessTranslation(player, "tport.command.PLTP.consent.state.alreadyInState", (consentState ?
                        formatTranslation(goodColor, varInfo2Color, "tport.command.PLTP.consent.enabled") :
                        formatTranslation(badColor, varInfo2Color, "tport.command.PLTP.consent.disabled")));
            } else {
                shouldAskConsent(player, consentState);
                sendSuccessTranslation(player, "tport.command.PLTP.consent.state.succeeded", (consentState ?
                        formatTranslation(goodColor, varInfo2Color, "tport.command.PLTP.consent.enabled") :
                        formatTranslation(badColor, varInfo2Color, "tport.command.PLTP.consent.disabled")));
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP consent [state]");
        }
    }
}
