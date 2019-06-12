package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.spaceman.tport.Permissions.hasPermission;

public class Cooldown extends SubCommand {

    public Cooldown() {
        EmptyCommand e = new EmptyCommand();
        e.setTabRunnable((args, player) -> {
            if (!Permissions.hasPermission(player, "TPort.command.cooldown.set", false) && !Permissions.hasPermission(player, "TPort.admin.cooldown", false)) {
                return new ArrayList<>();
            }

            ArrayList<String> originalList = new ArrayList<>();
            Arrays.stream(CooldownManager.values()).map(CooldownManager::name).forEach(originalList::add);
            ArrayList<String> list = new ArrayList<>(originalList);
            list.add("permission");
            list.remove(args[1]);
            if (originalList.contains(args[1])) {
                return list;
            }

            return new ArrayList<>();
        });
        addAction(e);
    }

    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        Arrays.stream(CooldownManager.values()).map(CooldownManager::name).forEach(list::add);
        return list;
    }

    @Override
    public void run(String[] args, Player player) {
        //tport cooldown <cooldown> [value]

        if (args.length == 2) {

            if (!hasPermission(player, "tport.command.cooldown")) {
                return;
            }
            if (CooldownManager.contains(args[1])) {
                player.sendMessage(ChatColor.DARK_AQUA + "Cooldown of " + args[1] + " is set to " + ChatColor.BLUE + CooldownManager.valueOf(args[1]).value());
            } else {
                player.sendMessage(ChatColor.RED + "Cooldown " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " does not exist");
            }

        } else if (args.length > 2) {
            if (!Permissions.hasPermission(player, "TPort.command.cooldown.set", false)) {
                if (!Permissions.hasPermission(player, "TPort.admin.cooldown", false)) {
                    Permissions.sendNoPermMessage(player, "TPort.command.cooldown.set", "TPort.admin.cooldown");
                    return;
                }
            }
            if (CooldownManager.contains(args[1])) {
                try {
                    Long.parseLong(args[2]);
                } catch (NumberFormatException nfe) {
                    if (!args[2].equals("permission")) {
                        if (!CooldownManager.contains(args[2])) {
                            player.sendMessage(ChatColor.DARK_RED + args[2] + ChatColor.RED + " is not a valid value, it must be a number or another cooldown name");
                            return;
                        } else if (args[1].equals(args[2])) {
                            player.sendMessage(ChatColor.RED + "The value of a cooldown can not be set to it self");
                            return;
                        }
                    }
                }

                CooldownManager.valueOf(args[1]).edit(args[2]);
                player.sendMessage(ChatColor.DARK_AQUA + "Cooldown of " + ChatColor.BLUE + args[1] + ChatColor.DARK_AQUA + " is now changed to " + ChatColor.BLUE + args[2]);
            } else {
                player.sendMessage(ChatColor.RED + "Cooldown " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " does not exist");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /tport cooldown <cooldown> [value]");
        }

    }
}
