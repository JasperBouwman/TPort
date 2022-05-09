package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tpEvents.TPRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.tpEvents.TPEManager.tpPlayerToPlayer;

public class TP extends SubCommand {
    
    private final EmptyCommand emptyPlayer;
    
    public TP() {
        emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(formatInfoTranslation("tport.command.PLTP.TP.player.commandDescription"));
        emptyPlayer.setPermissions("TPort.PLTP.tp", "TPort.basic");
        addAction(emptyPlayer);
    }
    
    @Override
    public String getName(String arg) {
        return "tp";
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport PLTP tp <player>
        
        if (args.length == 3) {
            if (!emptyPlayer.hasPermissionToRun(player, true)) {
                return;
            }
            
            Files tportData = GettingFiles.getFile("TPortData");
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(args[2]);
            
            if (newPlayerUUID == null || !tportData.getConfig().contains("tport." + newPlayerUUID)) {
                sendErrorTranslation(player, "tport.command.playerNotFound", args[2]);
                return;
            }
            
            Player warp = Bukkit.getPlayer(newPlayerUUID);
            if (warp == null) {
                sendErrorTranslation(player, "tport.command.playerNotOnline", asPlayer(newPlayerUUID));
                return;
            }
            
            ArrayList<String> whitelist = (ArrayList<String>) tportData.getConfig().getStringList("tport." + warp.getUniqueId() + ".tp.players");
            if (!tportData.getConfig().getBoolean("tport." + warp.getUniqueId() + ".tp.statement", true)) {
                if (!whitelist.contains(player.getUniqueId().toString())) {
                    sendErrorTranslation(player, "tport.command.PLTP.TP.player.setOffNotWhitelisted", warp);
                    return;
                }
            }
            
            if (tportData.getConfig().getBoolean("tport." + warp.getUniqueId() + ".tp.consent", false)) {
                if (!whitelist.contains(player.getUniqueId().toString())) {
                    
                    if (!CooldownManager.PlayerTP.hasCooled(player, true)) {
                        return;
                    }
                    
                    if (TPRequest.hasRequest(player, true)) {
                        return;
                    }
                    TPRequest.createPLTPRequest(player.getUniqueId(), warp.getUniqueId());
                    
                    Message accept = formatTranslation(varInfoColor, varInfo2Color, "tport.command.requests.here");
                    accept.getText().forEach(t -> t
                            .addTextEvent(ClickEvent.runCommand("/tport requests accept " + player.getName()))
                            .addTextEvent(new HoverEvent(textComponent("/tport requests accept " + player.getName(), infoColor))));
                    Message reject = formatTranslation(varInfoColor, varInfo2Color, "tport.command.requests.here");
                    reject.getText().forEach(t -> t
                            .addTextEvent(ClickEvent.runCommand("/tport requests reject " + player.getName()))
                            .addTextEvent(new HoverEvent(textComponent("/tport requests reject " + player.getName(), infoColor))));
                    Message revoke = formatTranslation(varInfoColor, varInfo2Color, "tport.command.requests.here");
                    revoke.getText().forEach(t -> t
                            .addTextEvent(ClickEvent.runCommand("/tport requests revoke"))
                            .addTextEvent(new HoverEvent(textComponent("/tport requests revoke", infoColor))));
                    
                    sendInfoTranslation(warp, "tport.command.PLTP.TP.player.askConsent", player, accept, reject);
                    sendInfoTranslation(player, "tport.command.PLTP.TP.player.consentAsked", warp, "true", revoke);
                    
                    return;
                }
            }
            
            tp(player, warp);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP tp <player>");
        }
    }
    
    public static boolean tp(Player player, Player toPlayer) {
        if (!CooldownManager.PlayerTP.hasCooled(player)) {
            return false;
        }
        
        tpPlayerToPlayer(player, toPlayer, () -> {
                    sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.PLTP.TP.player.succeeded", toPlayer);
                    sendInfoTranslation(Bukkit.getPlayer(toPlayer.getUniqueId()), "tport.command.PLTP.TP.player.succeededOtherPlayer", player);
                },
                ((p, delay, tickMessage, seconds, secondMessage) -> {
                    sendSuccessTranslation(p, "tport.command.PLTP.TP.player.succeededRequested", toPlayer, delay, tickMessage, seconds, secondMessage);
                    sendInfoTranslation(toPlayer, "tport.command.PLTP.TP.player.succeededOtherPlayerRequested", p, delay, tickMessage, seconds, secondMessage);
                }));
        CooldownManager.PlayerTP.update(player);
        return true;
    }
}
