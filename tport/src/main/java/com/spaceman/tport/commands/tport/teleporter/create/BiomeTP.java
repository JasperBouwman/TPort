package com.spaceman.tport.commands.tport.teleporter.create;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.spaceman.tport.commands.tport.teleporter.Create.createTeleporter;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class BiomeTP extends SubCommand {
    
    private final EmptyCommand emptyBiomeTPEmpty;
    
    public BiomeTP() {
        emptyBiomeTPEmpty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        emptyBiomeTPEmpty.setCommandName("", ArgumentType.FIXED);
        emptyBiomeTPEmpty.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.biomeTP.commandDescription"));
        emptyBiomeTPEmpty.setPermissions("TPort.teleporter.create");
        
        EmptyCommand emptyBiomeTPWhiteListBiome = new EmptyCommand();
        emptyBiomeTPWhiteListBiome.setCommandName("biome", ArgumentType.REQUIRED);
        emptyBiomeTPWhiteListBiome.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.biomeTP.whitelist.commandDescription"));
        emptyBiomeTPWhiteListBiome.setTabRunnable(((args, player) -> {
            List<String> biomeList = Arrays.asList(args).subList(4, args.length).stream().toList();
            return com.spaceman.tport.commands.tport.BiomeTP.availableBiomes(player.getWorld()).stream().filter(name -> biomeList.stream().noneMatch(name::equalsIgnoreCase)).toList();
        }));
        emptyBiomeTPWhiteListBiome.setLooped(true);
        emptyBiomeTPWhiteListBiome.setPermissions("TPort.teleporter.create");
        EmptyCommand emptyBiomeTPWhitelist = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyBiomeTPWhitelist.setCommandName("whitelist", ArgumentType.FIXED);
        emptyBiomeTPWhitelist.setTabRunnable(emptyBiomeTPWhiteListBiome.getTabRunnable());
        emptyBiomeTPWhitelist.addAction(emptyBiomeTPWhiteListBiome);
        
        EmptyCommand emptyBiomeTPBlackListBiome = new EmptyCommand();
        emptyBiomeTPBlackListBiome.setCommandName("biome", ArgumentType.REQUIRED);
        emptyBiomeTPBlackListBiome.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.biomeTP.blacklist.commandDescription"));
        emptyBiomeTPBlackListBiome.setTabRunnable(((args, player) -> emptyBiomeTPWhiteListBiome.tabList(player, args)));
        emptyBiomeTPBlackListBiome.setLooped(true);
        emptyBiomeTPBlackListBiome.setPermissions("TPort.teleporter.create");
        EmptyCommand emptyBiomeTPBlacklist = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyBiomeTPBlacklist.setCommandName("blacklist", ArgumentType.FIXED);
        emptyBiomeTPBlacklist.setTabRunnable(((args, player) -> emptyBiomeTPWhiteListBiome.tabList(player, args)));
        emptyBiomeTPBlacklist.addAction(emptyBiomeTPBlackListBiome);
        
        EmptyCommand emptyBiomeTPPresetPreset = new EmptyCommand();
        emptyBiomeTPPresetPreset.setCommandName("preset", ArgumentType.OPTIONAL);
        emptyBiomeTPPresetPreset.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.biomeTP.preset.preset.commandDescription"));
        emptyBiomeTPPresetPreset.setPermissions("TPort.teleporter.create");
        EmptyCommand emptyBiomeTPPreset = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyBiomeTPPreset.setCommandName("preset", ArgumentType.FIXED);
        emptyBiomeTPPreset.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.biomeTP.preset.commandDescription"));
        emptyBiomeTPPreset.setTabRunnable(((args, player) -> com.spaceman.tport.commands.tport.BiomeTP.BiomeTPPresets.getNames()));
        emptyBiomeTPPreset.addAction(emptyBiomeTPPresetPreset);
        emptyBiomeTPPreset.setPermissions("TPort.teleporter.create");
        
        EmptyCommand emptyBiomeTPRandom = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyBiomeTPRandom.setCommandName("random", ArgumentType.FIXED);
        emptyBiomeTPRandom.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.biomeTP.random.commandDescription"));
        emptyBiomeTPRandom.setPermissions("TPort.teleporter.create");
        
        addAction(emptyBiomeTPEmpty);
        addAction(emptyBiomeTPWhitelist);
        addAction(emptyBiomeTPBlacklist);
        addAction(emptyBiomeTPPreset);
        addAction(emptyBiomeTPRandom);
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport teleporter create biomeTP whitelist <biome...>
        //tport teleporter create biomeTP blacklist <biome...>
        //tport teleporter create biomeTP preset [preset]
        //tport teleporter create biomeTP random
        //tport teleporter create biomeTP
        
        if (args.length == 3) {
            if (!emptyBiomeTPEmpty.hasPermissionToRun(player, true)) {
                return;
            }
            createTeleporter(player, "BiomeTP", "BiomeTP");
            return;
        }
        if (args[3].equalsIgnoreCase("whitelist")) {
            if (args.length > 4) {
                if (!emptyBiomeTPEmpty.hasPermissionToRun(player, true)) {
                    return;
                }
                
                List<String> possibleBiomes = com.spaceman.tport.commands.tport.BiomeTP.availableBiomes();
                ArrayList<String> whitelist = new ArrayList<>();
                for (int i = 4; i < args.length; i++) {
                    String biomeName = args[i].toLowerCase();
                    BiomeEncapsulation biome = new BiomeEncapsulation(biomeName);
                    
                    if (!possibleBiomes.contains(biomeName)) {
                        sendErrorTranslation(player, "tport.command.teleporter.create.biomeTP.whitelist.biomeNotExist", biomeName);
                        continue;
                    }
                    
                    if (whitelist.contains(biomeName)) {
                        sendErrorTranslation(player, "tport.command.teleporter.create.biomeTP.whitelist.biomeAlreadyInList", biome);
                        continue;
                    }
                    whitelist.add(biomeName);
                }
                
                ArrayList<Message> addedLore = new ArrayList<>();
                addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomes.type",
                        formatTranslation(varInfoColor, varInfo2Color, "tport.command.teleporter.create.format.data.biomes.type.whitelist")));
                
                for (int i = 0; i < whitelist.size(); ) {
                    Message m = new Message();
                    m.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s", new BiomeEncapsulation(whitelist.get(i).toLowerCase())));
                    
                    if (i + 1 < whitelist.size()) {
                        if (i + 2 == whitelist.size()) {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.biomes.lastDelimiter"));
                        } else {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.biomes.delimiter"));
                        }
                        
                        m.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s", new BiomeEncapsulation(whitelist.get(i + 1).toLowerCase())));
                        
                        if (i + 3 == whitelist.size()) {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.biomes.lastDelimiter"));
                        } else if (i + 2 < whitelist.size()) {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.biomes.delimiter"));
                        }
                    }
                    
                    if (i == 0) {
                        if (whitelist.size() == 1) {
                            addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomes.singular", m));
                        } else {
                            addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomes.multiple", m));
                        }
                    } else {
                        addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomes.newLine", m));
                    }
                    
                    i += 2;
                }
                
                createTeleporter(player, "BiomeTP", "BiomeTP whitelist " + String.join(" ", whitelist), addedLore);
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create biomeTP whitelist <biome...>");
            }
        }
        else if (args[3].equalsIgnoreCase("blacklist")) {
            if (args.length > 4) {
                if (!emptyBiomeTPEmpty.hasPermissionToRun(player, true)) {
                    return;
                }
    
                List<String> possibleBiomes = com.spaceman.tport.commands.tport.BiomeTP.availableBiomes();
                ArrayList<String> blacklist = new ArrayList<>();
                for (int i = 4; i < args.length; i++) {
                    String biomeName = args[i].toLowerCase();
                    BiomeEncapsulation biome = new BiomeEncapsulation(biomeName);
    
                    if (!possibleBiomes.contains(biomeName)) {
                        sendErrorTranslation(player, "tport.command.teleporter.create.biomeTP.blacklist.biomeNotExist", biomeName);
                        continue;
                    }
                    
                    if (blacklist.contains(biomeName)) {
                        sendErrorTranslation(player, "tport.command.teleporter.create.biomeTP.blacklist.biomeAlreadyInList", biome);
                        continue;
                    }
                    blacklist.add(biomeName);
                }
                
                ArrayList<Message> addedLore = new ArrayList<>();
                addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomes.type",
                        formatTranslation(varInfoColor, varInfo2Color, "tport.command.teleporter.create.format.data.biomes.type.blacklist")));
                
                for (int i = 0; i < blacklist.size(); ) {
                    Message m = new Message();
                    m.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s", new BiomeEncapsulation(blacklist.get(i).toLowerCase())));
                    
                    if (i + 1 < blacklist.size()) {
                        if (i + 2 == blacklist.size()) {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.biomes.lastDelimiter"));
                        } else {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.biomes.delimiter"));
                        }
    
                        m.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s", new BiomeEncapsulation(blacklist.get(i + 1).toLowerCase())));
                        
                        if (i + 3 == blacklist.size()) {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.biomes.lastDelimiter"));
                        } else if (i + 2 < blacklist.size()) {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.biomes.delimiter"));
                        }
                    }
                    
                    if (i == 0) {
                        if (blacklist.size() == 1) {
                            addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomes.singular", m));
                        } else {
                            addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomes.multiple", m));
                        }
                    } else {
                        addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomes.newLine", m));
                    }
                    
                    i += 2;
                }
                
                createTeleporter(player, "BiomeTP", "BiomeTP blacklist " + String.join(" ", blacklist), addedLore);
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create biomeTP blacklist <biome...>");
            }
        }
        else if (args[3].equalsIgnoreCase("preset")) {
            if (args.length == 4) {
                if (!emptyBiomeTPEmpty.hasPermissionToRun(player, true)) {
                    return;
                }
                ArrayList<Message> pairs = new ArrayList<>();
                pairs.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomes.type",
                        formatTranslation(varInfoColor, varInfo2Color, "tport.command.teleporter.create.format.data.biomes.type.preset")));
                
                createTeleporter(player, "BiomeTP", "BiomeTP preset", pairs);
            } else if (args.length == 5) {
                if (!emptyBiomeTPEmpty.hasPermissionToRun(player, true)) {
                    return;
                }
                com.spaceman.tport.commands.tport.BiomeTP.BiomeTPPresets.BiomePreset preset = com.spaceman.tport.commands.tport.BiomeTP.BiomeTPPresets.getPreset(args[4], player.getWorld());
                if (preset != null) {
                    ArrayList<Message> pairs = new ArrayList<>();
                    pairs.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomes.type",
                            formatTranslation(varInfoColor, varInfo2Color, "tport.command.teleporter.create.format.data.biomes.type.preset")));
                    pairs.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomePreset", preset.name()));
                    
                    createTeleporter(player, "BiomeTP", "BiomeTP preset " + preset.name(), pairs);
                } else {
                    sendErrorTranslation(player, "tport.command.teleporter.create.biomeTP.preset.preset.presetNotExist", args[4]);
                }
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create biomeTP preset [preset]");
            }
        }
        else if (args[3].equalsIgnoreCase("random")) {
            if (args.length == 4) {
                if (!emptyBiomeTPEmpty.hasPermissionToRun(player, true)) {
                    return;
                }
                ArrayList<Message> pairs = new ArrayList<>();
                pairs.add(formatInfoTranslation("tport.command.teleporter.create.format.data.biomes.type",
                        formatTranslation(varInfoColor, varInfo2Color, "tport.command.teleporter.create.format.data.biomes.type.random")));
                
                createTeleporter(player, "BiomeTP", "BiomeTP random", pairs);
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create biomeTP random");
            }
        }
        else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create biomeTP [whitelist|blacklist|preset|random]");
        }
    }
}
