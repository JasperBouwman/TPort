package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class SearchTries extends SubCommand {
    
    private final EmptyCommand emptySize;
    
    public SearchTries() {
        emptySize = new EmptyCommand();
        emptySize.setCommandName("size", ArgumentType.OPTIONAL);
        emptySize.setCommandDescription(textComponent("This command is used to set the amount of tries it will search for finding a biome", ColorType.infoColor));
        emptySize.setPermissions("TPort.biomeTP.searchTries", "TPort.admin.biomeTP");
        addAction(emptySize);
    }
    
    public static int getBiomeSearches() {
        return getFile("TPortConfig").getConfig().getInt("biomeTP.searches", 100);
    }
    
    private static void setBiomeSearches(int searches) {
        Files tportConfig = getFile("TPortConfig");
        tportConfig.getConfig().set("biomeTP.searches", searches);
        tportConfig.saveConfig();
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the amount of tries it will search for finding a biome", ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP searchTries [tries]
        
        if (args.length == 2) {
            sendInfoTheme(player, "The amount of biome search tries is set to %s", String.valueOf(getBiomeSearches()));
        } else if (args.length == 3) {
            if (emptySize.hasPermissionToRun(player, true)) {
                try {
                    setBiomeSearches(Integer.parseInt(args[2]));
                    sendSuccessTheme(player, "Successfully set the biome searches tries to %s", args[2]);
                } catch (NumberFormatException nfe) {
                    sendErrorTheme(player, "%s is not a valid number", args[2]);
                }
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport biomeTP searchTries [tries]");
        }
    }
}
