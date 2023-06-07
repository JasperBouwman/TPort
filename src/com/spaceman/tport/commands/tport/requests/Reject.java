package com.spaceman.tport.commands.tport.requests;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tpEvents.TPRequest;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.requests.Accept.consentTabList;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Reject extends SubCommand {
    
    public Reject() {
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.OPTIONAL);
        emptyPlayer.setCommandDescription(formatInfoTranslation("tport.command.requests.reject.players.commandDescription"));
        emptyPlayer.setTabRunnable((args, player) -> consentTabList(player.getUniqueId(), Arrays.asList(args).subList(1, args.length)));
        emptyPlayer.setLooped(true);
        addAction(emptyPlayer);
        
        setCommandDescription(formatInfoTranslation("tport.command.requests.reject.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return consentTabList(player.getUniqueId(), Arrays.asList(args).subList(1, args.length));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport requests reject [player...]
        
        ArrayList<TPRequest> list = TPRequest.getRequestsToYou(player.getUniqueId());
        if (args.length == 2) {
            if (list.size() == 1) {
                TPRequest request = list.get(0);
                request.rejectRequest();
            } else if (list.isEmpty()) {
                sendErrorTranslation(player, "tport.command.requests.reject.noPending");
            } else {
                sendErrorTranslation(player, "tport.command.requests.reject.tooManyPending");
            }
        } else {
            playerLabel:
            for (int i = 2; i < args.length; i++) {
                UUID requesterUUID = PlayerUUID.getPlayerUUID(args[i], player);
                if (requesterUUID == null) {
                    continue;
                }
                for (TPRequest request : list) {
                    if (request.getRequesterUUID().equals(requesterUUID)) {
                        request.rejectRequest();
                        list.remove(request); //only here so the next iteration this request won't be in the loop
                        continue playerLabel;
                    }
                }
                sendErrorTranslation(player, "tport.command.requests.reject.players.notRequesting", asPlayer(requesterUUID));
            }
        }
    }
}
