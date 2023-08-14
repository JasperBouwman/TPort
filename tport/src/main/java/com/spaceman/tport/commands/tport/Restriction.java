package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.restriction.Get;
import com.spaceman.tport.commands.tport.restriction.Handler;
import com.spaceman.tport.commands.tport.restriction.Set;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public class Restriction extends SubCommand {
    
    public Restriction() {
        addAction(new Handler());
        addAction(new Set());
        addAction(new Get());
    }
    
    public static void setPermissionBased(boolean state) {
        tportConfig.getConfig().set("restriction.permission", state);
        tportConfig.saveConfig();
    }
    public static boolean isPermissionBased() {
        return tportConfig.getConfig().getBoolean("restriction.permission", false);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport restriction handler [state]
        // tport restriction set <player> <type>
        // tport restriction get [player]
        
        /*
         * movement restriction type is defined with the permission: TPort.restriction.type.<restriction name>
         * */
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport restriction <handler|set|get>");
    }
}
