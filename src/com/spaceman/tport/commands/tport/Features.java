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
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.runCommand;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class Features extends SubCommand {
    
    private final EmptyCommand emptyFeatureSetState;
    
    public Features() {
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
        emptyFeatureState.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
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
        Files tportConfig = GettingFiles.getFile("TPortConfig");
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
    
    public enum Feature implements MessageUtils.MessageDescription {
        BiomeTP(true),
        FeatureTP(true),
        BackTP(true),
        PublicTP(true),
        PLTP(true),
        Dynmap(true),
        Metrics(true),
        Permissions(false),
        ParticleAnimation(true),
        Redirects(true),
        FeatureSettings(false);
        
        private final boolean reloadCommands;
        
        Feature(boolean reloadCommands) {
            this.reloadCommands = reloadCommands;
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
            Files tportConfig = GettingFiles.getFile("TPortConfig");
            return tportConfig.getConfig().getBoolean("features." + this.name() + ".enabled", true);
        }
        
        public Message setState(boolean enable) {
            Files tportConfig = GettingFiles.getFile("TPortConfig");
            tportConfig.getConfig().set("features." + this.name() + ".enabled", enable);
            tportConfig.saveConfig();
            
            if (reloadCommands) TPortCommand.reRegisterActions();
            
            Message stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.features." + (enable ? "enable" : "disable"));
            return formatSuccessTranslation("tport.command.features.feature." + this.name() + ".setState", this, stateMessage, "/tport reload");
        }
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.command.features.feature." + this.name() + ".description");
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
