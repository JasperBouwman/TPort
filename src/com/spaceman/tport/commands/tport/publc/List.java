package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class List extends SubCommand {
    
    public List() {
        EmptyCommand emptyOwn = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "Own";
            }
        };
        emptyOwn.setCommandName("own", ArgumentType.FIXED);
        emptyOwn.setCommandDescription(textComponent("This command is used to list all your Public TPorts", ColorTheme.ColorType.infoColor));
        EmptyCommand emptyAll = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "All";
            }
        };
        emptyAll.setCommandName("all", ArgumentType.FIXED);
        emptyAll.setCommandDescription(textComponent("This command is used to list all available Public TPorts", ColorTheme.ColorType.infoColor));
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(textComponent("This command is used to list all available Public TPorts", ColorTheme.ColorType.infoColor));
        addAction(empty);
        addAction(emptyOwn);
        addAction(emptyAll);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public list [own|all]
        
        if (args.length == 2) {
            sendAllPublicTPorts(player);
        } else if (args.length == 3) {
            if (args[2].equalsIgnoreCase("all")) {
                sendAllPublicTPorts(player);
            } else if (args[2].equalsIgnoreCase("own")) {
                sendOwnPublicTPorts(player);
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport public list [own|all]");
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport public list [own|all]");
        }
    }
    
    private void sendAllPublicTPorts(Player player) {
        Files tportData = getFile("TPortData");
        Message message = new Message();
        ColorTheme theme = ColorTheme.getTheme(player);
        message.addText(textComponent("All Public TPorts: ", theme.getInfoColor()));
        boolean hasPublicTPorts = false;
        boolean color = true;
        
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport != null) {
                hasPublicTPorts = true;
                message.addText(textComponent(tport.getName(), color ? theme.getVarInfoColor() : theme.getVarInfo2Color(),
                        ClickEvent.runCommand("/tport public open " + tport.getName()),
                        new HoverEvent(textComponent("Owner: ", theme.getInfoColor()), textComponent(PlayerUUID.getPlayerName(tport.getOwner()), theme.getVarInfoColor()))));
                message.addText(textComponent(", ", theme.getInfoColor()));
                color = !color;
            }
        }
        message.removeLast();
        if (!hasPublicTPorts) {
            message.addText(textComponent("There are no Public TPorts", theme.getInfoColor()));
        }
        message.sendMessage(player);
    }
    
    private void sendOwnPublicTPorts(Player player) {
        Files tportData = getFile("TPortData");
        Message message = new Message();
        ColorTheme theme = ColorTheme.getTheme(player);
        message.addText(textComponent("All your Public TPorts: ", theme.getInfoColor()));
        boolean hasPublicTPorts = false;
        boolean color = true;
        
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport != null) {
                if (tport.getOwner().equals(player.getUniqueId())) {
                    hasPublicTPorts = true;
                    message.addText(textComponent(tport.getName(), color ? theme.getVarInfoColor() : theme.getVarInfo2Color(), ClickEvent.runCommand("/tport public open " + tport.getName())));
                    message.addText(textComponent(", ", theme.getInfoColor()));
                    color = !color;
                }
            }
        }
        message.removeLast();
        if (!hasPublicTPorts) {
            message.addText(textComponent("You don't have any public TPorts", theme.getInfoColor()));
        }
        message.sendMessage(player);
    }
}
