package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Clear extends SubCommand {
    
    public Clear() {
        EmptyCommand emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(TextComponent.textComponent("This command is used to clear the TPort log of the given TPort", ColorTheme.ColorType.infoColor));
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
                    sendSuccessTheme(player, "Successfully cleared the log of TPort %s", tport.getName());
                } else {
                    sendErrorTheme(player, "No TPort found called %s", args[i]);
                }
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport log clear <TPort name...>");
        }
    }
}
