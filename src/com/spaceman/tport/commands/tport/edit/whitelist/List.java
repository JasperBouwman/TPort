package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class List extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to list all players in the whitelist of the given TPort", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> whitelist list
        
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        
        if (tport == null) {
            sendErrorTheme(player, "No TPort found called %s", args[1]);
            return;
        }
        ArrayList<UUID> list = tport.getWhitelist();
    
        ColorTheme theme = ColorTheme.getTheme(player);
    
        Message message = new Message();
        message.addText("Players in the whitelist of the TPort " + theme.getVarInfoColor() + args[1] + theme.getInfoColor() + ": ", theme.getInfoColor());
        boolean color = true;
        message.addText("");
        
        for (UUID tmp : list) {
            if (color) {
                message.addText(PlayerUUID.getPlayerName(tmp), theme.getVarInfoColor());
            } else {
                message.addText(PlayerUUID.getPlayerName(tmp), theme.getVarInfo2Color());
            }
            message.addText(", ", theme.getInfoColor());
            color = !color;
        }
        message.removeLast();
        message.sendMessage(player);
    }
}
