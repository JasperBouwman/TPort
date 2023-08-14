package com.spaceman.tport.commands.tport.pltp.whitelist;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.pltp.Whitelist;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Remove extends SubCommand {
    
    public Remove() {
        EmptyCommand emptyRemove = new EmptyCommand();
        emptyRemove.setCommandName("player", ArgumentType.REQUIRED);
        emptyRemove.setCommandDescription(formatInfoTranslation("tport.command.PLTP.whitelist.remove.players.commandDescription"));
        emptyRemove.setTabRunnable((args, player) -> {
//            ArrayList<String> list = new ArrayList<>();
            List<String> list = Whitelist.getPLTPWhitelist(player).stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
            list.removeAll(Arrays.asList(args).subList(3, args.length));
            return list;
        });
        emptyRemove.setLooped(true);
        addAction(emptyRemove);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getActions().get(0).tabList(player, args);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP whitelist remove <player...>
        
        if (args.length < 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP whitelist remove <player...>");
            return;
        }
        
        ArrayList<String> list = Whitelist.getPLTPWhitelist(player);
        
        for (int i = 3; i < args.length; i++) {
            String removePlayerName = args[i];
            UUID removePlayerUUID = PlayerUUID.getPlayerUUID(removePlayerName, player);
            if (removePlayerUUID == null) {
                return;
            }
            
            if (!list.contains(removePlayerUUID.toString())) {
                sendErrorTranslation(player, "tport.command.PLTP.whitelist.remove.players.notInList", asPlayer(removePlayerUUID));
                return;
            }
            
            list.remove(removePlayerUUID.toString());
            Whitelist.setPLTPWhitelist(player, list);
            sendSuccessTranslation(player, "tport.command.PLTP.whitelist.remove.players.succeeded", asPlayer(removePlayerUUID));
    
            sendInfoTranslation(Bukkit.getPlayer(removePlayerUUID), "tport.command.PLTP.whitelist.remove.players.succeededOtherPlayer", asPlayer(player));
        }
    }
}
