package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class Version extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.version.commandDescription", Main.getInstance().getDescription().getVersion());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport version
        
        if (args.length == 1) {
            sendInfoTranslation(player, "tport.command.version.succeeded",
                    Main.getInstance().getDescription().getVersion(),
                    "1.18.2", //todo update compatible version
                    textComponent(
                            Main.getInstance().getDescription().getWebsite(),
                            varInfoColor,
                            hoverEvent(textComponent(Main.getInstance().getDescription().getWebsite(), varInfoColor)),
                            ClickEvent.openUrl(Main.getInstance().getDescription().getWebsite())
                    ));
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport version");
        }
    }
}
