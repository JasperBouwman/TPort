package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.spaceman.tport.commands.tport.Open.runNotPerm;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Open extends SubCommand {
    
    private final EmptyCommand emptyPage;
    private final EmptyCommand emptyTPort;
    
    public Open() {
        emptyPage = new EmptyCommand();
        emptyPage.setCommandName("page", ArgumentType.REQUIRED);
        emptyPage.setCommandDescription(textComponent("This command is used to open the Public TPort GUI on the given page", ColorTheme.ColorType.infoColor));
        emptyPage.setPermissions("TPort.public.open.page", "TPort.basic");
        emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(textComponent("This command is used to teleport to the given Public TPort", ColorTheme.ColorType.infoColor));
        emptyTPort.setPermissions("TPort.public.open.tp", "TPort.basic");
        addAction(emptyPage);
        addAction(emptyTPort);
    }
    
    public static TPort getPublicTPort(String name) {
        Files tportData = GettingFiles.getFile("TPortData");
        
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
            Files tportData = GettingFiles.getFile("TPortData");
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
        // tport public open <TPort name|page>
        
        if (args.length == 3) {
            
            try {
                int page = Integer.parseInt(args[2]);
                if (emptyPage.hasPermissionToRun(player, true)) {
                    TPortInventories.openPublicTPortGUI(player, page - 1);
                }
                return;
            } catch (NumberFormatException ignore) {
            }
            
            if (!emptyTPort.hasPermissionToRun(player, true)) {
                return;
            }
            
            TPort publicTPort = getPublicTPort(args[2]);
            if (publicTPort != null) {
                runNotPerm(new String[]{"open", PlayerUUID.getPlayerName(publicTPort.getOwner()), publicTPort.getName()}, player);
            } else {
                sendErrorTheme(player, "No public TPort found called %s", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport public open <TPort name|page>");
        }
    }
}
