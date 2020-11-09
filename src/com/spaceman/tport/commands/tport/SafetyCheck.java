package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class SafetyCheck extends SubCommand {
    
    public static final EmptyCommand emptySafetyCheck = new EmptyCommand();
    
    public SafetyCheck() {
        emptySafetyCheck.setCommandName("default state", ArgumentType.OPTIONAL);
        emptySafetyCheck.setCommandDescription(textComponent("This command is used to set your default safetyCheck value", infoColor));
        emptySafetyCheck.setPermissions("TPort.safetyCheck", "TPort.basic");
        addAction(emptySafetyCheck);
    }
    
    private static SafetyChecker safetyChecker = (l -> true);
    
    public static void setSafetyCheck(SafetyChecker safetyChecker) {
        SafetyCheck.safetyChecker = safetyChecker;
    }
    
    public static boolean isSafe(Location feet) {
        return safetyChecker.isSafe(feet);
    }
    
    public static boolean safetyCheck(Player player) {
        if (emptySafetyCheck.hasPermissionToRun(player, false)) {
            return getFile("TPortData").getConfig().getBoolean("tport." + player.getUniqueId() + ".safetyCheck", false);
        } else {
            return false;
        }
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to see if the safetyCheck is enabled or not. When enabled and not overridden, " +
                "it will preform a safetyCheck before you teleport. It checks if the location does not exist of solid blocks, and the block you are standing on is not lava/fire", infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport safetyCheck [default state]
        
        if (args.length == 1) {
            Files tportData = getFile("TPortData");
            boolean safetyCheck = tportData.getConfig().getBoolean("tport." + player.getUniqueId() + ".safetyCheck", false);
            sendInfoTheme(player, "Your default safeCheck value is set to %s", safetyCheck);
            
            if (!emptySafetyCheck.hasPermissionToRun(player, false)) {
                sendInfoTheme(player, "Since you don't have the permissions %s or %s your default value is overridden with %s", "TPort.safetyCheck", "TPort.basic", "false");
            }
            
        } else if (args.length == 2) {
            if (emptySafetyCheck.hasPermissionToRun(player, true)) {
                boolean safetyCheck = Boolean.parseBoolean(args[1]);
                Files tportData = getFile("TPortData");
                tportData.getConfig().set("tport." + player.getUniqueId() + ".safetyCheck", safetyCheck);
                tportData.saveConfig();
                sendInfoTheme(player, "Your default safeCheck value is set to %s", safetyCheck);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport safetyCheck [default state]");
        }
    }
    
    @FunctionalInterface
    public interface SafetyChecker {
        boolean isSafe(Location feet);
    }
}
