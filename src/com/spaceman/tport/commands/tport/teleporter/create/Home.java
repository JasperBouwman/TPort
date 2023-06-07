package com.spaceman.tport.commands.tport.teleporter.create;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.teleporter.Create;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Home extends SubCommand {
    
    public Home() {
        setPermissions("TPort.teleporter.create");
        setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.home.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport teleporter create home
        
        if (args.length == 3) {
            if (hasPermissionToRun(player, true)) {
                Create.createTeleporter(player, "Home", "home");
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create home");
        }
    }
}
