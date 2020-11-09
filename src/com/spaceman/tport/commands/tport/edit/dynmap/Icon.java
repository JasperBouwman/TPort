package com.spaceman.tport.commands.tport.edit.dynmap;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Icon extends SubCommand {
    
    private final EmptyCommand emptyIcon;
    
    public Icon() {
        emptyIcon = new EmptyCommand();
        emptyIcon.setCommandName("icon", ArgumentType.OPTIONAL);
        emptyIcon.setCommandDescription(TextComponent.textComponent("This command is used to set your TPort icon on Dynmap", ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.edit.dynmap.setIcon", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        emptyIcon.setPermissions("TPort.edit.dynmap.setIcon", "TPort.basic");
        addAction(emptyIcon);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Main.getOrDefault(DynmapHandler.getIcons(), new ArrayList<Pair<String, String>>()).stream().map(Pair::getRight).collect(Collectors.toList());
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(TextComponent.textComponent("This command is used to get your TPort icon on Dynmap", ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> dynmap icon [icon]
        
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            sendInfoTheme(player, "TPort %s has the icon %s", tport.getName(), DynmapHandler.getTPortIconName(tport));
        } else if (args.length == 5) {
            if (!emptyIcon.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                return;
            }
    
            String id = DynmapHandler.iconLabelToID(args[4]);
    
            if (id == null) {
                sendErrorTheme(player, "Icon %s does not exist", args[4]);
                return;
            }
            tport.setDynmapIconID(id);
            sendSuccessTheme(player, "Successfully set Dynmap icon of TPort %s to %s", tport.getName(), DynmapHandler.getTPortIconName(tport));
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> dynmap show [state]");
        }
    }
}
