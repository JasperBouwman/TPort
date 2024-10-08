package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
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
import static com.spaceman.tport.inventories.SettingsInventories.*;

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
        
        setCommandDescription(formatInfoTranslation("tport.command.redirect.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.stream(Redirects.values()).map(Enum::name).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport redirect [redirect] [state]
        
        if (Features.Feature.Redirects.isDisabled())  {
            Features.Feature.Redirects.sendDisabledMessage(player);
            return;
        }
        
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
                        formatTranslation(goodColor, ColorType.goodColor, "tport.command.redirect.redirect.enabled"));
            } else {
                redirect.setEnabled(false);
                sendSuccessTranslation(player, "tport.command.redirect.redirect.state.succeeded", redirect,
                        formatTranslation(badColor, ColorType.badColor, "tport.command.redirect.redirect.disabled"));
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport redirect [redirect] [state]");
        }
    }
    
    public enum Redirects implements MessageUtils.MessageDescription {
        ConsoleFeedback(true, formatInfoTranslation("tport.command.redirect.redirect.consoleFeedback.description"),
                settings_redirect_console_feedback_model, settings_redirect_console_feedback_grayed_model),
        TP_PLTP(true, formatInfoTranslation("tport.command.redirect.redirect.TP_PLTP.description", "/tp <player>", "/tport PLTP tp <player>"),
                settings_redirect_tp_pltp_model, settings_redirect_tp_pltp_grayed_model),
        Locate_FeatureTP(true, formatInfoTranslation("tport.command.redirect.redirect.Locate_FeatureTP.description", "/locate structure <structure>", "/locate <structure>", "/tport FeatureTP search <feature>"),
                settings_redirect_locate_feature_tp_model, settings_redirect_locate_feature_tp_grayed_model),
        LocateBiome_BiomeTP(true, formatInfoTranslation("tport.command.redirect.redirect.LocateBiome_BiomeTP.description", "/locate biome <biome>", "/locatebiome <biome>", "/tport BiomeTP whitelist <biome>"),
                settings_redirect_locate_biome_biome_tp_model, settings_redirect_locate_biome_biome_tp_grayed_model),
        Home_TPortHome(false, formatInfoTranslation("tport.command.redirect.redirect.Home_TPortHome.description", "/home", "/tport home"),
                settings_redirect_home_tport_home_model, settings_redirect_home_tport_home_grayed_model),
        Back_TPortBack(false, formatInfoTranslation("tport.command.redirect.redirect.Back_TPortBack.description", "/back", "/tport back"),
                settings_redirect_back_tport_back_model, settings_redirect_back_tport_back_grayed_model),
        TPA_PLTP_TP(false, formatInfoTranslation("tport.command.redirect.redirect.TPA_PLTP_TP.description", "/tpa <player>", "/tport pltp tp <player>"),
                settings_redirect_tpa_pltp_tp_model, settings_redirect_tpa_pltp_tp_grayed_model),
        TPAccept_Requests_accept(false, formatInfoTranslation("tport.command.redirect.redirect.TPAccept_Requests_accept.description", "/tpaccept [player]", "/tport requests accept [player]"),
                settings_redirect_tpaccept_requests_accept_model, settings_redirect_tpaccept_requests_accept_grayed_model),
        TPDeny_Requests_reject(false, formatInfoTranslation("tport.command.redirect.redirect.TPDeny_Requests_reject.description", "/tpdeny", "/tport requests reject [player]"),
                settings_redirect_tpdeny_requests_reject_model, settings_redirect_tpdeny_requests_reject_grayed_model),
        TPRevoke_Requests_revoke(false, formatInfoTranslation("tport.command.redirect.redirect.TPRevoke_Requests_revoke.description", "/tprevoke", "/tport requests revoke"),
                settings_redirect_tprevoke_requests_revoke_model, settings_redirect_tprevoke_requests_revoke_grayed_model),
        TPRandom_BiomeTP_random(false, formatInfoTranslation("tport.command.redirect.redirect.TPRandom_BiomeTP_random.description", "/tprandom", "/randomtp", "/rtp", "/tport biomeTP random"),
                settings_redirect_tprandom_biome_tp_random_model, settings_redirect_tprandom_biome_tp_random_grayed_model);
        
        private boolean enabled;
        private final Message description;
        private final InventoryModel enabledModel;
        private final InventoryModel disabledModel;
        
        Redirects(boolean defaultState, Message description, InventoryModel enabledModel, InventoryModel disabledModel) {
            this.enabled = defaultState;
            this.description = description;
            this.enabledModel = enabledModel;
            this.disabledModel = disabledModel;
        }
        
        public InventoryModel getModel() {
            return this.isEnabled() ? enabledModel : disabledModel;
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
        public Message getName(String color, String varColor) {
            return new Message(new TextComponent(name(), varColor));
        }
        
        @Override
        public String getInsertion() {
            return name();
        }
    }
}
