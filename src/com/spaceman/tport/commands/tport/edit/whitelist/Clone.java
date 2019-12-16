package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Clone extends SubCommand {
    
    public Clone() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(textComponent("This command is used to clone the whitelist of the second given TPort to the first given TPort",
                ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.edit.whitelist.clone", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        addAction(emptyCommand);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return TPortManager.getTPortList(player.getUniqueId()).stream().map(TPort::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> whitelist clone <TPort name>
        
        if (args.length == 5) {
            if (!hasPermission(player, "TPort.edit.whitelist.clone", "TPort.basic")) {
                return;
            }
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
