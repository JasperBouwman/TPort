package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
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

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class SetHome extends SubCommand {
    
    public SetHome() {
        EmptyCommand emptyPlayerTPort = new EmptyCommand();
        emptyPlayerTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyPlayerTPort.setCommandDescription(TextComponent.textComponent("This command is used to set your home TPort", ColorTheme.ColorType.infoColor));
        emptyPlayerTPort.setPermissions("TPort.setHome", "TPort.basic");
        
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setTabRunnable((args, player) -> {
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
        emptyPlayer.addAction(emptyPlayerTPort);
        addAction(emptyPlayer);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return getFile("TPortData").getKeys("tport").stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport setHome <player> <TPort name>
        
        if (args.length == 3) {
            
            if (!hasPermissionToRun(player, true)) {
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
