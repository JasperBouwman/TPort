package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import static com.spaceman.tport.commands.tport.Features.Feature.TPortTakesItem;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class Item extends SubCommand {
    
    public Item() {
        setPermissions("TPort.edit.item", "TPort.basic");
        setCommandDescription(formatInfoTranslation("tport.command.edit.item.commandDescription"));
    }
    
    public static void setTPortDisplayItem(Player player, TPort tport) {
        setTPortDisplayItem(player, tport, null, false);
    }
    // when item is null, it uses the ItemStack in the main hand
    public static void setTPortDisplayItem(Player player, TPort tport, @Nullable ItemStack item, boolean takeOne) {
        boolean takeFromMainHand = item == null;
        if (takeFromMainHand) {
            item = player.getInventory().getItemInMainHand();
            if (item.getType().equals(Material.AIR)) {
                sendErrorTranslation(player, "tport.command.edit.item.noItem");
                return;
            }
        }
        
        boolean returnItem = tport.shouldReturnItem();
        boolean takeItem = TPortTakesItem.isEnabled();
        ItemStack oldItem = tport.getItem();
        
//        if (returnItem && takeItem && takeFromMainHand) {
//            player.getInventory().setItemInMainHand(oldItem);
//        } else {
//            if (takeItem) {
//                if (takeOne) {
//                    for (ItemStack is : player.getInventory().getContents()) {
//                        if (is == null) continue;
//                        if (is.equals(item)) {
//                            is.setAmount(is.getAmount() - 1);
//                            item.setAmount(1);
//                            break;
//                        }
//                    }
//                } else {
//                    player.getInventory().remove(item);
//                }
//            }
//
//            if (returnItem) {
//                Main.giveItems(player, oldItem);
//            }
//        }
        
        if (returnItem && takeItem) {

            if (takeFromMainHand) {
                player.getInventory().setItemInMainHand(oldItem);
            } else {
                if (takeOne) {
                    for (ItemStack is : player.getInventory().getContents()) {
                        if (is == null) continue;
                        if (is.equals(item)) {
                            is.setAmount(is.getAmount() - 1);
                            item.setAmount(1);
                            break;
                        }
                    }
                } else {
                    player.getInventory().remove(item);
                }
                Main.giveItems(player, oldItem);
            }
        } else if (returnItem) {
            Main.giveItems(player, oldItem);
        } else if (takeItem) {

            if (takeFromMainHand) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            } else {
                if (takeOne) {
                    for (ItemStack is : player.getInventory().getContents()) {
                        if (is == null) continue;
                        if (is.equals(item)) {
                            is.setAmount(is.getAmount() - 1);
                            item.setAmount(1);
                            break;
                        }
                    }
                } else {
                    player.getInventory().remove(item);
                }
            }
        }
        
        tport.setItem(item);
        tport.setShouldReturnItem(takeItem);
        tport.save();
        
        sendSuccessTranslation(player, "tport.command.edit.item.succeeded", asTPort(tport), item);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> item
        
        if (args.length != 3) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> item");
            return;
        }
        if (!hasPermissionToRun(player, true)) {
            return;
        }
        
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
            return;
        }
        if (tport.isOffered()) {
            sendErrorTranslation(player, "tport.command.edit.item.isOffered",
                    asTPort(tport), asPlayer(tport.getOfferedTo()));
            return;
        }
        setTPortDisplayItem(player, tport);
    }
}
