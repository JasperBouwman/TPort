package com.spaceman.tport;

import com.google.common.collect.ImmutableList;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.spaceman.tport.events.InventoryClick.TPortSize;
import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class TabComplete implements TabCompleter {

    private List<String> copyContaining(String s, List<String> fullList) {
        ArrayList<String> list = new ArrayList<>();
        for (String ss : fullList) {
            if (ss.toLowerCase().contains(s.toLowerCase())) {
                list.add(ss);
            }
        }
        return list;
    }

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
    private static final List<String> TABCOMPLETE_EDIT_MOVE = new ArrayList<>();
    // tport extra
    private static final List<String> TABCOMPLETE_PLTP = new ArrayList<>();
    private static final List<String> TABCOMPLETE_PLTP_WHITELIST = new ArrayList<>();
    private static final List<String> TABCOMPLETE_PLTP_WHITELIST_REMOVE = new ArrayList<>();
    private static final List<String> TABCOMPLETE_PLTP_WHITELIST_ADD = new ArrayList<>();
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
    // tport biomeTP
    private static final List<String> TABCOMPLETE_BIOMETP = new ArrayList<>();
    // tport featureTP
    private static final List<String> TABCOMPLETE_FEATURETP = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {

        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;
        if (args.length > 1) {
            listPopulator(player, args[1]);
        } else {
            listPopulator(player, null);
        }

        if (args.length == 1) {
            return copyContaining(args[0], TABCOMPLETE);
        }
        // tport edit
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("edit")) {
                return copyContaining(args[1], TABCOMPLETE_EDIT);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("edit")) {
                return copyContaining(args[2], TABCOMPLETE_EDIT_TPORT);
            }
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("lore")) {
                return copyContaining(args[3], TABCOMPLETE_EDIT_LORE);
            } else if (args[2].equalsIgnoreCase("private")) {
                return copyContaining(args[3], TABCOMPLETE_EDIT_PRIVATE);
            } else if (args[2].equalsIgnoreCase("whitelist")) {
                return copyContaining(args[3], TABCOMPLETE_EDIT_TPORT_WHITELIST);
            } else if (args[2].equalsIgnoreCase("move")) {
                return copyContaining(args[3], TABCOMPLETE_EDIT_MOVE);
            }
        } else if (args.length == 5) {
            if (args[2].equalsIgnoreCase("whitelist")) {
                if (args[3].equalsIgnoreCase("add")) {
                    return copyContaining(args[4], TABCOMPLETE_EDIT_TPORT_WHITELIST_ADD);
                } else if (args[3].equalsIgnoreCase("remove")) {
                    TABCOMPLETE_EDIT_TPORT_WHITELIST_REMOVE.remove(player.getName());
                    return copyContaining(args[4], TABCOMPLETE_EDIT_TPORT_WHITELIST_REMOVE);
                }
            }
        }

        // tport PLTP
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("PLTP")) {
                return copyContaining(args[1], TABCOMPLETE_PLTP);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("PLTP")) {
                if (args[1].equalsIgnoreCase("whitelist")) {
                    return copyContaining(args[2], TABCOMPLETE_PLTP_WHITELIST);
                }
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("PLTP")) {
                if (args[1].equalsIgnoreCase("whitelist") && args[2].equalsIgnoreCase("remove")) {
                    return copyContaining(args[3], TABCOMPLETE_PLTP_WHITELIST_REMOVE);
                } else if (args[1].equalsIgnoreCase("whitelist") && args[2].equalsIgnoreCase("add")) {
                    return copyContaining(args[3], TABCOMPLETE_PLTP_WHITELIST_ADD);
                }
            }
        }

        // tport open
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("open")) {
                return copyContaining(args[1], TABCOMPLETE_OPEN);
            }

        }
        //tport open <player>
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("open")) {
                return copyContaining(args[2], TABCOMPLETE_OPEN_ITEM);
            }
        }

        //tport own
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("own")) {
                return copyContaining(args[1], TABCOMPLETE_OWN);
            }
        }

        // tport compass
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("compass")) {
                return copyContaining(args[1], TABCOMPLETE_OPEN);
            }

        }
        //tport compass <player>
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("compass")) {
                return copyContaining(args[2], TABCOMPLETE_OPEN_ITEM);
            }
        }

        //tport remove
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                return copyContaining(args[1], TABCOMPLETE_REMOVE);
            }
        }

        //tport removePlayer
        if (args.length == 2) {
            if (player.isOp()) {
                if (args[0].equalsIgnoreCase("removePlayer")) {
                    return copyContaining(args[1], TABCOMPLETE_REMOVEPLAYER);
                }
            }
        }

        //tport biomeTP
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("biomeTP")) {
                return copyContaining(args[1], TABCOMPLETE_BIOMETP);
            }
        }

        //tport biomeTP
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("featureTP")) {
                return copyContaining(args[1], TABCOMPLETE_FEATURETP);
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
        TABCOMPLETE_EDIT_MOVE.clear();
        TABCOMPLETE_OWN.clear();
        TABCOMPLETE_PLTP.clear();
        TABCOMPLETE_PLTP_WHITELIST.clear();
        TABCOMPLETE_PLTP_WHITELIST_REMOVE.clear();
        TABCOMPLETE_PLTP_WHITELIST_ADD.clear();
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
        TABCOMPLETE.add("back");
        TABCOMPLETE.add("compass");
        TABCOMPLETE.add("PLTP");
        TABCOMPLETE.add("edit");
        TABCOMPLETE.add("help");
        TABCOMPLETE.add("open");
        TABCOMPLETE.add("own");
        TABCOMPLETE.add("remove");
        TABCOMPLETE.add("biomeTP");
        TABCOMPLETE.add("featureTP");
        TABCOMPLETE.add("reload");
//        TABCOMPLETE.add("log");
        if (player.isOp()) {
            TABCOMPLETE.add("removePlayer");
        }

        // tport biomeTP
        for (Biome biome : Biome.values()) {
            TABCOMPLETE_BIOMETP.add(biome.toString());
        }
        TABCOMPLETE_BIOMETP.add("random");

        //tport featureTP
        for (TPortInventories.FeaturesTypes feature : TPortInventories.FeaturesTypes.values()) {
            TABCOMPLETE_FEATURETP.add(feature.toString());
        }

        // tport edit
        if (!tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            return;
        }
        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
            TABCOMPLETE_EDIT.add(name);
        }
        // tport edit <TPort>
        TABCOMPLETE_EDIT_TPORT.add("location");
        TABCOMPLETE_EDIT_TPORT.add("lore");
        TABCOMPLETE_EDIT_TPORT.add("name");
        TABCOMPLETE_EDIT_TPORT.add("private");
        TABCOMPLETE_EDIT_TPORT.add("item");
        TABCOMPLETE_EDIT_TPORT.add("whitelist");
        TABCOMPLETE_EDIT_TPORT.add("move");
        // tport edit <TPort> lore
        TABCOMPLETE_EDIT_LORE.add("remove");
        TABCOMPLETE_EDIT_LORE.add("set");
        // tport edit <TPort> private
        TABCOMPLETE_EDIT_PRIVATE.add("on");
        TABCOMPLETE_EDIT_PRIVATE.add("off");
        TABCOMPLETE_EDIT_PRIVATE.add("online");
        // tport edit <TPort name> whitelist
        TABCOMPLETE_EDIT_TPORT_WHITELIST.add("add");
        TABCOMPLETE_EDIT_TPORT_WHITELIST.add("list");
        TABCOMPLETE_EDIT_TPORT_WHITELIST.add("remove");
        // tport edit <TPort name> whitelist add
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            TABCOMPLETE_EDIT_TPORT_WHITELIST_ADD.add(p.getName());
        }
        TABCOMPLETE_EDIT_TPORT_WHITELIST_ADD.remove(player.getName());
        // tport edit <TPort> whitelist remove
        if (tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
                String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
                if (name.equalsIgnoreCase(argOne)) {
                    List<String> players = tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");
                    TABCOMPLETE_EDIT_TPORT_WHITELIST_REMOVE.addAll(uuidToName((players)));
                }
            }
        }
        //tport edit <TPort> move
        for (int i = 0; i < TPortSize; i++) {
            if (!tportData.getConfig().contains("tport." + playerUUID + ".items." + i)) {
                TABCOMPLETE_EDIT_MOVE.add(String.valueOf((i + 1)));
            }
        }

        //tport PLTP
        TABCOMPLETE_PLTP.add("on");
        TABCOMPLETE_PLTP.add("off");
        TABCOMPLETE_PLTP.add("whitelist");
        //tport PLTP whitelist
        TABCOMPLETE_PLTP_WHITELIST.add("list");
        TABCOMPLETE_PLTP_WHITELIST.add("add");
        TABCOMPLETE_PLTP_WHITELIST.add("remove");
        //tport PLTP whitelist remove
        TABCOMPLETE_PLTP_WHITELIST_REMOVE.addAll(tportData.getConfig().getStringList("tport." + playerUUID + ".tp.players"));
        //tport PLTP whitelist add
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            TABCOMPLETE_PLTP_WHITELIST_ADD.add(p.getName());
            TABCOMPLETE_PLTP_WHITELIST_ADD.removeAll(TABCOMPLETE_PLTP_WHITELIST_REMOVE);
        }

        // tport open
        TABCOMPLETE_OPEN.addAll(uuidToName(tportData.getConfig().getConfigurationSection("tport").getKeys(false)));
        // tport open <player>
        if (tportData.getConfig().contains("tport." + argOneUUID + ".items")) {
            for (String s : tportData.getConfig().getConfigurationSection("tport." + argOneUUID + ".items").getKeys(false)) {
                String name = tportData.getConfig().getString("tport." + argOneUUID + ".items." + s + ".name");

                if (tportData.getConfig().getString("tport." + argOneUUID + ".items." + s + ".private.statement").equals("true")) {
                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + argOneUUID + ".items." + s + ".private.players");
                    if (list.contains(player.getUniqueId().toString())) {
                        TABCOMPLETE_OPEN_ITEM.add(name);
                    }
                } else {
                    TABCOMPLETE_OPEN_ITEM.add(name);

                }
            }
        }

        //tport own
        if (tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
                String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");

                if (tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".private.statement").equals("true")) {
                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");
                    if (list.contains(player.getUniqueId().toString())) {
                        TABCOMPLETE_OWN.add(name);
                    }
                } else {
                    TABCOMPLETE_OWN.add(name);
                }
            }
        }

        // tport remove
        if (tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
                TABCOMPLETE_REMOVE.add(tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name"));
            }
        }

        // tport removePlayer
        if (player.isOp()) {
            TABCOMPLETE_REMOVEPLAYER.addAll(uuidToName(tportData.getConfig().getConfigurationSection("tport").getKeys(false)));
        }

    }
}
