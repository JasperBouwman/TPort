package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_safetyFirst;
import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class Notify extends SubCommand {
    
    private final EmptyCommand emptyTPortState;
    
    public Notify() {
        emptyTPortState = new EmptyCommand();
        emptyTPortState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyTPortState.setCommandDescription(formatInfoTranslation("tport.command.log.notify.tportName.state.commandDescription"));
        emptyTPortState.setPermissions("TPort.notify.set", "TPort.basic");
        
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.log.notify.tportName.commandDescription"));
        emptyTPort.setTabRunnable((args, player) -> Arrays.stream(TPort.NotifyMode.values()).map(TPort.NotifyMode::name).collect(Collectors.toList()));
        emptyTPort.addAction(emptyTPortState);
        addAction(emptyTPort);
        
        setCommandDescription(formatInfoTranslation("tport.command.log.notify.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log notify [TPort name] [state]
        if (args.length == 2) {
            boolean color = true;
            
            Message tportsMessage = new Message();
            ArrayList<TPort> tportList = TPortManager.getTPortList(player.getUniqueId());
            int notifySize = 0;
            final int notifyMax = (int) tportList.stream().filter(tport -> tport.getNotifyMode() != TPort.NotifyMode.NONE).count();
            
            for (TPort tport : tportList) {
                if (tport.getNotifyMode() != TPort.NotifyMode.NONE) {
                    
                    if (color) tportsMessage.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s", asTPort(tport)));
                    else       tportsMessage.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s", asTPort(tport)));
                    
                    if (notifySize + 2 == notifyMax) tportsMessage.addMessage(formatInfoTranslation("tport.command.log.notify.lastDelimiter"));
                    else                             tportsMessage.addMessage(formatInfoTranslation("tport.command.log.notify.delimiter"));
                    
                    color = !color;
                    notifySize++;
                }
            }
            tportsMessage.removeLast();
            
            color = true;
            Message statesMessage = new Message();
            TPort.NotifyMode[] values = TPort.NotifyMode.values();
            int valuesLength = values.length;
            for (int i = 0; i < valuesLength; i++) {
                TPort.NotifyMode notifyMode = values[i];
                
                if (color) statesMessage.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s", notifyMode));
                else       statesMessage.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s", notifyMode));
                
                if (i + 2 == valuesLength) statesMessage.addMessage(formatInfoTranslation("tport.command.log.notify.lastDelimiter"));
                else                       statesMessage.addMessage(formatInfoTranslation("tport.command.log.notify.delimiter"));
                
                color = !color;
            }
            statesMessage.removeLast();
            
            if (notifySize == 0) sendInfoTranslation(player, "tport.command.log.notify.succeeded.none", statesMessage);
            else if (notifySize == 1) sendInfoTranslation(player, "tport.command.log.notify.succeeded.singular", tportsMessage, statesMessage);
            else sendInfoTranslation(player, "tport.command.log.notify.succeeded.multiple", tportsMessage, statesMessage);
        } else if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport != null) {
                sendInfoTranslation(player, "tport.command.log.notify.tportName.succeeded", asTPort(tport), tport.getNotifyMode());
            } else {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
            }
        } else if (args.length == 4) {
            if (!emptyTPortState.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
                return;
            }
            TPort.NotifyMode notify = TPort.NotifyMode.get(args[3]);
            tport.setNotifyMode(notify);
            tport.save();
            
            sendSuccessTranslation(player, "tport.command.log.notify.tportName.state.succeeded", asTPort(tport), tport.getNotifyMode());
            
            Advancement_safetyFirst.grant(player);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log notify [TPort name] [state]");
        }
    }
}
