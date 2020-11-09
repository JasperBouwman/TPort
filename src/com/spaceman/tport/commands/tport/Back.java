package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
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

import static com.spaceman.tport.events.InventoryClick.requestTeleportPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Back extends SubCommand {
    
    public static HashMap<UUID, PrevTPort> prevTPorts = new HashMap<>();
    
    public static String getPrevLocName(Player player) {
        ColorTheme ct = ColorTheme.getTheme(player);
        if (prevTPorts.containsKey(player.getUniqueId())) {
            return ct.getInfoColor() + "Previous location: " + ct.getVarInfoColor() + prevTPorts.get(player.getUniqueId()).toString(player);
        } else {
            return ct.getInfoColor() + "Previous location: " + ct.getVarInfoColor() + "Unknown";
        }
    }
    
    public Back() {
        EmptyCommand emptySafetyCheck = new EmptyCommand(){
            @Override
            public Message permissionsHover() {
                Message message = new Message();
                message.addText(
                        textComponent("Permissions: (", infoColor),
                        textComponent("TPort.back", varInfoColor),
                        textComponent(" and ", infoColor),
                        textComponent("TPort.safetyCheck", varInfoColor),
                        textComponent(") or ", infoColor),
                        textComponent("TPort.basic", varInfoColor));
                return message;
            }
        };
        emptySafetyCheck.setCommandName("safetyCheck", ArgumentType.OPTIONAL);
        emptySafetyCheck.setCommandDescription(textComponent("This command is used to teleport back to your previous location, ", infoColor),
                textComponent("the safetyCheck argument overrides your default value", infoColor));
        emptySafetyCheck.setPermissions(Open.emptyPlayerTPortSafetyCheck.getPermissions());
        addAction(emptySafetyCheck);
        
        this.setPermissions("TPort.back", "TPort.basic");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to teleport back to your previous location", infoColor));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false", ChatColor.stripColor(getPrevLocName(player)));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport back [safetyCheck]
        
        if (!hasPermissionToRun(player, true)) {
            return;
        }
        if (!CooldownManager.Back.hasCooled(player)) {
            return;
        }
        
        if (prevTPorts.containsKey(player.getUniqueId())) {
            boolean safetyCheck;
            if (args.length == 2) {
                if (SafetyCheck.emptySafetyCheck.hasPermissionToRun(player, true)) {
                    safetyCheck = Boolean.parseBoolean(args[1]);
                } else {
                    return;
                }
            } else {
                safetyCheck = SafetyCheck.safetyCheck(player);
            }
            if (prevTPorts.get(player.getUniqueId()).tpBack(player, safetyCheck)) {
                CooldownManager.Back.update(player);
            }
        } else {
            sendErrorTheme(player, "No previous location known");
        }
    }
    
    public enum PrevType {
        TPORT((player, prevTPort, safetyCheck) -> {
            
            Location prevLoc = (Location) prevTPort.getData().get("prevLoc");
            UUID tportUUID = UUID.fromString((String) prevTPort.getData().get("tportUUID"));
            String tportOwner = (String) prevTPort.getData().get("tportOwner");
    
            TPort tport = TPortManager.getTPort(UUID.fromString(tportOwner), tportUUID);
            if (prevLoc == null) {
                if (tport != null) {
                    if (!Open.emptyPlayerTPort.hasPermissionToRun(player, true)) {
                        return false;
                    }
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("TPORT", "tportName", tport.getName(), "tportOwner", tportOwner, "prevLoc", player.getLocation()));
                    if (tport.teleport(player, true, false, safetyCheck, () -> sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported back to TPort %s", tport.getName()))) {
    
                        int delay = Delay.delayTime(player);
                        if (delay == 0) {
                            sendSuccessTheme(player, "Successfully teleported back to TPort %s", tport.getName());
                        } else {
                            sendSuccessTheme(player, "Successfully requested back teleportation back to TPort %s, delay time is %s ticks", tport.getName(), delay);
                        }
                        return true;
                    } else {
                        prevTPorts.put(player.getUniqueId(), new PrevTPort("TPORT", "tportUUID", tportUUID.toString(), "tportOwner", tportOwner, "prevLoc", null));
                        return false;
                    }
                } else {
                    sendErrorTheme(player, "Could not find TPort anymore, possibly deleted");
                    return false;
                }
            } else {
                if (!safetyCheck || SafetyCheck.isSafe(prevLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("TPORT", "tportUUID", tportUUID.toString(), "tportOwner", tportOwner, "prevLoc", null));
                    requestTeleportPlayer(player, prevLoc, () -> {
                        if (tport != null) {
                            sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported back from TPort %s", tport.getName());
                        } else {
                            sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported back from TPort");
                        }
                    });
                    int delay = Delay.delayTime(player);
                    if (tport != null) {
                        if (delay == 0) {
                            sendSuccessTheme(player, "Successfully teleported back from TPort %s", tport.getName());
                        } else {
                            sendSuccessTheme(player, "Successfully requested back teleportation back from TPort %s, delay time is %s ticks", tport.getName(), delay);
                        }
                    } else {
                        if (delay == 0) {
                            sendSuccessTheme(player, "Successfully teleported back from TPort");
                        } else {
                            sendSuccessTheme(player, "Successfully requested back teleportation back from TPort, delay time is %s ticks", delay);
                        }
                    }
                } else {
                    sendErrorTheme(player, "Its not safe to teleport back from TPort %s", tport.getName());
                    return false;
                }
                return true;
            }
        }, (player, prevTPort) -> {
            UUID tportUUID = UUID.fromString((String) prevTPort.getData().get("tportUUID"));
            String tportOwner = (String) prevTPort.getData().get("tportOwner");
            TPort tport = TPortManager.getTPort(UUID.fromString(tportOwner), tportUUID);
            if (tport != null) {
                Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
                return (prevLoc == null ? "To" : "From") + " TPort " + tport.getName();
            } else {
                return "Unknown";
            }
        }),
        BIOME((player, prevTPort, safetyCheck) -> {
            Location biomeLoc = (Location) prevTPort.getData().get("biomeLoc");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            String biomeName = (String) prevTPort.getData().get("biomeName");
            
            if (prevLoc == null) {
                if (!safetyCheck || SafetyCheck.isSafe(biomeLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("BIOME", "biomeLoc", biomeLoc, "prevLoc", player.getLocation(), "biomeName", biomeName));
                    requestTeleportPlayer(player, biomeLoc, () -> sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported back to biome %s", biomeName));
    
                    int delay = Delay.delayTime(player);
                    if (delay == 0) {
                        sendSuccessTheme(player, "Successfully teleported back to biome %s", biomeName);
                    } else {
                        sendSuccessTheme(player, "Successfully requested back teleportation back to biome %s, delay time is %s ticks", biomeName, delay);
                    }
                } else {
                    sendErrorTheme(player, "Its not safe to teleport back to biome %s", biomeName);
                    return false;
                }
            } else {
                if (!safetyCheck || SafetyCheck.isSafe(prevLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("BIOME", "biomeLoc", biomeLoc, "prevLoc", null, "biomeName", biomeName));
                    requestTeleportPlayer(player, prevLoc, () -> sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported back from biome %s", biomeName));
    
                    int delay = Delay.delayTime(player);
                    if (delay == 0) {
                        sendSuccessTheme(player, "Successfully teleported back from biome %s", biomeName);
                    } else {
                        sendSuccessTheme(player, "Successfully requested back teleportation back from biome %s, delay time is %s ticks", biomeName, delay);
                    }
                } else {
                    sendErrorTheme(player, "Its not safe to teleport back from biome %s", biomeName);
                    return false;
                }
            }
            return true;
        }, (player, prevTPort) -> {
            String biomeName = (String) prevTPort.getData().get("biomeName");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To" : "From") + " biome " + biomeName;
        }),
        FEATURE((player, prevTPort, safetyCheck) -> {
            Location featureLoc = (Location) prevTPort.getData().get("featureLoc");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            String featureName = (String) prevTPort.getData().get("featureName");
            
            if (prevLoc == null) {
                if (!safetyCheck || SafetyCheck.isSafe(featureLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("FEATURE", "featureLoc", featureLoc, "prevLoc", player.getLocation(), "featureName", featureName));
                    requestTeleportPlayer(player, featureLoc, () -> sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported back to %s", featureName));
    
                    int delay = Delay.delayTime(player);
                    if (delay == 0) {
                        sendSuccessTheme(player, "Successfully teleported back to %s", featureName);
                    } else {
                        sendSuccessTheme(player, "Successfully requested back teleportation back to %s, delay time is %s ticks", featureName, delay);
                    }
                } else {
                    sendErrorTheme(player, "Its not safe to teleport back to %s", featureName);
                    return false;
                }
            } else {
                if (!safetyCheck || SafetyCheck.isSafe(prevLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("FEATURE", "featureLoc", featureLoc, "prevLoc", null, "featureName", featureName));
                    requestTeleportPlayer(player, prevLoc, () -> sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported back from %s", featureName));
    
                    int delay = Delay.delayTime(player);
                    if (delay == 0) {
                        sendSuccessTheme(player, "Successfully teleported back from %s", featureName);
                    } else {
                        sendSuccessTheme(player, "Successfully requested teleportation from %s, delay time is %s ticks", featureName, delay);
                    }
                } else {
                    sendErrorTheme(player, "Its not safe to teleport back from %s", featureName);
                    return false;
                }
            }
            return true;
        }, (player, prevTPort) -> {
            String featureName = (String) prevTPort.getData().get("featureName");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To " : "From ") + featureName;
        }),
        PLAYER((player, prevTPort, safetyCheck) -> {
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
                    
                    if (!safetyCheck || SafetyCheck.isSafe(toPlayer.getLocation())) {
                        prevTPorts.put(player.getUniqueId(), new PrevTPort("PLAYER", "playerUUID", toPlayerUUID, "prevLoc", player.getLocation()));
                        requestTeleportPlayer(player, toPlayer.getLocation(), () -> sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported back to player %s", toPlayer.getName()));
    
                        int delay = Delay.delayTime(player);
                        if (delay == 0) {
                            sendSuccessTheme(player, "Successfully teleported back to player %s", toPlayer.getName());
                        } else {
                            sendSuccessTheme(player, "Successfully requested teleportation back to player %s, delay time is %s ticks", toPlayer.getName(), delay);
                        }
                    } else {
                        sendErrorTheme(player, "Its not safe to teleport back to player %s", toPlayer.getName());
                        return false;
                    }
                    return true;
                } else {
                    sendErrorTheme(player, "Player %s is not online anymore", PlayerUUID.getPlayerName(toPlayerUUID));
                    return false;
                }
            } else {
                if (!safetyCheck || SafetyCheck.isSafe(prevLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("PLAYER", "playerUUID", toPlayerUUID, "prevLoc", null));
                    requestTeleportPlayer(player, prevLoc, () ->
                            sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported back from player %s", PlayerUUID.getPlayerName(toPlayerUUID)));
    
                    int delay = Delay.delayTime(player);
                    if (delay == 0) {
                        sendSuccessTheme(player, "Successfully teleported back from player %s", PlayerUUID.getPlayerName(toPlayerUUID));
                    } else {
                        sendSuccessTheme(player, "Successfully requested teleportation back from player %s, delay time is %s ticks", PlayerUUID.getPlayerName(toPlayerUUID), delay);
                    }
                } else {
                    sendErrorTheme(player, "Its not safe to teleport back from player %s", PlayerUUID.getPlayerName(toPlayerUUID));
                    return false;
                }
                return true;
            }
        }, (player, prevTPort) -> {
            String playerName = PlayerUUID.getPlayerName((String) prevTPort.getData().get("playerUUID"));
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To " : "From ") + playerName;
        }),
        DEATH((player, prevTPort, safetyCheck) -> {
            Location deathLoc = (Location) prevTPort.getData().get("deathLoc");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            
            if (prevLoc == null) {
                if (!safetyCheck || SafetyCheck.isSafe(deathLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("DEATH", "deathLoc", deathLoc, "prevLoc", player.getLocation()));
                    requestTeleportPlayer(player, deathLoc, () -> sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported back to your %s location", "death"));
    
                    int delay = Delay.delayTime(player);
                    if (delay == 0) {
                        sendSuccessTheme(player, "Successfully teleported back to your %s location", "death");
                    } else {
                        sendSuccessTheme(player, "Successfully requested teleportation back to your %s location, delay time is %s ticks", "death", delay);
                    }
                } else {
                    sendErrorTheme(player, "Its not safe to teleport back to your %s location", "death");
                    return false;
                }
            } else {
                if (!safetyCheck || SafetyCheck.isSafe(prevLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("DEATH", "deathLoc", deathLoc, "prevLoc", null));
                    requestTeleportPlayer(player, prevLoc, () -> sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported back from your %s location", "death"));
    
                    int delay = Delay.delayTime(player);
                    if (delay == 0) {
                        sendSuccessTheme(player, "Successfully teleported back from your %s location", "death");
                    } else {
                        sendSuccessTheme(player, "Successfully requested teleportation back from your %s location, delay time is %s ticks", "death", delay);
                    }
                } else {
                    sendErrorTheme(player, "Its not safe to teleport back from your %s location", "death");
                    return false;
                }
            }
            return true;
        }, (player, prevTPort) -> {
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To" : "From") + " death location";
        });
        
        private final TPBack tpBack;
        private final BackString backString;
        
        PrevType(TPBack tpBack, BackString backString) {
            this.tpBack = tpBack;
            this.backString = backString;
        }
        
        public boolean tpBack(Player player, PrevTPort prevTPort, boolean safetyCheck) {
            return tpBack.tpBack(player, prevTPort, safetyCheck);
        }
        
        public String toString(Player player, PrevTPort prevTPort) {
            return backString.backString(player, prevTPort);
        }
        
        @FunctionalInterface
        private interface TPBack {
            boolean tpBack(Player player, PrevTPort prevTPort, boolean safetyCheck);
        }
        
        @FunctionalInterface
        private interface BackString {
            String backString(Player player, PrevTPort prevTPort);
        }
    }
    
    public static class PrevTPort {
        private final HashMap<String, Object> data = new HashMap<>();
        private final PrevType prevType;
        
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
        
        public boolean tpBack(Player player, boolean safetyCheck) {
            return prevType.tpBack(player, this, safetyCheck);
        }
        
        public String toString(Player player) {
            return prevType.toString(player, this);
        }
        
        public HashMap<String, Object> getData() {
            return data;
        }
    }
}
