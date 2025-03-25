package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.advancements.TPortAdvancement;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.*;

import static com.spaceman.tport.inventories.TPortInventories.openTPortGUI;
import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_OPEN;
import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_OWN;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Open extends SubCommand {
    
    private static final Open instance = new Open();
    public static Open getInstance() {
        return instance;
    }
    
    public final EmptyCommand emptyOpenPlayerTPort = new EmptyCommand();
    
    public Open() {
        EmptyCommand emptyOpenPlayerTPortSafetyCheck = new EmptyCommand() {
            @Override
            public Message permissionsHover() {
                return formatInfoTranslation("tport.command.open.player.tport.safetyCheck.permissionHover", "TPort.open", TPORT_OPEN.getPermission(), "TPort.basic");
            }
        };
        emptyOpenPlayerTPortSafetyCheck.setCommandName("safetyCheck", ArgumentType.OPTIONAL);
        emptyOpenPlayerTPortSafetyCheck.setCommandDescription(formatInfoTranslation("tport.command.open.player.tport.safetyCheck.commandDescription"));
        emptyOpenPlayerTPortSafetyCheck.setPermissions("TPort.open", TPORT_OPEN.getPermission(), "TPort.basic");
        
        emptyOpenPlayerTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyOpenPlayerTPort.setCommandDescription(formatInfoTranslation("tport.command.open.player.tport.commandDescription"));
        emptyOpenPlayerTPort.setTabRunnable(((args, player) -> {
            if (!emptyOpenPlayerTPortSafetyCheck.hasPermissionToRun(player, false)) {
                return Collections.emptyList();
            }
            return Arrays.asList("true", "false");
        }));
        emptyOpenPlayerTPort.addAction(emptyOpenPlayerTPortSafetyCheck);
        emptyOpenPlayerTPort.setPermissions("TPort.open", "TPort.basic");
        
        EmptyCommand emptyOpenPlayer = new EmptyCommand();
        emptyOpenPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyOpenPlayer.setCommandDescription(formatInfoTranslation("tport.command.open.player.commandDescription"));
        emptyOpenPlayer.setTabRunnable((args, player) -> {
            UUID playerUUID = PlayerUUID.getPlayerUUID(args[1]);
            if (playerUUID == null) {
                return Collections.emptyList();
            }
            List<String> list = new ArrayList<>();
            for (TPort tport : TPortManager.getTPortList(playerUUID)) {
                Boolean access = tport.hasAccess(player);
                if (access == null || access) {
                    list.add(tport.getName());
                }
            }
            return list;
        });
        emptyOpenPlayer.addAction(emptyOpenPlayerTPort);
        addAction(emptyOpenPlayer);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        if (!emptyOpenPlayerTPort.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return PlayerUUID.getPlayerNames();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport open <player> [TPort name] [safetyCheck]
        
        if (args.length == 2) {
            String newPlayerName = args[1];
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName, player);
            if (newPlayerUUID != null) {
                openTPortGUI(newPlayerUUID, player);
            }
        } else if (args.length == 3 || args.length == 4) {
            String newPlayerName = args[1];
            if (!emptyOpenPlayerTPort.hasPermissionToRun(player, true)) {
                return;
            }
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName, player);
            if (newPlayerUUID == null) {
                return;
            }
            TPort tport = TPortManager.getTPort(newPlayerUUID, args[2]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
                return;
            }
            
            Boolean safetyCheck;
            if (tport.getOwner().equals(player.getUniqueId())) {
                if (args.length == 4) {
                    if (!TPORT_OWN.hasPermission(player, true)) {
                        return;
                    }
                    safetyCheck = Main.toBoolean(args[3]);
                    if (safetyCheck == null) {
                        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport open <player> [TPort name] [true|false]");
                        return;
                    }
                } else {
                    safetyCheck = TPORT_OWN.getState(player);
                }
            } else {
                if (args.length == 4) {
                    if (!TPORT_OPEN.hasPermission(player, true)) {
                        return;
                    }
                    safetyCheck = Main.toBoolean(args[3]);
                    if (safetyCheck == null) {
                        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport open <player> [TPort name] [true|false]");
                        return;
                    }
                } else {
                    safetyCheck = TPORT_OPEN.getState(player);
                }
            }
            
            TPortAdvancement advancement = null;
            if (tport.getOwner().equals(player.getUniqueId())) advancement = TPortAdvancement.Advancement_familiar;
            
            tport.teleport(player, safetyCheck, advancement);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport open <player> [TPort name] [safetyCheck]");
        }
    }
}
