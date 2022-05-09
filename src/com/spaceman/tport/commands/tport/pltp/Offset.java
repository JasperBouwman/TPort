package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.SafetyCheck;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Offset extends SubCommand {
    
    public Offset() {
        EmptyCommand emptyOffset = new EmptyCommand();
        emptyOffset.setCommandName("offset", ArgumentType.OPTIONAL);
        emptyOffset.setCommandDescription(formatInfoTranslation("tport.command.PLTP.offset.offset.commandDescription"));
        addAction(emptyOffset);
    }
    
    public static PLTPOffset getPLTPOffset(Player player) {
        Files tportData = getFile("TPortData");
        return PLTPOffset.valueOf((tportData.getConfig().getString("tport." + player.getUniqueId() + ".tp.offset", "IN")));
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.PLTP.offset.commandDescription", "IN", "BEHIND");
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
            sendInfoTranslation(player, "tport.command.PLTP.offset.succeeded", getPLTPOffset(player));
        } else if (args.length == 3) {
            PLTPOffset newOffset = PLTPOffset.get(args[2]);
            if (newOffset != null) {
                Files tportData = getFile("TPortData");
                tportData.getConfig().set("tport." + player.getUniqueId() + ".tp.offset", newOffset.name());
                tportData.saveConfig();
                sendSuccessTranslation(player, "tport.command.PLTP.offset.offset.succeeded", newOffset);
            } else {
                sendErrorTranslation(player, "tport.command.PLTP.offset.offset.offsetNotExist", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP offset [offset]");
        }
    }
    
    public enum PLTPOffset implements MessageUtils.MessageDescription {
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
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.tport.tport.privateState." + this.name() + ".description", this.name());
        }
        
        @Override
        public String getName() {
            return name();
        }
        
        @Override
        public String getInsertion() {
            return getName();
        }
        
        @FunctionalInterface
        private interface OffsetApplier {
            Location ApplyOffset(Location origin);
        }
    }
}
