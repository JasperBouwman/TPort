package com.spaceman.tport.commands.tport.mainLayout;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.commandHandler.ArgumentType.OPTIONAL;
import static com.spaceman.tport.commands.tport.MainLayout.showPlayers;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Players extends SubCommand {
    
    private static final Players instance = new Players();
    public static Players getInstance() {
        return instance;
    }
    
    public final EmptyCommand emptyPlayersState;
    
    private Players() {
        emptyPlayersState = new EmptyCommand();
        emptyPlayersState.setCommandName("state", OPTIONAL);
        emptyPlayersState.setCommandDescription(formatInfoTranslation("tport.command.mainLayout.players.state.commandDescription"));
        emptyPlayersState.setPermissions("TPort.mainLayout.players");
        
        addAction(emptyPlayersState);
        
        setCommandDescription(formatInfoTranslation("tport.command.mainLayout.players.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyPlayersState.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Arrays.asList("true", "false");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport mainLayout players [state]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.mainLayout.players.succeeded",
                    formatTranslation(varInfoColor, varInfo2Color, "tport.command.mainLayout." + (showPlayers(player) ? "show" : "hide")));
        } else if (args.length == 3) {
            if (!emptyPlayersState.hasPermissionToRun(player, true)) {
                return;
            }
            Boolean state = Main.toBoolean(args[2]);
            if (state == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport mainLayout players [true|false]");
                return;
            }
            showPlayers(player, state);
            sendSuccessTranslation(player, "tport.command.mainLayout.players.state.succeeded",
                    formatTranslation(varInfoColor, varInfo2Color, "tport.command.mainLayout." + (state ? "show" : "hide")));
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport mainLayout players [state]");
        }
    }
}
