package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Default extends SubCommand {
    
    public Default() {
        EmptyCommand emptyMode = new EmptyCommand();
        emptyMode.setCommandName("default LogMode", ArgumentType.OPTIONAL);
        emptyMode.setCommandDescription(TextComponent.textComponent("This command is used to set the default LogMode of the given TPort. " +
                "Players that are not logged have the default LogMode", ColorType.infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.log", ColorTheme.ColorType.varInfoColor));
    
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(TextComponent.textComponent("This command is used to get the default LogMode of the given TPort. " +
                "Players that are not logged have the default LogMode", ColorType.infoColor));
        emptyTPort.setTabRunnable((args, player) -> hasPermission(player, false, "TPort.command.log") ?
                        Arrays.stream(TPort.LogMode.values()).map(TPort.LogMode::name).collect(Collectors.toList()) : Collections.emptyList());
        emptyTPort.addAction(emptyMode);
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log default <TPort name> [default LogMode]
    
        if (!hasPermission(player, true, "TPort.log")) {
            return;
        }
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                sendInfoTheme(player, "Default LogMode of TPort %s is %s", tport.getName(), tport.getDefaultLogMode().name());
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                tport.setDefaultLogMode(TPort.LogMode.get(args[3]));
                tport.save();
                sendSuccessTheme(player, "Successfully edited the default LogMode of TPort %s to %s", tport.getName(), tport.getDefaultLogMode().name());
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport log default <TPort name> [default LogMode]");
        }
    }
}
