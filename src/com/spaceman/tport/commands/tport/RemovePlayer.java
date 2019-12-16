package com.spaceman.tport.commands.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class RemovePlayer extends SubCommand {
    
    public RemovePlayer() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("player", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(TextComponent.textComponent("This command is used to remove a player from the Main TPort GUI, " +
                "this will also remove all TPorts of the given player and can not be undone. " +
                "Mostly used when a player is not coming back and you want to clear out the Main TPort GUI", ColorType.infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.admin.removePlayer", ColorTheme.ColorType.varInfoColor));
        addAction(emptyCommand);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        List<String> list = new ArrayList<>();
        if (hasPermission(player, false, "TPort.admin.removePlayer")) {
            Files tportData = GettingFiles.getFile("TPortData");
            list = tportData.getKeys("tport").stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
        }
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport removePlayer <player>
        
        if (!hasPermission(player, "TPort.admin.removePlayer")) {
            return;
        }
        
        if (args.length != 2) {
            sendErrorTheme(player, "Usage: %s", "/tport removePlayer <player>");
            return;
        }
        Files tportData = GettingFiles.getFile("TPortData");
        
        String newPlayerName = args[1];
        UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
        if (newPlayerUUID == null) {
            sendErrorTheme(player, "Could not find a player named %s", newPlayerName);
            return;
        }
        if (Bukkit.getPlayer(newPlayerUUID) != null) {
            sendErrorTheme(player, "Player %s has to be offline", newPlayerName);
            return;
        }
        
        if (tportData.getConfig().contains("tport." + newPlayerUUID)) {
            tportData.getConfig().set("tport." + newPlayerUUID, null);
            tportData.saveConfig();
            sendSuccessTheme(player, "Successfully removed %s from the TPort plugin", newPlayerName);
        } else {
            sendInfoTheme(player, "Player %s is not registered in the TPort plugin", newPlayerName);
        }
    }
}
