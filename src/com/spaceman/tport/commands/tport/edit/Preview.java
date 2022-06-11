package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Preview extends SubCommand {
    
    private final EmptyCommand emptyPreviewState;
    
    public Preview() {
        emptyPreviewState = new EmptyCommand();
        emptyPreviewState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyPreviewState.setCommandDescription(formatInfoTranslation("This command is used to set the preview state of the given TPort"));
        emptyPreviewState.setPermissions("TPort.edit.preview", "TPort.basic");
        
        addAction(emptyPreviewState);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return Arrays.stream(TPort.PreviewState.values()).map(s -> s.name().toLowerCase()).collect(Collectors.toList());
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.edit.preview.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> preview [state]
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            sendInfoTranslation(player, "tport.command.edit.preview.succeeded", tport, tport.getPreviewState());
        } else if (args.length == 4) {
            if (!emptyPreviewState.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.preview.state.isOffered",
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
    
            TPort.PreviewState previewState;
            previewState = TPort.PreviewState.get(args[3], null);
            if (previewState == null) {
                sendErrorTranslation(player, "tport.command.edit.preview.state.stateNotFound", args[3]);
                return;
            }
            if (tport.isPublicTPort()) {
                if (!previewState.canGoPublic()) {
                    sendErrorTranslation(player, "tport.command.edit.preview.state.isPublic", tport, previewState);
                    return;
                }
            }
            tport.setPreviewState(previewState);
            tport.save();
            sendSuccessTranslation(player, "tport.command.edit.preview.state.succeeded", tport, previewState);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> preview [state]");
        }
    }
}
