package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.permissions.PermissionHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.runCommand;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.inventories.SettingsInventories.*;

public class SafetyCheck extends SubCommand {
    
    private final EmptyCommand emptyCheck;
    
    public SafetyCheck() {
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        
        emptyCheck = new EmptyCommand();
        emptyCheck.setCommandName("check", ArgumentType.FIXED);
        emptyCheck.setCommandDescription(formatInfoTranslation("tport.command.safetyCheck.check.commandDescription"));
        emptyCheck.setPermissions("TPort.safetyCheck.check", "TPort.basic");
        
        EmptyCommand emptySourceState = new EmptyCommand();
        emptySourceState.setCommandName("state", ArgumentType.OPTIONAL);
        emptySourceState.setCommandDescription(formatInfoTranslation("tport.command.safetyCheck.source.state.commandDescription"));
        emptySourceState.setPermissions("TPort.safetyCheck.<source>", "TPort.basic");
        EmptyCommand emptySource = new EmptyCommand();
        emptySource.setCommandName("source", ArgumentType.OPTIONAL);
        emptySource.setCommandDescription(formatInfoTranslation("tport.command.safetyCheck.source.commandDescription"));
        emptySource.setTabRunnable(((args, player) -> SafetyCheckSource.get(args[1]) != null ? List.of("true", "false") : List.of()));
        emptySource.addAction(emptySourceState);
        
        addAction(empty);
        addAction(emptyCheck);
        addAction(emptySource);
        
        setCommandDescription(formatInfoTranslation("tport.command.safetyCheck.commandDescription"));
    }
    
    private static SafetyChecker safetyChecker = ((l, clearFeet) -> true);
    
    public static void setSafetyCheck(SafetyChecker safetyChecker) {
        SafetyCheck.safetyChecker = safetyChecker;
    }
    
    public static boolean isSafe(Location feet) {
        return safetyChecker.isSafe(feet, false);
    }
    public static boolean isSafe(Location feet, boolean clearFeet) {
        return safetyChecker.isSafe(feet, clearFeet);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        List<String> list = Arrays.stream(SafetyCheckSource.values()).map(Enum::name).collect(Collectors.toList());
        list.add("check");
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport safetyCheck
        // tport safetyCheck <source> [state]
        // tport safetyCheck check
        
        if (Features.Feature.SafetyCheck.isDisabled()) {
            Features.Feature.SafetyCheck.sendDisabledMessage(player);
            return;
        }
        
        if (args.length == 1) {
            Message list = new Message();
            Message delimiter = formatInfoTranslation("tport.command.safetyCheck.delimiter");
            SafetyCheckSource[] values = SafetyCheckSource.values();
            boolean color = true;
            
            for (int i = 0; i < values.length; i++) {
                SafetyCheckSource source = values[i];
                TextComponent stateMessage;
                
                if (source.getState(player)) stateMessage = textComponent("true", goodColor)
                        .addTextEvent(hoverEvent("/tport safetyCheck " + source.name() + " false", infoColor))
                        .addTextEvent(runCommand("/tport safetyCheck " + source.name() + " false"))
                        .setInsertion("/tport safetyCheck " + source.name() + " false");
                else                     stateMessage = textComponent("false", badColor)
                        .addTextEvent(hoverEvent("/tport safetyCheck " + source.name() + " true", infoColor))
                        .addTextEvent(runCommand("/tport safetyCheck " + source.name() + " true"))
                        .setInsertion("/tport safetyCheck " + source.name() + " true");
                
                if (color) list.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.safetyCheck.listElement", source, stateMessage));
                else       list.addMessage(formatTranslation(infoColor, varInfo2Color, "tport.command.safetyCheck.listElement", source, stateMessage));
                
                color = !color;
                if (i + 2 == values.length) list.addMessage(formatInfoTranslation("tport.command.safetyCheck.lastDelimiter"));
                else                        list.addMessage(delimiter);
            }
            
            sendInfoTranslation(player, "tport.command.safetyCheck.succeeded", list);
            return;
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("check")) {
                if (!emptyCheck.hasPermissionToRun(player, true)) {
                    return;
                }
                Message state;
                if (safetyChecker.isSafe(player.getLocation(), false)) {
                    state = formatTranslation(goodColor, goodColor, "tport.command.safetyCheck.check.safe");
                } else {
                    state = formatTranslation(badColor, badColor, "tport.command.safetyCheck.check.unsafe");
                }
                sendInfoTranslation(player, "tport.command.safetyCheck.check.succeeded", state);
                return;
            }
        }
        
