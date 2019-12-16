package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.Back;
import com.spaceman.tport.commands.tport.Delay;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tpEvents.TPEManager;
import com.spaceman.tport.tpEvents.TPRestriction;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.*;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.TPortInventories.*;
import static com.spaceman.tport.commands.tport.Back.prevTPort;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;
import static com.spaceman.tport.tpEvents.TPEManager.registerTP;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class InventoryClick implements Listener {
    
    public static final int TPortSize = 24;
    public static String NEXT = ChatColor.YELLOW + "Next";
    public static String PREVIOUS = ChatColor.YELLOW + "Previous";
    public static String BACK = ChatColor.YELLOW + "Back";
    public static String TPOFF = ChatColor.YELLOW + "Player tp is off";
    public static String WARP = ChatColor.YELLOW + "Warp to ";
    public static String OFFLINE = ChatColor.YELLOW + "Player is not online";
    
    private static boolean testHead(ItemStack itemStack) {
        //noinspection ConstantConditions
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            return ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).matches("PLTP state is set to .+, PLTP consent is set to .+");
        }
        return false;
    }
    
    public static void tpPlayerToPlayer(Player player, Player toPlayer) {
        prevTPort.put(player.getUniqueId(), new Back.PrevTPort(null, player.getLocation(), toPlayer.getUniqueId().toString(), null));
        requestTeleportPlayer(player, toPlayer.getLocation());
    }
    
    public static void tpPlayerToTPort(Player player, Location location, String tportName, String ownerUUID) {
        prevTPort.put(player.getUniqueId(), new Back.PrevTPort(tportName, player.getLocation(), ownerUUID, null));
        requestTeleportPlayer(player, location);
    }
    
    private static void teleportPlayer(@Nullable Player player, Location l) {
        if (player == null) return;
        
        ArrayList<LivingEntity> slaves = new ArrayList<>();
        for (Entity e : player.getWorld().getEntities()) {
            if (e instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) e;
                if (livingEntity.isLeashed()) {
                    if (livingEntity.getLeashHolder() instanceof Player) {
                        if (livingEntity.getLeashHolder().getUniqueId().equals(player.getUniqueId())) {
                            slaves.add(livingEntity);
                            livingEntity.setLeashHolder(null);
                        }
                    }
                }
            }
        }
        
        LivingEntity horse = null;
        TreeSpecies boatType = null;
        Entity sailor = null;
        if (player.getVehicle() instanceof LivingEntity) {
            horse = (LivingEntity) player.getVehicle();
        } else if (player.getVehicle() instanceof Boat) {
            Boat b = (Boat) player.getVehicle();
            boatType = b.getWoodType();
            if (b.getPassengers().size() > 1) {
                sailor = b.getPassengers().get(1);
            }
            b.remove();
        }
        
        TPEManager.getOldLocAnimation(player.getUniqueId()).showIfEnabled(player, player.getLocation().clone());
        player.teleport(l);
        TPEManager.removeTP(player.getUniqueId());
        TPEManager.getNewLocAnimation(player.getUniqueId()).showIfEnabled(player, l.clone());
        
        try {
            if (horse != null) {
                horse.teleport(player);
                horse.addPassenger(player);
            } else if (boatType != null) {
                Boat b = player.getWorld().spawn(player.getLocation(), Boat.class);
                b.setWoodType(boatType);
                b.teleport(player);
                b.addPassenger(player);
                if (sailor != null) {
                    sailor.teleport(player);
                    b.addPassenger(sailor);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        for (LivingEntity e : slaves) {
            try {
                e.teleport(player);
                e.setLeashHolder(player);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void requestTeleportPlayer(Player player, Location l) {
        if (TPEManager.hasTPRequest(player.getUniqueId())) {
            Message message = new Message();
            message.addText(textComponent("You already have a tp request, click ", ColorTheme.ColorType.errorColor));
            message.addText(textComponent("here", ColorTheme.ColorType.varErrorColor,
                    new HoverEvent(textComponent("/tport cancel", ColorTheme.ColorType.varInfoColor)), ClickEvent.runCommand("/tport cancel")));
            message.addText(textComponent(" to cancel it", ColorTheme.ColorType.errorColor));
            message.sendMessage(player);
            return;
        }
        int delay = Delay.delayTime(player);
        if (delay == 0) {
            teleportPlayer(player, l);
        } else {
            TPRestriction tpRestriction = TPEManager.getTPRestriction(player.getUniqueId());
            if (tpRestriction == null) {
                registerTP(player.getUniqueId(),
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> teleportPlayer(Bukkit.getPlayer(player.getUniqueId()), l), delay).getTaskId());
            } else {
                tpRestriction.start(player, registerTP(player.getUniqueId(),
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                            if (tpRestriction.shouldTeleport(player))
                                teleportPlayer(Bukkit.getPlayer(player.getUniqueId()), l);
                        }, delay).getTaskId()
                ));
            }
        }
    }
    
    @EventHandler
    @SuppressWarnings({"unused"})
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        String invTitle = e.getView().getTitle();
        
        if (e.getRawSlot() > inv.getSize()) {
            return;
        }
        
        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        if (!meta.hasDisplayName()) {
            return;
        }
        
        Files tportData = getFile("TPortData");
        
        if (invTitle.startsWith("Choose a player")) {
            
            String playerUUID = player.getUniqueId().toString();
            
            String pageNumber = invTitle.replace("Choose a player ", "").replace("(", "").replace(")", "");
            if (meta.getDisplayName().equals(NEXT)) {
                openMainTPortGUI(player, Integer.parseInt(pageNumber));
            }
            
            if (meta.getDisplayName().equals(PREVIOUS)) {
                openMainTPortGUI(player, Integer.parseInt(pageNumber) - 2);
            }
            
            e.setCancelled(true);
            for (String s : tportData.getKeys("tport")) {
                String playerName = PlayerUUID.getPlayerName(s);
                if (ChatColor.stripColor(meta.getDisplayName()).equals(playerName)) {
                    mainTPortGUIPage.put(player.getUniqueId(), Integer.parseInt(pageNumber) - 1);
                    openTPortGUI(UUID.fromString(s), player);
                    return;
                }
            }
            
        }
        else if (invTitle.startsWith("TPort: ")) {
            e.setCancelled(true);
            
            for (String ownerUUIDString : tportData.getKeys("tport")) {
                UUID ownerUUID = UUID.fromString(ownerUUIDString);
                
                if (invTitle.equals("TPort: " + PlayerUUID.getPlayerName(ownerUUID))) {
                    
                    //back button
                    if (item.getType().equals(Material.BARRIER)) {
                        if (meta.getDisplayName().equals(BACK)) {
                            if (e.getSlot() == 26) {
                                e.setCancelled(true);
                                if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {//right lick
                                    openPublicTPortGUI(player, 0);
                                    return;
                                } else if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {//left click
                                    openMainTPortGUI(player, mainTPortGUIPage.getOrDefault(player.getUniqueId(), 0));
                                    return;
                                } else if (e.getAction().equals(InventoryAction.CLONE_STACK) || e.getAction().equals(InventoryAction.UNKNOWN)) {//middle click
                                    openTPortGUI(player.getUniqueId(), player);
                                    return;
                                }
                                return;
                            } else {
                                e.setCancelled(true);
                            }
                        }
                    }
                    
                    //tp back, biomeTP and featureTP
                    if (item.getType().equals(Material.ELYTRA)) {
                        if (e.getSlot() == 17) {
                            e.setCancelled(true);
                            List<String> lore = meta.getLore();
                            if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {//left click
                                if (hasPermission(player, "TPort.biomeTP.open")) {
                                    openBiomeTP(player, 0);
                                }
                                return;
                            } else if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {//right click
                                TPortCommand.executeInternal(player, "back");
                                return;
                            } else if (e.getAction().equals(InventoryAction.CLONE_STACK) || e.getAction().equals(InventoryAction.UNKNOWN)) {//middle click
                                if (hasPermission(player, "TPort.featureTP.open")) {
                                    openFeatureTP(player, 0);
                                }
                                return;
                            }
                        }
                    }
                    
                    //PLTP
                    if (item.getType().equals(Material.PLAYER_HEAD)) {
                        if (e.getSlot() == 8) {
                            if (meta.getDisplayName().equals(OFFLINE) || meta.getDisplayName().equals(TPOFF)) {
                                e.setCancelled(true);
                                
                                if (!tportData.getConfig().getBoolean("tport." + ownerUUIDString + ".tp.statement") && Bukkit.getOfflinePlayer(ownerUUID).isOnline()) {
                                    
                                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                            .getStringList("tport." + ownerUUID + ".tp.players");
                                    
                                    if (list.contains(player.getUniqueId().toString())) {
                                        if (Bukkit.getPlayer(ownerUUID) != null) {
                                            TPortCommand.executeInternal(player, new String[]{"PLTP", "tp", PlayerUUID.getPlayerName(ownerUUID)});
                                        }
                                    } else {
                                        openTPortGUI(ownerUUID, player);
                                    }
                                }
                                
                            } else if (meta.getDisplayName().equals(WARP + PlayerUUID.getPlayerName(ownerUUID))) {
                                if (!Bukkit.getOfflinePlayer(ownerUUID).isOnline()) {
                                    openTPortGUI(ownerUUID, player);
                                    e.setCancelled(true);
                                } else {
                                    TPortCommand.executeInternal(player, new String[]{"PLTP", "tp", PlayerUUID.getPlayerName(ownerUUID)});
                                    return;
                                }
                            } else if (testHead(item)) {
                                e.setCancelled(true);
                                if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                                    boolean pltpState = tportData.getConfig().getBoolean("tport." + player.getUniqueId().toString() + ".tp.consent", false);
                                    TPortCommand.executeInternal(player, new String[]{"PLTP", "consent", String.valueOf(!pltpState)});
                                } else if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                                    boolean pltpState = tportData.getConfig().getBoolean("tport." + player.getUniqueId().toString() + ".tp.statement", true);
                                    TPortCommand.executeInternal(player, new String[]{"PLTP", "state", String.valueOf(!pltpState)});
                                }
                                openTPortGUI(player.getUniqueId(), player);
                            }
                        }
                    }
                    
                    //tp TPort, and quick edit
                    for (int i = 0; i < TPortSize; i++) {
                        
                        TPort tport = TPortManager.getTPort(ownerUUID, i);
                        
                        if (tport != null) {
                            if (ChatColor.stripColor(meta.getDisplayName()).equals(tport.getName())) {
                                if (e.getSlot() >= 0 && e.getSlot() < 8 ||
                                        e.getSlot() >= 9 && e.getSlot() < 17 ||
                                        e.getSlot() >= 18 && e.getSlot() < 26) {
                                    
                                    e.setCancelled(true);
                                    if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                                        if (player.getUniqueId().equals(ownerUUID)) {
                                            QuickEditType.get(tportData.getConfig().getString("tport." + player.getUniqueId() + ".editState"))
                                                    .edit(tport, player);
                                            openTPortGUI(player.getUniqueId(), player);
                                            return;
                                        }
                                    } else if (e.getAction().equals(InventoryAction.CLONE_STACK) || e.getAction().equals(InventoryAction.UNKNOWN)) {
                                        if (player.getUniqueId().equals(ownerUUID)) {
                                            QuickEditType type = QuickEditType.get(tportData.getConfig().getString("tport." + player.getUniqueId() + ".editState")).getNext();
                                            QuickEditType.clearData(player.getUniqueId());
                                            tportData.getConfig().set("tport." + player.getUniqueId() + ".editState", type.name());
                                            tportData.saveConfig();
                                            openTPortGUI(ownerUUID, player);
                                        }
                                    } else if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                                        TPortCommand.executeInternal(player, new String[]{"open", PlayerUUID.getPlayerName(ownerUUID), tport.getName()});
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (invTitle.startsWith("Select a Biome ")) {
            String pageNumber = invTitle.replace("Select a Biome ", "").replace("(", "").replace(")", "");
            if (e.getSlot() == 8) {//page up
                openBiomeTP(player, Integer.parseInt(pageNumber) - 2);
            } else if (e.getSlot() == inv.getSize() - 1) {//page down
                openBiomeTP(player, Integer.parseInt(pageNumber));
            } else {
                if (e.getSlot() % 9 != 0 && e.getSlot() % 9 != 8) {
                    e.setCancelled(true);
                    if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                        TPortCommand.executeInternal(player, new String[]{"biomeTP", meta.getDisplayName()});
                    }
                } else if (e.getSlot() == 18) {
                    e.setCancelled(true);
                    if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                        TPortCommand.executeInternal(player, new String[]{"biomeTP", "random"});
                    }
                } else if (item.getType().equals(Material.BARRIER) && meta.getDisplayName().equals(BACK)) {
                    e.setCancelled(true);
                    if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {//right lick
                        openTPortGUI(player.getUniqueId(), player);
                    } else if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {//left click
                        openMainTPortGUI(player, mainTPortGUIPage.getOrDefault(player.getUniqueId(), 0));
                    } else if (e.getAction().equals(InventoryAction.CLONE_STACK) || e.getAction().equals(InventoryAction.UNKNOWN)) {//middle click
                        openPublicTPortGUI(player, 0);
                    }
                }
            }
        }
        else if (invTitle.startsWith("Select a Feature ")) {
            
            String pageNumber = invTitle.replace("Select a Feature ", "").replace("(", "").replace(")", "");
            if (e.getSlot() == 8) {//page up
                openFeatureTP(player, Integer.parseInt(pageNumber) - 2);
            } else if (e.getSlot() == inv.getSize() - 1) {//page down
                openFeatureTP(player, Integer.parseInt(pageNumber));
            } else {
                if (e.getSlot() % 9 != 0 && e.getSlot() % 9 != 8) {
                    e.setCancelled(true);
                    if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                        TPortCommand.executeInternal(player, new String[]{"featureTP", meta.getDisplayName()});
                    }
                } else if (item.getType().equals(Material.BARRIER) && meta.getDisplayName().equals(BACK)) {
                    e.setCancelled(true);
                    if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {//right lick
                        openTPortGUI(player.getUniqueId(), player);
                    } else if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {//left click
                        openMainTPortGUI(player, mainTPortGUIPage.getOrDefault(player.getUniqueId(), 0));
                    } else if (e.getAction().equals(InventoryAction.CLONE_STACK) || e.getAction().equals(InventoryAction.UNKNOWN)) {//middle click
                        openPublicTPortGUI(player, 0);
                    }
                }
            }
        }
        else if (invTitle.startsWith("Select a Public TPort ")) {
            
            String pageNumber = invTitle.replace("Select a Public TPort ", "").replace("(", "").replace(")", "");
            if (meta.getDisplayName().equals(NEXT)) {
                e.setCancelled(true);
                openPublicTPortGUI(player, Integer.parseInt(pageNumber));
            }
            
            if (meta.getDisplayName().equals(PREVIOUS)) {
                e.setCancelled(true);
                openPublicTPortGUI(player, Integer.parseInt(pageNumber) - 2);
            }
            //back to main GUI
            if (item.getType().equals(Material.BARRIER)) {
                if (meta.getDisplayName().equals(BACK)) {
                    e.setCancelled(true);
                    if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {//right lick
                        openTPortGUI(player.getUniqueId(), player);
                    } else if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {//left click
                        openMainTPortGUI(player, mainTPortGUIPage.getOrDefault(player.getUniqueId(), 0));
                    } else if (e.getAction().equals(InventoryAction.CLONE_STACK) || e.getAction().equals(InventoryAction.UNKNOWN)) {//middle click
                        openPublicTPortGUI(player, 0);
                    }
                    return;
                }
            }
            
            for (String publicTPortSlot : tportData.getKeys("public.tports")) {
                String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
                
                //noinspection ConstantConditions
                TPort tport = getTPort(UUID.fromString(tportID));
                if (tport != null) {
                    if (ChatColor.stripColor(tport.getName()).equalsIgnoreCase(ChatColor.stripColor(meta.getDisplayName()))) {
                        e.setCancelled(true);
                        
                        if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                            if (quickEditPublicMoveList.containsKey(player.getUniqueId())) {
                                UUID otherTPortID = quickEditPublicMoveList.get(player.getUniqueId());
                                quickEditPublicMoveList.remove(player.getUniqueId());
                                if (!otherTPortID.equals(tport.getTportID())) {
                                    TPort tmpTPort = TPortManager.getTPort(otherTPortID);
                                    if (tmpTPort != null) {
                                        TPortCommand.executeInternal(player, new String[]{"public", "move", tmpTPort.getName(), tport.getName()});
                                    }
                                }
                            } else {
                                if (!hasPermission(player, false, "TPort.public.move", "TPort.admin.public")) {
                                    return;
                                }
                                quickEditPublicMoveList.put(player.getUniqueId(), tport.getTportID());
                            }
                            openPublicTPortGUI(player, Integer.parseInt(pageNumber) - 1);
                        } else {
                            if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                                TPortCommand.executeInternal(player, new String[]{"open", PlayerUUID.getPlayerName(tport.getOwner()), tport.getName()});
                            }
                        }
                        return;
                    }
                }
            }
        }
    }
}
