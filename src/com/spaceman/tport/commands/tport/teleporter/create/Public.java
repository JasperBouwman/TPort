package com.spaceman.tport.commands.tport.teleporter.create;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.publc.Open;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.teleporter.Create.createTeleporter;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Public extends SubCommand {
    
    public Public() {
        EmptyCommand emptyPublicTPort = new EmptyCommand();
        emptyPublicTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyPublicTPort.setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.public.player.commandDescription"));
        emptyPublicTPort.setPermissions("TPort.teleporter.create");
        
        addAction(emptyPublicTPort);
        
        setPermissions("TPort.teleporter.create");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.teleporter.create.public.commandDescription");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return tportData.getKeys("public.tports").stream()
                .map(publicTPortSlot -> tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString()))
                .map(tportID -> getTPort(UUID.fromString(tportID)))
                .filter(Objects::nonNull)
                .map(TPort::getName)
                .collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport teleporter create public [TPort name]
        
        if (args.length == 3) {
            if (hasPermissionToRun(player, true)) {
                createTeleporter(player, "Public", "public");
            }
        } else if (args.length == 4) {
            if (hasPermissionToRun(player, true)) {
                String tportName;
                TPort tport = Open.getPublicTPort(args[3]);
                if (tport == null) {
                    sendErrorTranslation(player, "tport.command.teleporter.create.public.player.TPortNotFound", args[3]);
                    return;
                }
                tportName = tport.getName();
                
                createTeleporter(player, "Public", "public open " + tportName,
                        List.of(formatInfoTranslation("tport.command.teleporter.create.format.data.public.tport", tport)));
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create public [TPort name]");
        }
    }
}
