package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.tport.TPortManager.TPortSize;

public class Move extends SubCommand {
    
    public static EmptyCommand emptySlot;
    
    public Move() {
        emptySlot = new EmptyCommand();
        emptySlot.setCommandName("slot", ArgumentType.REQUIRED);
        emptySlot.setCommandDescription(formatInfoTranslation("tport.command.edit.move.slot.commandDescription", "1", TPortSize));
        emptySlot.setPermissions("TPort.edit.move", "TPort.basic");
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.edit.move.tport.commandDescription"));
        emptyTPort.setPermissions("TPort.edit.move", "TPort.basic");
        addAction(emptySlot);
        addAction(emptyTPort);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        List<String> list = IntStream.rangeClosed(1, TPortSize).mapToObj(String::valueOf).collect(Collectors.toList());
        list.addAll(TPortManager.getTPortList(player.getUniqueId()).stream().map(TPort::getName).collect(Collectors.toList()));
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> move <slot|TPort name>
        
        if (!emptySlot.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            
            TPort secondTPort = TPortManager.getTPort(player.getUniqueId(), args[3]);
            
            if (secondTPort != null) {
                
                int tmpSlot = secondTPort.getSlot();
                secondTPort.setSlot(tport.getSlot());
                tport.setSlot(tmpSlot);
                tport.save();
                secondTPort.save();
                sendSuccessTranslation(player, "tport.command.edit.move.tport.succeeded", tport, secondTPort);
            } else {
                try {
                    int newSlot = Integer.parseInt(args[3]) - 1;
                    
                    secondTPort = TPortManager.getTPort(player.getUniqueId(), newSlot);
                    if (secondTPort != null) {
                        this.run(new String[]{"edit", tport.getName(), "move", secondTPort.getName()}, player);
                    } else {
                        if (newSlot < 0 || newSlot >= TPortSize) {
                            sendErrorTranslation(player, "tport.command.edit.move.slot.slotOutOfBounds", "1", TPortSize);
                            return;
                        }
                        tport.setSlot(newSlot);
                        tport.save();
                        sendSuccessTranslation(player, "tport.command.edit.move.slot.succeeded", tport, String.valueOf((newSlot + 1)));
                    }
                } catch (NumberFormatException nfe) {
                    sendErrorTranslation(player, "tport.command.edit.move.invalidArgument", args[3]);
                }
            }
            
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> move <slot|TPort name>");
        }
    }
}
