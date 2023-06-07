package com.spaceman.tport.commands.tport.teleporter.create;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.teleporter.Create;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Back extends SubCommand {
    
    public Back() {
        setPermissions("TPort.teleporter.create");
        setCommandDescription(formatInfoTranslation("tport.command.teleporter.create.back.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport teleporter create back
        
        if (args.length == 3) {
            if (hasPermissionToRun(player, true)) {
                Create.createTeleporter(player, "Back", "back");
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create back");
        }
    }
}
