package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.colorFormatter.ColorTheme;
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

import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.commands.tport.pltp.Consent.pltpConsentMap;
import static com.spaceman.tport.events.InventoryClick.tpPlayerToPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class TP extends SubCommand {
    
    public TP() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("player", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(textComponent("This command is used to teleport to players, but only if the given player has his PLTP on," +
                " when it's turned off only players in his whitelist can teleport", infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.PLTP.tp", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        addAction(emptyCommand);
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
            if (!hasPermission(player, true, true, "TPort.PLTP.tp", "TPort.basic")) {
                return;
            }
            
            Player warp = Bukkit.getPlayer(args[2]);
            if (warp == null) {
                sendErrorTheme(player, "Player %s is not online");
                return;
            }
            
            Files tportData = getFile("TPortData");
            
            if (!tportData.getConfig().getBoolean("tport." + warp.getUniqueId() + ".tp.statement", true)) {
                ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + warp.getUniqueId() + ".tp.players");
                if (!list.contains(player.getUniqueId().toString())) {
                    sendErrorTheme(player, "%s has set his PLTP to off, and you are not whitelisted", warp.getName());
                    return;
                }
            }
            if (tportData.getConfig().getBoolean("tport." + warp.getUniqueId() + ".tp.consent", false)) {
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
            
            if (!CooldownManager.PlayerTP.hasCooled(player)) {
                return;
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
    
        tpPlayerToPlayer(player, toPlayer);
        if (Delay.delayTime(player) == 0) {
            sendInfoTheme(player, "Teleported to %s", toPlayer.getName());
        } else {
            sendInfoTheme(player, "Requested teleportation to %s", toPlayer.getName());
        }
        CooldownManager.PlayerTP.update(player);
        return true;
    }
}
