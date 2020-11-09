package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.CommandTemplate;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.searchArea.*;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;

public class SearchArea extends SubCommand {
    
    public SearchArea() {
        addAction(new Set());
        addAction(new Get());
        addAction(new Configure());
        addAction(new Show());
        addAction(new Description());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport searchArea set <type>
        // tport searchArea get
        // tport searchArea configure [data...]
        // tport searchArea show
        // tport searchArea description <type>
        
        // polygon:     tport searchArea configure <world> <corner> set <x> <z>
        //              tport searchArea configure <world> <corner> get
        //              tport searchArea configure <world> <corner> remove
        // permission:  tport searchArea configure
        // worldBorder: tport searchArea configure
        
        /*
        * Permissions
        *
        * TPort.searchArea.subType.<type> {polygon or worldBorder}
        *
        * Polygon permissions:
        * TPort.searchArea.data.<world>.<corner>.x.<x>
        * TPort.searchArea.data.<world>.<corner>.z.<z>
        *
        * worldBorder permissions:
        * N/A
        *
        * */
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport searchArea " + CommandTemplate.convertToArgs(getActions(), true));
    }
}
