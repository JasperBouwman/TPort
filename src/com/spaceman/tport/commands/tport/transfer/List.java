package com.spaceman.tport.commands.tport.transfer;

import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class List extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to list all your offered TPorts and all your offers", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport transfer list
        
        if (args.length == 2) {
            Message message = new Message();
            ColorTheme theme = ColorTheme.getTheme(player);
            message.addText(textComponent("Your offered TPorts: ", theme.getInfoColor()));
            message.addText("");
            boolean color = true;
            for (TPort tport : TPortManager.getTPortList(player.getUniqueId())) {
                if (tport.isOffered()) {
                    HoverEvent hEvent = new HoverEvent();
                    hEvent.addText(textComponent("Offered to: ", theme.getInfoColor()));
                    hEvent.addText(textComponent(PlayerUUID.getPlayerName(tport.getOfferedTo()), theme.getVarInfoColor()));
                    hEvent.addText(textComponent("\nClick to revoke your offer (", theme.getInfoColor()));
                    hEvent.addText(textComponent("/tport transfer revoke " + tport.getName(), theme.getVarInfoColor()));
                    hEvent.addText(textComponent(")", theme.getInfoColor()));
                    message.addText(textComponent(tport.getName(), color ? theme.getVarInfoColor() : theme.getVarInfo2Color(),
                            hEvent, ClickEvent.runCommand("/tport transfer revoke " + tport.getName())));
                    message.addText(textComponent(", ", theme.getInfoColor()));
                    color = !color;
                }
            }
            message.removeLast();
            message.addText(textComponent("\nYour offers: ", theme.getInfoColor()));
            message.addText("");
    
            color = true;
            for (String uuidString : getFile("TPortData").getKeys("tport")) {
                UUID uuid = UUID.fromString(uuidString);
                for (TPort tport : TPortManager.getTPortList(uuid)) {
                    if (player.getUniqueId().equals(tport.getOfferedTo())) {
                        HoverEvent hEvent = new HoverEvent();
                        hEvent.addText(textComponent("Offered from: ", theme.getInfoColor()));
                        String fromName = PlayerUUID.getPlayerName(tport.getOwner());
                        hEvent.addText(textComponent(fromName, theme.getVarInfoColor()));
                        hEvent.addText(textComponent("\nClick to accept your the (", theme.getInfoColor()));
                        hEvent.addText(textComponent("/tport transfer accept " + fromName + " " + tport.getName(), theme.getVarInfoColor()));
                        hEvent.addText(textComponent(")", theme.getInfoColor()));
                        hEvent.addText(textComponent("\nShift click to reject the offer (", theme.getInfoColor()));
                        hEvent.addText(textComponent("/tport transfer reject " + fromName + " " + tport.getName(), theme.getVarInfoColor()));
                        hEvent.addText(textComponent(")", theme.getInfoColor()));
                        message.addText(textComponent(tport.getName(), color ? theme.getVarInfoColor() : theme.getVarInfo2Color(),
                                hEvent, ClickEvent.runCommand("/tport transfer accept " + fromName + " " + tport.getName())).setInsertion("/tport transfer reject " + fromName + " " + tport.getName()));
                        message.addText(textComponent(", ", theme.getInfoColor()));
                        color = !color;
                    }
                }
            }
            message.removeLast();
            
            message.sendMessage(player);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport transfer list");
        }
    }
}
