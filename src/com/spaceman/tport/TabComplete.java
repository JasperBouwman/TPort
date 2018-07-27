package com.spaceman.tport;

import com.google.common.collect.ImmutableList;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class TabComplete implements TabCompleter {

    // tport
    private static final List<String> TABCOMPLETE = new ArrayList<>();
    // tport add
    // tport edit
    private static final List<String> TABCOMPLETE_EDIT = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EDIT_TPORT = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EDIT_LORE = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EDIT_PRIVATE = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EDIT_TPORT_WHITELIST = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EDIT_TPORT_WHITELIST_ADD = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EDIT_TPORT_WHITELIST_REMOVE = new ArrayList<>();
    // tport extra
    private static final List<String> TABCOMPLETE_EXTRA = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EXTRA_TP = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EXTRA_WHITELIST = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EXTRA_WHITELIST_REMOVE = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EXTRA_WHITELIST_ADD = new ArrayList<>();
    // tport help
    // tport open
    private static final List<String> TABCOMPLETE_OPEN = new ArrayList<>();
    private static final List<String> TABCOMPLETE_OPEN_ITEM = new ArrayList<>();
    // tport own
    private static final List<String> TABCOMPLETE_OWN = new ArrayList<>();
    // tport remove
    private static final List<String> TABCOMPLETE_REMOVE = new ArrayList<>();
    // tport removePlayer
    private static final List<String> TABCOMPLETE_REMOVEPLAYER = new ArrayList<>();

    private static List<String> l = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {

        if (!(sender instanceof Player)) {
            return l;
        }

        Player player = (Player) sender;
        if (args.length > 1) {
            listPopulator(player, args[1]);
        } else {
            listPopulator(player, null);
        }

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], TABCOMPLETE,
                    new ArrayList<>(TABCOMPLETE.size()));
        }
        // tport edit
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("edit")) {
                return StringUtil.copyPartialMatches(args[1], TABCOMPLETE_EDIT, new ArrayList<>(TABCOMPLETE_EDIT.size()));
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("edit")) {
                return StringUtil.copyPartialMatches(args[2], TABCOMPLETE_EDIT_TPORT, new ArrayList<>(TABCOMPLETE_EDIT_TPORT.size()));
            }
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("lore")) {
                return StringUtil.copyPartialMatches(args[3], TABCOMPLETE_EDIT_LORE, new ArrayList<>(TABCOMPLETE_EDIT_LORE.size()));
            } else if (args[2].equalsIgnoreCase("private")) {
                return StringUtil.copyPartialMatches(args[3], TABCOMPLETE_EDIT_PRIVATE, new ArrayList<>(TABCOMPLETE_EDIT_PRIVATE.size()));
            } else if (args[2].equalsIgnoreCase("whitelist")) {
                return StringUtil.copyPartialMatches(args[3], TABCOMPLETE_EDIT_TPORT_WHITELIST, new ArrayList<>(TABCOMPLETE_EDIT_TPORT_WHITELIST.size()));
            }
        } else if (args.length == 5) {
            if (args[2].equalsIgnoreCase("whitelist")) {
                if (args[3].equalsIgnoreCase("add")) {
                    return StringUtil.copyPartialMatches(args[4], TABCOMPLETE_EDIT_TPORT_WHITELIST_ADD, new ArrayList<>(TABCOMPLETE_EDIT_TPORT_WHITELIST_ADD.size()));
                } else if (args[3].equalsIgnoreCase("remove")) {
                    TABCOMPLETE_EDIT_TPORT_WHITELIST_REMOVE.remove(player.getName());
                    return StringUtil.copyPartialMatches(args[4], TABCOMPLETE_EDIT_TPORT_WHITELIST_REMOVE, new ArrayList<>(TABCOMPLETE_EDIT_TPORT_WHITELIST_REMOVE.size()));
                }
            }
        }

        // tport extra
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("extra")) {
                return StringUtil.copyPartialMatches(args[1], TABCOMPLETE_EXTRA, new ArrayList<>(TABCOMPLETE_EXTRA.size()));
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("extra")) {
                if (args[1].equalsIgnoreCase("tp")) {
                    return StringUtil.copyPartialMatches(args[2], TABCOMPLETE_EXTRA_TP, new ArrayList<>(TABCOMPLETE_EXTRA_TP.size()));
                } else if (args[1].equalsIgnoreCase("whitelist")) {
                    return StringUtil.copyPartialMatches(args[2], TABCOMPLETE_EXTRA_WHITELIST, new ArrayList<>(TABCOMPLETE_EXTRA_WHITELIST.size()));
                }
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("extra")) {
                if (args[1].equalsIgnoreCase("whitelist") && args[2].equalsIgnoreCase("remove")) {
                    return StringUtil.copyPartialMatches(args[3], TABCOMPLETE_EXTRA_WHITELIST_REMOVE, new ArrayList<>(TABCOMPLETE_EXTRA_WHITELIST_REMOVE.size()));
                } else if (args[1].equalsIgnoreCase("whitelist") && args[2].equalsIgnoreCase("add")) {
                    return StringUtil.copyPartialMatches(args[3], TABCOMPLETE_EXTRA_WHITELIST_ADD, new ArrayList<>(TABCOMPLETE_EXTRA_WHITELIST_ADD.size()));
                }
            }
        }

        // tport open
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("open")) {
                return StringUtil.copyPartialMatches(args[1], TABCOMPLETE_OPEN, new ArrayList<>(TABCOMPLETE_OPEN.size()));
            }

        }
        //tport open <player>
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("open")) {
                return StringUtil.copyPartialMatches(args[2], TABCOMPLETE_OPEN_ITEM, new ArrayList<>(TABCOMPLETE_OPEN_ITEM.size()));
            }
        }

        //tport own
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("own")) {
                return StringUtil.copyPartialMatches(args[1], TABCOMPLETE_OWN, new ArrayList<>(TABCOMPLETE_OWN.size()));
            }
        }

        // tport compass
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("compass")) {
                return StringUtil.copyPartialMatches(args[1], TABCOMPLETE_OPEN, new ArrayList<>(TABCOMPLETE_OPEN.size()));
            }

        }
        //tport compass <player>
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("compass")) {
                return StringUtil.copyPartialMatches(args[2], TABCOMPLETE_OPEN_ITEM, new ArrayList<>(TABCOMPLETE_OPEN_ITEM.size()));
            }
        }

        //tport remove
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                return StringUtil.copyPartialMatches(args[1], TABCOMPLETE_REMOVE, new ArrayList<>(TABCOMPLETE_REMOVE.size()));
            }
        }

        //tport removePlayer
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("removePlayer")) {
                return StringUtil.copyPartialMatches(args[1], TABCOMPLETE_REMOVEPLAYER, new ArrayList<>(TABCOMPLETE_REMOVEPLAYER.size()));
            }
        }

        return ImmutableList.of();
    }

    private List<String> uuidToName(Set<String> list) {

        ArrayList<String> newList = new ArrayList<>();

        for (String uuid : list) {
            String name = PlayerUUID.getPlayerName(uuid);
            if (name != null) {
                newList.add(name);
            }
        }
        return newList;
    }
    private List<String> uuidToName(List<String> list) {

        ArrayList<String> newList = new ArrayList<>();

        for (String uuid : list) {
            String name = PlayerUUID.getPlayerName(uuid);
            if (name != null) {
                newList.add(name);
            }
        }
        return newList;
    }

    private void listPopulator(Player player, String argOne) {

        TABCOMPLETE.clear();
        TABCOMPLETE_EDIT.clear();
        TABCOMPLETE_EDIT_TPORT.clear();
        TABCOMPLETE_EDIT_LORE.clear();
        TABCOMPLETE_EDIT_PRIVATE.clear();
        TABCOMPLETE_EDIT_TPORT_WHITELIST.clear();
        TABCOMPLETE_EDIT_TPORT_WHITELIST_ADD.clear();
        TABCOMPLETE_EDIT_TPORT_WHITELIST_REMOVE.clear();
        TABCOMPLETE_OWN.clear();
        TABCOMPLETE_EXTRA.clear();
        TABCOMPLETE_EXTRA_TP.clear();
        TABCOMPLETE_EXTRA_WHITELIST.clear();
        TABCOMPLETE_EXTRA_WHITELIST_REMOVE.clear();
        TABCOMPLETE_EXTRA_WHITELIST_ADD.clear();
        TABCOMPLETE_OPEN.clear();
        TABCOMPLETE_OPEN_ITEM.clear();
        TABCOMPLETE_REMOVE.clear();
        TABCOMPLETE_REMOVEPLAYER.clear();

        Files tportData = getFiles("TPortData");

        String playerUUID = player.getUniqueId().toString();
        String argOneUUID = PlayerUUID.getPlayerUUID(argOne);
        if (argOneUUID == null) {
            ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(argOne);
            if (globalNames.size() == 1) {
                argOneUUID = globalNames.get(0);
            }
        }

        // tport
        TABCOMPLETE.add("add");
        TABCOMPLETE.add("compass");
        TABCOMPLETE.add("edit");
        TABCOMPLETE.add("extra");
        TABCOMPLETE.add("help");
        TABCOMPLETE.add("open");
        TABCOMPLETE.add("own");
        TABCOMPLETE.add("remove");
        if (player.isOp()) {
            TABCOMPLETE.add("removePlayer");
        }

        // tport edit
        if (!tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            return;
        }
        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
            ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item");
            String name = item.getItemMeta().getDisplayName();
            TABCOMPLETE_EDIT.add(name);
        }
        // tport edit <TPort>
        TABCOMPLETE_EDIT_TPORT.add("location");
        TABCOMPLETE_EDIT_TPORT.add("lore");
        TABCOMPLETE_EDIT_TPORT.add("name");
        TABCOMPLETE_EDIT_TPORT.add("private");
        TABCOMPLETE_EDIT_TPORT.add("whitelist");
        // tport edit <TPort> lore
        TABCOMPLETE_EDIT_LORE.add("remove");
        TABCOMPLETE_EDIT_LORE.add("set");
        // tport edit <TPort> private
        TABCOMPLETE_EDIT_PRIVATE.add("true");
        TABCOMPLETE_EDIT_PRIVATE.add("false");
        // tport edit <TPort name> whitelist
        TABCOMPLETE_EDIT_TPORT_WHITELIST.add("add");
        TABCOMPLETE_EDIT_TPORT_WHITELIST.add("list");
        TABCOMPLETE_EDIT_TPORT_WHITELIST.add("remove");
        // tport edit <TPort name> whitelist add
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            TABCOMPLETE_EDIT_TPORT_WHITELIST_ADD.add(p.getName());
        }
        TABCOMPLETE_EDIT_TPORT_WHITELIST_ADD.remove(player.getName());
        // tport whitelist <TPort> remove
        if (tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

                ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item");
                String name = item.getItemMeta().getDisplayName();
                if (name.equalsIgnoreCase(argOne)) {
                    List<String> players = tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");
                    TABCOMPLETE_EDIT_TPORT_WHITELIST_REMOVE.addAll(uuidToName((players)));
                }
            }
        }

        //tport extra
        TABCOMPLETE_EXTRA.add("tp");
        TABCOMPLETE_EXTRA.add("whitelist");
        //tport extra tp
        TABCOMPLETE_EXTRA_TP.add("on");
        TABCOMPLETE_EXTRA_TP.add("off");
        //tport extra whitelist
        TABCOMPLETE_EXTRA_WHITELIST.add("add");
        TABCOMPLETE_EXTRA_WHITELIST.add("list");
        TABCOMPLETE_EXTRA_WHITELIST.add("remove");
        //tport extra whitelist remove
        TABCOMPLETE_EXTRA_WHITELIST_REMOVE.addAll(tportData.getConfig().getStringList("tport." + playerUUID + ".tp.players"));
        //tport extra whitelist add
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            TABCOMPLETE_EXTRA_WHITELIST_ADD.add(p.getName());
            TABCOMPLETE_EXTRA_WHITELIST_ADD.removeAll(TABCOMPLETE_EXTRA_WHITELIST_REMOVE);
        }

        // tport open
        TABCOMPLETE_OPEN.addAll(uuidToName(tportData.getConfig().getConfigurationSection("tport").getKeys(false)));
        // tport open <player>
        if (tportData.getConfig().contains("tport." + argOneUUID + ".items")) {
            for (String s : tportData.getConfig().getConfigurationSection("tport." + argOneUUID + ".items").getKeys(false)) {
                ItemStack is = tportData.getConfig().getItemStack("tport." + argOneUUID + ".items." + s + ".item");
                if (is.hasItemMeta()) {

                    if (tportData.getConfig().getString("tport." + argOneUUID + ".items." + s + ".private.statement").equals("true")) {
                        ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + argOneUUID + ".items." + s + ".private.players");
                        if (list.contains(player.getUniqueId().toString())) {
                            TABCOMPLETE_OPEN_ITEM.add(is.getItemMeta().getDisplayName());
                        }
                    } else {
                        TABCOMPLETE_OPEN_ITEM.add(is.getItemMeta().getDisplayName());
                    }
                }
            }
        }

        //tport own
        if (tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
                ItemStack is = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item");
                if (is.hasItemMeta()) {

                    if (tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".private.statement").equals("true")) {
                        ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");
                        if (list.contains(player.getUniqueId().toString())) {
                            TABCOMPLETE_OWN.add(is.getItemMeta().getDisplayName());
                        }
                    } else {
                        TABCOMPLETE_OWN.add(is.getItemMeta().getDisplayName());
                    }
                }
            }
        }

        // tport remove
        if (tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
                TABCOMPLETE_REMOVE.add(tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item").getItemMeta().getDisplayName());
            }
        }

        // tport removePlayer
        if (player.isOp()) {
            TABCOMPLETE_REMOVEPLAYER.addAll(uuidToName(tportData.getConfig().getConfigurationSection("tport").getKeys(false)));
        }

    }
}