        if (args.length == 2 || args.length == 3) {
            SafetyCheckSource source = SafetyCheckSource.get(args[1]);
            if (source == null) {
                sendErrorTranslation(player, "tport.command.safetyCheck.source.sourceNotFound");
                return;
            }
            if (args.length == 2) { //get source
                sendInfoTranslation(player, "tport.command.safetyCheck.source.succeeded", source, source.getState(player));
            } else { //set source
                Boolean newState = Main.toBoolean(args[2]);
                if (newState == null) {
                    sendErrorTranslation(player, "tport.command.wrongUsage", "/tport safetyCheck <source> <true|false>");
                    return;
                }
                
                if (source.setState(player, newState)) {
                    sendSuccessTranslation(player, "tport.command.safetyCheck.source.state.succeeded", source, source.getState(player));
                }
            }
            return;
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport safetyCheck [source|state]");
    }
    
    @FunctionalInterface
    public interface SafetyChecker {
        boolean isSafe(Location feet, boolean clearFeet);
    }
    
    public enum SafetyCheckSource implements MessageUtils.MessageDescription {
        TPORT_OPEN(settings_safety_check_tport_open_model, settings_safety_check_tport_open_grayed_model),
        TPORT_OWN(settings_safety_check_tport_own_model, settings_safety_check_tport_own_grayed_model),
        TPORT_HOME(settings_safety_check_tport_home_model, settings_safety_check_tport_home_grayed_model),
        TPORT_BACK(settings_safety_check_tport_back_model, settings_safety_check_tport_back_grayed_model),
        PLTP(settings_safety_check_pltp_model, settings_safety_check_pltp_grayed_model),
        TPORT_PUBLIC(settings_safety_check_tport_public_model, settings_safety_check_tport_public_grayed_model);
        
        private final InventoryModel enabledModel;
        private final InventoryModel disabledModel;
        
        SafetyCheckSource(InventoryModel enabledModel, InventoryModel disabledModel) {
            this.enabledModel = enabledModel;
            this.disabledModel = disabledModel;
        }
        
        public InventoryModel getModel(Player player) {
            return this.getState(player) ? enabledModel : disabledModel;
        }
        
        public String getPermission() {
            return "TPort.safetyCheck." + this.name();
        }
        public boolean hasPermission(Player player, boolean sendMessage) {
            if (Features.Feature.SafetyCheck.isDisabled()) {
                if (sendMessage) Features.Feature.SafetyCheck.sendDisabledMessage(player);
                return false;
            }
            return PermissionHandler.hasPermission(player, sendMessage, true, this.getPermission(), "TPort.basic");
        }
        
        public boolean getState(Player player) {
            if (hasPermission(player, false)) {
                return tportData.getConfig().getBoolean("tport." + player.getUniqueId() + ".safetyCheck." + this.name() + ".state", true);
            } else {
                return false;
            }
        }
        public boolean setState(Player player, boolean state) {
            if (hasPermission(player, true)) {
                tportData.getConfig().set("tport." + player.getUniqueId() + ".safetyCheck." + this.name() + ".state", state);
                tportData.saveConfig();
                return true;
            }
            return false;
        }
        
        public static SafetyCheckSource get(String name) {
            for (SafetyCheckSource source : SafetyCheckSource.values()) {
                if (source.name().equalsIgnoreCase(name)) {
                    return source;
                }
            }
            return null;
        }
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.command.safetyCheck.safetyCheckSource." + this.name() + ".description");
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
