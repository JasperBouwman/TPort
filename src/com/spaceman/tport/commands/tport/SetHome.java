package com.spaceman.tport.commands.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class SetHome extends SubCommand {
    
    public SetHome() {
        EmptyCommand emptyCommand1 = new EmptyCommand();
        emptyCommand1.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyCommand1.setCommandDescription(TextComponent.textComponent("This command is used to set your home TPort", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.setHome", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("player", ArgumentType.REQUIRED);
        emptyCommand.setTabRunnable((args, player) -> {
            ArrayList<String> list = new ArrayList<>();
    
            UUID argOneUUID = PlayerUUID.getPlayerUUID(args[1]);
            if (argOneUUID == null) {
                return Collections.emptyList();
            }
            for (TPort tport : TPortManager.getTPortList(argOneUUID)) {
                if (tport.hasAccess(player.getUniqueId())) {
                    list.add(tport.getName());
                }
            }
            
            return list;
        });
        emptyCommand.addAction(emptyCommand1);
        addAction(emptyCommand);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return getFile("TPortData").getKeys("tport").stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport setHome <player> <TPort name>
        
        if (args.length == 3) {
            
            if (!hasPermission(player, false, "TPort.setHome", "TPort.basic")) {
                return;
            }
            
            Files tportData = getFile("TPortData");
            
            String newPlayerName = args[1];
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
            
            if (newPlayerUUID == null || !tportData.getConfig().contains("tport." + newPlayerUUID)) {
                sendErrorTheme(player, "Could not find a player named %s", newPlayerName);
                return;
            }
    
            TPort tport = TPortManager.getTPort(newPlayerUUID, args[2]);
            if (tport != null) {
                tportData.getConfig().set("tport." + player.getUniqueId() + ".home", tport.getTportID().toString());
                tportData.saveConfig();
                sendSuccessTheme(player, "Successfully set home to TPort %s", tport.getName());
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
            
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport setHome <player> <TPort>");
        }
    }
}
