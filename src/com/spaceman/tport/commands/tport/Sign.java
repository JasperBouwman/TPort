package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.SubCommand;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class Sign extends SubCommand implements Listener {
    
    private static Location signLocation = null;
    
    @EventHandler
    @SuppressWarnings("unused")
    public void test(SignChangeEvent e) {
        if (e.getBlock().getLocation().equals(signLocation)) {
            e.setCancelled(true);
            for (String line : e.getLines()) {
                e.getPlayer().sendMessage(line);
            }
        }
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport sign
        
//        if (signLocation == null) {
//            int y = player.getWorld().getMaxHeight();
//            signLocation = new Location(player.getWorld(), 0, y - 1, 0);
//            signLocation.getBlock().setType(Material.OAK_SIGN);
//        }
//
//        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signLocation.getBlock().getState();
//        player.openSign(sign);


        
//        FancyClickEvent.FancyClickRunnable onAccept = (whoClicked, clickType, pdc, fancyInventory) -> {
//            String s = getKeyboardOutput(fancyInventory);
//            whoClicked.sendMessage(s);
//        };
//
//        KeyboardGUI.openKeyboard(player, onAccept, null);
        

//        EnderCrystal c = player.getWorld().spawn(player.getLocation(), EnderCrystal.class);
//        c.setBeamTarget(c.getLocation().add(0, 10, 0));
//        c.setVisibleByDefault(false);
//        c.setShowingBottom(false);
//
//        player.showEntity(Main.getInstance(), c);
    }
}
