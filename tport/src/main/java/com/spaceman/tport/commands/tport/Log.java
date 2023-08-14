package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.log.Add;
import com.spaceman.tport.commands.tport.log.Remove;
import com.spaceman.tport.commands.tport.log.*;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Log extends SubCommand {
    
    public Log() {
        addAction(new Read());
        addAction(new TimeZone());
        addAction(new TimeFormat());
        addAction(new Clear());
        addAction(new LogData());
        addAction(new Add());
        addAction(new Remove());
        addAction(new Default());
        addAction(new Notify());
        addAction(new LogSize());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log read <TPort name> [player]
        // tport log TimeZone [TimeZone]
        // tport log timeFormat [format...]
        // tport log clear <TPort name...>
        // tport log logData [TPort name] [player]
        // tport log add <TPort name> <player[:LogMode]...>
        // tport log remove <TPort name> <player...>
        // tport log default <TPort name> [default LogMode]
        // tport log notify <TPort name> [state]
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log " + convertToArgs(getActions(), false));
    }
}
