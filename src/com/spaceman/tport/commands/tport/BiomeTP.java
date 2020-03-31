package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.CommandTemplate;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.biomeTP.Blacklist;
import com.spaceman.tport.commands.tport.biomeTP.Preset;
import com.spaceman.tport.commands.tport.biomeTP.SearchTries;
import com.spaceman.tport.commands.tport.biomeTP.Whitelist;
import com.spaceman.tport.cooldown.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.spaceman.tport.Main.replaceLast;
import static com.spaceman.tport.TPortInventories.openBiomeTP;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.events.InventoryClick.requestTeleportPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class BiomeTP extends SubCommand {
    
    public BiomeTP() {
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(textComponent("This command is used to open the BiomeTP GUI", infoColor));

        addAction(empty);
        
        addAction(new Whitelist());
        addAction(new Blacklist());
        addAction(new Preset());
        addAction(new com.spaceman.tport.commands.tport.biomeTP.Random());
        addAction(new SearchTries());
    }
    
    public static void biomeTP(Player player, List<Biome> biomes) {
        
        sendInfoTheme(player, "Searching for biome" + (biomes.size() == 1 ? "" : "s") + " %s, giving it %s tries...",
                biomesToString2(biomes, ColorTheme.getTheme(player)), String.valueOf(SearchTries.getBiomeSearches()));
        
        Random random = new Random();
        int x = random.nextInt(6000000) - 3000000;
        int z = random.nextInt(6000000) - 3000000;
        Block b = player.getWorld().getBlockAt(x, 64, z);
        
        biomeSearch:
        for (int i = 0; i < SearchTries.getBiomeSearches(); i++) {
            if (biomes.contains(b.getBiome())) {
                Location l;
                ySearch:
                switch (b.getWorld().getEnvironment()) {
                    case NETHER:
                        for (int y = 1; y < player.getWorld().getMaxHeight(); y++) {
                            Location tempFeet = new Location(player.getWorld(), x, y, z);
                            Location tempHead = new Location(player.getWorld(), x, y + 1, z);
                            Location tempLava = new Location(player.getWorld(), x, y - 1, z);
                            
                            if (!tempLava.getBlock().getType().equals(Material.AIR) &&
                                    !tempLava.getBlock().getType().equals(Material.LAVA) &&
                                    !tempLava.getBlock().getType().equals(Material.FIRE) &&
                                    (tempFeet.getBlock().getType().equals(Material.AIR) || tempFeet.getBlock().getType().equals(Material.CAVE_AIR)) &&
                                    (tempHead.getBlock().getType().equals(Material.AIR) || tempHead.getBlock().getType().equals(Material.CAVE_AIR))) {
                                l = tempFeet;
                                break ySearch;
                            } else if (tempFeet.getBlock().getType().equals(Material.BEDROCK) && y > 5) {
                                //break search, get new location
                                break;
                            }
                        }
                        
                        x = random.nextInt(6000000) - 3000000;
                        z = random.nextInt(6000000) - 3000000;
                        b = player.getWorld().getBlockAt(x, 64, z);
                        continue biomeSearch;
                    default:
                    case NORMAL:
                        l = player.getWorld().getHighestBlockAt(x, z).getLocation();
                        break;
                    case THE_END:
                        l = player.getWorld().getHighestBlockAt(x, z).getLocation();
                        if (l.getY() == 0) {
                            x = random.nextInt(6000000) - 3000000;
                            z = random.nextInt(6000000) - 3000000;
                            b = player.getWorld().getBlockAt(x, 64, z);
                            continue biomeSearch;
                        }
                        break;
                }
                
                l.setPitch(player.getLocation().getPitch());
                l.setYaw(player.getLocation().getYaw());
                l.add(0.5, 0.1, 0.5);
                
                prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.BIOME, "biomeLoc", l,
                        "prevLoc", player.getLocation(), "biomeName", b.getBiome().name()));
                
                requestTeleportPlayer(player, l);
                
                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported to biome %s", b.getBiome().name());
                } else {
                    sendSuccessTheme(player, "Successfully requested teleportation to biome %s", b.getBiome().name());
                }
                CooldownManager.BiomeTP.update(player);
                return;
            } else {
                x = random.nextInt(6000000) - 3000000;
                z = random.nextInt(6000000) - 3000000;
                b = player.getWorld().getBlockAt(x, 64, z);
            }
        }
        CooldownManager.BiomeTP.update(player);
        sendErrorTheme(player, "Could not find the biome" + (biomes.size() == 1 ? "" : "s") + " (or an open spot) %s in %s tries, try again",
                biomesToString(biomes, ColorTheme.getTheme(player)), String.valueOf(SearchTries.getBiomeSearches()));
    }
    
    private static String biomesToString(List<Biome> biomes, ColorTheme ct) {
        StringBuilder str = new StringBuilder();
        boolean color = true;
        int i;
        for (i = 0; i < biomes.size() - 1; i++) {
            str.append(color ? ct.getVarErrorColor() : ct.getVarError2Color()).append(biomes.get(i));
            str.append(ct.getErrorColor()).append(", ");
            color = !color;
        }
        str.append(color ? ct.getVarErrorColor() : ct.getVarError2Color()).append(biomes.get(i));
        return replaceLast(str.toString(), ",", " or");
    }
    
    private static String biomesToString2(List<Biome> biomes, ColorTheme ct) {
        StringBuilder str = new StringBuilder();
        boolean color = true;
        int i;
        for (i = 0; i < biomes.size() - 1; i++) {
            str.append(color ? ct.getVarInfoColor() : ct.getVarInfo2Color()).append(biomes.get(i));
            str.append(ct.getInfoColor()).append(", ");
            color = !color;
        }
        str.append(color ? ct.getVarInfoColor() : ct.getVarInfo2Color()).append(biomes.get(i));
        return replaceLast(str.toString(), ",", " and");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP
        // tport biomeTP whitelist <biome...>
        // tport biomeTP blacklist <biome...>
        // tport biomeTP preset [preset]
        // tport biomeTP random
        // tport biomeTP searchTries [tries]
        
        if (args.length == 1) {
            if (!hasPermission(player, "TPort.biomeTP.open", "TPort.biomeTP.all")) {
                return;
            }
            openBiomeTP(player, 0);
        } else {
            if (!runCommands(getActions(), args[1], args, player)) {
                sendErrorTheme(player, "Usage: %s", "/tport biomeTP " + CommandTemplate.convertToArgs(getActions(), true));
            }
        }
    }
    
    public static class BiomeTPPresets {
        private static ArrayList<Preset> presets = new ArrayList<>();
        
        public static boolean registerPreset(String name, List<Biome> biomes, boolean whitelist, Material material) {
            if (Main.containsSpecialCharacter(name)) {
                return false;
            }
            if (presets.stream().map(Preset::getName).noneMatch(n -> n.equalsIgnoreCase(name))) {
                presets.add(new Preset(name, biomes, whitelist, material));
                return true;
            }
            return false;
        }
        
        public static List<String> getNames() {
            return presets.stream().map(Preset::getName).collect(Collectors.toList());
        }
        
        public static List<ItemStack> getItems(Player player) {
            return getItems(ColorTheme.getTheme(player));
        }
        
        public static List<ItemStack> getItems(ColorTheme ct) {
            ArrayList<ItemStack> items = new ArrayList<>();
            for (Preset preset : presets) {
                ItemStack is = new ItemStack(preset.getMaterial());
                ItemMeta im = is.getItemMeta();
                if (im == null) {
                    im = Bukkit.getItemFactory().getItemMeta(is.getType());
                }
                if (im == null) {
                    continue;
                }
                
                im.setDisplayName(ct.getVarInfoColor() + preset.getName());
                
                List<String> lore = new ArrayList<>();
                lore.add("");
                if (preset.isWhitelist()) {
                    lore.add(ct.getInfoColor() + "This is a " + ct.getVarInfoColor() + "whitelist" + ct.getInfoColor() + " with the biomes: ");
                } else {
                    lore.add(ct.getInfoColor() + "This is a " + ct.getVarInfoColor() + "blacklist" + ct.getInfoColor() + " with the biomes: ");
                }
                
                int width = 2;
                int i;
                for (i = 0; i < preset.getBiomes().size() - width; i += width) {
                    lore.add(ct.getVarInfoColor() + preset.getBiomes().subList(i, i + width).
                            stream().map(Enum::name).collect(Collectors.joining(ct.getInfoColor() + ", " + ct.getVarInfo2Color())) + ct.getInfoColor() + ",");
                }
                lore.add(ct.getVarInfoColor() + preset.getBiomes().subList(i, i + Math.min(width, preset.getBiomes().size() - i))
                        .stream().map(Enum::name).collect(Collectors.joining(ct.getInfoColor() + ", " + ct.getVarInfo2Color())));
                
                im.setLore(lore);
                is.setItemMeta(im);
                items.add(is);
            }
            
            return items;
        }
        
        public static Preset getPreset(String name) {
            for (Preset preset : presets) {
                if (preset.getName().equalsIgnoreCase(name)) {
                    return preset;
                }
            }
            return null;
        }
        
        public static class Preset {
            private String name;
            private List<Biome> biomes;
            private boolean whitelist;
            private Material material;
            
            private Preset(String name, List<Biome> biomes, boolean whitelist, Material material) {
                this.name = name;
                this.biomes = biomes;
                this.whitelist = whitelist;
                this.material = material;
            }
            
            public List<Biome> getBiomes() {
                return biomes;
            }
            
            public Material getMaterial() {
                return material;
            }
            
            public String getName() {
                return name;
            }
            
            public boolean isWhitelist() {
                return whitelist;
            }
        }
    }
}
