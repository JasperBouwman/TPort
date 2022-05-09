package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.SubCommand;
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

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Teleporter extends SubCommand {
    
    public Teleporter() {
        addAction(new Create());
        addAction(new Remove());
    }
    
    @Deprecated
    public final static String teleporterTitle = ChatColor.DARK_AQUA + "TPort Teleporter";
    
    public static boolean removeTeleporter(ItemStack is) {
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            PersistentDataContainer dataContainer = im.getPersistentDataContainer();
            NamespacedKey keyVersion = new NamespacedKey(Main.getInstance(), "teleporterVersion");
            NamespacedKey keySize = new NamespacedKey(Main.getInstance(), "teleporterSize");
            NamespacedKey keyCommand = new NamespacedKey(Main.getInstance(), "teleporterCommand");
            NamespacedKey keyTPortUUID = new NamespacedKey(Main.getInstance(), "teleporterTPortUUID");
            NamespacedKey keyPlayerUUID = new NamespacedKey(Main.getInstance(), "teleporterPlayerUUID");
            
            if (dataContainer.has(keyVersion, PersistentDataType.INTEGER)) {
                
                int version = dataContainer.get(keyVersion, PersistentDataType.INTEGER);
                
                if (version == 2) {
                    if (dataContainer.has(keySize, PersistentDataType.INTEGER)) {
                        //noinspection ConstantConditions
                        int size = dataContainer.get(keySize, PersistentDataType.INTEGER);
                        if (im.hasLore()) {
                            List<String> lore = im.getLore();
                            assert lore != null;
                            if (size > 0) {
                                lore.subList(0, size).clear();
                            }
                            im.setLore(lore);
                            
                            dataContainer.remove(keySize);
                            dataContainer.remove(keyCommand);
                            dataContainer.remove(keyTPortUUID);
                            dataContainer.remove(keyVersion);
                            dataContainer.remove(keyPlayerUUID);
                            is.setItemMeta(im);
                            return true;
                        }
                    }
                }
            } else { //version 1
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
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter " + CommandTemplate.convertToArgs(getActions(), false));
    }
}
