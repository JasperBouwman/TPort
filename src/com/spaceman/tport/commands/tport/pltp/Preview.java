package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Preview extends SubCommand {
    
    private final EmptyCommand emptyPreviewState;
    
    public Preview() {
        emptyPreviewState = new EmptyCommand();
        emptyPreviewState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyPreviewState.setCommandDescription(formatInfoTranslation("tport.command.pltp.preview.state.commandDescription"));
        emptyPreviewState.setPermissions("TPort.pltp.preview.set", "TPort.basic");
        
        addAction(emptyPreviewState);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        if (!emptyPreviewState.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Arrays.stream(PreviewState.values()).map(s -> s.name().toLowerCase()).collect(Collectors.toList());
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.pltp.preview.commandDescription");
    }
    
    public static PreviewState getPreviewState(UUID uuid) {
        PreviewState def = PreviewState.ON;
        return PreviewState.get(tportData.getConfig().getString("tport." + uuid + ".tp.previewState", def.name()), def);
    }
    public static void setPreviewState(UUID uuid, PreviewState previewState) {
        tportData.getConfig().set("tport." + uuid + ".tp.previewState", previewState.name());
        tportData.saveConfig();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP preview [state]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.pltp.preview.succeeded", getPreviewState(player.getUniqueId()));
        } else if (args.length == 3) {
            if (!emptyPreviewState.hasPermissionToRun(player, true)) {
                return;
            }
            
            PreviewState previewState;
            previewState = PreviewState.get(args[2], null);
            if (previewState == null) {
                sendErrorTranslation(player, "tport.command.pltp.preview.state.stateNotFound", args[2]);
                return;
            }
            setPreviewState(player.getUniqueId(), previewState);
            sendSuccessTranslation(player, "tport.command.pltp.preview.state.succeeded", previewState);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP preview [state]");
        }
    }
    
    public enum PreviewState implements MessageUtils.MessageDescription {
        ON(ChatColor.RED + "on"),
        OFF(ChatColor.GREEN + "off"),
        NOTIFIED(ChatColor.YELLOW + "notified");
        
        private final String displayName;
        
        PreviewState(String displayName) {
            this.displayName = displayName;
        }
        
        @Nullable
        public static PreviewState get(@Nullable String name, PreviewState def) {
            try {
                return PreviewState.valueOf(name != null ? name.toUpperCase() : ON.name());
            } catch (IllegalArgumentException | NullPointerException iae) {
                return def;
            }
        }
        
        public PreviewState getNext() {
            return switch (this) {
                case ON -> OFF;
                case OFF -> NOTIFIED;
                case NOTIFIED -> ON;
            };
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.command.pltp.previewState." + this.name() + ".description", this.getDisplayName());
        }
        
        @Override
        public String getName() {
            return name();
        }
        
        @Override
        public String getInsertion() {
            return getName();
        }
    }
    
}
