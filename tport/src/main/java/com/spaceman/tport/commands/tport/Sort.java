package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.inventories.ItemFactory;
import com.spaceman.tport.inventories.TPortInventories;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.inventories.ItemFactory.getHead;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Sort extends SubCommand {
    
    private static final String permissionPrefix = "TPort.sort.";
    
    public Sort() {
        EmptyCommand emptySorter = new EmptyCommand();
        emptySorter.setCommandName("sorter", ArgumentType.OPTIONAL);
        emptySorter.setCommandDescription(formatInfoTranslation("tport.command.sort.sorter.commandDescription"));
        emptySorter.setPermissions(permissionPrefix + "<sorter>");
        addAction(emptySorter);
        
        setCommandDescription(formatInfoTranslation("tport.command.sort.commandDescription"));
        
        registerSorters();
    }
    
    private static final HashMap<String, Pair<Sorter, Message>> sorters = new HashMap<>();
    
    private void registerSorters() {
        Sort.addSorter("alphabet", (player, attributes, headData) -> {
            ArrayList<String> playerList = new ArrayList<>(tportData.getKeys("tport"));
            return playerList.stream().map(playerUUID -> Main.getOrDefault(getHead(UUID.fromString(playerUUID), player, attributes, headData), new ItemStack(Material.AIR))).sorted((item1, item2) -> {
                //noinspection ConstantConditions
                return item1.getItemMeta().getDisplayName().compareToIgnoreCase(item2.getItemMeta().getDisplayName());
            }).collect(Collectors.toList());
            
        }, formatInfoTranslation("tport.main.sorter.alphabet.description"));
        
        Sort.addSorter("oldest", (player, attributes, headData) -> {
            ArrayList<String> playerList = new ArrayList<>(tportData.getKeys("tport"));
            return playerList.stream().map(playerUUID -> Main.getOrDefault(getHead(UUID.fromString(playerUUID), player, attributes, headData), new ItemStack(Material.AIR))).collect(Collectors.toList());
        }, formatInfoTranslation("tport.main.sorter.oldest.description"));
        
        Sort.addSorter("newest", (player, attributes, headData) -> {
            ArrayList<String> playerList = new ArrayList<>(tportData.getKeys("tport"));
            Collections.reverse(playerList);
            return playerList.stream().map(playerUUID -> Main.getOrDefault(getHead(UUID.fromString(playerUUID), player, attributes, headData), new ItemStack(Material.AIR))).collect(Collectors.toList());
        }, formatInfoTranslation("tport.main.sorter.newest.description"));
    }
    
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
    
    public static String getSorterForPlayer(Player player) {
        return tportData.getConfig().getString("tport." + player.getUniqueId() + ".sorter", "oldest");
    }
    
    public static Sorter getSorter(Player player) {
        return getSorter(getSorterForPlayer(player));
    }
    
    public static String getNextSorterName(Player player) { //todo test
        String currentSorter = Sort.getSorterForPlayer(player);
        ArrayList<String> list = new ArrayList<>(getSorters());
        
        boolean next = false;
        int indexOfCurrent = 0;
        for (int i = 0; i < list.size(); i++) {
            String sorter = list.get(i);
            
            if (next) {
                if (hasPermission(player, false, permissionPrefix + sorter)) {
                    return sorter;
                }
            } else if (sorter.equalsIgnoreCase(currentSorter)) {
                next = true;
                indexOfCurrent = i;
            }
        }
        
        for (int i = 0; i < indexOfCurrent; i++) {
            String sorter = list.get(i);
            if (hasPermission(player, false, permissionPrefix + sorter)) {
                return sorter;
            }
        }
    
        return null;
    }
    
    @Nullable
    public static String getPreviousSorterName(Player player) { //todo test
        String currentSorter = Sort.getSorterForPlayer(player);
        ArrayList<String> list = new ArrayList<>(getSorters());
        
        boolean next = false;
        int indexOfCurrent = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            String sorter = list.get(i);
            
            if (next) {
                if (hasPermission(player, false, permissionPrefix + sorter)) {
                    return sorter;
                }
            } else if (sorter.equalsIgnoreCase(currentSorter)) {
                next = true;
                indexOfCurrent = i;
            }
        }
        
        for (int i = list.size() - 1; i > indexOfCurrent; i--) {
            String sorter = list.get(i);
            if (hasPermission(player, false, permissionPrefix + sorter)) {
                return sorter;
            }
        }
        
        return null;
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
            if (setSorter(player, args[1])) {
                TPortInventories.openMainTPortGUI(player);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport sort [sorter]");
        }
    }
    
    public static boolean setSorter(Player player, String sorterName) {
        Sorter sorter = getSorterExact(sorterName);
        
        if (sorter == null) {
            sendErrorTranslation(player, "tport.command.sort.sorter.notExist", sorterName);
            return false;
        }
        
        if (!hasPermission(player, true, permissionPrefix + sorterName)) {
            return false;
        }
        
        tportData.getConfig().set("tport." + player.getUniqueId() + ".sorter", sorterName);
        tportData.saveConfig();
        
        sendSuccessTranslation(player, "tport.command.sort.sorter.succeeded", sorterName);
        return true;
    }
    
    @FunctionalInterface
    public interface Sorter {
        List<ItemStack> sort(Player player, List<ItemFactory.HeadAttributes> attributes, @Nullable Object headData);
    }
}
