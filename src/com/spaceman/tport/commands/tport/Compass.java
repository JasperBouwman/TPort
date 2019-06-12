package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Compass extends SubCommand {
    
    public Compass() {
        EmptyCommand emptyCommand1 = new EmptyCommand();
        emptyCommand1.setTabRunnable((args, player) -> {
            if (args[1].equalsIgnoreCase("TPort")) {
                ArrayList<String> list = new ArrayList<>();
                Files tportData = GettingFiles.getFile("TPortData");
                
                String argOneUUID = PlayerUUID.getPlayerUUID(args[2]);
                if (argOneUUID == null) {
                    ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(args[2]);
                    if (globalNames.size() == 1) {
                        argOneUUID = globalNames.get(0);
                    }
                }
                
                if (tportData.getConfig().contains("tport." + argOneUUID + ".items")) {
                    for (String s : tportData.getConfig().getConfigurationSection("tport." + argOneUUID + ".items").getKeys(false)) {
                        String name = tportData.getConfig().getString("tport." + argOneUUID + ".items." + s + ".name");
                        
                        if (tportData.getConfig().getString("tport." + argOneUUID + ".items." + s + ".private.statement").equals("true")) {
                            ArrayList<String> listTmp = (ArrayList<String>) tportData.getConfig().getStringList("tport." + argOneUUID + ".items." + s + ".private.players");
                            if (listTmp.contains(player.getUniqueId().toString())) {
                                list.add(name);
                            }
                        } else {
                            list.add(name);
                        }
                    }
                }
                
                return list;
            }
            return Collections.emptyList();
        });
        
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setTabRunnable((args, player) -> {
            if ("TPort".equalsIgnoreCase(args[1]) || "PLTP".equalsIgnoreCase(args[1])) {
                ArrayList<String> list = new ArrayList<>();
                Files tportData = GettingFiles.getFile("TPortData");
                for (String s : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {
                    list.add(PlayerUUID.getPlayerName(s));
                }
                return list;
            } else if ("biomeTP".equalsIgnoreCase(args[1])) {
                ArrayList<String> list = new ArrayList<>();
                for (Biome biome : Biome.values()) {
                    list.add(biome.name());
                }
                list.add("random");
                return list;
            } else if ("featureTP".equalsIgnoreCase(args[1])) {
                ArrayList<String> list = new ArrayList<>();
                Arrays.stream(TPortInventories.FeatureTypes.values()).map(TPortInventories.FeatureTypes::name).forEach(list::add);
                return list;
            }
            return Collections.emptyList();
        });
        emptyCommand.addAction(emptyCommand1);
        addAction(emptyCommand);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return Arrays.stream(CompassType.values()).map(CompassType::getDisplayName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport compass <type> [data]
        
        if (!Permissions.hasPermission(player, "TPort.command.compass.create", false)) {
            if (!Permissions.hasPermission(player, "TPort.compass.create", false)) {
                Permissions.sendNoPermMessage(player, "TPort.command.compass.create", "TPort.compass.create");
                return;
            }
        }
        
        /*
         * Compass
         *
         * TPort Compass
         * Type: <type>
         * Data: <data...>
         *
         * */
        
        /*
         * /tport compass <type> [data]
         *
         * /tport compass TPort [player] [TPort]
         * /tport compass biomeTP [biome]
         * /tport compass featureTP [featureType]
         * /tport compass back
         * /tport compass PLTP <player>
         * */
        
        
        if (args.length == 1) {
            player.sendMessage(ChatColor.RED + "Usage: " + ChatColor.DARK_RED + "/tport compass <type> [data...]");
        } else if (args.length > 1) {
            CompassType type = CompassType.getType(args[1]);
            
            if (type == null) {
                player.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a valid compass type");
                return;
            }
            
            if (type.getArgsMax() < args.length - 2 || type.getArgsMin() > args.length - 2) {
                player.sendMessage("Compass type " + type.getDisplayName() + " does not support " + (args.length - 2) + " arguments");
                return;
            }
            
            ItemStack is = new ItemStack(Material.COMPASS);
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                ArrayList<String> lore = new ArrayList<>();
                
                lore.add(ChatColor.DARK_AQUA + "TPort Compass");
                lore.add("");
                lore.add(ChatColor.GRAY + "Type: " + type.getDisplayName());
                
                for (int i = 0; i < args.length - 2; i++) {
                    lore.add(ChatColor.GRAY + type.getArgNames()[i] + ": " + args[i + 2]);
                }
                
                im.setLore(lore);
                
                is.setItemMeta(im);
            }
            
            for (ItemStack item : player.getInventory().addItem(is).values()) {
                player.getWorld().dropItem(player.getLocation(), item);
            }
            
            player.sendMessage(ChatColor.BLUE + "Right-click with the with compass to use it");
        } else {
            player.sendMessage(ChatColor.RED + "Use: " + ChatColor.DARK_RED + "/tport compass <type> [data]");
        }
    }
    
    public enum CompassType {
        TPORT("TPort", 0, 2, "Player", "TPort"),
        PLTP("PLTP", 1, 1, "Player"),
        BIOME_TP("biomeTP", 0, 1, "Biome"),
        FEATURE_TP("featureTP", 0, 1, "Feature"),
        BACK("back", 0, 0);
        
        private String displayName;
        private int argsMax;
        private int argsMin;
        private String[] argNames;
        
        CompassType(String displayName, int argsMin, int argsMax, String... argNames) {
            this.displayName = displayName;
            this.argsMax = argsMax;
            this.argsMin = argsMin;
            this.argNames = argNames;
        }
        
        public static CompassType getType(String type) {
            for (CompassType compassType : CompassType.values()) {
                if (compassType.name().replace("_", "").equalsIgnoreCase(type)) {
                    return compassType;
                }
            }
            return null;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public int getArgsMax() {
            return argsMax;
        }
        
        public int getArgsMin() {
            return argsMin;
        }
        
        public String[] getArgNames() {
            return argNames;
        }
    }
}
