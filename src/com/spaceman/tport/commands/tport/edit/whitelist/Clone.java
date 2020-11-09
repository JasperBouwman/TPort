package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Clone extends SubCommand {
    
    private final EmptyCommand emptyTPort;
    
    public Clone() {
        emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(textComponent("This command is used to clone the whitelist of the second given TPort to the first given TPort",
                ColorTheme.ColorType.infoColor));
        emptyTPort.setPermissions("TPort.edit.whitelist.clone", "TPort.basic");
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return TPortManager.getTPortList(player.getUniqueId()).stream().map(TPort::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> whitelist clone <TPort name>
    
        if (!emptyTPort.hasPermissionToRun(player, true)) {
            return;
        }
        if (args.length == 5) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport != null) {
                TPort cloneTPort = TPortManager.getTPort(player.getUniqueId(), args[4]);
                if (cloneTPort != null) {
                    tport.setWhitelist(new ArrayList<>(cloneTPort.getWhitelist()));
                    tport.save();
                    sendSuccessTheme(player, "Successfully cloned whitelist of TPort %s to TPort %s", cloneTPort.getName(), tport.getName());
                } else {
                    sendErrorTheme(player, "No TPort found called %s", args[1]);
                }
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> whitelist clone <TPort name>");
        }
    }
}
