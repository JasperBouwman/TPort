package com.spaceman.tport.commands.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.events.InventoryClick.requestTeleportPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Back extends SubCommand {
    
    public static HashMap<UUID, PrevTPort> prevTPort = new HashMap<>();
    
    private static int tpBack(Player player) {
        /*return codes:
         * 0= no location known
         * 1= teleported
         * 2= player not online
         * 3= player not whitelisted
         * 4= invalid prevTPort value
         * 5= tport set to private*/
        
        if (!prevTPort.containsKey(player.getUniqueId())) {
            return 0;
        }
        
        PrevTPort prev = prevTPort.get(player.getUniqueId());
        Files tportData = GettingFiles.getFile("TPortData");
        if (prev.getL() != null) {
            
            prevTPort.put(player.getUniqueId(), new PrevTPort(prev.getTportName(), null, prev.getToPlayerUUID(), prev.getDeathLoc()));
            requestTeleportPlayer(player, prev.getL());
            return 1;
            
        } else if (prev.getTportName() != null) {
            TPort tport = TPortManager.getTPort(UUID.fromString(prev.getToPlayerUUID()), prev.getTportName());
            if (tport != null) {
                if (!hasPermission(player, false, "TPort.open", "TPort.basic")) {
                    return 4;
                }
                tport.teleport(player, true);
                prevTPort.put(player.getUniqueId(), new PrevTPort(prev.getTportName(), player.getLocation(), prev.getToPlayerUUID(), null));
                return 1;
            }
        } else if (prev.getToPlayerUUID() != null) {
            Player toPlayer = Bukkit.getPlayer(UUID.fromString(prev.getToPlayerUUID()));
            if (toPlayer != null) {
                if (Objects.equals(tportData.getConfig().getString("tport." + prev.getToPlayerUUID() + ".tp.statement"), "off")) {
                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                            .getStringList("tport." + prev.getToPlayerUUID() + "tp.players");
                    if (!list.contains(player.getUniqueId().toString())) {
                        return 3;
                    }
                }
                prevTPort.put(player.getUniqueId(), new PrevTPort(null, player.getLocation(), prev.getToPlayerUUID(), null));
                requestTeleportPlayer(player, toPlayer.getLocation());
                return 1;
            } else {
                return 2;
            }
        } else if (prev.getDeathLoc() != null) {
            prevTPort.put(player.getUniqueId(), new PrevTPort(null, player.getLocation(), null, prev.getDeathLoc()));
            requestTeleportPlayer(player, prev.getDeathLoc());
            return 1;
        }
        return 4;
    }
    
    public static String getPrevLocName(Player player) {
        ColorTheme theme = ColorTheme.getTheme(player);
        return theme.getInfoColor() + "Previous location: " + theme.getVarInfoColor() +
                (prevTPort.containsKey(player.getUniqueId()) ?
                        (prevTPort.get(player.getUniqueId()).getL() == null ? "To " : "From ") +
                                (prevTPort.get(player.getUniqueId()).getTportName() != null
                                        ? prevTPort.get(player.getUniqueId()).getTportName() :
                                        (prevTPort.get(player.getUniqueId()).getToPlayerUUID() != null ?
                                                PlayerUUID.getPlayerName(prevTPort.get(player.getUniqueId()).getToPlayerUUID()) :
                                                "death location")
                                ) :
                        "Unknown");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to teleport back to your previous location", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.back", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Collections.singletonList(ChatColor.stripColor(getPrevLocName(player)));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport back
        
        if (!hasPermission(player, "TPort.back", "TPort.basic")) {
            return;
        }
        if (!CooldownManager.Back.hasCooled(player)) {
            return;
        }
        
        switch (tpBack(player)) {
            case 1:
                if (Delay.delayTime(player) != 0) {
                    sendSuccessTheme(player, "Successfully requested back teleportation");
                } else {
                    sendSuccessTheme(player, "Successfully teleported back");
                }
                CooldownManager.Back.update(player);
                return;
            case 2:
                sendErrorTheme(player, "Player not online anymore");
                return;
            case 3:
                sendErrorTheme(player, "You are not whitelisted anymore");
                return;
            case 4:
                sendErrorTheme(player, "Could not teleport you back");
                return;
            case 0:
                sendErrorTheme(player, "No previous location known");
                return;
            default:
        }
    }
    
    @SuppressWarnings("WeakerAccess")
    public static class PrevTPort {
        private String tportName;
        private Location backLoc;
        private String toPlayerUUID;
        private Location deathLoc;
        
        public PrevTPort(String tportName, Location backLoc, String toPlayerUUID, Location deathLoc) {
            this.tportName = tportName;
            this.backLoc = backLoc;
            this.toPlayerUUID = toPlayerUUID;
            this.deathLoc = deathLoc;
        }
        
        public Location getL() {
            return backLoc;
        }
        
        public String getToPlayerUUID() {
            return toPlayerUUID;
        }
        
        public String getTportName() {
            return tportName;
        }
        
        public Location getDeathLoc() {
            return deathLoc;
        }
    }
}
