package com.spaceman.tport.commandHander;

import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SubCommand {

    private ArrayList<SubCommand> actions = new ArrayList<>();

    public void addAction(SubCommand subCommand) {
        actions.add(subCommand);
    }

    public ArrayList<SubCommand> getActions() {
        return actions;
    }

    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        for (SubCommand subCommand : actions) {
            if (!(subCommand instanceof EmptyCommand)) {
                if (!subCommand.getName().equals("")) {
                    list.add(subCommand.getName());
                }
            }
        }
        return list;
    }

    public static String lowerCaseFirst(String string) {
        return string == null || string.isEmpty() ? "" : Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    public String getName() {
        return lowerCaseFirst(this.getClass().getSimpleName());
    }

    public List<String> getAliases() {
        return new ArrayList<>();
    }

    public abstract void run(String[] args, Player player);

}
