package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.pltp.Offset;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import com.spaceman.tport.fancyMessage.encapsulation.FeatureEncapsulation;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;
import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_PUBLIC;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class Back extends SubCommand {
    
    public static HashMap<UUID, PrevTPort> prevTPorts = new HashMap<>();
    
    public static String getPrevLocNameAsString(Player player) {
        if (prevTPorts.containsKey(player.getUniqueId())) {
            return "Previous location: " + prevTPorts.get(player.getUniqueId()).toString();
        } else {
            return "Previous location: " + "Unknown";
        }
    }
    
    public static Message getPrevLocName(Player player) {
        if (prevTPorts.containsKey(player.getUniqueId())) {
            return formatInfoTranslation("tport.command.back.previousLocation", prevTPorts.get(player.getUniqueId()).toMessage());
        } else {
            return formatInfoTranslation("tport.command.back.previousLocation", formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.back.previousLocationUnknown"));
        }
    }
    
    public static boolean hasBack(Player player) {
        return prevTPorts.containsKey(player.getUniqueId());
    }
    
    private final EmptyCommand emptySafetyCheck;
    
    public Back() {
        emptySafetyCheck = new EmptyCommand() {
            @Override
            public Message permissionsHover() {
                return formatInfoTranslation("tport.command.back.safetyCheck.permissionHover", "TPort.back", TPORT_PUBLIC.getPermission(), "TPort.basic");
            }
        };
        emptySafetyCheck.setCommandName("safetyCheck", ArgumentType.OPTIONAL);
        emptySafetyCheck.setCommandDescription(formatInfoTranslation("tport.command.back.safetyCheck.commandDescription"));
        emptySafetyCheck.setPermissions("TPort.back", TPORT_PUBLIC.getPermission(), "TPort.basic");
        addAction(emptySafetyCheck);
        
        this.setPermissions("TPort.back", "TPort.basic");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.back.commandDescription");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptySafetyCheck.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Arrays.asList("true", "false", getPrevLocNameAsString(player));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport back [safetyCheck]
        
        if (args.length > 2) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport back [safetyCheck]");
            return;
        }
        
        if (!hasPermissionToRun(player, true)) {
            return;
        }
        if (!CooldownManager.Back.hasCooled(player)) {
            return;
        }
        
        if (prevTPorts.containsKey(player.getUniqueId())) {
            Boolean safetyCheck;
            if (args.length == 2) {
                if (TPORT_BACK.hasPermission(player, true)) {
                    safetyCheck = Main.toBoolean(args[1]);
                    if (safetyCheck == null) {
                        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport back [true|false]");
                        return;
                    }
                } else {
                    return;
                }
            } else {
                safetyCheck = SafetyCheck.SafetyCheckSource.TPORT_BACK.getState(player);
            }
            if (prevTPorts.get(player.getUniqueId()).tpBack(player, safetyCheck)) {
                CooldownManager.Back.update(player);
            }
        } else {
            sendErrorTranslation(player, "tport.command.back.noLocationKnown");
        }
    }
    
    public enum PrevType {
        TPORT((player, prevTPort, safetyCheck) -> {
            
            Location prevLoc = (Location) prevTPort.getData().get("prevLoc");
            UUID tportUUID = (UUID) prevTPort.getData().get("tportUUID");
            String tportName = (String) prevTPort.getData().get("tportName");
            UUID tportOwner = (UUID) prevTPort.getData().get("tportOwner");
            
            TPort tport = TPortManager.getTPort(tportOwner, tportUUID);
            if (prevLoc == null) {
                if (!Open.getInstance().emptyOpenPlayerTPort.hasPermissionToRun(player, true)) {
                    return false;
                }
                if (tport == null) {
                    sendErrorTranslation(player, "tport.command.back.TPORT.to.tportNotFound", tportName);
                    return false;
                }
                prevTPorts.put(player.getUniqueId(), new PrevTPort("TPORT", "tportName", tport.getName(), "tportUUID", tport.getTportID(), "tportOwner", tportOwner, "prevLoc", player.getLocation()));
                if (tport.teleport(player, safetyCheck, false,
                        "tport.command.back.TPORT.to.succeeded", "tport.command.back.TPORT.to.tpRequested")) {
                    return true;
                } else {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("TPORT", "tportUUID", tportUUID, "tportOwner", tportOwner, "prevLoc", null));
                    return false;
                }
            } else {
                if (!safetyCheck || SafetyCheck.isSafe(prevLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("TPORT", "tportName", tportName, "tportUUID", tportUUID, "tportOwner", tportOwner, "prevLoc", null));
                    requestTeleportPlayer(player, prevLoc, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.TPORT.from.succeeded", asTPort(tport, tportUUID, tportOwner, tportName)),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.back.TPORT.from.tpRequested", asTPort(tport, tportUUID, tportOwner, tportName), delay, tickMessage, seconds, secondMessage));
                } else {
                    sendErrorTranslation(player, "tport.command.back.TPORT.from.notSafe", asTPort(tport, tportUUID, tportOwner, tportName));
                    return false;
                }
                return true;
            }
        }, (prevTPort) -> {
            Location prevLoc = (Location) prevTPort.getData().get("prevLoc");
            UUID tportUUID = (UUID) prevTPort.getData().get("tportUUID");
            UUID tportOwner = (UUID) prevTPort.getData().get("tportOwner");
            TPort tport = TPortManager.getTPort(tportOwner, tportUUID);
            
            if (prevLoc == null) {
                if (tport == null) {
                    return "Unknown";
                } else {
                    return "To TPort " + tport.getName();
                }
            } else {
                String tportName = (String) prevTPort.getData().get("tportName");
                return "From TPort " + tportName;
            }
        }, ((prevTPort) -> {
            Location prevLoc = (Location) prevTPort.getData().get("prevLoc");
            UUID tportOwner = (UUID) prevTPort.getData().get("tportOwner");
            UUID tportUUID = (UUID) prevTPort.getData().get("tportUUID");
            TPort tport = TPortManager.getTPort(tportOwner, tportUUID);
            String tportName = (String) prevTPort.getData().get("tportName");
            
            if (prevLoc == null) {
                if (tport == null) {
                    return formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.back.TPORT.to.previousLocationUnknown", tportName);
                } else {
                    return formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.back.TPORT.to.previousLocation", asTPort(tport));
                }
            } else {
                return formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.back.TPORT.from.previousLocation", asTPort(tport, tportUUID, tportOwner, tportName));
            }
        })),
        BIOME((player, prevTPort, safetyCheck) -> {
            Location biomeLoc = (Location) prevTPort.getData().get("biomeLoc");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            String biomeName = (String) prevTPort.getData().get("biomeName");
            BiomeEncapsulation biome = new BiomeEncapsulation(biomeName);
            
            if (prevLoc == null) {
                if (!safetyCheck || SafetyCheck.isSafe(biomeLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("BIOME", "biomeLoc", biomeLoc, "prevLoc", player.getLocation(), "biomeName", biomeName));
                    requestTeleportPlayer(player, biomeLoc, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.BIOME.to.succeeded", biome),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.back.BIOME.to.tpRequested", biome, delay, tickMessage, seconds, secondMessage));
                } else {
                    sendErrorTranslation(player, "tport.command.back.BIOME.to.notSafe", biome);
                    return false;
                }
            } else {
                if (!safetyCheck || SafetyCheck.isSafe(prevLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("BIOME", "biomeLoc", biomeLoc, "prevLoc", null, "biomeName", biomeName));
                    requestTeleportPlayer(player, prevLoc, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.BIOME.from.succeeded", biome),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.back.BIOME.from.tpRequested", biome, delay, tickMessage, seconds, secondMessage));
                } else {
                    sendErrorTranslation(player, "tport.command.back.BIOME.from.notSafe", biome);
                    return false;
                }
            }
            return true;
        }, (prevTPort) -> {
            String biomeName = (String) prevTPort.getData().get("biomeName");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To" : "From") + " biome " + biomeName;
        }, (prevTPort) -> {
            String biomeName = (String) prevTPort.getData().get("biomeName");
            BiomeEncapsulation biome = new BiomeEncapsulation(biomeName);
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.back.BIOME." + (prevLoc == null ? "to" : "from") + ".previousLocation", biome);
        }),
        FEATURE((player, prevTPort, safetyCheck) -> {
            Location featureLoc = (Location) prevTPort.getData().get("featureLoc");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            String featureName = (String) prevTPort.getData().get("featureName");
            FeatureEncapsulation feature = new FeatureEncapsulation(featureName);
            
            if (prevLoc == null) {
                if (!safetyCheck || SafetyCheck.isSafe(featureLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("FEATURE", "featureLoc", featureLoc, "prevLoc", player.getLocation(), "featureName", featureName));
                    requestTeleportPlayer(player, featureLoc, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.FEATURE.to.succeeded", feature),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.back.FEATURE.to.tpRequested", feature, delay, tickMessage, seconds, secondMessage));
                } else {
                    sendErrorTranslation(player, "tport.command.back.FEATURE.to.notSafe", feature);
                    return false;
                }
            } else {
                if (!safetyCheck || SafetyCheck.isSafe(prevLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("FEATURE", "featureLoc", featureLoc, "prevLoc", null, "featureName", featureName));
                    requestTeleportPlayer(player, prevLoc, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.FEATURE.from.succeeded", feature),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.back.FEATURE.from.tpRequested", feature, delay, tickMessage, seconds, secondMessage));
                } else {
                    sendErrorTranslation(player, "tport.command.back.FEATURE.from.notSafe", feature);
                    return false;
                }
            }
            return true;
        }, (prevTPort) -> {
            String featureName = (String) prevTPort.getData().get("featureName");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To " : "From ") + featureName;
        }, (prevTPort) -> {
            String featureName = (String) prevTPort.getData().get("featureName");
            FeatureEncapsulation feature = new FeatureEncapsulation(featureName);
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.back.FEATURE." + (prevLoc == null ? "to" : "from") + ".previousLocation", feature);
        }),
        PLAYER((player, prevTPort, safetyCheck) -> {
            String toPlayerUUID = (String) prevTPort.getData().get("playerUUID");
            
            Player toPlayer = Bukkit.getPlayer(UUID.fromString(toPlayerUUID));
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            
            if (prevLoc == null) {
                if (toPlayer == null) {
                    sendErrorTranslation(player, "tport.command.back.PLAYER.to.notOnlineAnymore", PlayerUUID.getPlayerName(toPlayerUUID));
                    return false;
                }
                if (!tportData.getConfig().getBoolean("tport." + toPlayerUUID + ".tp.statement", true)) {
                    ArrayList<String> whitelist = (ArrayList<String>) tportData.getConfig().getStringList("tport." + toPlayerUUID + "tp.players");
                    if (!whitelist.contains(player.getUniqueId().toString())) {
                        sendErrorTranslation(player, "tport.command.back.PLAYER.to.whitelistError");
                        return false;
                    }
                }
                
                Location location = Offset.getPLTPOffset(toPlayer).applyOffset(toPlayer.getLocation());
                
                if (!safetyCheck || SafetyCheck.isSafe(location)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("PLAYER", "playerUUID", toPlayerUUID, "prevLoc", player.getLocation()));
                    requestTeleportPlayer(player, location, () -> {
                        sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.PLAYER.to.succeeded", toPlayer);
                        sendInfoTranslation(Bukkit.getPlayer(toPlayer.getUniqueId()), "tport.command.back.PLAYER.to.succeededOtherPlayer", player);
                    }, (p, delay, tickMessage, seconds, secondMessage) -> {
                        sendSuccessTranslation(p, "tport.command.back.PLAYER.to.toRequested", toPlayer, delay, tickMessage, seconds, secondMessage);
                        sendInfoTranslation(toPlayer, "tport.command.back.PLAYER.to.requestedOtherPlayer", p);
                    });
                } else {
                    sendErrorTranslation(player, "tport.command.back.PLAYER.to.notSafe", toPlayer);
                    return false;
                }
            } else {
                if (!safetyCheck || SafetyCheck.isSafe(prevLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("PLAYER", "playerUUID", toPlayerUUID, "prevLoc", null));
                    requestTeleportPlayer(player, prevLoc, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.PLAYER.from.succeeded", asPlayer(toPlayer, UUID.fromString(toPlayerUUID))),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.back.PLAYER.from.toRequested", asPlayer(toPlayer, UUID.fromString(toPlayerUUID)), delay, tickMessage, seconds, secondMessage));
                } else {
                    sendErrorTranslation(player, "tport.command.back.PLAYER.from.notSafe", asPlayer(toPlayer, UUID.fromString(toPlayerUUID)));
                    return false;
                }
            }
            return true;
        }, (prevTPort) -> {
            String playerName = PlayerUUID.getPlayerName((String) prevTPort.getData().get("playerUUID"));
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To " : "From ") + playerName;
        }, (prevTPort) -> {
            String playerName = PlayerUUID.getPlayerName((String) prevTPort.getData().get("playerUUID"));
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.back.PLAYER." + (prevLoc == null ? "to" : "from") + ".previousLocation", playerName);
        }),
        DEATH((player, prevTPort, safetyCheck) -> {
            Location deathLoc = (Location) prevTPort.getData().get("deathLoc");
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            Message deathMessage = formatTranslation(ColorType.varSuccessColor, ColorType.varSuccess2Color, "tport.command.back.DEATH.death");
            
            if (prevLoc == null) {
                if (!safetyCheck || SafetyCheck.isSafe(deathLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("DEATH", "deathLoc", deathLoc, "prevLoc", player.getLocation()));
                    requestTeleportPlayer(player, deathLoc, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.DEATH.to.succeeded", deathMessage),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.back.DEATH.to.tpRequested", deathMessage, delay, tickMessage, seconds, secondMessage));
                } else {
                    sendErrorTranslation(player, "tport.command.back.DEATH.to.notSafe", deathMessage);
                    return false;
                }
            } else {
                if (!safetyCheck || SafetyCheck.isSafe(prevLoc)) {
                    prevTPorts.put(player.getUniqueId(), new PrevTPort("DEATH", "deathLoc", deathLoc, "prevLoc", null));
                    requestTeleportPlayer(player, prevLoc, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.DEATH.from.succeeded", deathMessage),
                            (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.back.DEATH.from.tpRequested", deathMessage, delay, tickMessage, seconds, secondMessage));
                } else {
                    sendErrorTranslation(player, "tport.command.back.DEATH.from.notSafe", deathMessage);
                    return false;
                }
            }
            return true;
        }, (prevTPort) -> {
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return (prevLoc == null ? "To" : "From") + " death location";
        }, (prevTPort) -> {
            Location prevLoc = (Location) prevTPort.getData().getOrDefault("prevLoc", null);
            return formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color, "tport.command.back.DEATH." + (prevLoc == null ? "to" : "from") + ".previousLocation");
        });
        
        private final TPBack tpBack;
        private final BackString backString;
        private final BackMessage backMessage;
        
        PrevType(TPBack tpBack, BackString backString, BackMessage backMessage) {
            this.tpBack = tpBack;
            this.backString = backString;
            this.backMessage = backMessage;
        }
        
        public boolean tpBack(Player player, PrevTPort prevTPort, boolean safetyCheck) {
            return tpBack.tpBack(player, prevTPort, safetyCheck);
        }
        
        public String toString(PrevTPort prevTPort) {
            return backString.backString(prevTPort);
        }
        
        public Message toMessage(PrevTPort prevTPort) {
            return backMessage.backMessage(prevTPort);
        }
        
        @FunctionalInterface
        private interface TPBack {
            boolean tpBack(Player player, PrevTPort prevTPort, boolean safetyCheck);
        }
        
        @FunctionalInterface
        private interface BackString {
            String backString(PrevTPort prevTPort);
        }
        
        @FunctionalInterface
        private interface BackMessage {
            Message backMessage(PrevTPort prevTPort);
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
        
        public PrevTPort(String prevType, String name1, Object data1, String name2, Object data2, String name3, Object data3, String name4, Object data4) {
            this.data.put(name1, data1);
            this.data.put(name2, data2);
            this.data.put(name3, data3);
            this.data.put(name4, data4);
            this.prevType = PrevType.valueOf(prevType);
        }
        
        public boolean tpBack(Player player, boolean safetyCheck) {
            return prevType.tpBack(player, this, safetyCheck);
        }
        
        @Override
        public String toString() {
            return prevType.toString(this);
        }
        
        public Message toMessage() {
            return prevType.toMessage(this);
        }
        
        public HashMap<String, Object> getData() {
            return data;
        }
    }
}
