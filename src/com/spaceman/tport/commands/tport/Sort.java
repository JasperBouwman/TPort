package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Pair;
import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Sort extends SubCommand {
    
    public Sort() {
        EmptyCommand emptySorter = new EmptyCommand();
        emptySorter.setCommandName("sorter", ArgumentType.OPTIONAL);
        emptySorter.setCommandDescription(formatInfoTranslation("tport.command.sort.sorter.commandDescription"));
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
        return tportData.getConfig().getString("tport." + player.getUniqueId() + ".sorter", "oldest");
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
    
    public static String getPreviousSorterName(String currentSorter) {
    
        ArrayList<String> list = new ArrayList<>(getSorters());
        String sorter = list.get(list.size() - 1);
        
        for (String s : list) {
            if (s.equalsIgnoreCase(currentSorter)) {
                return sorter;
            }
            sorter = s;
        }
        
        return sorter;
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
        return formatInfoTranslation("tport.command.sort.commandDescription");
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
            
            Files tportData = getFile("TPortData");
    
            String sorterName = tportData.getConfig().getString("tport." + player.getUniqueId() + ".sorter", "oldest");
            if (getSorter(sorterName) != null) {
                tportData.getConfig().set("tport." + player.getUniqueId() + ".sorter", "oldest");
                tportData.saveConfig();
                sorterName = "oldest";
            }
            
            Message availableSorters = new Message();
            ArrayList<String> sorters = new ArrayList<>(getSorters());
            int sortersSize = sorters.size();
            boolean color = true;
            for (int i = 0; i < sortersSize; i++) {
                String sorter = sorters.get(i);
                availableSorters.addText(textComponent(sorter, (color ? varInfoColor : varInfo2Color), new HoverEvent(getDescription(sorter))));
                
                if (i + 2 == sortersSize) availableSorters.addMessage(formatInfoTranslation("tport.command.sort.lastDelimiter"));
                else                      availableSorters.addMessage(formatInfoTranslation("tport.command.sort.delimiter"));
                
                color = !color;
            }
            availableSorters.removeLast();
            
            sendInfoTranslation(player, "tport.command.sort.succeeded",
                    textComponent(sorterName, varInfoColor, new HoverEvent(getDescription(sorterName))),
                    availableSorters);
        } else if (args.length == 2) {
            Sorter sorter = getSorterExact(args[1]);
            
            if (sorter == null) {
                sendErrorTranslation(player, "tport.command.sort.sorter.notExist", args[1]);
                return;
            }
            
            if (!hasPermission(player, true, "TPort.sort." + args[1])) {
                return;
            }
            
            Files tportData = getFile("TPortData");
            tportData.getConfig().set("tport." + player.getUniqueId() + ".sorter", args[1]);
            tportData.saveConfig();
            
            TPortInventories.openMainTPortGUI(player);
            
            sendSuccessTranslation(player, "tport.command.sort.sorter.succeeded", args[1]);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport sort [sorter]");
        }
    }
    
    @FunctionalInterface
    public interface Sorter {
        List<ItemStack> sort(Player player);
    }
}
