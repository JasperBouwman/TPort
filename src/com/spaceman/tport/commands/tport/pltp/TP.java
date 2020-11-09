package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Delay;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.pltp.Consent.pltpConsentMap;
import static com.spaceman.tport.events.InventoryClick.tpPlayerToPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class TP extends SubCommand {
    
    private final EmptyCommand emptyPlayer;
    
    public TP() {
        emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(textComponent("This command is used to teleport to players, but only if the given player has his PLTP on," +
                        " when it's turned off only players in his whitelist can teleport", infoColor));
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
            
            Player warp = Bukkit.getPlayer(args[2]);
            if (warp == null) {
                sendErrorTheme(player, "Player %s is not online", args[2]);
                return;
            }
            
            Files tportData = getFile("TPortData");
            
            ArrayList<String> whitelist = (ArrayList<String>) tportData.getConfig().getStringList("tport." + warp.getUniqueId() + ".tp.players");
            if (!tportData.getConfig().getBoolean("tport." + warp.getUniqueId() + ".tp.statement", true)) {
                if (!whitelist.contains(player.getUniqueId().toString())) {
                    sendErrorTheme(player, "%s has set his PLTP to off, and you are not whitelisted", warp.getName());
                    return;
                }
            }
            if (tportData.getConfig().getBoolean("tport." + warp.getUniqueId() + ".tp.consent", false)) {
                if (!whitelist.contains(player.getUniqueId().toString())) {
                    Message message = new Message();
                    message.addText(textComponent("Player ", infoColor));
                    message.addText(textComponent(player.getName(), varInfoColor));
                    message.addText(textComponent(" wants to teleport to you, click ", infoColor));
                    message.addText(textComponent("here", varInfoColor,
                            ClickEvent.runCommand("/tport PLTP accept " + player.getName()), new HoverEvent(textComponent("/tport PLTP accept " + player.getName(), infoColor))));
                    message.addText(textComponent(" to accept, click ", infoColor));
                    message.addText(textComponent("here", varInfoColor,
                            ClickEvent.runCommand("/tport PLTP reject " + player.getName()), new HoverEvent(textComponent("/tport PLTP reject " + player.getName(), infoColor))));
                    message.addText(textComponent(" to reject", infoColor));
                    message.sendMessage(warp);
                    
                    ArrayList<UUID> list = pltpConsentMap.getOrDefault(warp.getUniqueId(), new ArrayList<>());
                    if (!list.contains(player.getUniqueId())) {
                        list.add(player.getUniqueId());
                        pltpConsentMap.put(warp.getUniqueId(), list);
                    }
                    
                    sendInfoTheme(player, "Player %s has set his PLTP consent state to %s, it is asked", warp.getName(), "true");
                    return;
                }
            }
            
            tp(player, warp);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport PLTP tp <player>");
        }
    }
    
    public static boolean tp(Player player, Player toPlayer) {
        if (!CooldownManager.PlayerTP.hasCooled(player)) {
            return false;
        }
        
        tpPlayerToPlayer(player, toPlayer, () -> {
            sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Teleported to %s", toPlayer.getName());
            sendInfoTheme(Bukkit.getPlayer(toPlayer.getUniqueId()), "Player %s has teleported to you", player.getName());
        });
        int delay = Delay.delayTime(player);
        if (delay == 0) {
            sendSuccessTheme(player, "Successfully teleported to %s", toPlayer.getName());
            sendInfoTheme(toPlayer, "Player %s has teleported to you", player.getName());
        } else {
            sendSuccessTheme(player, "Successfully requested teleportation to %s, delay time is %s ticks", toPlayer.getName(), delay);
            sendInfoTheme(toPlayer, "Player %s has requested to teleported to you, delay time is %s ticks", player.getName(), delay);
        }
        CooldownManager.PlayerTP.update(player);
        return true;
    }
}
