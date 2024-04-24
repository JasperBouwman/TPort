package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.history.HistoryEvents;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.runCommand;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;
import static com.spaceman.tport.fileHander.Files.tportConfig;
import static com.spaceman.tport.inventories.SettingsInventories.*;

public class Features extends SubCommand {
    
    private final EmptyCommand emptyFeatureSetState;
    private final boolean isDisabled;
    
    public Features() {
        isDisabled = Feature.FeatureSettings.isDisabled();
        
        emptyFeatureSetState = new EmptyCommand();
        emptyFeatureSetState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyFeatureSetState.setCommandDescription(formatInfoTranslation("tport.command.features.feature.state.state.commandDescription"));
        emptyFeatureSetState.setPermissions("TPort.features.setState", "TPort.admin.features");
        
        EmptyCommand emptyFeatureState = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                return this.getCommandName();
            }
        };
        emptyFeatureState.setCommandName("state", ArgumentType.FIXED);
        emptyFeatureState.setTabRunnable(((args, player) -> {
            if (!emptyFeatureSetState.hasPermissionToRun(player, false)) {
                return Collections.emptyList();
            }
            return Arrays.asList("true", "false");
        }));
        emptyFeatureState.setCommandDescription(formatInfoTranslation("tport.command.features.feature.state.commandDescription"));
        emptyFeatureState.addAction(emptyFeatureSetState);
        
        EmptyCommand emptyFeatureEmpty = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        emptyFeatureEmpty.setCommandName("", ArgumentType.FIXED);
        emptyFeatureEmpty.setCommandDescription(formatInfoTranslation("tport.command.features.feature.commandDescription"));
        EmptyCommand emptyFeature = new EmptyCommand();
        emptyFeature.setCommandName("feature", ArgumentType.REQUIRED);
        emptyFeature.addAction(emptyFeatureEmpty);
        emptyFeature.addAction(emptyFeatureState);
        
        EmptyCommand empty = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.features.commandDescription"));
        
        addAction(empty);
        addAction(emptyFeature);
    }
    
    public static void convert() {
        Boolean b = null;
        
        if (tportConfig.getConfig().contains("public.enabled")) {
            b = tportConfig.getConfig().getBoolean("public.enabled", true);
            tportConfig.getConfig().set("public.enabled", null);
            tportConfig.getConfig().set("features." + Feature.PublicTP + ".enabled", b);
        }
        
        if (tportConfig.getConfig().contains("metrics.enabled")) {
            b = tportConfig.getConfig().getBoolean("metrics.enabled", true);
            tportConfig.getConfig().set("metrics.enabled", null);
            tportConfig.getConfig().set("features." + Feature.Metrics + ".enabled", b);
        }
        
        if (tportConfig.getConfig().contains("dynmap.enabled")) {
            b = tportConfig.getConfig().getBoolean("dynmap.enabled", true);
            tportConfig.getConfig().set("dynmap.enabled", null);
            tportConfig.getConfig().set("features." + Feature.Dynmap + ".enabled", b);
        }
        
        if (tportConfig.getConfig().contains("Permissions.enabled")) {
            b = tportConfig.getConfig().getBoolean("Permissions.enabled", true);
            tportConfig.getConfig().set("Permissions.enabled", null);
            tportConfig.getConfig().set("features." + Feature.Permissions + ".enabled", b);
        }
        
        if (b != null) tportConfig.saveConfig();
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Feature.getStringValues();
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport features <feature> state [state]
        
        if (this.isDisabled)  {
            Feature.FeatureSettings.sendDisabledMessage(player);
            return;
        }
        
        if (args.length == 1) { //get info about all features
            Message list = new Message();
            Message delimiter = formatInfoTranslation("tport.command.features.delimiter");
            Feature[] values = Feature.values();
            boolean color = true;
            
            for (int i = 0; i < values.length; i++) {
                Feature feature = values[i];
                TextComponent stateMessage;
                
                if (feature.isEnabled()) stateMessage = textComponent("tport.command.features.enable", goodColor)
                        .setType(TextType.TRANSLATE)
                        .addTextEvent(hoverEvent("/tport features " + feature.name() + " state false", infoColor))
                        .addTextEvent(runCommand("/tport features " + feature.name() + " state false"))
                        .setInsertion("/tport features " + feature.name() + " state false");
                else                     stateMessage = textComponent("tport.command.features.disable", badColor)
                        .setType(TextType.TRANSLATE)
                        .addTextEvent(hoverEvent("/tport features " + feature.name() + " state true", infoColor))
                        .addTextEvent(runCommand("/tport features " + feature.name() + " state true"))
                        .setInsertion("/tport features " + feature.name() + " state true");
                
                if (color) list.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.features.listElement", feature, stateMessage));
                else       list.addMessage(formatTranslation(infoColor, varInfo2Color, "tport.command.features.listElement", feature, stateMessage));
                
                color = !color;
                if (i + 2 == values.length) list.addMessage(formatInfoTranslation("tport.command.features.lastDelimiter"));
                else                        list.addMessage(delimiter);
            }
            list.removeLast();
            
            sendInfoTranslation(player, "tport.command.features.succeeded", list);
        }
        else if (args.length == 2) { //get info about feature
            Feature feature = Feature.get(args[1]);
            if (feature == null) {
                sendErrorTranslation(player, "tport.command.features.feature.featureNotFound", args[1]);
                return;
            }
            
            Message stateMessage;
            if (feature.isEnabled()) stateMessage = formatTranslation(goodColor, varInfoColor, "tport.command.features.enable");
            else                     stateMessage = formatTranslation(badColor, varInfoColor, "tport.command.features.disable");
            sendInfoTranslation(player, "tport.command.features.feature.succeeded", feature, feature.getDescription(), stateMessage);
        }
        else if (args.length == 3 && args[2].equalsIgnoreCase("state")) { //get state of feature
            Feature feature = Feature.get(args[1]);
            if (feature == null) {
                sendErrorTranslation(player, "tport.command.features.feature.featureNotFound", args[1]);
                return;
            }
            
            Message stateMessage;
            if (feature.isEnabled()) stateMessage = formatTranslation(goodColor, varInfoColor, "tport.command.features.enable");
            else                     stateMessage = formatTranslation(badColor, varInfoColor, "tport.command.features.disable");
            sendInfoTranslation(player, "tport.command.features.feature.getState", feature, stateMessage);
        }
        else if (args.length == 4 && args[2].equalsIgnoreCase("state")) { //set state of feature
            if (!emptyFeatureSetState.hasPermissionToRun(player, true)) {
                return;
            }
            Feature feature = Feature.get(args[1]);
            if (feature == null) {
                sendErrorTranslation(player, "tport.command.features.feature.featureNotFound", args[1]);
                return;
            }
            
            Boolean newState = Main.toBoolean(args[3]);
            if (newState == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport features <feature> state [true|false]");
                return;
            }
            
            boolean oldState = feature.isEnabled();
            if (oldState == newState) {
                Message stateMessage = formatTranslation(ColorType.varErrorColor, ColorType.varError2Color, "tport.command.features." + (newState ? "enable" : "disable"));
                sendErrorTranslation(player, "tport.command.features.feature.alreadyInState", feature, stateMessage);
                return;
            }
            
            feature.setState(newState).sendAndTranslateMessage(player);
        }
        else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport features <feature> state [state]");
        }
    }
    
    public enum Feature implements MessageUtils.MessageDescription {// todo reload command??? (onStateChange for Dynmap and BlueMap)
        BiomeTP(true, true, settings_features_biome_tp_model, settings_features_biome_tp_grayed_model),
        FeatureTP(true, true, settings_features_feature_tp_model, settings_features_feature_tp_grayed_model),
        BackTP(true, true, settings_features_back_tp_model, settings_features_back_tp_grayed_model),
        PublicTP(true, true, settings_features_public_tp_model, settings_features_public_tp_grayed_model),
        PLTP(true, true, settings_features_pltp_model, settings_features_pltp_grayed_model),
        Dynmap(true, true, settings_features_dynmap_model, settings_features_dynmap_grayed_model),
        BlueMap(true, true, settings_features_bluemap_model, settings_features_bluemap_grayed_model),
        Metrics(true, true, settings_features_metrics_model, settings_features_metrics_grayed_model),
        CompanionTP(false, true, settings_features_companion_tp_model, settings_features_companion_tp_grayed_model),
        Permissions(false, false, settings_features_permissions_model, settings_features_permissions_grayed_model),
        ParticleAnimation(true, true, settings_features_particle_animation_model, settings_features_particle_animation_grayed_model),
        Redirects(true, true, settings_features_redirects_model, settings_features_redirects_grayed_model),
        History(true, true, HistoryEvents::onStateChange, settings_features_history_model, settings_features_history_grayed_model),
        Preview(true, true, settings_features_preview_model, settings_features_preview_grayed_model),
        WorldTP(true, true, settings_features_world_tp_model, settings_features_world_tp_grayed_model),
        TPortTakesItem(false, true, settings_features_tport_takes_item_model, settings_features_tport_takes_item_grayed_model),
        InterdimensionalTeleporting(false, true, settings_features_interdimensional_teleporting_model, settings_features_interdimensional_teleporting_grayed_model),
        DeathTP(false, true, settings_features_death_tp_model, settings_features_death_tp_grayed_model),
        LookTP(false, true, settings_features_look_tp_model, settings_features_look_tp_grayed_model),
        EnsureUniqueUUID(false, false, settings_features_ensure_unique_uuid_model, settings_features_ensure_unique_uuid_grayed_model),
        PrintErrorsInConsole(false, false, settings_features_print_errors_in_console_model, settings_features_print_errors_in_console_grayed_model),
        FeatureSettings(false, true, settings_features_feature_settings_model, settings_features_feature_settings_grayed_model);
        
        private final boolean reloadCommands;
        private final boolean defaultValue;
        private final InventoryModel enabledModel;
        private final InventoryModel disabledModel;
        private final OnStateChange stateChange;
        
        Feature(boolean reloadCommands, boolean defaultValue, InventoryModel enabledModel, InventoryModel disabledModel) {
            this.reloadCommands = reloadCommands;
            this.defaultValue = defaultValue;
            this.stateChange = (newState -> {});
            this.enabledModel = enabledModel;
            this.disabledModel = disabledModel;
        }
        Feature(boolean reloadCommands, boolean defaultValue, OnStateChange stateChange, InventoryModel enabledModel, InventoryModel disabledModel) {
            this.reloadCommands = reloadCommands;
            this.defaultValue = defaultValue;
            this.stateChange = stateChange;
            this.enabledModel = enabledModel;
            this.disabledModel = disabledModel;
        }
        
        @FunctionalInterface
        private interface OnStateChange {
            void onChange(boolean newState);
        }
        
        public static List<String> getStringValues() {
            return Arrays.stream(Feature.values()).map(Enum::name).collect(Collectors.toList());
        }
        
        public static Feature get(String name) {
            for (Feature feature : Feature.values()) {
                if (feature.name().equalsIgnoreCase(name)) {
                    return feature;
                }
            }
            return null;
        }
        
        public boolean isEnabled() {
            return tportConfig.getConfig().getBoolean("features." + this.name() + ".enabled", defaultValue);
        }
        public boolean isDisabled() {
            return !isEnabled();
        }
        
        public Message setState(boolean newState) {
            tportConfig.getConfig().set("features." + this.name() + ".enabled", newState);
            tportConfig.saveConfig();
            
            if (reloadCommands) TPortCommand.reRegisterActions();
            if (stateChange != null) stateChange.onChange(newState);
            
            Message stateMessage = formatTranslation(varSuccessColor, varSuccess2Color, "tport.command.features." + (newState ? "enable" : "disable"));
            return formatSuccessTranslation("tport.command.features.feature." + this.name() + ".setState", this, stateMessage, "/tport reload");
        }
        
        public Message getDisabledMessage() {
            Message stateMessage = formatTranslation(varErrorColor, varError2Color, "tport.command.features.disable");
            String command = "/tport features " + this.name() + " state true";
            return formatErrorTranslation("tport.command.features.feature." + this.name() + ".disabledMessage", stateMessage, command);
        }
        public void sendDisabledMessage(Player player) {
            getDisabledMessage().sendAndTranslateMessage(player);
        }
        
        public static void printSmallNMSErrorInConsole(String nmsError, boolean usedBackup) {
            String message = "NMS Error in: " + nmsError;
            if (usedBackup) message += ", used built-in backup. Some features may not keep their full functionality when their backup is used";
            Main.getInstance().getLogger().log(Level.WARNING, message);
        }
        
        public InventoryModel getModel() {
            return this.isEnabled() ? enabledModel : disabledModel;
        }
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.command.features.feature." + this.name() + ".description");
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
