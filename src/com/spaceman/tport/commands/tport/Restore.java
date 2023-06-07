package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.Add.emptyAddTPort_permissions;
import static com.spaceman.tport.commands.tport.Add.emptyAddTPort_permissionsHoverID;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Restore extends SubCommand {
    
    public Restore() {
        setPermissions(emptyAddTPort_permissions);
        setCommandDescription(formatInfoTranslation("tport.command.restore.commandDescription"));
    }
    
    @Override
    public Message permissionsHover() {
        return formatInfoTranslation(emptyAddTPort_permissionsHoverID, (Object[]) emptyAddTPort_permissions);
    }
    
    private static final HashMap<UUID, TPort> tportBin = new HashMap<>();
    public static TPort getRestoreTPort(UUID owner) {
        return tportBin.getOrDefault(owner, null);
    }
    public static void setRestoreTPort(UUID owner, TPort toRestore) {
        tportBin.put(owner, toRestore);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport restore
        
        if (args.length != 1) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport restore");
            return;
        }
        
        TPort restoreTPort = getRestoreTPort(player.getUniqueId());
        if (restoreTPort == null) {
            sendErrorTranslation(player, "tport.command.restore.noRestore");
            return;
        }
        
        if (Features.Feature.TPortTakesItem.isEnabled()) {
            ItemStack item = new ItemStack(player.getInventory().getItemInMainHand());
            if (item.getType().equals(Material.AIR)) {
                sendErrorTranslation(player, "tport.command.restore.noItem");
                return;
            }
            restoreTPort.setItem(item);
        }
        
        TPort addedTPort = TPortManager.addTPort(player, restoreTPort, true);
        if (addedTPort != null) { //tport is added
            tportBin.remove(player.getUniqueId());
            if (Features.Feature.TPortTakesItem.isEnabled()) {
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
                addedTPort.setShouldReturnItem(true);
            } else {
                addedTPort.setShouldReturnItem(false);
            }
            addedTPort.save();
        }
    }
}
