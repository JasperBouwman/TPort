package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.SafetyCheck;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Offset extends SubCommand {
    
    public Offset() {
        EmptyCommand emptyOffset = new EmptyCommand();
        emptyOffset.setCommandName("offset", ArgumentType.OPTIONAL);
        emptyOffset.setCommandDescription(textComponent("This command is used to set your PLTP offset", infoColor));
        addAction(emptyOffset);
    }
    
    public static PLTPOffset getPLTPOffset(Player player) {
        Files tportData = getFile("TPortData");
        return PLTPOffset.valueOf((tportData.getConfig().getString("tport." + player.getUniqueId() + ".tp.offset", "IN")));
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get your PLTP offset. " +
                "When the offset is set to ", infoColor),
                textComponent("BEHIND", varInfoColor),
                textComponent(" players will teleport 1 meter behind you, instead of in you with the exact same location", infoColor));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.stream(PLTPOffset.values()).map(Enum::name).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP offset
        // tport PLTP offset <offset>
        
        if (args.length == 2) {
            sendInfoTheme(player, "Your PLTP offset is set to %s", getPLTPOffset(player).name());
        } else if (args.length == 3) {
            PLTPOffset newOffset = PLTPOffset.get(args[2]);
            if (newOffset != null) {
                Files tportData = getFile("TPortData");
                tportData.getConfig().set("tport." + player.getUniqueId() + ".tp.offset", newOffset.name());
                tportData.saveConfig();
                sendSuccessTheme(player, "Successfully set your PLTP offset to %s", newOffset.name());
            } else {
                sendErrorTheme(player, "Offset %s does not exist", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport PLTP offset <offset>");
        }
    }
    
    public enum PLTPOffset {
        IN(origin -> origin),
        BEHIND(origin -> {
            Location l = origin.clone();
            l.setPitch(0);
            l = l.add(l.getDirection().multiply(-1));
            l.setPitch(origin.getPitch());
            return SafetyCheck.isSafe(l) ? l : origin;
        });
        
        private final OffsetApplier offsetApplier;
        
        PLTPOffset(OffsetApplier offsetApplier) {
            this.offsetApplier = offsetApplier;
        }
        
        public Location applyOffset(Location origin) {
            return offsetApplier.ApplyOffset(origin);
        }
        
        public static PLTPOffset get(String name) {
            for (PLTPOffset offset : PLTPOffset.values()) {
                if (offset.name().equalsIgnoreCase(name)) {
                    return offset;
                }
            }
            return null;
        }
        
        public PLTPOffset getNext() {
            boolean next = false;
            for (PLTPOffset offset : values()) {
                if (offset.equals(this)) {
                    next = true;
                } else if (next) {
                    return offset;
                }
            }
            return Arrays.asList(values()).get(0);
        }
        
        @FunctionalInterface
        private interface OffsetApplier {
            Location ApplyOffset(Location origin);
        }
    }
}
