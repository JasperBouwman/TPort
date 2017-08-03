package com.spaceman.tport;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    // tport
    private static final List<String> TABCOMPLETE = new ArrayList<>();
    // tport add
    // tport edit
    private static final List<String> TABCOMPLETE_EDIT = new ArrayList<>();

    //    @Override
//    public java.util.List<String> onTabComplete(CommandSender sender, Command
//            cmd, String CommandLabel, String[] args) {
//        if (args.length >= 5) {
//            List<String> list = new ArrayList<>();
//            return list;
//        }
//        if (cmd.getName().equalsIgnoreCase("tport")) {
//            if (sender instanceof Player) {
//                Player player = (Player) sender;
//                if (args.length == 1) {
//                    List<String> list = new ArrayList<>();
//
//                    list.add("add");
//                    list.add("remove");
//                    list.add("edit");
//                    list.add("whitelist");
//                    list.add("help");
//                    list.add("open");
//                    list.add("extra");
//                    return list;
//                } else if (args.length == 2) {
//
//                    if (args[0].equalsIgnoreCase("edit")) {
//                        List<String> list = new ArrayList<>();
//
//                        if (!p.getConfig().contains("tport." + player.getName() + ".items")) {
//                            return list;
//                        }
//
//                        for (String s : p.getConfig().getConfigurationSection("tport." +
//                                player.getName() + ".items")
//                                .getKeys(false)) {
//
//                            ItemStack item = p.getConfig()
//                                    .getItemStack("tport." + player.getName() + ".items." + s + ".item");
//                            String name = item.getItemMeta().getDisplayName();
//                            list.add(name);
//                        }
//                        return list;
//                    } else if (args[0].equalsIgnoreCase("remove")) {
//
//                        List<String> list = new ArrayList<String>();
//
//                        if (!p.getConfig().contains("tport." + player.getName() + ".items")) {
//                            return list;
//                        }
//
//                        for (String s : p.getConfig().getConfigurationSection("tport." +
//                                player.getName() + ".items")
//                                .getKeys(false)) {
//
//                            ItemStack item = p.getConfig()
//                                    .getItemStack("tport." + player.getName() + ".items." + s + ".item");
//                            String name = item.getItemMeta().getDisplayName();
//                            list.add(name);
//                        }
//
//                        return list;
//
//                    } else if (args[0].equalsIgnoreCase("add") ||
//                            args[0].equalsIgnoreCase("help")) {
//                        List<String> list = new ArrayList<String>();
//                        return list;
//                    } else if (args[0].equalsIgnoreCase("whitelist")) {
//
//                        List<String> list = new ArrayList<String>();
//
//                        if (!p.getConfig().contains("tport." + player.getName() + ".items")) {
//                            return list;
//                        }
//                        for (String s : p.getConfig().getConfigurationSection("tport." +
//                                player.getName() + ".items")
//                                .getKeys(false)) {
//
//                            ItemStack item = p.getConfig()
//                                    .getItemStack("tport." + player.getName() + ".items." + s + ".item");
//                            String name = item.getItemMeta().getDisplayName();
//                            list.add(name);
//                        }
//                        return list;
//                    } else if (args[0].equalsIgnoreCase("extra")) {
//                        List<String> list = new ArrayList<String>();
//                        list.add("whitelist");
//                        list.add("item");
//                        list.add("tp");
//                        return list;
//                    }
//
//                } else if (args.length == 3) {
//
//                    if (args[0].equalsIgnoreCase("edit")) {
//
//                        List<String> list = new ArrayList<>();
//
//                        list.add("lore");
//                        list.add("name");
//                        list.add("item");
//                        list.add("private");
//                        list.add("location");
//
//                        return list;
//
//                    } else if (args[0].equalsIgnoreCase("whitelist")) {
//
//                        List<String> list = new ArrayList<>();
//
//                        list.add("add");
//                        list.add("remove");
//                        list.add("list");
//
//                        return list;
//
//                    } else if (args[0].equalsIgnoreCase("add") ||
//                            args[0].equalsIgnoreCase("remove")
//                            || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("open")
//                            || args[1].equalsIgnoreCase("item")) {
//                        List<String> list = new ArrayList<>();
//                        return list;
//                    } else if (args[1].equalsIgnoreCase("whitelist")) {
//                        List<String> list = new ArrayList<>();
//
//                        list.add("add");
//                        list.add("remove");
//                        list.add("list");
//
//                        return list;
//                    } else if (args[1].equalsIgnoreCase("tp")) {
//                        List<String> list = new ArrayList<>();
//
//                        list.add("on");
//                        list.add("off");
//                        return list;
//                    }
//
//                } else if (args.length == 4) {
//
//                    if (args[2].equalsIgnoreCase("lore")) {
//
//                        List<String> list = new ArrayList<>();
//
//                        list.add("set");
//                        list.add("remove");
//                        return list;
//
//                    } else if (args[2].equalsIgnoreCase("private")) {
//
//                        List<String> list = new ArrayList<>();
//
//                        list.add("true");
//                        list.add("false");
//                        return list;
//
//                    } else if (args[2].equalsIgnoreCase("item") ||
//                            args[2].equalsIgnoreCase("name")
//                            || args[2].equalsIgnoreCase("location") ||
//                            args[1].equalsIgnoreCase("item")
//                            || args[1].equalsIgnoreCase("tp") || args[2].equalsIgnoreCase("list")) {
//                        List<String> list = new ArrayList<>();
//                        return list;
//
//                    } else if (args[2].equalsIgnoreCase("remove") &&
//                            args[0].equalsIgnoreCase("whitelist")) {
//
//                        List<String> list = new ArrayList<>();
//
//                        if (!p.getConfig().contains("tport." + player.getName() + ".items")) {
//                            return list;
//                        }
//
//                        for (String s : p.getConfig().getConfigurationSection("tport." +
//                                player.getName() + ".items")
//                                .getKeys(false)) {
//
//                            ItemStack item = p.getConfig()
//                                    .getItemStack("tport." + player.getName() + ".items." + s + ".item");
//                            String name = item.getItemMeta().getDisplayName();
//                            if (name.equalsIgnoreCase(args[1])) {
//                                ArrayList<String> players = (ArrayList<String>)
//                                        p.getConfig().getStringList(
//                                                "tport." + player.getName() + ".items." + s + ".private.players");
//                                list.addAll(players);
//                            }
//                        }
//                        return list;
//                    } else if (args[0].equalsIgnoreCase("add") ||
//                            args[0].equalsIgnoreCase("remove")
//                            || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("open")) {
//                        List<String> list = new ArrayList<>();
//                        return list;
//                    } else if (args[2].equalsIgnoreCase("list") &&
//                            args[0].equalsIgnoreCase("whitelist")) {
//                        List<String> list = new ArrayList<>();
//                        return list;
//
//                    } else if (args[2].equalsIgnoreCase("remove") &&
//                            args[1].equalsIgnoreCase("whitelist")) {
//
//
//                        List<String> list = p.getConfig().getStringList("tport." +
//                                player.getName() + ".tp.players");
//                        list.addAll(list);
//
//                        return list;
//                    }
//                }
//            }
//        }
//        return null;
//    }
    private static final List<String> TABCOMPLETE_EDIT_TPORT = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EDIT_LORE = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EDIT_PRIVATE = new ArrayList<>();
    // tport extra
    private static final List<String> TABCOMPLETE_EXTRA = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EXTRA_TP = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EXTRA_WHITELIST = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EXTRA_WHITELIST_REMOVE = new ArrayList<>();
    private static final List<String> TABCOMPLETE_EXTRA_WHITELIST_ADD = new ArrayList<>();
    // tport help
    // tport open
    private static final List<String> TABCOMPLETE_OPEN = new ArrayList<>();
    // tport remove
    private static final List<String> TABCOMPLETE_REMOVE = new ArrayList<>();
    // tport whitelist
    private static final List<String> TABCOMPLETE_WHITELIST = new ArrayList<>();
    private static final List<String> TABCOMPLETE_WHITELIST_TPORT = new ArrayList<>();
    private static final List<String> TABCOMPLETE_WHITELIST_ADD = new ArrayList<>();
    private static final List<String> TABCOMPLETE_WHITELIST_REMOVE = new ArrayList<>();
    private static List<String> TABCOMPLETE_OPEN_ITEM = new ArrayList<>();
    private static List<String> l = new ArrayList<>();
    private Main p;

    public TabComplete(Main instance) {
        p = instance;
    }
