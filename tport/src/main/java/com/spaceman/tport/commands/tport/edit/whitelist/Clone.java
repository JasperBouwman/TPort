package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Clone extends SubCommand {
    
    private final EmptyCommand emptyTPort;
    
    public Clone() {
        emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.edit.whitelist.clone.commandDescription"));
        emptyTPort.setPermissions("TPort.edit.whitelist.clone", "TPort.basic");
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyTPort.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return TPortManager.getTPortList(player.getUniqueId()).stream().map(TPort::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> whitelist clone <TPort name>
        
        if (args.length != 5) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> whitelist clone <TPort name>");
            return;
        }
        if (!emptyTPort.hasPermissionToRun(player, true)) {
            return;
        }
        
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
            return;
        }
        TPort cloneTPort = TPortManager.getTPort(player.getUniqueId(), args[4]);
        if (cloneTPort == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
            return;
        }
        
        tport.setWhitelist(new ArrayList<>(cloneTPort.getWhitelist()));
        tport.save();
        sendSuccessTranslation(player, "tport.command.edit.whitelist.clone.succeeded", cloneTPort, tport);
    }
}
