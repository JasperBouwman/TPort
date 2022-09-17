package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.TextType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.runCommand;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public class Redirect extends SubCommand {
    
    private final EmptyCommand emptyRedirectState;
    
    public Redirect() {
        Redirect.Redirects.loadRedirects();
        
        emptyRedirectState = new EmptyCommand();
        emptyRedirectState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyRedirectState.setCommandDescription(formatInfoTranslation("tport.command.redirect.redirect.state.commandDescription"));
        emptyRedirectState.setPermissions("TPort.redirect.set", "TPort.admin.redirect");
        
        EmptyCommand emptyRedirect = new EmptyCommand();
        emptyRedirect.setCommandName("redirect", ArgumentType.OPTIONAL);
        emptyRedirect.setCommandDescription(formatInfoTranslation("tport.command.redirect.redirect.commandDescription", "/tport help redirects"));
        emptyRedirect.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyRedirect.addAction(emptyRedirectState);
        addAction(emptyRedirect);
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.redirect.commandDescription");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.stream(Redirects.values()).map(Enum::name).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport redirect [redirect] [state]
        
        if (args.length == 1) {
            Message list = new Message();
            Message delimiter = formatInfoTranslation("tport.command.redirect.delimiter");
            Redirects[] redirects = Redirects.values();
            boolean color = true;
            
            for (int i = 0; i < redirects.length; i++) {
                Redirects redirect = redirects[i];
                TextComponent stateMessage;
                
                if (redirect.isEnabled()) stateMessage = textComponent("tport.command.redirect.redirect.enabled", goodColor)
                        .setType(TextType.TRANSLATE)
                        .addTextEvent(hoverEvent("/tport redirect " + redirect.name() + " false", infoColor))
                        .addTextEvent(runCommand("/tport redirect " + redirect.name() + " false"))
                        .setInsertion("/tport redirect " + redirect.name() + " false");
                else                     stateMessage = textComponent("tport.command.redirect.redirect.disabled", badColor)
                        .setType(TextType.TRANSLATE)
                        .addTextEvent(hoverEvent("/tport redirect " + redirect.name() + " true", infoColor))
                        .addTextEvent(runCommand("/tport redirect " + redirect.name() + " true"))
                        .setInsertion("/tport redirect " + redirect.name() + " true");
                
                if (color) list.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.redirect.listElement", redirect, stateMessage));
                else       list.addMessage(formatTranslation(infoColor, varInfo2Color, "tport.command.redirect.listElement", redirect, stateMessage));
                color = !color;
                
                if (i + 2 == redirects.length) list.addMessage(formatInfoTranslation("tport.command.redirect.lastDelimiter"));
                else                           list.addMessage(delimiter);
            }
            
            list.removeLast();
            
            sendInfoTranslation(player, "tport.command.redirect.succeeded", list);
        } else if (args.length == 2) {
            Redirects redirect = Redirects.get(args[1]);
            if (redirect == null) {
                sendErrorTranslation(player, "tport.command.redirect.redirect.notExist", args[1]);
                return;
            }
            Message redirectStateMessage = formatTranslation(varInfoColor, ColorType.varInfo2Color,
                    "tport.command.redirect.redirect." + (redirect.isEnabled() ? "enabled" : "disabled"));
            sendInfoTranslation(player, "tport.command.redirect.redirect.succeeded", redirect, redirectStateMessage);
        } else if (args.length == 3) {
            if (!emptyRedirectState.hasPermissionToRun(player, true)) {
                return;
            }
            
            Redirects redirect = Redirects.get(args[1]);
            if (redirect == null) {
                sendErrorTranslation(player, "tport.command.redirect.redirect.notExist", args[1]);
                return;
            }
            if (Main.isTrue(args[2])) {
                redirect.setEnabled(true);
                sendSuccessTranslation(player, "tport.command.redirect.redirect.state.succeeded", redirect,
                        formatTranslation(varInfoColor, ColorType.varInfo2Color, "tport.command.redirect.redirect.enabled"));
            } else {
                redirect.setEnabled(false);
                sendSuccessTranslation(player, "tport.command.redirect.redirect.state.succeeded", redirect,
                        formatTranslation(varInfoColor, ColorType.varInfo2Color, "tport.command.redirect.redirect.disabled"));
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport redirect [redirect] [state]");
        }
    }
    
    public enum Redirects implements MessageUtils.MessageDescription {
        ConsoleFeedback(true, formatInfoTranslation("tport.command.redirect.redirect.consoleFeedback.description")),
        TP_PLTP(true, formatInfoTranslation("tport.command.redirect.redirect.TP_PLTP.description", "/tp <player>", "/tport PLTP tp <player>")),
        Locate_FeatureTP(true, formatInfoTranslation("tport.command.redirect.redirect.Locate_FeatureTP.description", "/locate <StructureType>", "/tport FeatureTP <feature>")),
        LocateBiome_BiomeTP(true, formatInfoTranslation("tport.command.redirect.redirect.LocateBiome_BiomeTP.description", "/locateBiome <biome>", "/tport BiomeTP whitelist <biome>")),
        Home_TPortHome(false, formatInfoTranslation("tport.command.redirect.redirect.Home_TPortHome.description", "/home", "/tport home", "/home")),
        Back_TPortBack(false, formatInfoTranslation("tport.command.redirect.redirect.Back_TPortBack.description", "/back", "/tport back"));
        
        private boolean enabled;
        private final Message description;
        
        Redirects(boolean defaultState, Message description) {
            this.enabled = defaultState;
            this.description = description;
        }
        
        public static void saveRedirects() {
            for (Redirects redirect : Redirects.values()) {
                tportConfig.getConfig().set("redirects." + redirect.name(), redirect.enabled);
            }
            tportConfig.saveConfig();
        }
        
        public static void loadRedirects() {
            for (Redirects redirect : Redirects.values()) {
                redirect.enabled = tportConfig.getConfig().getBoolean("redirects." + redirect.name(), redirect.enabled);
            }
        }
        
        public static Redirects get(String name) {
            for (Redirects redirect : Redirects.values()) {
                if (redirect.name().equalsIgnoreCase(name)) {
                    return redirect;
                }
            }
            return null;
        }
        
        public boolean isEnabled() {
            return Features.Feature.Redirects.isEnabled() && enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        @Override
        public Message getDescription() {
            return description;
        }
        
        @Override
        public String getName() {
            return name();
        }
        
        @Override
        public String getInsertion() {
            return getName();
        }
    }
}
