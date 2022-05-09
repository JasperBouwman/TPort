package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Clear extends SubCommand {
    
    public Clear() {
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.log.clear.tportName.commandDescription"));
        emptyTPort.setTabRunnable((args, player) -> {
            List<String> list = TPortManager.getTPortList(player.getUniqueId()).stream().filter(tport -> !tport.isLogBookEmpty()).map(TPort::getName).collect(Collectors.toList());
            list.removeAll(Arrays.asList(args).subList(2, args.length));
            return list;
        });
        emptyTPort.setLooped(true);
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return TPortManager.getTPortList(player.getUniqueId()).stream().filter(tport -> !tport.isLogBookEmpty()).map(TPort::getName).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log clear <TPort name...>
        
        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                TPort tport = TPortManager.getTPort(player.getUniqueId(), args[i]);
                if (tport != null) {
                    tport.clearLogBook();
                    tport.save();
                    sendSuccessTranslation(player, "tport.command.log.clear.tportName.succeeded", tport);
                } else {
                    sendErrorTranslation(player, "tport.command.noTPortFound", args[i]);
                }
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log clear <TPort name...>");
        }
    }
}
