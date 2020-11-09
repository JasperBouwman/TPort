package com.spaceman.tport.commands.tport.featureTP;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.FeatureTP.getDefMode;
import static com.spaceman.tport.commands.tport.FeatureTP.setDefMode;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Mode extends SubCommand {
    
    public static final EmptyCommand emptyModeMode = new EmptyCommand();
    
    public Mode() {
        emptyModeMode.setCommandName("mode", ArgumentType.OPTIONAL);
        emptyModeMode.setCommandDescription(textComponent("This command is used to set your default FeatureTP Mode", infoColor));
        emptyModeMode.setPermissions("TPort.featureTP.mode.<mode>");
        addAction(emptyModeMode);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.stream(FeatureTP.FeatureTPMode.values()).map(Enum::name).collect(Collectors.toList());
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get your default FeatureTP Mode", infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport featureTP mode [mode]
        
        if (args.length == 2) {
            sendInfoTheme(player, "Your default FeatureTP Mode is set to %s", getDefMode(player.getUniqueId()).name());
        } else if (args.length == 3) {
            try {
                FeatureTP.FeatureTPMode mode = FeatureTP.FeatureTPMode.valueOf(args[2].toUpperCase());
                if (!hasPermission(player, true, mode.getPerm())) {
                    return;
                }
                setDefMode(player.getUniqueId(), mode);
                sendSuccessTheme(player, "Successfully set you default FeatureTP Mode to %s", mode.name());
            } catch (IllegalArgumentException iae) {
                sendErrorTheme(player, "FeatureTP mode %s does not exist", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport featureTP mode [mode]");
        }
    }
}
