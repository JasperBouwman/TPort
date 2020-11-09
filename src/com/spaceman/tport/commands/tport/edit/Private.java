package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.runCommand;

public class Private extends SubCommand {
    
    private Message getEmptyCommandDescription() {
        Message message = new Message();
        message.addText(textComponent("This command is used to edit the private statement of the given TPort.", infoColor));
        for (TPort.PrivateStatement privateStatement : TPort.PrivateStatement.values()) {
            message.addText(textComponent("\n"));
            message.addMessage(privateStatement.getDescription());
        }
        return message;
    }
    
    private final EmptyCommand emptyStatement;
    
    public Private() {
        emptyStatement = new EmptyCommand();
        emptyStatement.setCommandName("statement", ArgumentType.OPTIONAL);
        emptyStatement.setCommandDescription(getEmptyCommandDescription());
        emptyStatement.setPermissions("TPort.edit.private", "TPort.basic");
        addAction(emptyStatement);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return Arrays.stream(TPort.PrivateStatement.values()).map(s -> s.name().toLowerCase()).collect(Collectors.toList());
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the private statement of the given TPort", infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport edit <TPort name> private [statement]
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            
            Message message = new Message();
            message.addText(textComponent("TPort ", infoColor));
            message.addText(textComponent(tport.getName(), varInfoColor));
            message.addText(textComponent(" is ", infoColor));
            HoverEvent hEvent = new HoverEvent();
            hEvent.addMessage(tport.getPrivateStatement().getDescription());
            message.addText(textComponent(tport.getPrivateStatement().getDisplayName(), varInfoColor, hEvent));
            message.addText(textComponent(". For the description of all the other states click ", infoColor));
            message.addText(textComponent("here", varInfoColor, new HoverEvent(textComponent("/tport help tport edit <tport name> private <statement>", infoColor)), runCommand("/tport help tport edit <tport name> private <statement>")));
            
            message.sendMessage(player);
        } else if (args.length == 4) {
            if (!emptyStatement.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                return;
            }
            
            TPort.PrivateStatement ps;
            try {
                ps = TPort.PrivateStatement.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException iae) {
                sendErrorTheme(player, "%s is not a valid private statement", args[3]);
                return;
            }
            if (tport.isPublicTPort()) {
                if (!ps.canGoPublic()) {
                    sendErrorTheme(player, "TPort %s is a Public TPort, Private Statement %s can't go Public", tport.getName(), ps.getDisplayName());
                    return;
                }
            }
            tport.setPrivateStatement(ps);
            tport.save();
            sendSuccessTheme(player, "Successfully set TPort %s to %s", tport.getName(), ps.getDisplayName());
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> private [statement]");
        }
        
    }
}
