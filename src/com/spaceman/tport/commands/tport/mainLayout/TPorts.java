package com.spaceman.tport.commands.tport.mainLayout;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.commandHandler.ArgumentType.OPTIONAL;
import static com.spaceman.tport.commands.tport.MainLayout.showTPorts;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class TPorts extends SubCommand {
    
    private static final TPorts instance = new TPorts();
    public static TPorts getInstance() {
        return instance;
    }
    
    public final EmptyCommand emptyTPortsState;
    
    private TPorts() {
        emptyTPortsState = new EmptyCommand();
        emptyTPortsState.setCommandName("state", OPTIONAL);
        emptyTPortsState.setCommandDescription(formatInfoTranslation("tport.command.mainLayout.tports.state.commandDescription"));
        emptyTPortsState.setPermissions("TPort.mainLayout.TPorts");
        
        addAction(emptyTPortsState);
        
        setCommandDescription(formatInfoTranslation("tport.command.mainLayout.tports.commandDescription"));
    }
    
    @Override
    public String getName(String arg) {
        return "TPorts";
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyTPortsState.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Arrays.asList("true", "false");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport mainLayout TPorts [state]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.mainLayout.tports.succeeded",
                    formatTranslation(varInfoColor, varInfo2Color, "tport.command.mainLayout." + (showTPorts(player) ? "show" : "hide")));
        } else if (args.length == 3) {
            if (!emptyTPortsState.hasPermissionToRun(player, true)) {
                return;
            }
            Boolean state = Main.toBoolean(args[2]);
            if (state == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport mainLayout TPorts [true|false]");
                return;
            }
            showTPorts(player, state);
            sendSuccessTranslation(player, "tport.command.mainLayout.tports.state.succeeded",
                    formatTranslation(varInfoColor, varInfo2Color, "tport.command.mainLayout." + (state ? "show" : "hide")));
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport mainLayout TPorts [state]");
        }
    }
}
