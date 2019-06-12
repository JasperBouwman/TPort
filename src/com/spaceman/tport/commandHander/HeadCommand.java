package com.spaceman.tport.commandHander;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class HeadCommand implements TabCompleter, CommandExecutor {

    private ArrayList<SubCommand> actions = new ArrayList<>();
    
    public static List<String> filterContaining(String arg, Collection<String> fullList) {
        ArrayList<String> list = new ArrayList<>();
        for (String ss : fullList) {
            if (ss != null && arg != null) {
                if (ss.toLowerCase().contains(arg.toLowerCase())) {
                    list.add(ss);
                }
            }
        }
        return list;
    }

    private static List<String> tabList(List<SubCommand> actions, String[] args, Player player, int i) {

        //first tier subCommands
        if (args.length == 1) {
            ArrayList<String> tabList = new ArrayList<>();
            for (SubCommand subCommand : actions) {
                tabList.add(subCommand.getName());
            }
            return filterContaining(args[0], tabList);
        }

        if (args.length == i) {
            ArrayList<String> tabList = new ArrayList<>();

            for (SubCommand subCommand : actions) {

                if (subCommand.getName().equalsIgnoreCase(args[i - 2])) {
                    tabList.addAll(subCommand.tabList(player, args));
                }
                if (subCommand instanceof EmptyCommand) {
                    if (!subCommand.getName().equals("")) {
                        if (subCommand.getName().equalsIgnoreCase(args[i - 2])) {
                            tabList.addAll(subCommand.tabList(player, args));
                        }
                    } else {
                        tabList.addAll(subCommand.tabList(player, args));
                    }
                }
            }
            return filterContaining(args[i - 1], tabList);
        } else {
            try {
                for (SubCommand subCommand : actions) {
                    if (subCommand.getName().equalsIgnoreCase(args[i - 2])) {
                        return tabList(subCommand.getActions(), args, player, i + 1);
                    }
                    if (subCommand instanceof EmptyCommand) {
                        return tabList(subCommand.getActions(), args, player, i + 1);
                    }
                }
            } catch (Exception ignore) {
            }
            return tabList(actions, args, player, i + 1);
        }
    }

    public static boolean runCommands(List<SubCommand> actions, String arg, String[] args, Player player) {

        for (SubCommand action : actions) {
            if (action.getName().equalsIgnoreCase(arg) || action.getAliases().contains(arg)) {
                action.run(args, player);
                return true;
            }
        }
        return false;
    }

    public List<String> tabList(String[] args, Player player) {
        return tabList(actions, args, player, 1);
    }

    public void addAction(SubCommand subCommand) {
        actions.add(subCommand);
    }

    public ArrayList<SubCommand> getActions() {
        return actions;
    }

    protected boolean runCommands(String arg, String[] args, Player player) {
        return runCommands(actions, arg, args, player);
    }

    @Override
    public abstract boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings);

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return tabList(strings, (Player) commandSender);
    }
}
