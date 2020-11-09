package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Pair;
import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Sort extends SubCommand {
    
    public Sort() {
        EmptyCommand emptySorter = new EmptyCommand();
        emptySorter.setCommandName("sorter", ArgumentType.OPTIONAL);
        emptySorter.setCommandDescription(textComponent("This command is used to set your preferred sorter", infoColor));
        emptySorter.setPermissions("TPort.sort.<sorter>");
        addAction(emptySorter);
    }
    
    private static final HashMap<String, Pair<Sorter, Message>> sorters = new HashMap<>();
    
    public static Set<String> getSorters() {
        return sorters.keySet();
    }
    
    public static Message getDescription(String name) {
        return sorters.getOrDefault(name, new Pair<>(null, null)).getRight();
    }
    
    public static Sorter getSorter(String name) {
        Sorter sorter = getSorterExact(name);
        if (sorter == null) {
            sorter = getSorterExact("oldest");
            if (sorter == null) {
                sorter = getSorterExact(getSorters().iterator().next());
            }
        }
        return sorter;
    }
    
    public static String getSorterName(Player player) {
        Files tportData = getFile("TPortData");
        return tportData.getConfig().getString("tport." + player.getUniqueId().toString() + ".sorter", "oldest");
    }
    
    public static Sorter getSorter(Player player) {
        return getSorter(getSorterName(player));
    }
    
    public static String getNextSorterName(String currentSorter) {
    
        for (Iterator<String> iterator = getSorters().iterator(); iterator.hasNext(); ) {
            String sorter = iterator.next();
            if (sorter.equalsIgnoreCase(currentSorter)) {
                if (iterator.hasNext()) {
                    return iterator.next();
                } else {
                    return getSorters().iterator().next();
                }
            }
        }
        
        return currentSorter;
    }
    
    public static Sorter getSorterExact(String name) {
        return sorters.getOrDefault(name, new Pair<>(null, null)).getLeft();
    }
    
    public static boolean addSorter(String name, Sorter sorter, Message description) {
        if (!sorters.containsKey(name) && !name.contains(" ")) {
            sorters.put(name, new Pair<>(sorter, description));
            return true;
        }
        return false;
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getSorters();
    }
    
    @Override
    public Message getCommandDescription() {
        Message message = new Message();
        message.addText(textComponent("This command is used to get all the available sorters and their description", infoColor));
        return message;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport sort [sorter]
        
        /*
         * sort types:
         * popularity //todo
         * alphabet
         * newest
         * oldest (default)
         * */
        
        if (args.length == 1) {
            
            Message message = new Message();
            Files tportData = getFile("TPortData");
            
            String sorterName = tportData.getConfig().getString("tport." + player.getUniqueId().toString() + ".sorter", "oldest");
            Sort.Sorter ownSorter = getSorter(sorterName);
            if (ownSorter != null) {
                tportData.getConfig().set("tport." + player.getUniqueId().toString() + ".sorter", "oldest");
                tportData.saveConfig();
                sorterName = "oldest";
            }
            
            message.addText(textComponent("Your sorter: ", infoColor));
            message.addText(textComponent(sorterName, varInfoColor, new HoverEvent(getDescription(sorterName))));
            
            message.addText(textComponent("\nAvailable sorters: ", infoColor));
            message.addText("");
            boolean color = true;
            for (String sorter : getSorters()) {
                message.addText(textComponent(sorter, (color ? varInfoColor : varInfo2Color), new HoverEvent(getDescription(sorter))));
                message.addText(textComponent(", ", infoColor));
                color = !color;
            }
            message.removeLast();
            
            message.sendMessage(player);
        } else if (args.length == 2) {
            Sorter sorter = getSorterExact(args[1]);
            
            if (sorter == null) {
                sendErrorTheme(player, "Sorter %s does not exist", args[1]);
                return;
            }
            
            if (!hasPermission(player, true, "TPort.sort." + args[1])) {
                return;
            }
            
            Files tportData = getFile("TPortData");
            tportData.getConfig().set("tport." + player.getUniqueId().toString() + ".sorter", args[1]);
            tportData.saveConfig();
            
            TPortInventories.openMainTPortGUI(player, 0, sorter.sort(player), true);
            
            sendSuccessTheme(player, "Successfully set your sort type to %s", args[1]);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport sort [sorter]");
        }
    }
    
    @FunctionalInterface
    public interface Sorter {
        List<ItemStack> sort(Player player);
    }
}
