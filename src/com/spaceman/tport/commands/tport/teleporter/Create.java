package com.spaceman.tport.commands.tport.teleporter;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.commands.tport.Teleporter;
import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.commands.tport.publc.Open;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Create extends SubCommand {
    
    public Create() {
        
        EmptyCommand emptyTPortPlayerTPort = new EmptyCommand();
        emptyTPortPlayerTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyTPortPlayerTPort.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        EmptyCommand emptyTPortPlayer = new EmptyCommand();
        emptyTPortPlayer.setCommandName("Player", ArgumentType.OPTIONAL);
        emptyTPortPlayer.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        emptyTPortPlayer.setTabRunnable(((args, player) -> {
            UUID otherUUID = PlayerUUID.getPlayerUUID(args[3]);
            if (otherUUID == null) {
                return Collections.emptyList();
            }
            return TPortManager.getTPortList(otherUUID).stream()
                    .map(TPort::getName)
                    .collect(Collectors.toList());
        }));
        emptyTPortPlayer.addAction(emptyTPortPlayerTPort);
        EmptyCommand emptyTPort = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyTPort.setCommandName("TPort", ArgumentType.FIXED);
        emptyTPort.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        emptyTPort.setTabRunnable((args, player) -> GettingFiles.getFile("TPortData").getKeys("tport").stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList()));
        emptyTPort.addAction(emptyTPortPlayer);
        
        EmptyCommand emptyPLTPPlayer = new EmptyCommand();
        emptyPLTPPlayer.setCommandName("Player", ArgumentType.REQUIRED);
        emptyPLTPPlayer.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        EmptyCommand emptyPLTP = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyPLTP.setCommandName("PLTP", ArgumentType.FIXED);
        emptyPLTP.setTabRunnable(((args, player) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())));
        emptyPLTP.addAction(emptyPLTPPlayer);
        
        EmptyCommand emptyBiomeTPEmpty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        emptyBiomeTPEmpty.setCommandName("", ArgumentType.FIXED);
        emptyBiomeTPEmpty.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        EmptyCommand emptyBiomeTPWhiteListBiome = new EmptyCommand();
        emptyBiomeTPWhiteListBiome.setCommandName("biome", ArgumentType.REQUIRED);
        emptyBiomeTPWhiteListBiome.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        emptyBiomeTPWhiteListBiome.setTabRunnable(((args, player) -> {
            List<String> biomeList = Arrays.asList(args).subList(4, args.length).stream().map(String::toUpperCase).collect(Collectors.toList());
            return Arrays.stream(Biome.values()).filter(biome -> !biomeList.contains(biome.name())).map(Enum::name).collect(Collectors.toList());
        }));
        emptyBiomeTPWhiteListBiome.setLooped(true);
        EmptyCommand emptyBiomeTPWhitelist = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyBiomeTPWhitelist.setCommandName("Whitelist", ArgumentType.FIXED);
        emptyBiomeTPWhitelist.setTabRunnable(emptyBiomeTPWhiteListBiome.getTabRunnable());
        emptyBiomeTPWhitelist.addAction(emptyBiomeTPWhiteListBiome);
        EmptyCommand emptyBiomeTPBlackListBiome = new EmptyCommand();
        emptyBiomeTPBlackListBiome.setCommandName("biome", ArgumentType.REQUIRED);
        emptyBiomeTPBlackListBiome.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        emptyBiomeTPBlackListBiome.setTabRunnable(((args, player) -> emptyBiomeTPWhiteListBiome.tabList(player, args)));
        emptyBiomeTPBlackListBiome.setLooped(true);
        EmptyCommand emptyBiomeTPBlacklist = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyBiomeTPBlacklist.setCommandName("Blacklist", ArgumentType.FIXED);
        emptyBiomeTPBlacklist.setTabRunnable(((args, player) -> emptyBiomeTPWhiteListBiome.tabList(player, args)));
        emptyBiomeTPBlacklist.addAction(emptyBiomeTPBlackListBiome);
        EmptyCommand emptyBiomeTPPresetPreset = new EmptyCommand();
        emptyBiomeTPPresetPreset.setCommandName("Preset", ArgumentType.OPTIONAL);
        emptyBiomeTPPresetPreset.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        EmptyCommand emptyBiomeTPPreset = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyBiomeTPPreset.setCommandName("Preset", ArgumentType.FIXED);
        emptyBiomeTPPreset.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        emptyBiomeTPPreset.setTabRunnable(((args, player) -> BiomeTP.BiomeTPPresets.getNames()));
        emptyBiomeTPPreset.addAction(emptyBiomeTPPresetPreset);
        EmptyCommand emptyBiomeTPRandom = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyBiomeTPRandom.setCommandName("Random", ArgumentType.FIXED);
        emptyBiomeTPRandom.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        EmptyCommand emptyBiomeTP = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyBiomeTP.setCommandName("BiomeTP", ArgumentType.FIXED);
        emptyBiomeTP.addAction(emptyBiomeTPEmpty);
        emptyBiomeTP.addAction(emptyBiomeTPWhitelist);
        emptyBiomeTP.addAction(emptyBiomeTPBlacklist);
        emptyBiomeTP.addAction(emptyBiomeTPPreset);
        emptyBiomeTP.addAction(emptyBiomeTPRandom);
        
        EmptyCommand emptyFeatureTPFeatureMode = new EmptyCommand();
        emptyFeatureTPFeatureMode.setCommandName("Mode", ArgumentType.OPTIONAL);
        emptyFeatureTPFeatureMode.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        EmptyCommand emptyFeatureTPFeature = new EmptyCommand();
        emptyFeatureTPFeature.setCommandName("Feature", ArgumentType.OPTIONAL);
        emptyFeatureTPFeature.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        emptyFeatureTPFeature.setTabRunnable(((args, player) -> Arrays.stream(FeatureTP.FeatureTPMode.values()).map(Enum::name).collect(Collectors.toList())));
        emptyFeatureTPFeature.addAction(emptyFeatureTPFeatureMode);
        EmptyCommand emptyFeatureTP = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyFeatureTP.setCommandName("FeatureTP", ArgumentType.FIXED);
        emptyFeatureTP.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        emptyFeatureTP.setTabRunnable(((args, player) -> Arrays.stream(FeatureTP.FeatureType.values()).map(FeatureTP.FeatureType::name).collect(Collectors.toList())));
        emptyFeatureTP.addAction(emptyFeatureTPFeature);
        
        EmptyCommand emptyHome = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyHome.setCommandName("Home", ArgumentType.FIXED);
        emptyHome.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        
        EmptyCommand emptyBack = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyBack.setCommandName("Back", ArgumentType.FIXED);
        emptyBack.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        
        EmptyCommand emptyPublicTPort = new EmptyCommand();
        emptyPublicTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyPublicTPort.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        EmptyCommand emptyPublic = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyPublic.setCommandName("Public", ArgumentType.FIXED);
        emptyPublic.setCommandDescription(textComponent("This command is used to create a Teleporter", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.teleporter.create", ColorTheme.ColorType.varInfoColor));
        emptyPublic.setTabRunnable(((args, player) -> {
            Files tportData = GettingFiles.getFile("TPortData");
            return tportData.getKeys("public.tports").stream()
                    .map(publicTPortSlot -> tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString()))
                    .map(tportID -> getTPort(UUID.fromString(tportID)))
                    .filter(Objects::nonNull)
                    .map(TPort::getName)
                    .collect(Collectors.toList());
        }));
        emptyPublic.addAction(emptyPublicTPort);
        
        addAction(emptyTPort);
        addAction(emptyPLTP);
        addAction(emptyBiomeTP);
        addAction(emptyFeatureTP);
        addAction(emptyHome);
        addAction(emptyBack);
        addAction(emptyPublic);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport teleporter create <type> [data...]
        
        if (!hasPermission(player, true, true, "TPort.teleporter.create")) {
            return;
        }
        
        /*
         * /tport teleporter create <type> [data]
         *
         * /tport teleporter create TPort [player] [TPort name]
         * /tport teleporter create biomeTP whitelist <biome...>
         * /tport teleporter create biomeTP blacklist <biome...>
         * /tport teleporter create biomeTP preset [preset]
         * /tport teleporter create biomeTP random
         * /tport teleporter create biomeTP
         * /tport teleporter create featureTP [featureType] [mode]
         * /tport teleporter create back
         * /tport teleporter create PLTP <player>
         * /tport teleporter create home
         * /tport teleporter create public [TPort name]
         * */
        
        if (args.length == 1) {
            sendErrorTheme(player, "Usage: %s", "/tport teleporter create <type> [data...]");
        } else if (args.length > 2) {
            ItemStack is = player.getInventory().getItemInMainHand();
            if (is.getType().isAir()) {
                sendErrorTheme(player, "You must hold an item in your main hand to turn it into a TPort Teleporter");
                return;
            }
            if (is.getType().isEdible()) {
                sendErrorTheme(player, "You can't turn an edible into a TPort Teleporter");
                return;
            }
            Files tportData = getFile("TPortData");
            
            if (args[2].equalsIgnoreCase("TPort")) {
                String newPlayerName = null;
                String tportName = null;
                UUID newPlayerUUID = null;
                if (args.length > 3) {
                    Pair<String, UUID> profile = PlayerUUID.getProfile(args[3]);
                    newPlayerName = profile.getLeft();
                    newPlayerUUID = profile.getRight();
                    if (newPlayerUUID == null || !tportData.getConfig().contains("tport." + newPlayerUUID)) {
                        sendErrorTheme(player, "Could not find a player named %s", args[3]);
                        return;
                    }
                }
                if (args.length > 4) {
                    TPort tport = TPortManager.getTPort(newPlayerUUID, args[4]);
                    if (tport == null) {
                        sendErrorTheme(player, "No TPort found called %s", args[4]);
                        return;
                    }
                    tportName = tport.getName();
                }
                createTeleporter(player, "TPort",
                        (newPlayerName == null ? "" : "open " + newPlayerName) + " " + Main.getOrDefault(tportName, ""),
                        new Pair<>("Player", newPlayerName), new Pair<>("TPort", tportName));
            }
            else if (args[2].equalsIgnoreCase("BiomeTP")) {
                if (args.length == 3) {
                    createTeleporter(player, "BiomeTP", "BiomeTP");
                    return;
                }
                if (args[3].equalsIgnoreCase("Whitelist")) {
                    if (args.length > 4) {
                        
                        ArrayList<String> whitelist = new ArrayList<>();
                        for (int i = 4; i < args.length; i++) {
                            String biomeName = args[i].toUpperCase();
                            Biome biome;
                            try {
                                biome = Biome.valueOf(biomeName);
                            } catch (IllegalArgumentException iae) {
                                sendErrorTheme(player, "Biome %s does not exist", biomeName);
                                return;
                            }
                            if (whitelist.contains(biome.name())) {
                                sendErrorTheme(player, "Biome %s is already in your whitelist", biomeName);
                                return;
                            }
                            
                            whitelist.add(biome.name());
                        }
                        
                        StringBuilder str = new StringBuilder();
                        str.append("[");
                        final int width = 2;
                        int currWidth = 0;
                        for (int i = 0; i < whitelist.size(); i++) {
                            str.append(whitelist.get(i)).append(", ");
                            currWidth++;
                            if (currWidth == width && i + 1 < whitelist.size()) {
                                currWidth = 0;
                                str.append("\n          ");
                            }
                        }
                        
                        String biomes = Main.replaceLast(str.toString(), ", ", "") + "]";
                        
                        createTeleporter(player, "BiomeTP", "BiomeTP blacklist " + biomes.replace(",", "").replace("[", "").replace("]", ""),
                                new Pair<>("SubType", "Whitelist"), new Pair<>("Biomes", biomes));
                    } else {
                        sendErrorTheme(player, "Usage: %s", "/tport teleporter BiomeTP whitelist <biome...>");
                    }
                } else if (args[3].equalsIgnoreCase("Blacklist")) {
                    if (args.length > 4) {
                        
                        ArrayList<String> blacklist = new ArrayList<>();
                        for (int i = 4; i < args.length; i++) {
                            String biomeName = args[i].toUpperCase();
                            Biome biome;
                            try {
                                biome = Biome.valueOf(biomeName);
                            } catch (IllegalArgumentException iae) {
                                sendErrorTheme(player, "Biome %s does not exist", biomeName);
                                return;
                            }
                            if (blacklist.contains(biome.name())) {
                                sendErrorTheme(player, "Biome %s is already in your blacklist", biomeName);
                                return;
                            }
                            
                            blacklist.add(biome.name());
                        }
                        
                        StringBuilder str = new StringBuilder();
                        str.append("[");
                        final int width = 2;
                        int currWidth = 0;
                        for (int i = 0; i < blacklist.size(); i++) {
                            str.append(blacklist.get(i)).append(", ");
                            currWidth++;
                            if (currWidth == width && i + 1 < blacklist.size()) {
                                currWidth = 0;
                                str.append("\n          ");
                            }
                        }
                        
                        String biomes = Main.replaceLast(str.toString(), ", ", "") + "]";
                        
                        createTeleporter(player, "BiomeTP", "BiomeTP blacklist " + biomes.replace(",", "").replace("[", "").replace("]", "")
                                , new Pair<>("SubType", "Blacklist"), new Pair<>("Biomes", biomes));
                    } else {
                        sendErrorTheme(player, "Usage: %s", "/tport teleporter BiomeTP blacklist <biome...>");
                    }
                } else if (args[3].equalsIgnoreCase("Preset")) {
                    if (args.length == 4) {
                        createTeleporter(player, "BiomeTP", "BiomeTP preset", new Pair<>("SubType", "Preset"));
                    } else {
                        BiomeTP.BiomeTPPresets.Preset preset = BiomeTP.BiomeTPPresets.getPreset(args[4]);
                        if (preset != null) {
                            createTeleporter(player, "BiomeTP", "BiomeTP preset " + preset.getName()
                                    , new Pair<>("SubType", "Preset"), new Pair<>("Preset", preset.getName()));
                        } else {
                            sendErrorTheme(player, "Preset %s does not exist", args[3]);
                        }
                    }
                } else if (args[3].equalsIgnoreCase("Random")) {
                    createTeleporter(player, "BiomeTP", "BiomeTP random", new Pair<>("SubType", "Random"));
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport teleporter BiomeTP [Whitelist|Blacklist|Preset|Random]");
                }
            }
            else if (args[2].equalsIgnoreCase("FeatureTP")) {
                
                String type = null;
                String mode = null;
                
                if (args.length > 3) {
                    try {
                        type = FeatureTP.FeatureType.valueOf(args[3]).name();
                        mode = FeatureTP.FeatureTPMode.CLOSEST.name();
                    } catch (IllegalArgumentException | NullPointerException error) {
                        sendErrorTheme(player, "Feature %s does not exist", args[3]);
                        return;
                    }
                }
                if (args.length > 4) {
                    try {
                        mode = FeatureTP.FeatureTPMode.valueOf(args[4].toUpperCase()).name();
                    } catch (IllegalArgumentException | NullPointerException error) {
                        sendErrorTheme(player, "FeatureTP mode %s does not exist", args[4]);
                        return;
                    }
                }
                createTeleporter(player, "FeatureTP",
                        "FeatureTP " + Main.getOrDefault(type, "") + " " + Main.getOrDefault(mode, ""),
                        new Pair<>("Feature", type), new Pair<>("Mode", mode));
            }
            else if (args[2].equalsIgnoreCase("Back")) {
                createTeleporter(player, "Back", "back");
            }
            else if (args[2].equalsIgnoreCase("PLTP")) {
                if (args.length > 3) {
                    Pair<String, UUID> profile = PlayerUUID.getProfile(args[3]);
                    String newPlayerName = profile.getLeft();
                    UUID newPlayerUUID = PlayerUUID.getPlayerUUID(args[3]);
                    if (newPlayerUUID == null || !tportData.getConfig().contains("tport." + newPlayerUUID)) {
                        sendErrorTheme(player, "Could not find a player named %s", args[3]);
                        return;
                    }
                    createTeleporter(player, "PLTP", "PLTP " + newPlayerName, new Pair<>("Player", newPlayerName));
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport teleporter create PLTP <player>");
                }
            }
            else if (args[2].equalsIgnoreCase("Home")) {
                createTeleporter(player, "Home", "home");
            }
            else if (args[2].equalsIgnoreCase("Public")) {
                String tportName = null;
                if (args.length > 3) {
                    TPort tport = Open.getPublicTPort(args[3]);
                    if (tport == null) {
                        sendErrorTheme(player, "No public TPort found called %s", args[3]);
                        return;
                    }
                    tportName = tport.getName();
                }
                createTeleporter(player, "Public", "public " + tportName, new Pair<>("TPort", tportName));
            }
            else {
                sendErrorTheme(player, "%s is not a Teleporter type", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport teleporter create <type> [data...]");
        }
    }
    
    @SafeVarargs
    private final void createTeleporter(Player player, String type, String command, Pair<String, String>... pairs) {
        ItemStack is = player.getInventory().getItemInMainHand();
        Teleporter.removeTeleporter(is);
        ItemMeta im = is.getItemMeta();
    
        if (im == null) {
            im = Bukkit.getItemFactory().getItemMeta(is.getType());
        }
        if (im == null) {
            sendErrorTheme(player, "Could not turn it into a TPort Teleporter");
            return;
        }
        List<String> lore = im.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        
        
        int size = 3;
        
        lore.add(Teleporter.teleporterTitle);
        lore.add("");
        lore.add(ChatColor.GRAY + "Type: " + type);
        
        for (Pair<String, String> pair : pairs) {
            if (pair.getRight() != null) {
                String[] split = pair.getRight().split("\n");
                for (int i = 0; i < split.length; i++) {
                    String part = split[i];
                    lore.add(ChatColor.GRAY + (i == 0 ? pair.getLeft() + ": " : "") + part);
                    size++;
                }
            }
        }
        
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "teleporterCommand"), PersistentDataType.STRING, StringUtils.normalizeSpace(command));
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "teleporterSize"), PersistentDataType.INTEGER, size);
        
        im.setLore(lore);
        is.setItemMeta(im);
    }
}
