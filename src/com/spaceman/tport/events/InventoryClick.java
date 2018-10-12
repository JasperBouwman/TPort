package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.tport.Back;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
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

import java.util.*;

import static com.spaceman.tport.Main.Cooldown.*;
import static com.spaceman.tport.TPortInventories.*;
import static com.spaceman.tport.commands.TPort.pltp;
import static com.spaceman.tport.commands.tport.Back.prevTPort;
import static com.spaceman.tport.commands.tport.Back.tpBack;
import static com.spaceman.tport.commands.tport.BiomeTP.biomeTP;
import static com.spaceman.tport.commands.tport.FeatureTP.featureTP;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class InventoryClick implements Listener {

    public static final int TPortSize = 24;
    public static String NEXT = ChatColor.DARK_AQUA + "Next";
    public static String BACK = ChatColor.YELLOW + "Back";
    public static String TPOFF = ChatColor.YELLOW + "Player tp is off";
    public static String WARP = ChatColor.YELLOW + "Warp to ";
    public static String OFFLINE = ChatColor.YELLOW + "Player is not online";
    public static ItemMeta SET_TP_ON = getItemMeta(ChatColor.YELLOW + "Set PLTP on", "When clicking this Player Teleportation will be turned on");
    public static ItemMeta SET_TP_OFF = getItemMeta(ChatColor.YELLOW + "Set PLTP off", "When clicking this Player Teleportation will be turned off");
    public static String PREVIOUS = ChatColor.DARK_AQUA + "Previous";

    private static ItemMeta getItemMeta(String name, String lore) {
        ItemStack is = new ItemStack(Material.STONE);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore.split("\n")));
        return im;
    }

    private static boolean testHeadOn(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta im = itemStack.getItemMeta();
            return im.getDisplayName().equals(SET_TP_ON.getDisplayName()) && im.getLore().equals(SET_TP_ON.getLore());
        }
        return false;
    }

    private static boolean testHeadOff(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta im = itemStack.getItemMeta();
            return im.getDisplayName().equals(SET_TP_OFF.getDisplayName()) && im.getLore().equals(SET_TP_OFF.getLore());
        }
        return false;
    }

    private static void tpPlayerToPlayer(Player player, Player toPlayer) {
        prevTPort.put(player.getUniqueId(), new Back.PrevTPort(null, player.getLocation(), toPlayer.getUniqueId().toString()));
        teleportPlayer(player, toPlayer.getLocation());
    }

    public static void tpPlayerToTPort(Player player, Location location, String tPort, String tPortOwner) {
        prevTPort.put(player.getUniqueId(), new Back.PrevTPort(tPort, player.getLocation(), tPortOwner));
        teleportPlayer(player, location);
    }

    public static void teleportPlayer(Player player, Location l) {

        ArrayList<LivingEntity> livingEntityO = new ArrayList<>();
        for (Entity e : player.getWorld().getEntities()) {
            if (e instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) e;
                if (livingEntity.isLeashed()) {
                    if (livingEntity.getLeashHolder() instanceof Player) {
                        if (livingEntity.getLeashHolder().getUniqueId().equals(player.getUniqueId())) {
                            livingEntityO.add(livingEntity);
                        }
                    }
                }
            }
        }

        LivingEntity ridingEntity = null;
        Boat boat = null;
        Entity tmpEntity = null;
        if (player.getVehicle() instanceof LivingEntity) {
            ridingEntity = (LivingEntity) player.getVehicle();
        } else if (player.getVehicle() instanceof Boat) {
            boat = (Boat) player.getVehicle();
            if (boat.getPassengers().size() > 1) {
                tmpEntity = boat.getPassengers().get(1);
            }
        }

        player.teleport(l);

        try {
            if (ridingEntity != null) {
                ridingEntity.teleport(player);
                ridingEntity.addPassenger(player);
            } else if (boat != null) {
                if (!livingEntityO.isEmpty()) {
                    for (LivingEntity e : livingEntityO) {
                        e.setLeashHolder(null);
                    }
                }
                if (tmpEntity != null) {
                    tmpEntity.teleport(player);
                    boat.teleport(player);
                    boat.addPassenger(player);
                    boat.addPassenger(tmpEntity);
                } else {
                    boat.teleport(player);
                    boat.addPassenger(player);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (LivingEntity e : livingEntityO) {
            try {
                e.teleport(player);
                e.setLeashHolder(player);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();

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
        if (meta.getDisplayName() == null) {
            return;
        }

        Files tportData = getFiles("TPortData");

        if (inv.getTitle().startsWith("Choose a player")) {

            String playerUUID = player.getUniqueId().toString();

            if (meta.getDisplayName().equals(NEXT)) {
                openMainTPortGUI(player, Integer.parseInt(inv.getTitle().replace("Choose a player ", "").replace("(", "").replace(")", "")));
            }

            if (meta.getDisplayName().equals(PREVIOUS)) {
                openMainTPortGUI(player, Integer.parseInt(inv.getTitle().replace("Choose a player ", "").replace("(", "").replace(")", "")) - 2);
            }

            e.setCancelled(true);
            for (String s : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {
                if (meta.getDisplayName().equals(PlayerUUID.getPlayerName(s))) {
                    mainTPortGUIPage.put(player.getUniqueId(), Integer.parseInt(inv.getTitle().replace("Choose a player ", "").replace("(", "").replace(")", "")) - 1);
                    openTPortGUI(meta.getDisplayName(), s, player);
                    return;
                }
            }

        }
        else if (inv.getTitle().startsWith("TPort: ")) {
            e.setCancelled(true);

            for (String s : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {

                if (inv.getTitle().equals("TPort: " + PlayerUUID.getPlayerName(s))) {

                    String playerUUID = player.getUniqueId().toString();

                    if (item.getType().equals(Material.BARRIER)) {
                        if (meta.getDisplayName().equals(BACK)) {
                            if (e.getSlot() == 26) {
                                openMainTPortGUI(player, mainTPortGUIPage.getOrDefault(playerUUID, 0));
                            } else {
                                e.setCancelled(true);
                            }
                        }
                    }

                    if (item.getType().equals(Material.ELYTRA)) {
                        if (e.getSlot() == 17) {
                            e.setCancelled(true);
                            List<String> lore = meta.getLore();
                            if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                                openBiomeTP(player, 0);
                            } else if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                                long cooldown = cooldownBack(player);
                                if (cooldown / 1000 > 0) {
                                    player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                                    return;
                                }
                                int i = tpBack(player);

                                switch (i) {
                                    case 1:
                                        updateBackCooldown(player);
                                        break;
                                    case 2:
                                        player.sendMessage(ChatColor.RED + "Player not online anymore");
                                        openTPortGUI(PlayerUUID.getPlayerName(s), s, player);
                                        break;
                                    case 3:
                                        player.sendMessage(ChatColor.RED + "You are not whitelisted anymore");
                                        openTPortGUI(PlayerUUID.getPlayerName(s), s, player);
                                        break;
                                    default:
                                        player.sendMessage(ChatColor.RED + "Could not teleport you back");
                                        openTPortGUI(PlayerUUID.getPlayerName(s), s, player);
                                        break;
                                }
//                                if (i != 1) {
//                                    openTPortGUI(PlayerUUID.getPlayerName(s), s, player);
//                                } else {
//                                    updateBackCooldown(player);
//                                }
                            } else if (e.getAction().equals(InventoryAction.CLONE_STACK) || e.getAction().equals(InventoryAction.UNKNOWN)) {
                                openFeatureTP(player, 0);
                            }
                        }
                    }

                    if (item.getType().equals(Material.PLAYER_HEAD)) {
                        if (e.getSlot() == 8) {
                            if (meta.getDisplayName().equals(OFFLINE) || meta.getDisplayName().equals(TPOFF)) {
                                e.setCancelled(true);

                                if (tportData.getConfig().getString("tport." + s + ".tp.statement").equals("off")) {

                                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                            .getStringList("tport." + s + "tp.players");

                                    if (list.contains(player.getUniqueId().toString())) {
                                        if (Bukkit.getPlayerExact(inv.getTitle().replaceAll("TPort:", "").trim()) != null) {
                                            Player warp = Bukkit.getPlayerExact(inv.getTitle().replaceAll("TPort:", "").trim());
                                            long cooldown = cooldownPlayerTP(player);
                                            if (cooldown / 1000 > 0) {
                                                player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                                                return;
                                            }
                                            tpPlayerToPlayer(player, warp);
                                            player.sendMessage("§3Teleported to §9" + warp.getName());
                                            updatePlayerTPCooldown(player);
                                        }
                                    } else {
                                        openTPortGUI(PlayerUUID.getPlayerName(s), s, player);
                                    }
                                }

                            } else if (meta.getDisplayName().equals(WARP + PlayerUUID.getPlayerName(s))) {
                                if (Bukkit.getPlayer(UUID.fromString(s)) == null) {
                                    openTPortGUI(PlayerUUID.getPlayerName(s), s, player);
                                    e.setCancelled(true);
                                } else {
                                    Player warp = Bukkit.getPlayer(UUID.fromString(s));
                                    e.setCancelled(true);
                                    long cooldown = cooldownPlayerTP(player);
                                    if (cooldown / 1000 > 0) {
                                        player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                                        return;
                                    }
                                    tpPlayerToPlayer(player, warp);
                                    player.sendMessage("§3Teleported to §9" + warp.getName());
                                    updatePlayerTPCooldown(player);
                                }
                            } else if (testHeadOn(item)) {
                                e.setCancelled(true);
                                pltp.run(new String[]{"PLTP", "on"}, player);
                                openTPortGUI(player.getName(), playerUUID, player);
                            } else if (testHeadOff(item)) {
                                e.setCancelled(true);
                                pltp.run(new String[]{"PLTP", "off"}, player);
                                openTPortGUI(player.getName(), playerUUID, player);
                            }
                        }
                    }

                    for (int i = 0; i < TPortSize; i++) {
                        if (tportData.getConfig().contains("tport." + s + ".items." + i + ".item")) {
                            String tportName = tportData.getConfig().getString("tport." + s + ".items." + i + ".name");
                            if (meta.getDisplayName().equals(tportName)) {
                                if (e.getSlot() >= 0 && e.getSlot() < 8 ||
                                        e.getSlot() >= 9 && e.getSlot() < 17 ||
                                        e.getSlot() >= 18 && e.getSlot() < 26) {

                                    if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                                        e.setCancelled(true);
                                        if (player.getName().equals(PlayerUUID.getPlayerName(s))) {

                                            switch (tportData.getConfig().getString("tport." + s + ".items." + i + ".private.statement")) {
                                                case "off":
                                                    tportData.getConfig().set("tport." + s + ".items." + i + ".private.statement", "on");
                                                    tportData.saveConfig();
                                                    player.sendMessage("§3TPort " + ChatColor.BLUE + meta.getDisplayName() + ChatColor.DARK_AQUA + " is now private");
                                                    break;
                                                case "on":
                                                    tportData.getConfig().set("tport." + s + ".items." + i + ".private.statement", "online");
                                                    tportData.saveConfig();
                                                    player.sendMessage("§3TPort " + ChatColor.BLUE + meta.getDisplayName() + ChatColor.DARK_AQUA + " is now open only if you are online");
                                                    break;
                                                case "online":
                                                    tportData.getConfig().set("tport." + s + ".items." + i + ".private.statement", "off");
                                                    tportData.saveConfig();
                                                    player.sendMessage("§3TPort " + ChatColor.BLUE + meta.getDisplayName() + ChatColor.DARK_AQUA + " is now open");
                                                    break;
                                            }

                                            openTPortGUI(player.getName(), playerUUID, player);
                                        } else {
                                            player.sendMessage(ChatColor.RED + "You can't edit this TPort");
                                        }

                                    } else {
                                        e.setCancelled(true);

                                        long cooldown = cooldownTPortTP(player);
                                        if (cooldown / 1000 > 0) {
                                            player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                                            return;
                                        }

                                        switch (tportData.getConfig().getString("tport." + s + ".items." + i + ".private.statement")) {
                                            case "off":
                                                break;
                                            case "on":
                                                if (!s.equals(playerUUID)) {
                                                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                                            .getStringList("tport." + s + ".items." + i + ".private.players");
                                                    if (!list.contains(player.getUniqueId().toString())) {
                                                        return;
                                                    }
                                                }
                                                break;
                                            case "online":
                                                if (Bukkit.getPlayer(UUID.fromString(s)) == null) {
                                                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                                            .getStringList("tport." + s + ".items." + i + ".private.players");
                                                    if (!list.contains(player.getUniqueId().toString())) {
                                                        return;
                                                    }
                                                }
                                                break;
                                            default:
                                                return;
                                        }


                                        Location l = Main.getLocation("tport." + s + ".items." + i + ".location");

                                        if (l == null) {
                                            player.sendMessage("§cThe world for this location has not been found");
                                            return;
                                        }
                                        player.closeInventory();
                                        tpPlayerToTPort(player, l, meta.getDisplayName(), s);
                                        updateTPortTPCooldown(player);

                                        Message message = new Message();
                                        message.addText("Teleported to ", ChatColor.DARK_AQUA);
                                        message.addText(textComponent(tportName,
                                                ChatColor.BLUE, ClickEvent.runCommand("/tport open " + PlayerUUID.getPlayerName(s) + " " + tportName)));
                                        message.sendMessage(player);
                                    }
                                } else {
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (inv.getTitle().startsWith("Select a Biome ")) {

            if (e.getSlot() == 8) {
                openBiomeTP(player, Integer.parseInt(inv.getTitle().replace("Select a Biome ", "").replace("(", "").replace(")", "")) - 2);
            } else if (e.getSlot() == inv.getSize() - 1) {
                openBiomeTP(player, Integer.parseInt(inv.getTitle().replace("Select a Biome ", "").replace("(", "").replace(")", "")));
            } else {

                if (e.getSlot() % 9 != 0 && e.getSlot() % 9 != 8) {
                    e.setCancelled(true);

                    Biome biome;
                    try {
                        biome = Biome.valueOf(meta.getDisplayName());
                    } catch (IllegalArgumentException iae) {
                        player.sendMessage(ChatColor.RED + "Biome " + ChatColor.DARK_RED + meta.getDisplayName() + ChatColor.RED + " does not exist");
                        return;
                    }

                    long cooldown = cooldownBiomeTP(player);
                    if (cooldown / 1000 > 0) {
                        player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                        return;
                    }
                    biomeTP(player, biome);
                } else if (e.getSlot() == 18) {
                    long cooldown = cooldownBiomeTP(player);
                    if (cooldown / 1000 > 0) {
                        player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                        return;
                    }
                    Random random = new Random();
                    int x = random.nextInt(6000000) - 3000000;
                    int z = random.nextInt(6000000) - 3000000;
//                    player.teleport(player.getWorld().getHighestBlockAt(x, z).getLocation().add(0.5, 0, 0.5));
                    teleportPlayer(player, player.getWorld().getHighestBlockAt(x, z).getLocation().add(0.5, 0, 0.5));
                    player.sendMessage(ChatColor.DARK_AQUA + "Teleported to a random location");

                } else if (e.getSlot() == 26) {
                    openMainTPortGUI(player, 0);
                }

            }


        }
        else if (inv.getTitle().startsWith("Select a Feature ")) {

            if (e.getSlot() == 8) {
                openFeatureTP(player, Integer.parseInt(inv.getTitle().replace("Select a Feature ", "").replace("(", "").replace(")", "")) - 2);
            } else if (e.getSlot() == inv.getSize() - 1) {
                openFeatureTP(player, Integer.parseInt(inv.getTitle().replace("Select a Feature ", "").replace("(", "").replace(")", "")));
            } else {

                if (e.getSlot() % 9 != 0 && e.getSlot() % 9 != 8) {
                    e.setCancelled(true);
                    FeaturesTypes featuresType;
                    try {
                        featuresType = FeaturesTypes.valueOf(meta.getDisplayName());
                    } catch (IllegalArgumentException iae) {
                        player.sendMessage(ChatColor.RED + "Feature " + ChatColor.DARK_RED + meta.getDisplayName() + ChatColor.RED + " does not exist");
                        return;
                    }
                    long cooldown = cooldownFeatureTP(player);
                    if (cooldown / 1000 > 0) {
                        player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                        return;
                    }
                    featureTP(player, featuresType);
                } else if (e.getSlot() == 26) {
                    openMainTPortGUI(player, 0);
                }

            }


        }
    }
}