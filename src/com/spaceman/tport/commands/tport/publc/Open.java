package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.Main;
import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_PUBLIC;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Open extends SubCommand {
    
    private final EmptyCommand emptyPage;
    private final EmptyCommand emptyTPort;
    
    public Open() {
        emptyPage = new EmptyCommand();
        emptyPage.setCommandName("page", ArgumentType.REQUIRED);
        emptyPage.setCommandDescription(formatInfoTranslation("tport.command.public.open.page.commandDescription"));
        emptyPage.setPermissions("TPort.public.open.page", "TPort.basic");
        
        EmptyCommand emptyTPortSafetyCheck = new EmptyCommand() {
            @Override
            public Message permissionsHover() {
                return formatInfoTranslation("tport.command.public.open.tport.safetyCheck.permissionHover", "TPort.public.open.tp", TPORT_PUBLIC.getPermission(), "TPort.basic");
            }
        };
        emptyTPortSafetyCheck.setCommandName("safetyCheck", ArgumentType.OPTIONAL);
        emptyTPortSafetyCheck.setCommandDescription(formatInfoTranslation("tport.command.public.open.tport.safetyCheck.commandDescription"));
        emptyTPortSafetyCheck.setPermissions("TPort.public.open.tp", TPORT_PUBLIC.getPermission(), "TPort.basic");
        emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.public.open.tport.commandDescription"));
        emptyTPort.setPermissions("TPort.public.open.tp", "TPort.basic");
        emptyTPort.addAction(emptyTPortSafetyCheck);
        emptyTPort.setTabRunnable(((args, player) -> {
            try {
                Integer.parseInt(args[args.length - 2]);
                return new ArrayList<>();
            } catch (NumberFormatException nfe) {
                return Arrays.asList("true", "false");
            }
        }));
        addAction(emptyPage);
        addAction(emptyTPort);
    }
    
    public static TPort getPublicTPort(String name) {
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport != null) {
                if (tport.getName().equalsIgnoreCase(name)) {
                    
                    return tport;
                }
            }
        }
        return null;
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (emptyTPort.hasPermissionToRun(player, false)) {
            for (String publicTPortSlot : tportData.getKeys("public.tports")) {
                String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
                TPort tport = getTPort(UUID.fromString(tportID));
                if (tport != null) {
                    list.add(tport.getName());
                }
            }
        }
        if (emptyPage.hasPermissionToRun(player, false)) {
            IntStream.range(Math.min(1, list.size() / 7), Math.max(1, list.size() / 7)).mapToObj(i -> String.valueOf(i + 1)).forEach(list::add);
        }
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public open <page>
        // tport public open <TPort name> [safetyCheck]
        
        if (args.length == 3 || args.length == 4) {
            
            try {
                int page = Integer.parseInt(args[2]);
                
                if (args.length == 4) {
                    sendErrorTranslation(player, "tport.command.wrongUsage", "/tport public open <page>");
                    return;
                }
                
                if (emptyPage.hasPermissionToRun(player, true)) {
                    TPortInventories.openPublicTPortGUI(player, page - 1, null);
                }
                return;
            } catch (NumberFormatException ignore) { }
            
            if (!emptyTPort.hasPermissionToRun(player, true)) {
                return;
            }
            
            TPort publicTPort = getPublicTPort(args[2]);
            if (publicTPort != null) {
                
                Boolean safetyCheckState;
                if (args.length == 3) {
                    if (TPORT_PUBLIC.hasPermission(player, true)) {
                        safetyCheckState = Main.toBoolean(args[1]);
                        if (safetyCheckState == null) {
                            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport public open <TPort name> [true|false]");
                            return;
                        }
                    } else {
                        return;
                    }
                } else {
                    safetyCheckState = TPORT_PUBLIC.getState(player);
                }
                
                publicTPort.teleport(player, safetyCheckState);
            } else {
                sendErrorTranslation(player, "tport.command.public.open.noPublicTPortFound", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport public open <TPort name|page>");
        }
    }
}
