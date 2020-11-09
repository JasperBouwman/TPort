package com.spaceman.tport.commands.tport;

import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class Own extends SubCommand {
    
    public Own() {
        EmptyCommand emptyOwnTPortSafetyCheck = new EmptyCommand(){
            @Override
            public Message permissionsHover() {
                return Open.emptyPlayerTPortSafetyCheck.permissionsHover();
            }
        };
        emptyOwnTPortSafetyCheck.setCommandName("safetyCheck", ArgumentType.OPTIONAL);
        emptyOwnTPortSafetyCheck.setCommandDescription(textComponent("This command is used to teleport to one of your own TPorts, " +
                "the safetyCheck argument overrides your default value", ColorTheme.ColorType.infoColor));
        emptyOwnTPortSafetyCheck.setPermissions(Open.emptyPlayerTPortSafetyCheck.getPermissions());
        
        EmptyCommand emptyOwnTPort = new EmptyCommand();
        emptyOwnTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyOwnTPort.setCommandDescription(textComponent("This command is used to teleport to one of your own TPorts", ColorTheme.ColorType.infoColor));
        emptyOwnTPort.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyOwnTPort.addAction(emptyOwnTPortSafetyCheck);
        emptyOwnTPort.setPermissions(Open.emptyPlayerTPort.getPermissions());
        
        addAction(emptyOwnTPort);
        
        this.setPermissions(Open.emptyPlayerTPort.getPermissions());
    }
    
    public static List<String> getOwnTPorts(Player player) {
        return TPortManager.getTPortList(player.getUniqueId()).stream().map(TPort::getName).collect(Collectors.toList());
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to open your own TPort GUI", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport own [TPort name] [safetyCheck]
        
        if (args.length == 1) {
            if (Open.emptyPlayerTPort.hasPermissionToRun(player, true)) {
                Open.runNotPerm(new String[]{"open", player.getName()}, player);
            }
        } else if (args.length == 2) {
            if (Open.emptyPlayerTPort.hasPermissionToRun(player, true)) {
                Open.runNotPerm(new String[]{"open", player.getName(), args[1]}, player);
            }
        } else if (args.length == 3) {
            if (Open.emptyPlayerTPortSafetyCheck.hasPermissionToRun(player, true)) {
                Open.runNotPerm(new String[]{"open", player.getName(), args[1], args[2]}, player);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport own [TPort name] [safetyCheck]");
        }
    }
}
