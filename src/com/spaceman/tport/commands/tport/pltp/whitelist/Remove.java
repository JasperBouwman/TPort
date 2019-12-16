package com.spaceman.tport.commands.tport.pltp.whitelist;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class Remove extends SubCommand {
    
    public Remove() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("player", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(textComponent("This command is used to remove players from your PLTP whitelist", ColorType.infoColor));
        emptyCommand.setTabRunnable((args, player) -> {
            Files tportData = GettingFiles.getFile("TPortData");
            ArrayList<String> list = new ArrayList<>();
            new ArrayList<>(tportData.getConfig().getStringList("tport." + player.getUniqueId() + ".tp.players")).stream().map(PlayerUUID::getPlayerName).forEach(list::add);
            list.removeAll(Arrays.asList(args).subList(3, args.length));
            return list;
        });
        emptyCommand.setLooped(true);
        addAction(emptyCommand);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getActions().get(0).tabList(player, args);
    }

    @Override
    public void run(String[] args, Player player) {
        // tport PLTP whitelist remove <player...>
        
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();
        ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                .getStringList("tport." + playerUUID + ".tp.players");
    
        for (int i = 3; i < args.length; i++) {
            String removePlayerName = args[i];
            UUID removePlayerUUID = PlayerUUID.getPlayerUUID(removePlayerName);
    
            if (removePlayerUUID == null || !tportData.getConfig().contains("tport." + removePlayerUUID)) {
                sendErrorTheme(player, "Could not find a player named %s", removePlayerName);
                return;
            }
    
            if (!list.contains(removePlayerUUID.toString())) {
                sendErrorTheme(player, "Player %s is not in your PLTP whitelist");
                return;
            }
    
            list.remove(removePlayerUUID.toString());
            tportData.getConfig().set("tport." + playerUUID + ".tp.players", list);
            tportData.saveConfig();
            sendSuccessTheme(player, "Successfully removed player %s from your PLTP whitelist");
    
            Player removePlayer = Bukkit.getPlayer(removePlayerUUID);
            if (removePlayer != null) {
                sendInfoTheme(removePlayer, "You have been removed from the PLTP whitelist of player %s", player.getName());
            }
        }
    }
}
