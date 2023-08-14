package com.spaceman.tport.commands.tport.teleporter.create;

import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.featureTP.Mode;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.spaceman.tport.commands.tport.teleporter.Create.createTeleporter;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class FeatureTP extends SubCommand {
    
    public FeatureTP() {
        EmptyCommand emptyFeatureTPModeFeature = new EmptyCommand();
        emptyFeatureTPModeFeature.setCommandName("feature", ArgumentType.REQUIRED);
        emptyFeatureTPModeFeature.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.featureTP.mode.feature.commandDescription"));
        emptyFeatureTPModeFeature.setTabRunnable(((args, player) -> {
            ArrayList<String> list = new ArrayList<>(com.spaceman.tport.commands.tport.FeatureTP.getFeatures(player.getWorld()));
            com.spaceman.tport.commands.tport.FeatureTP.getTags(player.getWorld()).stream().map(Pair::getLeft).forEach(list::add);
            List<String> featureList = Arrays.asList(args).subList(3, args.length).stream().map(String::toLowerCase).toList();
            return list.stream().filter(name -> featureList.stream().noneMatch(name::equalsIgnoreCase)).toList();
        }));
        emptyFeatureTPModeFeature.setLooped(true);
        emptyFeatureTPModeFeature.setPermissions("TPort.teleporter.create");
        EmptyCommand emptyFeatureTPMode = new EmptyCommand();
        emptyFeatureTPMode.setCommandName("mode", ArgumentType.OPTIONAL);
        emptyFeatureTPMode.addAction(emptyFeatureTPModeFeature);
        
        EmptyCommand emptyFeatureTPFeature = new EmptyCommand();
        emptyFeatureTPFeature.setCommandName("feature", ArgumentType.OPTIONAL);
        emptyFeatureTPFeature.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.featureTP.feature.commandDescription"));
        emptyFeatureTPFeature.setTabRunnable(emptyFeatureTPModeFeature.getTabRunnable());
        emptyFeatureTPFeature.setPermissions("TPort.teleporter.create");
        emptyFeatureTPFeature.setLooped(true);
        
        addAction(emptyFeatureTPMode);
        addAction(emptyFeatureTPFeature);
        
        setPermissions("TPort.teleporter.create");
        setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.featureTP.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>(com.spaceman.tport.commands.tport.FeatureTP.getFeatures(player.getWorld()));
        com.spaceman.tport.commands.tport.FeatureTP.getTags(player.getWorld()).stream().map(Pair::getLeft).forEach(list::add);
        Arrays.stream(Mode.WorldSearchMode.values()).map(Enum::name).forEach(list::add);
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport teleporter create featureTP
        //tport teleporter create featureTP <mode> <featureType...>
        //tport teleporter create featureTP <featureType...>
        
        if (args.length > 2) {
            int startArgumentAt = 4;
            Mode.WorldSearchMode mode = null;
            if (args.length > 3) {
                mode = Mode.WorldSearchMode.getForPlayer(args[3].toUpperCase(), player, false);
            }
            if (mode == null) {
                startArgumentAt--;
            } else if (args.length == 4) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create featureTP [[mode] <featureType...>]");
                return;
            }
            
            List<String> features = com.spaceman.tport.commands.tport.FeatureTP.getFeatures(player.getWorld());
            List<Pair<String, List<String>>> tags = com.spaceman.tport.commands.tport.FeatureTP.getTags(player.getWorld());
            
            ArrayList<String> selectedFeatures = new ArrayList<>(args.length - 2);
            label:
            for (int i = startArgumentAt; i < args.length; i++) {
                String argument = args[i].toLowerCase();
                
                if (argument.charAt(0) == '#') { //tag list
                    for (Pair<String, List<String>> tag : tags) {
                        if (tag.getLeft().equals(argument)) {
                            for (String feature : tag.getRight()) {
                                if (selectedFeatures.contains(feature)) {
                                    sendInfoTranslation(player, "tport.command.featureTP.search.tag.featureAlreadySelected", tag.getLeft(), feature);
                                } else {
                                    selectedFeatures.add(feature);
                                }
                            }
                            continue label;
                        }
                    }
                    sendErrorTranslation(player, "tport.command.featureTP.search.tag.tagNotExist", argument);
                } else { //feature
                    for (String feature : features) {
                        if (feature.equals(argument)) {
                            if (selectedFeatures.contains(feature)) {
                                sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureAlreadySelected", feature);
                            } else {
                                selectedFeatures.add(feature);
                            }
                            continue label;
                        }
                    }
                    sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotExist", argument);
                }
            }
            
            List<Message> addedLore = new ArrayList<>();
            String command = "featureTP";
            
            if (mode != null) {
                command += " search " + mode.name();
                addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.features.mode", mode));
            }
            if (!selectedFeatures.isEmpty()) {
                if (mode == null) command += " search";
                command += " " + String.join(" ", selectedFeatures);
                
                for (int i = 0; i < selectedFeatures.size(); ) {
                    Message m = new Message();
                    m.addText(textComponent(selectedFeatures.get(i).toLowerCase(), varInfoColor));
                    
                    if (i + 1 < selectedFeatures.size()) {
                        if (i + 2 == selectedFeatures.size()) {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.features.lastDelimiter"));
                        } else {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.features.delimiter"));
                        }
                        
                        m.addText(textComponent(selectedFeatures.get(i + 1).toLowerCase(), varInfo2Color));
                        
                        if (i + 3 == selectedFeatures.size()) {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.features.lastDelimiter"));
                        } else if (i + 2 < selectedFeatures.size()) {
                            m.addMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.data.features.delimiter"));
                        }
                    }
                    
                    if (i == 0) {
                        if (selectedFeatures.size() == 1) {
                            addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.features.singular", m));
                        } else {
                            addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.features.multiple", m));
                        }
                    } else {
                        addedLore.add(formatInfoTranslation("tport.command.teleporter.create.format.data.features.newLine", m));
                    }
                    
                    i += 2;
                }
            }
            
            createTeleporter(player, "FeatureTP", command, addedLore);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create featureTP [[mode] <featureType...>]");
        }
    }
}
