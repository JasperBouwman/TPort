package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Notify extends SubCommand {
    
    public Notify() {
        EmptyCommand emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(textComponent("This command is used to set the notify state", ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.notify.set", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
    
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(textComponent("This command is used to get the notify state", ColorType.infoColor));
        emptyTPort.setTabRunnable((args, player) -> Arrays.stream(TPort.NotifyMode.values()).map(TPort.NotifyMode::name).collect(Collectors.toList()));
        emptyTPort.addAction(emptyState);
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log notify <TPort name> [state]
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                Message message = new Message();
                message.addText(textComponent("Notify mode of TPort ", ColorType.infoColor));
                message.addText(textComponent(tport.getName(), ColorType.varInfoColor, ClickEvent.runCommand("/tport own " + tport.getName())));
                message.addText(textComponent(" is set to ", ColorType.infoColor));
                message.addText(textComponent(tport.getNotifyMode().name(),
                        ColorType.varInfoColor, new HoverEvent(textComponent(tport.getNotifyMode().getDescription(), ColorType.infoColor))));
                message.sendMessage(player);
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else if (args.length == 4) {
            if (!hasPermission(player, true, "TPort.notify.set", "TPort.basic")) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                TPort.NotifyMode notify = TPort.NotifyMode.get(args[3]);
                tport.setNotifyMode(notify);
                tport.save();
                
                Message message = new Message();
                message.addText(textComponent("Successfully set notify value of TPort ", ColorType.successColor));
                message.addText(textComponent(tport.getName(), ColorType.varSuccessColor, ClickEvent.runCommand("/tport own " + tport.getName())));
                message.addText(textComponent(" to ", ColorType.successColor));
                message.addText(textComponent(tport.getNotifyMode().name(),
                        ColorType.varSuccessColor, new HoverEvent(textComponent(tport.getNotifyMode().getDescription(), ColorType.varInfoColor))));
                message.sendMessage(player);
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport log notify <TPort name> [state]");
        }
    }
}
