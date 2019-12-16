package com.spaceman.tport.commands.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Cooldown extends SubCommand {

    public Cooldown() {
        EmptyCommand emptyValue = new EmptyCommand();
        emptyValue.setCommandName("value", ArgumentType.OPTIONAL);
        emptyValue.setCommandDescription(textComponent("This command is used to set the cooldown value of the given cooldown", ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.cooldown.set", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.admin.cooldown", ColorTheme.ColorType.varInfoColor));
        
        EmptyCommand emptyCooldown = new EmptyCommand();
        emptyCooldown.setCommandName("cooldown", ArgumentType.REQUIRED);
        emptyCooldown.setCommandDescription(textComponent("This command is used to get the cooldown value of the given cooldown", ColorType.infoColor));
        emptyCooldown.setTabRunnable((args, player) -> {
            if (!hasPermission(player, false, true, "TPort.cooldown.set", "TPort.admin.cooldown")) {
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
        emptyCooldown.addAction(emptyValue);
        addAction(emptyCooldown);
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
            if (CooldownManager.contains(args[1])) {
                sendInfoTheme(player, "Cooldown value of %s is set to %s", args[1], CooldownManager.valueOf(args[1]).value());
            } else {
                sendErrorTheme(player, "Cooldown %s does not exist", args[1]);
            }

        } else if (args.length > 2) {
            if (!hasPermission(player, true, true, "TPort.cooldown.set", "TPort.admin.cooldown")) {
                return;
            }
            if (CooldownManager.contains(args[1])) {
                try {
                    Long.parseLong(args[2]);
                } catch (NumberFormatException nfe) {
                    if (!args[2].equals("permission")) {
                        if (!CooldownManager.contains(args[2])) {
                            sendErrorTheme(player, "%s is not a valid value, it must be a number or another cooldown name", args[2]);
                            return;
                        } else if (args[1].equals(args[2])) {
                            sendErrorTheme(player, "The value of a cooldown can not be set to it self");
                            return;
                        }
                    }
                }

                CooldownManager.valueOf(args[1]).edit(args[2]);
                sendSuccessTheme(player, "Successfully set cooldown value of %s to %s", args[1], args[2]);
            } else {
                sendErrorTheme(player, "Cooldown %s does not exist", args[1]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport <cooldown> [value]");
        }

    }
}
