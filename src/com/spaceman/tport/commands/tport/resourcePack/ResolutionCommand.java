package com.spaceman.tport.commands.tport.resourcePack;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.ResourcePack;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.ResourcePack.setResourcePackResolution;
import static com.spaceman.tport.commands.tport.ResourcePack.updateResourcePack;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class ResolutionCommand extends SubCommand {
    
    public ResolutionCommand() {
        EmptyCommand emptyResolutionResolution = new EmptyCommand();
        emptyResolutionResolution.setCommandName("resolution", ArgumentType.OPTIONAL);
        emptyResolutionResolution.setCommandDescription(formatInfoTranslation("tport.command.resourcePack.resolution.resolution.commandDescription", "custom"));
        
        addAction(emptyResolutionResolution);
        
        setCommandDescription(formatInfoTranslation("tport.command.resourcePack.resolution.commandDescription"));
    }
    
    @Override
    public String getName(String arg) {
        return "resolution";
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Resolution.getStringValues();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport resourcePack resolution [resolution]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.resourcePack.resolution.succeeded", ResourcePack.getResourcePackResolution(player.getUniqueId()));
            
            Message resolutionsMessage = new Message();
            Message delimiter = formatInfoTranslation("tport.command.resourcePack.resolution.delimiter");
            ArrayList<Resolution> resolutions = Resolution.getResolutions();
            boolean color = true;
            
            for (int i = 0; i < resolutions.size(); i++) {
                Resolution res = resolutions.get(i);
                resolutionsMessage.addMessage(formatTranslation((color ? varInfoColor : varInfo2Color), (color ? varInfoColor : varInfo2Color), "%s", res));
                
                color = !color;
                
                if (i + 2 == resolutions.size()) resolutionsMessage.addMessage(formatInfoTranslation("tport.command.resourcePack.resolution.lastDelimiter"));
                else                           resolutionsMessage.addMessage(delimiter);
            }
            resolutionsMessage.removeLast();
            
            sendInfoTranslation(player, "tport.command.resourcePack.resolution.allResolutions", resolutionsMessage);
        } else if (args.length == 3) {
            Resolution newResolution = Resolution.getResolution(args[2]);
            if (newResolution == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport resourcePack state [true|false]");
                return;
            }
            
            Resolution currentResolution = ResourcePack.getResourcePackResolution(player.getUniqueId());
            if (newResolution == currentResolution) {
                sendErrorTranslation(player, "tport.command.resourcePack.resolution.resolution.alreadyInResolution", currentResolution);
                return;
            }
            
            setResourcePackResolution(player.getUniqueId(), newResolution);
            updateResourcePack(player);
            sendSuccessTranslation(player, "tport.command.resourcePack.resolution.resolution.succeeded", newResolution);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport resourcePack resolution [resolution]");
        }
    }
    
    public static class Resolution {
        private static ArrayList<Resolution> resolutions = new ArrayList<>();
        
        private final String name;
        private final String url;
        
        private Resolution(String name, String url) {
            this.name = name;
            this.url = url;
        }
        
        public String getName() {
            return name;
        }
        
        public String getUrl() {
            return url;
        }
        
        public Message getDescription() {
            return formatInfoTranslation("tport.command.resourcePack.resolution.resolution." + name + ".description");
        }
        
        public static boolean registerResourcePackResolution(String name, String url) {
            if (resolutions.stream().noneMatch(res -> res.name.equalsIgnoreCase(name))) {
                resolutions.add(new Resolution(name, url));
                return true;
            }
            return false;
        }
        
        public static Resolution getResolution(String name) {
            return getResolution(name, null);
        }
        public static Resolution getResolution(String name, @Nullable String def) {
            Resolution defRes = null;
            for (Resolution res : resolutions) {
                if (res.name.equalsIgnoreCase(name)) {
                    return res;
                }
                if (res.name.equalsIgnoreCase(def)) {
                    defRes = res;
                }
            }
            return defRes;
        }
        
        public static List<String> getStringValues() {
            return resolutions.stream().map(Resolution::getName).collect(Collectors.toList());
        }
        
        public static ArrayList<Resolution> getResolutions() {
            return new ArrayList<>(resolutions);
        }
    }
}
