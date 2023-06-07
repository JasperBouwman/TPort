package com.spaceman.tport.commands.tport.backup;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Save extends SubCommand {
    
    private final EmptyCommand emptyName;
    
    public Save() {
        emptyName = new EmptyCommand();
        emptyName.setCommandName("name", ArgumentType.OPTIONAL);
        emptyName.setCommandDescription(formatInfoTranslation("tport.command.backup.save.name.commandDescription"));
        emptyName.setPermissions("TPort.admin.backup.save");
        addAction(emptyName);
        
        setPermissions(emptyName.getPermissions());
        setCommandDescription(formatInfoTranslation("tport.command.backup.save.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport backup save [name]
        
        if (args.length == 2) {
            if (!this.hasPermissionToRun(player, true)) {
                return;
            }
            
            Auto.save(Auto.getBackupName(), player);
        } else if (args.length == 3) {
            if (!emptyName.hasPermissionToRun(player, true)) {
                return;
            }
            
            if (args[2].startsWith("auto-")) {
                sendErrorTranslation(player, "tport.command.backup.save.name.prefixError", "auto-");
                return;
            }
            
            Auto.save(args[2], player);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport backup save <name>");
        }
    }
}
