package com.spaceman.tport.commands.tport.log;

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

import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Notify extends SubCommand {
    
    private final EmptyCommand emptyTPortState;
    
    public Notify() {
        emptyTPortState = new EmptyCommand();
        emptyTPortState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyTPortState.setCommandDescription(textComponent("This command is used to set the notify state", ColorType.infoColor));
        emptyTPortState.setPermissions("TPort.notify.set", "TPort.basic");
        
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyTPort.setCommandDescription(textComponent("This command is used to get the notify state", ColorType.infoColor));
        emptyTPort.setTabRunnable((args, player) -> Arrays.stream(TPort.NotifyMode.values()).map(TPort.NotifyMode::name).collect(Collectors.toList()));
        emptyTPort.addAction(emptyTPortState);
        addAction(emptyTPort);
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get info about the different notify states", ColorType.infoColor));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log notify
        // tport log notify [TPort name] [state]
        if (args.length == 2) {
            Message message = new Message();
            boolean color = true;
            
            message.addText(textComponent("The different notify states are: ", ColorType.infoColor));
            for (TPort.NotifyMode notifyMode : TPort.NotifyMode.values()) {
                message.addText(textComponent(notifyMode.name(), (color ? varInfoColor : varInfo2Color), new HoverEvent(textComponent(notifyMode.getDescription(), varInfoColor))));
                message.addText(textComponent(", ", infoColor));
                color = !color;
            }
            message.removeLast();
            
            message.sendMessage(player);
        } else if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                Message message = new Message();
                message.addText(textComponent("Notify mode of TPort ", ColorType.infoColor));
                message.addText(textComponent(tport.getName(), varInfoColor, ClickEvent.runCommand("/tport own " + tport.getName())));
                message.addText(textComponent(" is set to ", ColorType.infoColor));
                message.addText(textComponent(tport.getNotifyMode().name(),
                        varInfoColor, new HoverEvent(textComponent(tport.getNotifyMode().getDescription(), ColorType.infoColor))));
                message.sendMessage(player);
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else if (args.length == 4) {
            if (!emptyTPortState.hasPermissionToRun(player, true)) {
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
                        ColorType.varSuccessColor, new HoverEvent(textComponent(tport.getNotifyMode().getDescription(), varInfoColor))));
                message.sendMessage(player);
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport log notify [TPort name] [state]");
        }
    }
}
