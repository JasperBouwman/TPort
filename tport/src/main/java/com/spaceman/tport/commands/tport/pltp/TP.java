package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.SafetyCheck;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tpEvents.TPRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.PLTP;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.tpEvents.TPEManager.tpPlayerToPlayer;

public class TP extends SubCommand {
    
    private final EmptyCommand emptyPlayer;
    
    public TP() {
        EmptyCommand emptyPlayerSafetyCheck = new EmptyCommand() {
            @Override
            public Message permissionsHover() {
                return formatInfoTranslation("tport.command.PLTP.TP.player.safetyCheck.permissionHover", "TPort.PLTP.tp", PLTP.getPermission(), "TPort.basic");
            }
        };
        emptyPlayerSafetyCheck.setCommandName("safetyCheck", ArgumentType.OPTIONAL);
        emptyPlayerSafetyCheck.setCommandDescription(formatInfoTranslation("tport.command.PLTP.TP.player.safetyCheck.commandDescription"));
        emptyPlayerSafetyCheck.setPermissions("TPort.own", PLTP.getPermission(), "TPort.basic");
        
        emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(formatInfoTranslation("tport.command.PLTP.TP.player.commandDescription"));
        emptyPlayer.setPermissions("TPort.PLTP.tp", "TPort.basic");
        emptyPlayer.setTabRunnable((args, player) -> {
            if (emptyPlayerSafetyCheck.hasPermissionToRun(player, false)) {
                return List.of("true", "false");
            }
            return Collections.emptyList();
        });
        emptyPlayer.addAction(emptyPlayerSafetyCheck);
        
        addAction(emptyPlayer);
    }
    
    @Override
    public String getName(String arg) {
        return "tp";
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        if (!emptyPlayer.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport PLTP tp <player> [safetyCheck]
        
        if (args.length != 3 && args.length != 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP tp <player> [safetyCheck]");
            return;
        }
        if (!emptyPlayer.hasPermissionToRun(player, true)) {
            return;
        }
        
        UUID newPlayerUUID = PlayerUUID.getPlayerUUID(args[2], player);
        if (newPlayerUUID == null) {
            return;
        }
        Player warpTo = Bukkit.getPlayer(newPlayerUUID);
        if (warpTo == null) {
            sendErrorTranslation(player, "tport.command.playerNotOnline", asPlayer(newPlayerUUID));
            return;
        }
        
        ArrayList<String> whitelist = Whitelist.getPLTPWhitelist(warpTo);
        if (!State.getPLTPState(warpTo)) {
            if (!whitelist.contains(player.getUniqueId().toString())) {
                sendErrorTranslation(player, "tport.command.PLTP.TP.player.setOffNotWhitelisted", warpTo);
                return;
            }
        }
        
        Boolean safetyCheckState;
        if (args.length == 4) {
            if (PLTP.hasPermission(player, true)) {
                safetyCheckState = Main.toBoolean(args[3]);
                if (safetyCheckState == null) {
                    sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP tp <player> [true|false]");
                    return;
                }
            } else {
                return;
            }
        } else {
            safetyCheckState = PLTP.getState(player);
        }
        
        if (Consent.shouldAskConsent(warpTo)) {
            if (!whitelist.contains(player.getUniqueId().toString())) {
                
                if (!CooldownManager.PlayerTP.hasCooled(player, true)) {
                    return;
                }
                
                if (TPRequest.hasRequest(player, true)) {
                    return;
                }
                TPRequest.createPLTPRequest(player.getUniqueId(), warpTo.getUniqueId(), safetyCheckState);
                
                Message accept = formatTranslation(varInfoColor, varInfo2Color, "tport.command.requests.here");
                accept.addTextEvent(ClickEvent.runCommand("/tport requests accept " + player.getName()));
                accept.addTextEvent(new HoverEvent(textComponent("/tport requests accept " + player.getName(), infoColor)));
                
                Message reject = formatTranslation(varInfoColor, varInfo2Color, "tport.command.requests.here");
                reject.addTextEvent(ClickEvent.runCommand("/tport requests reject " + player.getName()));
                reject.addTextEvent(new HoverEvent(textComponent("/tport requests reject " + player.getName(), infoColor)));
                
                Message revoke = formatTranslation(varInfoColor, varInfo2Color, "tport.command.requests.here");
                revoke.addTextEvent(ClickEvent.runCommand("/tport requests revoke"));
                revoke.addTextEvent(new HoverEvent(textComponent("/tport requests revoke", infoColor)));
                
                sendInfoTranslation(warpTo, "tport.command.PLTP.TP.player.askConsent", player, accept, reject);
                sendInfoTranslation(player, "tport.command.PLTP.TP.player.consentAsked", warpTo, "true", revoke);
                
                return;
            }
        }
        
        tp(player, warpTo, safetyCheckState, false);
    }
    
    public static boolean tp(Player player, Player toPlayer, boolean safetyCheck, boolean requested) {
        if (!CooldownManager.PlayerTP.hasCooled(player)) {
            return false;
        }
        
        if (safetyCheck && !SafetyCheck.isSafe(Offset.getPLTPOffset(player).applyOffset(toPlayer.getLocation()))) {
            sendErrorTranslation(player, "tport.command.PLTP.TP.player.notSafe", asPlayer(toPlayer));
            if (requested) sendErrorTranslation(toPlayer, "tport.command.PLTP.TP.player.notSafeOtherPlayer", asPlayer(player));
            return false;
        }
        
        tpPlayerToPlayer(player, toPlayer, () -> {
                    sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.PLTP.TP.player.succeeded", asPlayer(toPlayer));
                    sendInfoTranslation(Bukkit.getPlayer(toPlayer.getUniqueId()), "tport.command.PLTP.TP.player.succeededOtherPlayer", asPlayer(player));
                },
                ((p, delay, tickMessage, seconds, secondMessage) -> {
                    sendSuccessTranslation(p, "tport.command.PLTP.TP.player.succeededRequested", asPlayer(toPlayer), delay, tickMessage, seconds, secondMessage);
                    sendInfoTranslation(toPlayer, "tport.command.PLTP.TP.player.succeededOtherPlayerRequested", p, delay, tickMessage, seconds, secondMessage);
                }));
        CooldownManager.PlayerTP.update(player);
        return true;
    }
}
