package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fileHander.Files;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Accuracy extends SubCommand {
    
    private final EmptyCommand emptySize;
    
    public Accuracy() {
        emptySize = new EmptyCommand();
        emptySize.setCommandName("size", ArgumentType.OPTIONAL);
        emptySize.setCommandDescription(formatInfoTranslation("tport.command.biomeTP.accuracy.accuracy.commandDescription"));
        emptySize.setPermissions("TPort.biomeTP.accuracy", "TPort.admin.biomeTP");
        addAction(emptySize);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return accuracies.keySet();
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.biomeTP.accuracy.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP accuracy [accuracy]
        
        if (args.length == 2) {
            AccuracySettings accuracy = getDefaultAccuracySettings();
            sendInfoTranslation(player, "tport.command.biomeTP.accuracy.succeeded", accuracy);
        } else if (args.length == 3) {
            if (emptySize.hasPermissionToRun(player, true)) {
                if (setBiomeTPAccuracy(args[2])) {
                    AccuracySettings accuracy = getDefaultAccuracySettings();
                    sendSuccessTranslation(player, "tport.command.biomeTP.accuracy.accuracy.succeeded", accuracy);
                } else {
                    sendErrorTranslation(player, "tport.command.biomeTP.accuracy.accuracy.notAnAccuracy", args[2]);
                }
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport biomeTP accuracy [accuracy]");
        }
    }
    
    public static HashMap<String, AccuracySettings> accuracies = new HashMap<>();
    
    public static boolean createAccuracy(String name, int range, int increment, List<Integer> yLevels) {
        if (!accuracies.containsKey(name)) {
            accuracies.put(name, new AccuracySettings(name, range, increment, yLevels));
            return true;
        }
        return false;
    }
    
    public static String getDefaultAccuracy() {
        return getFile("TPortConfig").getConfig().getString("biomeTP.accuracy", "default");
    }
    
    public static AccuracySettings getDefaultAccuracySettings() {
        String accuracy = getDefaultAccuracy();
        return getAccuracySettings(accuracy);
    }
    
    public static AccuracySettings getAccuracySettings(String accuracy) {
        AccuracySettings accuracySettings = accuracies.getOrDefault(accuracy, null);
        if (accuracySettings == null) accuracySettings = new AccuracySettings("default", 6400, 8, Arrays.asList(0, 200));
        else accuracySettings.name = accuracy;
        
        return accuracySettings;
    }
    
    public static boolean setBiomeTPAccuracy(String accuracy) {
        if (accuracies.containsKey(accuracy)) {
            Files tportConfig = getFile("TPortConfig");
            tportConfig.getConfig().set("biomeTP.accuracy", accuracy);
            tportConfig.saveConfig();
            return true;
        }
        return false;
    }
    
    public static class AccuracySettings implements MessageUtils.MessageDescription {
        String name;
        int range;
        int increment;
        List<Integer> yLevels;
        
        public AccuracySettings(String name, int range, int increment, List<Integer> yLevels) {
            this.name = name;
            this.range = range;
            this.increment = increment;
            this.yLevels = yLevels;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public String getInsertion() {
            return null;
        }
        
        public int getRange() {
            return range;
        }
        
        public int getIncrement() {
            return increment;
        }
        
        public List<Integer> getYLevels() {
            return yLevels;
        }
        
        @Override
        public Message getDescription() {
            Message message = new Message();
            message.addMessage(formatInfoTranslation("tport.command.biomeTP.accuracy.accuracySettings.hoverData.range", getRange()));
            message.addNewLine();
            message.addMessage(formatInfoTranslation("tport.command.biomeTP.accuracy.accuracySettings.hoverData.increment", getIncrement()));
            message.addNewLine();
            message.addMessage(formatInfoTranslation("tport.command.biomeTP.accuracy.accuracySettings.hoverData.yLevels", StringUtils.join(yLevels, ' ')));
            return message;
        }
    }
}
