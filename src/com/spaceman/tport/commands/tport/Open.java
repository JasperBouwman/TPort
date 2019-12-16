package com.spaceman.tport.commands.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.TPortInventories.openTPortGUI;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Open extends SubCommand {
    
    public Open() {
        EmptyCommand emptyCommand1 = new EmptyCommand();
        emptyCommand1.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyCommand1.setCommandDescription(textComponent("This command is used to teleport to the given TPort", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.open", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("player", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(textComponent("This command is used to open the TPort GUI of the given player", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.open", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        emptyCommand.setTabRunnable((args, player) -> {
    
            UUID argOneUUID = PlayerUUID.getPlayerUUID(args[1]);
            if (argOneUUID == null) {
                return Collections.emptyList();
            }
            return TPortManager.getTPortList(argOneUUID).stream()
                    .filter(tport -> tport.hasAccess(player))
                    .map(TPort::getName)
                    .collect(Collectors.toCollection(ArrayList::new));
        });
        emptyCommand.addAction(emptyCommand1);
        addAction(emptyCommand);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return GettingFiles.getFile("TPortData").getKeys("tport").stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport open <player> [TPort name]
        if (hasPermission(player, "TPort.open", "TPort.basic")) {
            runNotPerm(args, player);
        }
    }
    
    public static void runNotPerm(String[] args, Player player) {
        if (args.length == 1 || args.length > 3) {
            sendErrorTheme(player, "Usage: %s", "/tport open <player> [TPort name]");
            return;
        }
    
        Files tportData = GettingFiles.getFile("TPortData");
    
        String newPlayerName = args[1];
        UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
    
        if (newPlayerUUID == null || !tportData.getConfig().contains("tport." + newPlayerUUID)) {
            sendErrorTheme(player, "Could not find a player named %s", newPlayerName);
            return;
        }
    
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(newPlayerUUID, args[2]);
            if (tport != null) {
                tport.teleport(player, true);
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
            return;
        }
    
        openTPortGUI(newPlayerUUID, player);
    }
}
