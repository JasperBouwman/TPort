package com.spaceman.tport.commands.tport.featureTP;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class SearchTries extends SubCommand {
    
    private final EmptyCommand emptySize;
    
    public SearchTries() {
        emptySize = new EmptyCommand();
        emptySize.setCommandName("size", ArgumentType.OPTIONAL);
        emptySize.setCommandDescription(textComponent("This command is used to set the amount of tries it will search for finding a feature", infoColor));
        emptySize.setPermissions("TPort.featureTP.searchTries", "TPort.admin.featureTP");
        addAction(emptySize);
    }
    
    public static int getFeatureSearches() {
        return getFile("TPortConfig").getConfig().getInt("featureTP.searches", 100);
    }
    
    private static void setFeatureSearches(int searches) {
        Files tportConfig = getFile("TPortConfig");
        tportConfig.getConfig().set("featureTP.searches", searches);
        tportConfig.saveConfig();
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the amount of tries it will search for finding a feature", infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport featureTP searchTries [tries]
        
        if (args.length == 2) {
            sendInfoTheme(player, "The amount of feature search tries is set to %s", String.valueOf(getFeatureSearches()));
        } else if (args.length == 3) {
            if (emptySize.hasPermissionToRun(player, true)) {
                try {
                    setFeatureSearches(Integer.parseInt(args[2]));
                    sendSuccessTheme(player, "Successfully set the feature searches tries to %s", args[2]);
                } catch (NumberFormatException nfe) {
                    sendErrorTheme(player, "%s is not a valid number", args[2]);
                }
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport featureTP searchTries [tries]");
        }
    }
}
