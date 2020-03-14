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
    
    public static HashMap<UUID, PrevTPort> prevTPorts = new HashMap<>();
    
    public static String getPrevLocName(Player player) {
        ColorTheme ct = ColorTheme.getTheme(player);
        if (prevTPorts.containsKey(player.getUniqueId())) {
            return ct.getInfoColor() + "Previous location: " + ct.getVarInfoColor() + prevTPorts.get(player.getUniqueId()).toString(player);
        } else {
            return ct.getInfoColor() + "Previous location: Unknown";
        }
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
        
        if (prevTPorts.containsKey(player.getUniqueId())) {
            if (prevTPorts.get(player.getUniqueId()).tpBack(player)) {
                CooldownManager.Back.update(player);
            }
        } else {
            sendErrorTheme(player, "No previous location known");
        }
    }
    
    public enum PrevType {
        TPORT((player, prevTPort) -> {
            
            Location prevLoc = (Location) prevTPort.getData().get("prevLoc");
            String tportName = (String) prevTPort.getData().get("tportName");
            String tportOwner = (String) prevTPort.getData().get("tportOwner");
            
            if (prevLoc == null) {
                TPort tport = TPortManager.getTPort(UUID.fromString(tportOwner), tportName);
                if (tport != null) {
                    if (!hasPermission(player, true, "TPort.open", "TPort.basic")) {
                        return false;
                    }
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("TPORT", "tportName", tportName, "tportOwner", tportOwner, "prevLoc", player.getLocation()));
                    if (tport.teleport(player, true, false)) {
                        
                        if (Delay.delayTime(player) == 0) {
                            sendSuccessTheme(player, "Successfully teleported back to TPort %s", tportName);
                        } else {
                            sendSuccessTheme(player, "Successfully requested back teleportation back to TPort %s", tportName);
                        }
                        return true;
                    } else {
                        prevTPorts.put(player.getUniqueId(), new PrevTPort("TPORT", "tportName", tportName, "tportOwner", tportOwner, "prevLoc", null));
                        return false;
                    }
                } else {
                    sendErrorTheme(player, "Could not find TPort %s anymore", tportName);
                    return false;
                }
            } else {
                prevTPorts.put(player.getUniqueId(), new PrevTPort("TPORT", "tportName", tportName, "tportOwner", tportOwner, "prevLoc", null));
                requestTeleportPlayer(player, prevLoc);
    
                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported back from TPort %s", tportName);
                } else {
                    sendSuccessTheme(player, "Successfully requested back teleportation back from TPort %s", tportName);
                }
                return true;
            }
        }, (player, prevTPort) -> {
            String tportName = (String) prevTPort.getData().get("tportName");
            String tportOwner = (String) prevTPort.getData().get("tportOwner");
            TPort tport = TPortManager.getTPort(UUID.fromString(tportOwner), tportName);
            if (tport != null) {
                Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
                return (prevLoc == null ? "To" : "From") + " TPort" + tport.getName();
            } else {
                return "Unknown";
            }
        }),
        BIOME((player, prevTPort) -> {
            Location biomeLoc = (Location) prevTPort.getData().get("biomeLoc");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            String biomeName = (String) prevTPort.getData().get("biomeName");
            
            if (prevLoc == null) {
                prevTPorts.put(player.getUniqueId(), new PrevTPort("BIOME", "biomeLoc", biomeLoc, "prevLoc", player.getLocation(), "biomeName", biomeName));
                requestTeleportPlayer(player, biomeLoc);
    
                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported back to biome %s", biomeName);
                } else {
                    sendSuccessTheme(player, "Successfully requested back teleportation back to biome %s", biomeName);
                }
            } else {
                prevTPorts.put(player.getUniqueId(), new PrevTPort("BIOME", "biomeLoc", biomeLoc, "prevLoc", null, "biomeName", biomeName));
                requestTeleportPlayer(player, prevLoc);
    
                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported back from biome %s", biomeName);
                } else {
                    sendSuccessTheme(player, "Successfully requested back teleportation back from biome %s", biomeName);
                }
            }
            return true;
        }, (player, prevTPort) -> {
            String biomeName = (String) prevTPort.getData().get("biomeName");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To" : "From") + " biome " + biomeName;
        }),
        FEATURE((player, prevTPort) -> {
            Location featureLoc = (Location) prevTPort.getData().get("featureLoc");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            String featureName = (String) prevTPort.getData().get("featureName");
            
            if (prevLoc == null) {
                prevTPorts.put(player.getUniqueId(), new PrevTPort("FEATURE", "featureLoc", featureLoc, "prevLoc", player.getLocation(), "featureName", featureName));
                requestTeleportPlayer(player, featureLoc);

                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported back to %s", featureName);
                } else {
                    sendSuccessTheme(player, "Successfully requested back teleportation back to %s", featureName);
                }
            } else {
                prevTPorts.put(player.getUniqueId(), new PrevTPort("FEATURE", "featureLoc", featureLoc, "prevLoc", null, "featureName", featureName));
                requestTeleportPlayer(player, prevLoc);

                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported back from %s", featureName);
                } else {
                    sendSuccessTheme(player, "Successfully requested teleportation from %s", featureName);
                }
            }
            return true;
        }, (player, prevTPort) -> {
            String featureName = (String) prevTPort.getData().get("featureName");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To" : "From") + featureName;
        }),
        PLAYER((player, prevTPort) -> {
            Files tportData = GettingFiles.getFile("TPortData");
            String toPlayerUUID = (String) prevTPort.getData().get("playerUUID");
            
            Player toPlayer = Bukkit.getPlayer(UUID.fromString(toPlayerUUID));
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            
            if (prevLoc == null) {
                if (toPlayer != null) {
                    if (Objects.equals(tportData.getConfig().getString("tport." + toPlayerUUID + ".tp.statement"), "off")) {
                        ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                .getStringList("tport." + toPlayerUUID + "tp.players");
                        if (!list.contains(player.getUniqueId().toString())) {
                            sendErrorTheme(player, "%s has set his PLTP to off, and you are not whitelisted", toPlayer.getName());
                            return false;
                        }
                    }
                    
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("PLAYER", "playerUUID", toPlayerUUID, "prevLoc", player.getLocation()));
                    requestTeleportPlayer(player, toPlayer.getLocation());
                    
                    if (Delay.delayTime(player) == 0) {
                        sendSuccessTheme(player, "Successfully teleported back to player %s", toPlayer.getName());
                    } else {
                        sendSuccessTheme(player, "Successfully requested teleportation back to player %s", toPlayer.getName());
                    }
                    return true;
                } else {
                    sendErrorTheme(player, "Player %s is not online anymore", PlayerUUID.getPlayerName(toPlayerUUID));
                    return false;
                }
            } else {
                prevTPorts.put(player.getUniqueId(), new PrevTPort("PLAYER", "playerUUID", toPlayerUUID, "prevLoc", null));
                requestTeleportPlayer(player, prevLoc);

                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported back from player %s", PlayerUUID.getPlayerName(toPlayerUUID));
                } else {
                    sendSuccessTheme(player, "Successfully requested teleportation back from player %s", PlayerUUID.getPlayerName(toPlayerUUID));
                }
                return true;
            }
        }, (player, prevTPort) -> {
            String playerName = PlayerUUID.getPlayerName((String) prevTPort.getData().get("playerUUID"));
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To" : "From") + playerName;
        }),
        DEATH((player, prevTPort) -> {
            Location deathLoc = (Location) prevTPort.getData().get("deathLoc");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            
            if (prevLoc == null) {
                prevTPorts.put(player.getUniqueId(), new PrevTPort("DEATH", "deathLoc", deathLoc, "prevLoc", player.getLocation()));
                requestTeleportPlayer(player, deathLoc);
                
                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported back to your %s location", "death");
                } else {
                    sendSuccessTheme(player, "Successfully requested teleportation back to your %s location", "death");
                }
            } else {
                prevTPorts.put(player.getUniqueId(), new PrevTPort("DEATH", "deathLoc", deathLoc, "prevLoc", null));
                requestTeleportPlayer(player, prevLoc);
                
                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported back from your %s location", "death");
                } else {
                    sendSuccessTheme(player, "Successfully requested teleportation back from your %s location", "death");
                }
            }
            return true;
        }, (player, prevTPort) -> {
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To" : "From") + " death location";
        });
        
        private TPBack tpBack;
        private BackString backString;
        
        PrevType(TPBack tpBack, BackString backString) {
            this.tpBack = tpBack;
            this.backString = backString;
        }
        
        public boolean tpBack(Player player, PrevTPort prevTPort) {
            return tpBack.tpBack(player, prevTPort);
        }
        
        public String toString(Player player, PrevTPort prevTPort) {
            return backString.backString(player, prevTPort);
        }
        
        @FunctionalInterface
        private interface TPBack {
            boolean tpBack(Player player, PrevTPort prevTPort);
        }
        
        @FunctionalInterface
        private interface BackString {
            String backString(Player player, PrevTPort prevTPort);
        }
    }
    
    public static class PrevTPort {
        private HashMap<String, Object> data = new HashMap<>();
        private PrevType prevType;
        
        public PrevTPort(PrevType prevType, String name1, Object data1, String name2, Object data2) {
            this.data.put(name1, data1);
            this.data.put(name2, data2);
            this.prevType = prevType;
        }
        
        public PrevTPort(PrevType prevType, String name1, Object data1, String name2, Object data2, String name3, Object data3) {
            this.data.put(name1, data1);
            this.data.put(name2, data2);
            this.data.put(name3, data3);
            this.prevType = prevType;
        }
        
        public PrevTPort(String prevType, String name1, Object data1, String name2, Object data2) {
            this.data.put(name1, data1);
            this.data.put(name2, data2);
            this.prevType = PrevType.valueOf(prevType);
        }
        
        public PrevTPort(String prevType, String name1, Object data1, String name2, Object data2, String name3, Object data3) {
            this.data.put(name1, data1);
            this.data.put(name2, data2);
            this.data.put(name3, data3);
            this.prevType = PrevType.valueOf(prevType);
        }
        
        public boolean tpBack(Player player) {
            return prevType.tpBack(player, this);
        }
        
        public String toString(Player player) {
            return prevType.toString(player, this);
        }
        
        public HashMap<String, Object> getData() {
            return data;
        }
    }
}
