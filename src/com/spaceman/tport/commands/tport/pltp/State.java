package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class State extends SubCommand {
    
    private final EmptyCommand emptyState;
    
    public State() {
        emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(textComponent("This command is used to set your PLTP statement. If ", ColorTheme.ColorType.infoColor),
                textComponent("true", ColorTheme.ColorType.varInfoColor),
                textComponent(" players can teleport to you, if ", ColorTheme.ColorType.infoColor),
                textComponent("false", ColorTheme.ColorType.varInfoColor),
                textComponent(" only players in your PLTP whitelist can teleport to you", ColorTheme.ColorType.infoColor));
        emptyState.setPermissions("TPort.PLTP.state.set", "TPort.basic");
        addAction(emptyState);
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get your PLTP statement", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP state [state]
        
        if (args.length == 2) {
            Files tportData = getFile("TPortData");
            boolean pltpState = tportData.getConfig().getBoolean("tport." + player.getUniqueId().toString() + ".tp.statement", true);
            Message message = new Message();
            message.addText(textComponent("Your PLTP state is: ", ColorTheme.ColorType.infoColor));
            message.addText(textComponent(String.valueOf(pltpState), ColorTheme.ColorType.varInfoColor));
            message.sendMessage(player);
        } else if (args.length == 3) {
            if (!emptyState.hasPermissionToRun(player, true)) {
                return;
            }
            Files tportData = getFile("TPortData");
            boolean pltpState = Boolean.parseBoolean(args[2]);
            tportData.getConfig().set("tport." + player.getUniqueId().toString() + ".tp.statement", pltpState);
            tportData.saveConfig();
            Message message = new Message();
            message.addText(textComponent("Your PLTP state is set to: ", ColorTheme.ColorType.infoColor));
            message.addText(textComponent(String.valueOf(pltpState), ColorTheme.ColorType.varInfoColor));
            message.sendMessage(player);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport PLTP state [state]");
        }
    }
}
