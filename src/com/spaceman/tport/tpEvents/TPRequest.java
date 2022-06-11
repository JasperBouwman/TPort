package com.spaceman.tport.tpEvents;

import com.spaceman.tport.commands.tport.pltp.TP;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class TPRequest {
    
    //player UUID
    private static final HashMap<UUID, TPRequest> requests = new HashMap<>();
    
    private final UUID requesterUUID;
    
    //if this is a PLTP request the 'requestToUUID' should be the player's UUID, if it's a TPort request it should be the owner of the TPort
    private final UUID requestToUUID; //the UUID of the requester
    
    //if 'null' it's a PLTP request, if 'nonnull' it should be the UUID of the TPort
    private final UUID tportUUID;
    
    private TPRequest(UUID requesterUUID, UUID requestToUUID) {
        this.requesterUUID = requesterUUID;
        this.requestToUUID = requestToUUID;
        this.tportUUID = null;
    }
    private TPRequest(UUID requesterUUID, UUID tportOwner, UUID tportUUID) {
        this.requesterUUID = requesterUUID;
        this.requestToUUID = tportOwner;
        this.tportUUID = tportUUID;
    }
    
    public static TPRequest createPLTPRequest(UUID requester, UUID requestTo) {
        TPRequest request = new TPRequest(requester, requestTo);
        requests.put(requester, request);
        return request;
    }
    public static TPRequest createTPortRequest(UUID requester, TPort tport) {
        return createTPortRequest(requester, tport.getOwner(), tport.getTportID());
    }
    public static TPRequest createTPortRequest(UUID requester, UUID tportOwner, UUID tportUUID) {
        TPRequest request = new TPRequest(requester, tportOwner, tportUUID);
        requests.put(requester, request);
        return request;
    }
    
    public UUID getRequesterUUID() {
        return requesterUUID;
    }
    public UUID getRequestToUUID() {
        return requestToUUID;
    }
    public boolean isPLTPRequest() {
        return tportUUID == null;
    }
    public boolean isTPortRequest() {
        return tportUUID != null;
    }
    @Nullable
    public UUID getTPortUUID() {
        return tportUUID;
    }
    
    public void acceptRequest() {
        requests.remove(requesterUUID);
        
        Player requester = Bukkit.getPlayer(requesterUUID);
        Player requestTo = Bukkit.getPlayer(requestToUUID);
        
        if (isTPortRequest()) {
            TPort tport = TPortManager.getTPort(requestToUUID, tportUUID);
            if (tport == null) {
                //something went wrong
                return;
            }
            
            sendSuccessTranslation(requestTo, "tport.tpEvents.TPRequest.accept.tport.requestTo", asPlayer(requester, requesterUUID), asTPort(tport));
            sendInfoTranslation(requester,    "tport.tpEvents.TPRequest.accept.tport.requester", asPlayer(requestTo, requestToUUID), asTPort(tport));
            
            if (!tport.teleport(requester,
                    false /*safetyCheck was already preformed before asking consent*/,
                    false, null, null)) {
                sendErrorTranslation(requestTo, "tport.tpEvents.TPRequest.accept.tport.couldNotTP", asPlayer(requester, requesterUUID));
            }
        } else {
            sendSuccessTranslation(requestTo,"tport.tpEvents.TPRequest.accept.pltp.requestTo", asPlayer(requester, requesterUUID));
            sendInfoTranslation(requester,   "tport.tpEvents.TPRequest.accept.pltp.requester", asPlayer(requestTo, requestToUUID));
            TP.tp(requester, requestTo);
        }
    }
    public void rejectRequest() {
        requests.remove(requesterUUID);
        
        Player requester = Bukkit.getPlayer(requesterUUID);
        Player requestTo = Bukkit.getPlayer(requestToUUID);
        
        if (isTPortRequest()) {
            sendSuccessTranslation(requestTo, "tport.tpEvents.TPRequest.reject.tport.requestTo", asPlayer(requester, requesterUUID), asTPort(tportUUID, requestToUUID));
            sendInfoTranslation(requester,    "tport.tpEvents.TPRequest.reject.tport.requester", asPlayer(requestTo, requestToUUID), asTPort(tportUUID, requestToUUID));
        } else {
            sendSuccessTranslation(requestTo, "tport.tpEvents.TPRequest.reject.pltp.requestTo", asPlayer(requester, requesterUUID));
            sendInfoTranslation(requester,    "tport.tpEvents.TPRequest.reject.pltp.requester", asPlayer(requestTo, requestToUUID));
        }
    }
    public void revokeRequest() {
        requests.remove(requesterUUID);
        
        Player requester = Bukkit.getPlayer(requesterUUID);
        Player requestTo = Bukkit.getPlayer(requestToUUID);
        
        if (isTPortRequest()) {
            sendSuccessTranslation(requestTo, "tport.tpEvents.TPRequest.revoke.tport.requestTo", asPlayer(requester, requesterUUID), asTPort(tportUUID, requestToUUID));
            sendInfoTranslation(requester,    "tport.tpEvents.TPRequest.revoke.tport.requester", asPlayer(requestTo, requestToUUID), asTPort(tportUUID, requestToUUID));
        } else {
            sendSuccessTranslation(requestTo, "tport.tpEvents.TPRequest.revoke.pltp.requestTo", asPlayer(requester, requesterUUID));
            sendInfoTranslation(requester,    "tport.tpEvents.TPRequest.revoke.pltp.requester", asPlayer(requestTo, requestToUUID));
        }
    }
    
    public static boolean hasRequest(Player player, boolean sendError) {
        TPRequest request = getRequest(player.getUniqueId());
        if (request == null) {
            return false;
        } else {
            if (sendError) {
                Message revoke = formatTranslation(varErrorColor, varError2Color, "tport.command.requests.here");
                revoke.getText().forEach(t -> t
                        .addTextEvent(ClickEvent.runCommand("/tport requests revoke"))
                        .addTextEvent(new HoverEvent(textComponent("/tport requests revoke", infoColor))));
                sendErrorTranslation(player, "tport.tpEvents.TPRequest.hasRequest.error", revoke, request.toError());
            }
            return true;
        }
    }
    public static TPRequest getRequest(UUID uuid) {
        return requests.get(uuid);
    }
    public static ArrayList<TPRequest> getRequestsToYou(UUID uuid) {
        ArrayList<TPRequest> list = new ArrayList<>();
        for (Map.Entry<UUID, TPRequest> entry : requests.entrySet()) {
            if (entry.getValue().getRequestToUUID().equals(uuid)) {
                list.add(entry.getValue());
            }
        }
        return list;
    }
    public static void playerLeft(Player player) {
        for (Map.Entry<UUID, TPRequest> entry : requests.entrySet()) {
            UUID requesterUUID = entry.getKey();
            TPRequest request = entry.getValue();
            
            if (requesterUUID.equals(player.getUniqueId())) { //requester left
                requests.remove(requesterUUID);
                
                Player requestTo = Bukkit.getPlayer(request.getRequestToUUID());
                if (request.isTPortRequest()) {
                    sendInfoTranslation(requestTo, "tport.tpEvents.TPRequest.requesterLeft.tport", asPlayer(player, player.getUniqueId()), asTPort(request.tportUUID, request.requestToUUID));
                } else {
                    sendInfoTranslation(requestTo, "tport.tpEvents.TPRequest.requesterLeft.pltp", asPlayer(player, player.getUniqueId()));
                }
            } else if (request.getRequestToUUID().equals(player.getUniqueId())) { //request to player left
                requests.remove(requesterUUID);
                
                Player requester = Bukkit.getPlayer(requesterUUID);
                if (request.isTPortRequest()) {
                    sendInfoTranslation(requester, "tport.tpEvents.TPRequest.requestToLeft.tport", asTPort(request.tportUUID, request.requestToUUID), asPlayer(player, player.getUniqueId()));
                } else {
                    sendInfoTranslation(requester, "tport.tpEvents.TPRequest.requestToLeft.pltp", asPlayer(player, player.getUniqueId()));
                }
            }
        }
    }
    public static void tportRemoved(TPort tport) {
        for (Map.Entry<UUID, TPRequest> entry : requests.entrySet()) {
            UUID requesterUUID = entry.getKey();
            TPRequest request = entry.getValue();
            
            if (request.isTPortRequest()) {
                if (tport.getTportID().equals(request.getTPortUUID())) {
                    requests.remove(requesterUUID);
                    
                    Player requester = Bukkit.getPlayer(requesterUUID);
                    Player requestTo = Bukkit.getPlayer(request.getRequestToUUID());
                    sendInfoTranslation(requester, "tport.tpEvents.TPRequest.tportRemoved.requester", tport);
                    sendInfoTranslation(requestTo, "tport.tpEvents.TPRequest.tportRemoved.requestTo", requester, tport);
                }
            }
        }
    }

    public Message toInfo() {
        Message description = new Message();
        String type;
        Object requestTo;
        if (isTPortRequest()) {
            type = "TPort";
            requestTo = asTPort(getTPortUUID(), getRequestToUUID());
        } else {
            type = "PLTP";
            requestTo = asPlayer(getRequestToUUID());
        }
        return formatInfoTranslation("tport.fancyMessage.MessageUtils.tpRequest.description", type, requestTo);
    }
    public Message toInfo2() {
        Message description = new Message();
        String type;
        Object requestTo;
        if (isTPortRequest()) {
            type = "TPort";
            requestTo = asTPort(getTPortUUID(), getRequestToUUID());
        } else {
            type = "PLTP";
            requestTo = asPlayer(getRequestToUUID());
        }
        return formatTranslation(infoColor, varInfo2Color, "tport.fancyMessage.MessageUtils.tpRequest.description", type, requestTo);
    }
    public Message toError() {
        Message description = new Message();
        String type;
        Object requestTo;
        if (isTPortRequest()) {
            type = "TPort";
            requestTo = asTPort(getTPortUUID(), getRequestToUUID());
        } else {
            type = "PLTP";
            requestTo = asPlayer(getRequestToUUID());
        }
        return formatErrorTranslation("tport.fancyMessage.MessageUtils.tpRequest.description", type, requestTo);
    }
    public Message toSuccess() {
        Message description = new Message();
        String type;
        Object requestTo;
        if (isTPortRequest()) {
            type = "TPort";
            requestTo = asTPort(getTPortUUID(), getRequestToUUID());
        } else {
            type = "PLTP";
            requestTo = asPlayer(getRequestToUUID());
        }
        return formatSuccessTranslation("tport.fancyMessage.MessageUtils.tpRequest.description", type, requestTo);
    }
}
