package com.spaceman.tport.commands.tport.resourcePack;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.ResourcePack;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.ResourcePack.setResourcePackResolution;
import static com.spaceman.tport.commands.tport.ResourcePack.updateResourcePack;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Resolution extends SubCommand {
    
    public Resolution() {
        EmptyCommand emptyResolutionResolution = new EmptyCommand();
        emptyResolutionResolution.setCommandName("resolution", ArgumentType.OPTIONAL);
        emptyResolutionResolution.setCommandDescription(formatInfoTranslation("tport.command.resourcePack.resolution.resolution.commandDescription", "custom"));
        
        addAction(emptyResolutionResolution);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Resolutions.getStringValues();
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.resourcePack.resolution.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport resourcePack resolution [resolution]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.resourcePack.resolution.succeeded", ResourcePack.getResourcePackResolution(player.getUniqueId()));
            
            Message resolutionsMessage = new Message();
            Message delimiter = formatInfoTranslation("tport.command.resourcePack.resolution.delimiter");
            Resolutions[] resolutions = Resolutions.values();
            boolean color = true;
            
            for (int i = 0; i < resolutions.length; i++) {
                Resolutions res = resolutions[i];
                resolutionsMessage.addMessage(formatTranslation((color ? varInfoColor : varInfo2Color), (color ? varInfoColor : varInfo2Color), "%s", res));
                
                color = !color;
                
                if (i + 2 == resolutions.length) resolutionsMessage.addMessage(formatInfoTranslation("tport.command.resourcePack.resolution.lastDelimiter"));
                else                           resolutionsMessage.addMessage(delimiter);
            }
            resolutionsMessage.removeLast();
            
            sendInfoTranslation(player, "tport.command.resourcePack.resolution.allResolutions", resolutionsMessage);
        } else if (args.length == 3) {
            Resolutions newResolution = Resolutions.get(args[2], null);
            if (newResolution == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport resourcePack state [true|false]");
                return;
            }
            
            Resolutions currentResolution = ResourcePack.getResourcePackResolution(player.getUniqueId());
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
    
    public enum Resolutions {
        x16("https://github.com/JasperBouwman/TPort/releases/download/TPort-" +
                Main.getInstance().getDescription().getVersion() + "/resource_pack_16x.zip"),
        x32("https://github.com/JasperBouwman/TPort/releases/download/TPort-" +
                Main.getInstance().getDescription().getVersion() + "/resource_pack_32x.zip"),
        CUSTOM(null);
        
        private final String url;
        
        Resolutions(String url) {
            this.url = url;
        }
        
        public String getURL() {
            return url;
        }
        
        @Nullable
        public static Resolutions get(String name, Resolutions def) {
            for (Resolutions r : Resolutions.values()) {
                if (r.name().equalsIgnoreCase(name)) {
                    return r;
                }
            }
            return def;
        }
        
        public static List<String> getStringValues() {
            return Arrays.stream(Resolutions.values()).map(Enum::name).collect(Collectors.toList());
        }
        
        public Message getDescription() {
            return formatInfoTranslation("tport.command.features.feature." + this.name() + ".description");
        }
    }
}
