package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.TPortInventories.openTPortGUI;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Open extends SubCommand {
    
    public static final EmptyCommand emptyPlayerTPortSafetyCheck = new EmptyCommand(){
        @Override
        public Message permissionsHover() {
            Message message = new Message();
            message.addText(
                    textComponent("Permissions: (", infoColor),
                    textComponent("TPort.open", varInfoColor),
                    textComponent(" and ", infoColor),
                    textComponent("TPort.safetyCheck", varInfoColor),
                    textComponent(") or ", infoColor),
                    textComponent("TPort.basic", varInfoColor));
            return message;
        }
    };
    
    public static final EmptyCommand emptyPlayerTPort = new EmptyCommand();
    
    public Open() {
        emptyPlayerTPortSafetyCheck.setCommandName("safetyCheck", ArgumentType.OPTIONAL);
        emptyPlayerTPortSafetyCheck.setCommandDescription(textComponent("This command is used to teleport to the given TPort, " +
                        "the safetyCheck argument overrides your default value", ColorTheme.ColorType.infoColor));
        emptyPlayerTPortSafetyCheck.setPermissions("TPort.open", "TPort.safetyCheck", "TPort.basic");
        
        emptyPlayerTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyPlayerTPort.setCommandDescription(textComponent("This command is used to teleport to the given TPort", ColorTheme.ColorType.infoColor));
        emptyPlayerTPort.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyPlayerTPort.addAction(emptyPlayerTPortSafetyCheck);
        emptyPlayerTPort.setPermissions("TPort.open", "TPort.basic");
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setCommandDescription(textComponent("This command is used to open the TPort GUI of the given player", ColorTheme.ColorType.infoColor));
        emptyPlayer.setTabRunnable((args, player) -> {
            UUID argOneUUID = PlayerUUID.getPlayerUUID(args[1]);
            if (argOneUUID == null) {
                return Collections.emptyList();
            }
            return TPortManager.getTPortList(argOneUUID).stream()
                    .filter(tport -> tport.hasAccess(player))
                    .map(TPort::getName)
                    .collect(Collectors.toList());
        });
        emptyPlayer.addAction(emptyPlayerTPort);
        emptyPlayer.setPermissions(emptyPlayerTPort.getPermissions());
        addAction(emptyPlayer);
    }
    
    public static void runNotPerm(String[] args, Player player) {
        if (args.length == 1 || args.length > 4) {
            sendErrorTheme(player, "Usage: %s", "/tport open <player> [TPort name] [safetyCheck]");
            return;
        }
        
        Files tportData = GettingFiles.getFile("TPortData");
        
        String newPlayerName = args[1];
        UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
        
        boolean safetyCheck;
        if (args.length == 4) {
            if (SafetyCheck.emptySafetyCheck.hasPermissionToRun(player, true)) {
                safetyCheck = Boolean.parseBoolean(args[3]);
            } else {
                return;
            }
        } else {
            safetyCheck = SafetyCheck.safetyCheck(player);
        }
    
        if (newPlayerUUID == null || !tportData.getConfig().contains("tport." + newPlayerUUID)) {
            sendErrorTheme(player, "Could not find a player named %s", newPlayerName);
            return;
        }
        
        if (args.length > 2) {
            TPort tport = TPortManager.getTPort(newPlayerUUID, args[2]);
            if (tport != null) {
                tport.teleport(player, true, safetyCheck);
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
            return;
        }
        
        openTPortGUI(newPlayerUUID, player);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return GettingFiles.getFile("TPortData").getKeys("tport").stream().map(PlayerUUID::getPlayerName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport open <player> [TPort name] [safetyCheck]
        
        if (emptyPlayerTPort.hasPermissionToRun(player, true)) {
            runNotPerm(args, player);
        }
    }
}
