package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Default extends SubCommand {
    
    private final EmptyCommand emptyTPortMode;
    
    public Default() {
        emptyTPortMode = new EmptyCommand();
        emptyTPortMode.setCommandName("default LogMode", ArgumentType.OPTIONAL);
        emptyTPortMode.setCommandDescription(formatInfoTranslation("tport.command.log.default.tportName.defaultMode.commandDescription"));
        emptyTPortMode.setPermissions("TPort.log");
        
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.log.default.tportName.commandDescription"));
        emptyTPort.setTabRunnable((args, player) -> emptyTPortMode.hasPermissionToRun(player, false) ?
                Arrays.stream(TPort.LogMode.values()).map(TPort.LogMode::name).collect(Collectors.toList()) : Collections.emptyList());
        emptyTPort.addAction(emptyTPortMode);
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log default <TPort name> [default LogMode]
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                sendInfoTranslation(player, "tport.command.log.default.tportName.succeeded", tport, tport.getDefaultLogMode());
            } else {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
            }
        } else if (args.length == 4) {
            if (!emptyTPortMode.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                tport.setDefaultLogMode(TPort.LogMode.get(args[3]));
                tport.save();
                sendSuccessTranslation(player, "tport.command.log.default.tportName.defaultMode.succeeded", tport, tport.getDefaultLogMode());
            } else {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log default <TPort name> [default LogMode]");
        }
    }
}
