package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class Version extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the TPort plugin version (version: ", ColorTheme.ColorType.infoColor),
                textComponent(Main.getInstance().getDescription().getVersion(), ColorTheme.ColorType.varInfoColor),
                textComponent(")", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        sendInfoTheme(player, "This server is running TPort version: %s", Main.getInstance().getDescription().getVersion());
    }
}
