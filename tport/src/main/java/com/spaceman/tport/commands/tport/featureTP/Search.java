package com.spaceman.tport.commands.tport.featureTP;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.advancements.TPortAdvancement;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Back;
import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.encapsulation.FeatureEncapsulation;
import com.spaceman.tport.history.TeleportHistory;
import com.spaceman.tport.history.locationSource.FeatureLocationSource;
import com.spaceman.tport.metrics.FeatureSearchCounter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_FeatureTP_OneIsNotEnough;
import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.commands.tport.featureTP.Mode.getDefMode;
import static com.spaceman.tport.commands.tport.featureTP.Mode.worldSearchString;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class Search extends SubCommand {
    
    public Search() {
        EmptyCommand emptySearchFeature = new EmptyCommand();
        EmptyCommand emptySearchModeFeature = new EmptyCommand();
        emptySearchFeature.setPermissions("TPort.featureTP.type.<feature>");
        
        emptySearchModeFeature.setCommandName("feature", ArgumentType.REQUIRED);
        emptySearchModeFeature.setCommandDescription(formatInfoTranslation("tport.command.featureTP.search.mode.feature.commandDescription"));
        List<String> permissions = new ArrayList<>(emptySearchFeature.getPermissions());
        permissions.add(worldSearchString);
        emptySearchModeFeature.setPermissions(permissions);
        emptySearchModeFeature.permissionsOR(false);
        emptySearchModeFeature.setTabRunnable((args, player) -> {
            ArrayList<String> list = new ArrayList<>(FeatureTP.getFeatures(player.getWorld()));
            FeatureTP.getTags(player.getWorld()).stream().map(Pair::getLeft).forEach(list::add);
            List<String> featureList = Arrays.asList(args).subList(2, args.length).stream().map(String::toLowerCase).toList();
            return list.stream().filter(name -> featureList.stream().noneMatch(name::equalsIgnoreCase)).toList();
        });
        emptySearchModeFeature.setLooped(true);
        
        EmptyCommand emptySearchMode = new EmptyCommand();
        emptySearchMode.setCommandName("mode", ArgumentType.REQUIRED);
        emptySearchMode.addAction(emptySearchModeFeature);
        
        emptySearchFeature.setCommandName("feature", ArgumentType.REQUIRED);
        emptySearchFeature.setCommandDescription(formatInfoTranslation("tport.command.featureTP.search.feature.commandDescription"));
        emptySearchFeature.setTabRunnable(emptySearchModeFeature.getTabRunnable());
        emptySearchFeature.setLooped(true);
        
        addAction(emptySearchMode);
        addAction(emptySearchFeature);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>(FeatureTP.getFeatures(player.getWorld()));
        FeatureTP.getTags(player.getWorld()).stream().map(Pair::getLeft).forEach(list::add);
        Arrays.stream(Mode.WorldSearchMode.values()).map(Enum::name).forEach(list::add);
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport featureTP search [mode] <feature...>
        // ->
        // tport featureTP search <mode> <feature...>
        // tport featureTP search <feature...>
        
        if (args.length <= 2) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport featureTP search [mode] <feature...>");
            return;
        }
        
        int startArgumentAt = 3;
        Mode.WorldSearchMode mode = Mode.WorldSearchMode.getForPlayer(args[2].toUpperCase(), player, true);
        if (mode == null) {
            mode = getDefMode(player.getUniqueId());
            startArgumentAt--;
        }
        
        List<String> features = FeatureTP.getFeatures(player.getWorld());
        List<Pair<String, List<String>>> tags = FeatureTP.getTags(player.getWorld());
        
        ArrayList<String> selectedFeatures = new ArrayList<>(args.length - 2);
        label:
        for (int i = startArgumentAt; i < args.length; i++) {
            String argument = args[i].toLowerCase();
            
            if (argument.charAt(0) == '#') { //tag list
                if (argument.startsWith("#minecraft:")) argument = "#" + argument.substring(11);
                for (Pair<String, List<String>> tag : tags) {
                    if (!tag.getLeft().equals(argument)) {
                        continue;
                    }
                    for (String feature : tag.getRight()) {
                        if (selectedFeatures.contains(feature)) {
                            sendInfoTranslation(player, "tport.command.featureTP.search.tag.featureAlreadySelected", tag.getLeft(), feature);
                        } else {
                            if (hasPermission(player, true, "TPort.featureTP.type." + feature)) {
                                selectedFeatures.add(feature);
                                Advancement_FeatureTP_OneIsNotEnough.grant(player);
                            }
                        }
                    }
                    continue label;
                }
                sendErrorTranslation(player, "tport.command.featureTP.search.tag.tagNotExist", argument);
            } else { //feature
                if (argument.startsWith("minecraft:")) argument = argument.substring(10);
                for (String feature : features) {
                    if (!feature.equals(argument)) {
                        continue;
                    }
                    if (selectedFeatures.contains(feature)) {
                        sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureAlreadySelected", feature);
                    } else {
                        if (hasPermission(player, true, "TPort.featureTP.type." + feature)) {
                            selectedFeatures.add(feature);
                        }
                    }
                    continue label;
                }
                sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotExist", argument);
            }
        }
        
        featureTP(player, mode, selectedFeatures);
    }
    
    private static Message featuresToMessageInfo(List<String> features) {
        Message featureList = new Message();
        int listSize = features.size();
        boolean color = true;
        
        for (int i = 0; i < listSize; i++) {
            String feature = features.get(i).toLowerCase();
            if (color) {
                featureList.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s", new FeatureEncapsulation(feature)));
            } else {
                featureList.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s", new FeatureEncapsulation(feature)));
            }
            
            if (i + 2 == listSize)
                featureList.addMessage(formatInfoTranslation("tport.command.featureTP.listFeatures.info.lastDelimiter"));
            else featureList.addMessage(formatInfoTranslation("tport.command.featureTP.listFeatures.info.delimiter"));
        
            color = !color;
        }
        featureList.removeLast();
        return featureList;
    }
    public static Message featuresToMessageError(List<String> features) {
        Message featureList = new Message();
        int listSize = features.size();
        boolean color = true;
        
        for (int i = 0; i < listSize; i++) {
            String feature = features.get(i).toLowerCase();
            if (color) {
                featureList.addMessage(formatTranslation(varErrorColor, varErrorColor, "%s", new FeatureEncapsulation(feature)));
            } else {
                featureList.addMessage(formatTranslation(varError2Color, varError2Color, "%s", new FeatureEncapsulation(feature)));
            }
            
            if (i + 2 == listSize)
                featureList.addMessage(formatErrorTranslation("tport.command.featureTP.listFeatures.error.lastDelimiter"));
            else featureList.addMessage(formatErrorTranslation("tport.command.featureTP.listFeatures.error.delimiter"));
        
            color = !color;
        }
        featureList.removeLast();
        return featureList;
    }
    
    public static void featureTP(Player player, Mode.WorldSearchMode mode, List<String> features) {
        FeatureSearchCounter.add(features);
        Location startLocation = mode.getLoc(player);
        
        if (features.size() == 1) sendInfoTranslation(player, "tport.command.featureTP.search.feature.starting.singular", featuresToMessageInfo(features));
        else                      sendInfoTranslation(player, "tport.command.featureTP.search.feature.starting.multiple", featuresToMessageInfo(features));
        Pair<Location, String> featureLoc = searchFeature(player, startLocation, features);
        if (featureLoc == null) {
            if (features.size() == 1) sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotFound.singular", featuresToMessageError(features));
            else                      sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotFound.multiple", featuresToMessageError(features));
        } else {
            Location loc = featureLoc.getLeft();
            loc = FeatureTP.setSafeY(player.getWorld(), loc.getBlockX(), loc.getBlockZ());
            
            if (loc != null) {
                loc.add(0.5, 0.1, 0.5);
                loc.setPitch(player.getLocation().getPitch());
                loc.setYaw(player.getLocation().getYaw());
                
                prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.FEATURE, "featureLoc", loc,
                        "prevLoc", player.getLocation(), "featureName", featureLoc.getRight()));
                TeleportHistory.setLocationSource(player.getUniqueId(), new FeatureLocationSource(featureLoc.getRight()));
                
                requestTeleportPlayer(player, loc,
                        () -> {
                            sendSuccessTranslation(player, "tport.command.featureTP.search.feature.succeeded", new FeatureEncapsulation(featureLoc.getRight()));
                            if (features.size() == 1) {
                                TPortAdvancement.Advancement_FeatureTP_Certainty.grant(player);
                            } else {
                                TPortAdvancement.Advancement_FeatureTP_Surprise.grant(player);
                            }
                        },
                        (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.featureTP.search.feature.tpRequested", new FeatureEncapsulation(featureLoc.getRight()), delay, tickMessage, seconds, secondMessage));
            } else {
                sendErrorTranslation(player, "tport.command.featureTP.search.feature.noSafeLocation");
            }
            CooldownManager.FeatureTP.update(player);
        }
    }
    
    public static Pair<Location, String> searchFeature(Player player, Location startLocation, List<String> features) {
        try {
            return Main.getInstance().adapter.searchFeature(player, startLocation, features);
        } catch (Throwable ex) {
            Features.Feature.printSmallNMSErrorInConsole("FeatureTP search", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
//            startLocation.getWorld().locateNearestStructure(startLocation, StructureType.FORTRESS /*structures*/, 0, true /*find unexplored*/);
            return null;
        }
    }
}
