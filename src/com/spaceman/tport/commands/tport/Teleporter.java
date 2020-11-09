package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHander.CommandTemplate;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.teleporter.Create;
import com.spaceman.tport.commands.tport.teleporter.Remove;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;

public class Teleporter extends SubCommand {
    
    public Teleporter() {
        addAction(new Create());
        addAction(new Remove());
    }
    
    public final static String teleporterTitle = ChatColor.DARK_AQUA + "TPort Teleporter";
    
    public static boolean isTeleporter(ItemStack is) {
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            PersistentDataContainer dataContainer = im.getPersistentDataContainer();
            NamespacedKey keyCommand = new NamespacedKey(Main.getInstance(), "teleporterCommand");
            return dataContainer.has(keyCommand, PersistentDataType.STRING);
        }
        return false;
    }
    
    public static boolean removeTeleporter(ItemStack is) {
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            PersistentDataContainer dataContainer = im.getPersistentDataContainer();
            NamespacedKey keySize = new NamespacedKey(Main.getInstance(), "teleporterSize");
            NamespacedKey keyCommand = new NamespacedKey(Main.getInstance(), "teleporterCommand");
            NamespacedKey keyTPortUUID = new NamespacedKey(Main.getInstance(), "teleporterTPortUUID");
            if (dataContainer.has(keySize, PersistentDataType.INTEGER)) {
                //noinspection ConstantConditions
                int size = dataContainer.get(keySize, PersistentDataType.INTEGER);
                if (im.hasLore()) {
                    List<String> lore = im.getLore();
                    assert lore != null;
                    for (int i = 0; i < im.getLore().size(); i++) {
                        if (lore.get(i).equals(teleporterTitle)) {
                            lore.subList(i, i + size).clear();
                            im.setLore(lore);
    
                            dataContainer.remove(keySize);
                            dataContainer.remove(keyCommand);
                            dataContainer.remove(keyTPortUUID);
                            is.setItemMeta(im);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport teleporter create <type> [data...]
        // tport teleporter remove
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport teleporter " + CommandTemplate.convertToArgs(getActions(), false));
    }
}
