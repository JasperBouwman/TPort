package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.util.*;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Consent extends SubCommand {
    
    static HashMap<UUID, ArrayList<UUID>> pltpConsentMap = new HashMap<>();
    
    public Consent() {
        EmptyCommand emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(textComponent("This command is used to set your PLTP consent statement. If ", ColorTheme.ColorType.infoColor),
                textComponent("true", ColorTheme.ColorType.varInfoColor),
                textComponent(" you will be asked if that player can teleport to you, if ", ColorTheme.ColorType.infoColor),
                textComponent("false", ColorTheme.ColorType.varInfoColor),
                textComponent(" you wont be asked", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.PLTP.consent.set", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        addAction(emptyState);
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get your PLTP consent statement", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP consent [state]
        
        if (args.length == 2) {
            Files tportData = getFile("TPortData");
            boolean pltpState = tportData.getConfig().getBoolean("tport." + player.getUniqueId().toString() + ".tp.consent", false);
            Message message = new Message();
            message.addText(textComponent("Your PLTP consent state is: ", ColorTheme.ColorType.infoColor));
            message.addText(textComponent(String.valueOf(pltpState), ColorTheme.ColorType.varInfoColor));
            message.sendMessage(player);
        } else if (args.length == 3) {
            if (!hasPermission(player, true, "TPort.PLTP.consent.set")) {
                return;
            }
            Files tportData = getFile("TPortData");
            boolean pltpState = Boolean.parseBoolean(args[2]);
            tportData.getConfig().set("tport." + player.getUniqueId().toString() + ".tp.consent", pltpState);
            tportData.saveConfig();
            Message message = new Message();
            message.addText(textComponent("Your PLTP consent state is set to: ", ColorTheme.ColorType.infoColor));
            message.addText(textComponent(String.valueOf(pltpState), ColorTheme.ColorType.varInfoColor));
            message.sendMessage(player);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport PLTP consent [state]");
        }
    }
}
