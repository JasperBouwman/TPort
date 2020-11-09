package com.spaceman.tport.commands.tport.pltp.whitelist;

import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.getTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class List extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to list all player in your PLTP whitelist", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP whitelist list
        
        Files tportData = GettingFiles.getFile("TPortData");
        if (args.length == 3) {
            boolean color = true;
            
            Message message = new Message();
            ColorTheme theme = getTheme(player);
            message.addText(textComponent("Players in your PLTP whitelist: ", theme.getInfoColor()));
            for (String ss : tportData.getConfig().getStringList("tport." + player.getUniqueId().toString() + ".tp.players")) {
                if (color) {
                    message.addText(textComponent(PlayerUUID.getPlayerName(ss), theme.getVarInfoColor()));
                } else {
                    message.addText(textComponent(PlayerUUID.getPlayerName(ss), theme.getVarInfo2Color()));
                }
                color = !color;
            }
            
            message.sendMessage(player);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport PLTP whitelist list");
        }
    }
}
