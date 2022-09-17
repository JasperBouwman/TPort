package com.spaceman.tport.cooldown;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class CooldownCommand extends SubCommand {
    
    private static final CooldownCommand instance = new CooldownCommand();
    public static CooldownCommand getInstance() {
        return instance;
    }
    
    protected final EmptyCommand emptyCooldownCooldownValue = new EmptyCommand();
    
    public CooldownCommand() {
        emptyCooldownCooldownValue.setCommandName("value", ArgumentType.OPTIONAL);
        emptyCooldownCooldownValue.setCommandDescription(formatInfoTranslation("tport.cooldown.cooldownCommand.value.commandDescription"));
        emptyCooldownCooldownValue.setPermissions("TPort.cooldown.set", "TPort.admin.cooldown");
        
        EmptyCommand emptyCooldownCooldown = new EmptyCommand();
        emptyCooldownCooldown.setCommandName("cooldown", ArgumentType.REQUIRED);
        emptyCooldownCooldown.setCommandDescription(formatInfoTranslation("tport.cooldown.cooldownCommand.commandDescription"));
        emptyCooldownCooldown.setTabRunnable((args, player) -> {
            if (!emptyCooldownCooldownValue.hasPermissionToRun(player, false)) return new ArrayList<>();
            
            ArrayList<String> originalList = new ArrayList<>();
            Arrays.stream(CooldownManager.values()).map(CooldownManager::name).forEach(originalList::add);
            if (originalList.contains(args[1])) {
                ArrayList<String> list = new ArrayList<>(originalList);
                list.add("permission");
                list.remove(args[1]);
                return list;
            }
            
            return new ArrayList<>();
        });
        emptyCooldownCooldown.addAction(emptyCooldownCooldownValue);
        addAction(emptyCooldownCooldown);
    }
    
    @Override
    public String getName(String arg) {
        return "cooldown";
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
                //noinspection ConstantConditions -> CooldownManager#get should not return 'null' because of the CooldownManager#contains
                CooldownManager.get(args[1]).printValue(player);
            } else {
                sendErrorTranslation(player, "tport.cooldown.cooldownCommand.get.error", args[1]);
            }
            
        } else if (args.length == 3) {
            if (!emptyCooldownCooldownValue.hasPermissionToRun(player, true)) {
                return;
            }
            if (CooldownManager.contains(args[1])) {
                try {
                    Long.parseLong(args[2]);
                } catch (NumberFormatException nfe) {
                    if (!args[2].equalsIgnoreCase("permission")) {
                        if (!CooldownManager.contains(args[2])) {
                            sendErrorTranslation(player, "tport.cooldown.cooldownCommand.set.invalidEntry", args[2], "permissions");
                            return;
                        } else if (args[1].equalsIgnoreCase(args[2])) {
                            sendErrorTranslation(player, "tport.cooldown.cooldownCommand.set.setToSelf");
                            return;
                        }
                    } else {
                        sendInfoTranslation(player, "tport.cooldown.cooldownCommand.set.permissionInfo", "TPort.cooldown." + args[1] + ".<X>");
                    }
                }
                
                //noinspection ConstantConditions -> CooldownManager#get should not return 'null' because of the CooldownManager#contains
                CooldownManager.get(args[1]).edit(args[2]);
                sendSuccessTranslation(player, "tport.cooldown.cooldownCommand.set.succeeded", args[1], args[2]);
            } else {
                sendErrorTranslation(player, "tport.cooldown.cooldownCommand.set.error", args[1]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport cooldown <cooldown> [value]");
        }
        
    }
}