//    private static final List<String> TABCOMPLETE_ADD_SET = ImmutableList.of("1", "2");
//    private static final List<String> TABCOMPLETE_ADD = l;
//    private static final List<String> TABCOMPLETE_DAY_NIGHT = ImmutableList.of("1.1", "1.2");

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
//		if (args.length == 1)
//			return (List<String>) StringUtil.copyPartialMatches(args[0], TABCOMPLETE_ADD_SET,
//					new ArrayList<String>(TABCOMPLETE_ADD_SET.size()));
//		if ((args.length == 2) && (args[0].equalsIgnoreCase("set"))) {
//			return (List<String>) StringUtil.copyPartialMatches(args[1], TABCOMPLETE_DAY_NIGHT,
//					new ArrayList<String>(TABCOMPLETE_DAY_NIGHT.size()));
//		}

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

        //tport remove
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                return StringUtil.copyPartialMatches(args[1], TABCOMPLETE_REMOVE, new ArrayList<>(TABCOMPLETE_REMOVE.size()));
            }
        }

        // tport whitelist
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("whitelist")) {
                return StringUtil.copyPartialMatches(args[1], TABCOMPLETE_WHITELIST, new ArrayList<>(TABCOMPLETE_WHITELIST.size()));
            }
        }
        // tport whitelist <tport>
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("whitelist")) {
                return StringUtil.copyPartialMatches(args[2], TABCOMPLETE_WHITELIST_TPORT, new ArrayList<>(TABCOMPLETE_WHITELIST_TPORT.size()));
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("whitelist")) {
                //tport whitelist <tport> open
                if (args[2].equalsIgnoreCase("open")) {
                    return StringUtil.copyPartialMatches(args[3], TABCOMPLETE_WHITELIST_ADD, new ArrayList<>(TABCOMPLETE_WHITELIST_ADD.size()));
                }
                //tport whitelist <tport> remove
                if (args[2].equalsIgnoreCase("remove")) {
                    return StringUtil.copyPartialMatches(args[3], TABCOMPLETE_WHITELIST_REMOVE, new ArrayList<>(TABCOMPLETE_WHITELIST_REMOVE.size()));
                }
            }
        }

        return ImmutableList.of();
    }

    private void listPopulator(Player player, String one) {

        TABCOMPLETE.clear();
        TABCOMPLETE_EDIT.clear();
        TABCOMPLETE_EDIT_TPORT.clear();
        TABCOMPLETE_EDIT_LORE.clear();
        TABCOMPLETE_EDIT_PRIVATE.clear();
        TABCOMPLETE_EXTRA.clear();
        TABCOMPLETE_EXTRA_TP.clear();
        TABCOMPLETE_EXTRA_WHITELIST.clear();
        TABCOMPLETE_EXTRA_WHITELIST_REMOVE.clear();
        TABCOMPLETE_EXTRA_WHITELIST_ADD.clear();
        TABCOMPLETE_OPEN.clear();
        TABCOMPLETE_OPEN_ITEM.clear();
        TABCOMPLETE_REMOVE.clear();
        TABCOMPLETE_WHITELIST.clear();
        TABCOMPLETE_WHITELIST_TPORT.clear();
        TABCOMPLETE_WHITELIST_ADD.clear();
        TABCOMPLETE_WHITELIST_REMOVE.clear();


        // tport
        TABCOMPLETE.add("add");
        TABCOMPLETE.add("edit");
        TABCOMPLETE.add("extra");
        TABCOMPLETE.add("help");
        TABCOMPLETE.add("open");
        TABCOMPLETE.add("remove");
        TABCOMPLETE.add("whitelist");

        // tport edit
        if (!p.getConfig().contains("tport." + player.getName() + ".items")) {
            return;
        }
        for (String s : p.getConfig().getConfigurationSection("tport." + player.getName() + ".items").getKeys(false)) {
            ItemStack item = p.getConfig().getItemStack("tport." + player.getName() + ".items." + s + ".item");
            String name = item.getItemMeta().getDisplayName();
            TABCOMPLETE_EDIT.add(name);
        }
        // tport edit <TPort>
        TABCOMPLETE_EDIT_TPORT.add("item");
        TABCOMPLETE_EDIT_TPORT.add("location");
        TABCOMPLETE_EDIT_TPORT.add("lore");
        TABCOMPLETE_EDIT_TPORT.add("name");
        TABCOMPLETE_EDIT_TPORT.add("private");
        // tport edit <TPort> lore
        TABCOMPLETE_EDIT_LORE.add("remove");
        TABCOMPLETE_EDIT_LORE.add("set");
        // tport edit <TPort> private
        TABCOMPLETE_EDIT_PRIVATE.add("true");
        TABCOMPLETE_EDIT_PRIVATE.add("false");

        //tport extra
        TABCOMPLETE_EXTRA.add("item");
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
        TABCOMPLETE_EXTRA_WHITELIST_REMOVE.addAll(p.getConfig().getStringList("tport." + player.getName() + ".tp.players"));
        //tport extra whitelist add
        for (Player p : Bukkit.getOnlinePlayers()) {
            TABCOMPLETE_EXTRA_WHITELIST_ADD.add(p.getName());
        }

        // tport open
        TABCOMPLETE_OPEN.addAll(p.getConfig().getConfigurationSection("tport").getKeys(false));
        // tport open <player>
        if (p.getConfig().contains("tport." + one + ".items")) {

            for (String s : p.getConfig().getConfigurationSection("tport." + one + ".items").getKeys(false)) {
                ItemStack is = p.getConfig().getItemStack("tport." + one + ".items." + s + ".item");
                if (is.hasItemMeta()) {
                    TABCOMPLETE_OPEN_ITEM.add(is.getItemMeta().getDisplayName());
                }
            }
        }

        // tport remove
        if (p.getConfig().contains("tport." + player.getName() + ".items")) {
            for (String s : p.getConfig().getConfigurationSection("tport." + player.getName() + ".items").getKeys(false)) {
                TABCOMPLETE_REMOVE.add(p.getConfig().getItemStack("tport." + player.getName() + ".items." + s + ".item").getItemMeta().getDisplayName());
            }
        }

        // tport whitelist
        if (p.getConfig().contains("tport." + player.getName() + ".items")) {
            for (String s : p.getConfig().getConfigurationSection("tport." + player.getName() + ".items").getKeys(false)) {
                TABCOMPLETE_WHITELIST.add(p.getConfig().getItemStack("tport." + player.getName() + ".items." + s + ".item").getItemMeta().getDisplayName());
            }
        }
        // tport whitelist <tport>
        TABCOMPLETE_WHITELIST_TPORT.add("add");
        TABCOMPLETE_WHITELIST_TPORT.add("list");
        TABCOMPLETE_WHITELIST_TPORT.add("remove");
        // tport whitelist <TPort> add
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            TABCOMPLETE_WHITELIST_ADD.add(p.getName());
        }
        TABCOMPLETE_WHITELIST_ADD.remove(player.getName());
        // tport whitelist <TPort> remove
        if (p.getConfig().contains("tport." + player.getName() + ".items")) {
            for (String s : p.getConfig().getConfigurationSection("tport." +
                    player.getName() + ".items")
                    .getKeys(false)) {

                ItemStack item = p.getConfig()
                        .getItemStack("tport." + player.getName() + ".items." + s + ".item");
                String name = item.getItemMeta().getDisplayName();
                if (name.equalsIgnoreCase(one)) {
                    ArrayList<String> players = (ArrayList<String>)
                            p.getConfig().getStringList(
                                    "tport." + player.getName() + ".items." + s + ".private.players");
                    TABCOMPLETE_WHITELIST_REMOVE.addAll(players);
                }
            }
        }


    }
}
