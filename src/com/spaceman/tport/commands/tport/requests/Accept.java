package com.spaceman.tport.commands.tport.requests;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tpEvents.TPRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Accept extends SubCommand {
    
    static List<String> consentTabList(UUID uuid, List<String> currentArgs) {
        return TPRequest.getRequestsToYou(uuid).stream()
                .map(r -> PlayerUUID.getPlayerName(r.getRequesterUUID()))
                .filter(Objects::nonNull)
                .filter(s -> currentArgs.stream().noneMatch(s::equalsIgnoreCase))
                .collect(Collectors.toList());
    }
    
    public Accept() {
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyPlayer.setCommandDescription(formatInfoTranslation("tport.command.requests.accept.players.commandDescription"));
        emptyPlayer.setTabRunnable((args, player) -> consentTabList(player.getUniqueId(), Arrays.asList(args).subList(1, args.length)));
        emptyPlayer.setLooped(true);
        addAction(emptyPlayer);
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.requests.accept.commandDescription");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return consentTabList(player.getUniqueId(), Arrays.asList(args).subList(1, args.length));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport requests accept [player...]
        
        ArrayList<TPRequest> list = TPRequest.getRequestsToYou(player.getUniqueId());
        if (args.length == 2) {
            if (list.size() == 1) {
                TPRequest request = list.get(0);
                request.acceptRequest();
            } else if (list.isEmpty()) {
                sendErrorTranslation(player, "tport.command.requests.accept.noPending");
            } else {
                sendErrorTranslation(player, "tport.command.requests.accept.tooManyPending");
            }
        } else {
            playerLabel:
            for (int i = 2; i < args.length; i++) {
                UUID requesterUUID = PlayerUUID.getPlayerUUID(args[i]);
                if (requesterUUID == null || !tportData.getConfig().contains("tport." + requesterUUID)) {
                    sendErrorTranslation(player, "tport.command.playerNotFound", args[i]);
                    continue;
                }
                for (TPRequest request : list) {
                    if (request.getRequesterUUID().equals(requesterUUID)) {
                        request.acceptRequest();
                        list.remove(request); //only here so the next iteration this request won't be in the loop
                        continue playerLabel;
                    }
                }
                sendErrorTranslation(player, "tport.command.requests.accept.players.notRequesting", asPlayer(Bukkit.getPlayer(requesterUUID), requesterUUID));
            }
        }
    }
}
