package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.spaceman.tport.events.InventoryClick.TPortSize;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Move extends SubCommand {
    
    public static EmptyCommand emptySlot;
    
    public Move() {
        emptySlot = new EmptyCommand();
        emptySlot.setCommandName("slot", ArgumentType.REQUIRED);
        emptySlot.setCommandDescription(textComponent("This command is used to move/swap the given TPort to the given slot, you can choose between ", infoColor),
                textComponent("0", varInfoColor),
                textComponent(" and ", infoColor),
                textComponent(String.valueOf(TPortSize), varInfoColor));
        emptySlot.setPermissions("TPort.edit.move", "TPort.basic");
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(textComponent("This command us used to swap the first given TPort with the second given TPort", infoColor));
        emptyTPort.setPermissions(emptySlot.getPermissions()); //clone permissions of emptySlot
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
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            
            TPort secondTPort = TPortManager.getTPort(player.getUniqueId(), args[3]);
            
            if (secondTPort != null) {
                
                int tmpSlot = secondTPort.getSlot();
                secondTPort.setSlot(tport.getSlot());
                tport.setSlot(tmpSlot);
                tport.save();
                secondTPort.save();
                sendSuccessTheme(player, "Successfully swapped TPort %s with %s", tport.getName(), secondTPort.getName());
            } else {
                try {
                    int newSlot = Integer.parseInt(args[3]) - 1;
                    
                    secondTPort = TPortManager.getTPort(player.getUniqueId(), newSlot);
                    if (secondTPort != null) {
                        this.run(new String[]{"edit", tport.getName(), "move", secondTPort.getName()}, player);
                    } else {
                        if (newSlot < 0 || newSlot >= TPortSize) {
                            sendErrorTheme(player, "Slot number must be between %s and %s", "1", String.valueOf(TPortSize));
                            return;
                        }
                        tport.setSlot(newSlot);
                        tport.save();
                        sendSuccessTheme(player, "Successfully moved TPort %s to slot %s", tport.getName(), String.valueOf((newSlot + 1)));
                    }
                } catch (NumberFormatException nfe) {
                    sendErrorTheme(player, "%s is not a slot number or a TPort", args[3]);
                }
            }
            
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> move <slot|TPort name>");
        }
    }
}
