package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.log.Add;
import com.spaceman.tport.commands.tport.log.*;
import com.spaceman.tport.commands.tport.log.Remove;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHander.HeadCommand.runCommands;

public class Log extends SubCommand {

    public Log() {
        addAction(new Add());
        addAction(new AddAll());
        addAction(new GetMode());
        addAction(new List());
        addAction(new Remove());
        addAction(new RemoveAll());
        addAction(new SetAll());
        addAction(new SetMode());
        addAction(new IsLogged());
        addAction(new Clear());
        addAction(new Read());
    }

    @Override
    public void run(String[] args, Player player) {
        //modes: online, offline, all

        ///tport log list [TPort name]
        ///tport log add <TPort name> <player name[:mode]...>
        ///tport log addAll <TPort name>
        ///tport log remove <TPort name> <player name...>
        ///tport log removeAll <TPort name>
        ///tport log getMode <TPort name> <player name>
        ///tport log setMode <TPort name> <player name> <LogMode>
        ///tport log setAll <TPort name> <LogMode>
        ///tport log isLogged <player name> <TPort name>
        //tport log read <TPort name>
        //tport log clear [TPort name...]
        
        if (true) {
            player.sendMessage("Feature not ready yet! :(");
            return;
        }

        if (args.length == 1) {
            player.sendMessage("some explanation");//todo
            return;
        }

        if (!runCommands(getActions(), args[1], args, player)) {
            player.sendMessage("§cUse: §4/tport log <list:add:addAll:remove:removeAll:getMode:setMode:setAll:isLogged:read:clear>");
        }
    }
}
