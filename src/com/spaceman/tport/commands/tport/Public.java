package com.spaceman.tport.commands.tport;

import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.CommandTemplate;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.publc.Add;
import com.spaceman.tport.commands.tport.publc.Open;
import com.spaceman.tport.commands.tport.publc.Remove;
import com.spaceman.tport.commands.tport.publc.*;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Public extends SubCommand {
    
    private final EmptyCommand empty;
    
    public Public() {
        empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(textComponent("This command is used to open the Public TPort GUI", ColorTheme.ColorType.infoColor));
        empty.setPermissions("TPort.public.open", "TPort.basic");
        addAction(empty);
        addAction(new Open());
        addAction(new Add());
        addAction(new Remove());
        addAction(new List());
        addAction(new Move());
        addAction(new ListSize());
    }
    
    public static boolean isEnabled() {
        Files tportConfig = getFile("TPortConfig");
        if (!tportConfig.getConfig().contains("public.enabled")) {
            tportConfig.getConfig().set("public.enabled", true);
            tportConfig.saveConfig();
            return true;
        } else {
            return tportConfig.getConfig().getBoolean("public.enabled");
        }
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public
        // tport public open <TPort name|page>
        // tport public add <TPort name>
        // tport public remove <own TPort name|all TPort name>
        // tport public list [own|all]
        // tport public move <TPort name> <slot|TPort name>
        
        if (isEnabled()) {
            if (args.length == 1) {
                if (empty.hasPermissionToRun(player, true)) {
                    TPortInventories.openPublicTPortGUI(player, 0);
                }
            } else {
                if (!runCommands(getActions(), args[1], args, player)) {
                    sendErrorTheme(player, "Usage: %s", "/tport public " + CommandTemplate.convertToArgs(getActions(), true));
                }
            }
        } else {
            sendErrorTheme(player, "Public TPorts is not enabled");
        }
    }
}
