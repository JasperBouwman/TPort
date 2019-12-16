package com.spaceman.tport.commands.tport;

import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Compass extends SubCommand {
    
    public Compass() {
        EmptyCommand emptyCommand1 = new EmptyCommand();
        emptyCommand1.setCommandName("data...", ArgumentType.OPTIONAL);
        emptyCommand1.setCommandDescription(textComponent("This command is used when you need to add extra data to your TPort compass", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.compass.create", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        emptyCommand1.setTabRunnable((args, player) -> {
            if (args[1].equalsIgnoreCase("TPort")) {
                ArrayList<String> list = new ArrayList<>();
                
                UUID argOneUUID = PlayerUUID.getPlayerUUID(args[2]);
                if (argOneUUID == null) {
                    return Collections.emptyList();
                }
    
                for (TPort tport : TPortManager.getTPortList(argOneUUID)) {
                    if (tport.hasAccess(player)) {
                        list.add(tport.getName());
                    }
                }
                
                return list;
            }
            return Collections.emptyList();
        });
        
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("type", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(textComponent("This command is used to create TPort compasses. " +
                "When right-clicking with a TPort compass you will trigger a certain action. " +
                "TPort compasses will work when they are in a item frame, to rotate a TPort compass in an item frame you have to sneak", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.compass.create", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        emptyCommand.setTabRunnable((args, player) -> {
            if ("TPort".equalsIgnoreCase(args[1]) || "PLTP".equalsIgnoreCase(args[1])) {
                ArrayList<String> list = new ArrayList<>();
                Files tportData = GettingFiles.getFile("TPortData");
                for (String s : tportData.getKeys("tport")) {
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
                Arrays.stream(TPortInventories.FeatureType.values()).map(TPortInventories.FeatureType::name).forEach(list::add);
                return list;
            } else if ("Public".equalsIgnoreCase(args[1])) {
                ArrayList<String> list = new ArrayList<>();
    
                Files tportData = GettingFiles.getFile("TPortData");
                for (String publicTPortSlot : tportData.getKeys("public.tports")) {
                    String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
                    TPort tport = getTPort(UUID.fromString(tportID));
                    if (tport != null) {
                        list.add(tport.getName());
                    }
                }
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
        //tport compass <type> [data...]
        
        if (!hasPermission(player, true, true, "TPort.compass.create")) {
            return;
        }
        
        /*
         * Compass:
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
         * /tport compass home
         * /tport compass public [tport]
         * */
        
        
        if (args.length == 1) {
            sendErrorTheme(player, "Usage: %s", "/tport compass <type> [data...]");
        } else if (args.length > 1) {
            CompassType type = CompassType.getType(args[1]);
            
            if (type == null) {
                sendErrorTheme(player, "%s is not a valid compass type", args[1]);
                return;
            }
            
            if (type.getArgsMax() < args.length - 2 || type.getArgsMin() > args.length - 2) {
                sendErrorTheme(player, "Compass type %s does not support %s arguments", type.getDisplayName(), String.valueOf(args.length - 2));
                return;
            }
            
            ItemStack is = player.getInventory().getItemInMainHand();
            if (!is.getType().equals(Material.COMPASS)) {
                sendErrorTheme(player, "You need to hold a compass to turn it into a TPort Compass");
                return;
            }
            
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                im = Bukkit.getItemFactory().getItemMeta(is.getType());
            }
            if (im == null) {
                sendErrorTheme(player, "Could not turn it into a TPort Compass");
                return;
            }
            ArrayList<String> lore = new ArrayList<>();
            
            lore.add(ChatColor.DARK_AQUA + "TPort Compass");
            lore.add("");
            lore.add(ChatColor.GRAY + "Type: " + type.getDisplayName());
            
            for (int i = 0; i < args.length - 2; i++) {
                lore.add(ChatColor.GRAY + type.getArgNames()[i] + ": " + args[i + 2]);
            }
            im.setLore(lore);
            is.setItemMeta(im);

            sendInfoTheme(player, "Right-click with the compass to use it");
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport compass <type> [data...]");
        }
    }
    
    public enum CompassType {
        TPORT("TPort", 0, 2, "Player", "TPort"),
        PLTP("PLTP", 1, 1, "Player"),
        BIOME_TP("biomeTP", 0, 1, "Biome"),
        FEATURE_TP("featureTP", 0, 1, "Feature"),
        BACK("back", 0, 0),
        HOME("home", 0, 0),
        PUBLIC("Public", 0, 1, "Public TPort");
        
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
