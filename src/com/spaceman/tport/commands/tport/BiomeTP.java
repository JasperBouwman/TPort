package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.CommandTemplate;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.biomeTP.Blacklist;
import com.spaceman.tport.commands.tport.biomeTP.Preset;
import com.spaceman.tport.commands.tport.biomeTP.SearchTries;
import com.spaceman.tport.commands.tport.biomeTP.Whitelist;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.metrics.BiomeSearchCounter;
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
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.events.InventoryClick.requestTeleportPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class BiomeTP extends SubCommand {
    
    private final EmptyCommand empty;
    
    public BiomeTP() {
        empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(textComponent("This command is used to open the BiomeTP GUI", infoColor));
        empty.setPermissions("TPort.biomeTP.open");
        
        addAction(empty);
        addAction(new Whitelist());
        addAction(new Blacklist());
        addAction(new Preset());
        addAction(new com.spaceman.tport.commands.tport.biomeTP.Random());
        addAction(new SearchTries());
    }
    
    public static void biomeTP(Player player, List<Biome> biomes) {
        biomeTP(player, biomes, false);
    }
    
    public static void biomeTP(Player player, List<Biome> biomes, boolean randomTP) {
        BiomeSearchCounter.add(biomes);
        
        if (!randomTP) {
            sendInfoTheme(player, "Searching for biome" + (biomes.size() == 1 ? "" : "s") + " %s, giving it %s tries...",
                    biomesToStringSearch(biomes, ColorTheme.getTheme(player)), String.valueOf(SearchTries.getBiomeSearches()));
        }
        
        Random random = new Random();
        int x = random.nextInt(6000000) - 3000000;
        int z = random.nextInt(6000000) - 3000000;
        Block b = player.getWorld().getBlockAt(x, 64, z);
        
        for (int i = 0; i < SearchTries.getBiomeSearches(); i++) {
            if (randomTP || biomes.contains(b.getBiome())) {
                Location l = FeatureTP.FeatureType.safeYSetter().setY(player.getWorld(), x, z);
                if (l == null) {
                    continue;
                }
                
                l.setPitch(player.getLocation().getPitch());
                l.setYaw(player.getLocation().getYaw());
                l.add(0.5, 0.1, 0.5);
                
                prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.BIOME, "biomeLoc", l,
                        "prevLoc", player.getLocation(), "biomeName", (randomTP ? "Random" : b.getBiome().name())));
    
                int finalI = i;
                Block finalB = b;
                requestTeleportPlayer(player, l, () -> {
                    if (randomTP) {
                        sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported to a random location, it took %s tries", String.valueOf(finalI));
                    } else {
                        sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported to biome %s, it took %s tries", finalB.getBiome().name(), String.valueOf(finalI));
                    }
                });
                int delay = Delay.delayTime(player);
                if (randomTP) {
                    if (delay == 0) {
                        sendSuccessTheme(player, "Successfully teleported to a random location, it took %s tries", String.valueOf(i));
                    } else {
                        sendSuccessTheme(player, "Successfully requested teleportation to a random location, it took %s tries, delay time is %s ticks", String.valueOf(i), delay);
                    }
                } else {
                    if (delay == 0) {
                        sendSuccessTheme(player, "Successfully teleported to biome %s, it took %s tries", b.getBiome().name(), String.valueOf(i));
                    } else {
                        sendSuccessTheme(player, "Successfully requested teleportation to biome %s, it took %s tries, delay time is %s ticks", b.getBiome().name(), String.valueOf(i), delay);
                    }
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
        if (randomTP) {
            sendErrorTheme(player, "Could not find an open spot in %s tries, try again", String.valueOf(SearchTries.getBiomeSearches()));
        } else {
            sendErrorTheme(player, "Could not find the biome" + (biomes.size() == 1 ? "" : "s") + " (or an open spot) %s in %s tries, try again",
                    biomesToStringError(biomes, ColorTheme.getTheme(player)), String.valueOf(SearchTries.getBiomeSearches()));
        }
    }
    
    private static String biomesToStringError(List<Biome> biomes, ColorTheme ct) {
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
    
    private static String biomesToStringSearch(List<Biome> biomes, ColorTheme ct) {
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
            if (!empty.hasPermissionToRun(player, true)) {
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
        private static final ArrayList<Preset> presets = new ArrayList<>();
        
        @SuppressWarnings("UnusedReturnValue")
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
                TPortInventories.addCommand(im, TPortInventories.Action.LEFT_CLICK, "biomeTP preset " + preset.getName());
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
            private final String name;
            private final List<Biome> biomes;
            private final boolean whitelist;
            private final Material material;
            
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
