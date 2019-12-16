package com.spaceman.tport.commands.tport.pa;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import com.spaceman.tport.tpEvents.TPEManager;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Old extends SubCommand {
    
    public Old() {
        EmptyCommand emptySetParticleData = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptySetParticleData.setCommandName("data", ArgumentType.OPTIONAL);
        emptySetParticleData.setCommandDescription(textComponent("This command is used to change your old location particle animation, and give if your data", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.particleAnimation.old.set", ColorTheme.ColorType.varInfoColor));
        emptySetParticleData.setTabRunnable((args, player) -> ParticleAnimation.getNewAnimation(args[3]).tabList(player, Arrays.copyOfRange(args, 4, args.length)));
        emptySetParticleData.setRunnable(((args, player) -> {
            if (hasPermission(player, true, "TPort.particleAnimation.old.set")) {
                ParticleAnimation pa = ParticleAnimation.getNewAnimation(args[3], Arrays.copyOfRange(args, 4, args.length), player);
                if (pa != null) {
                    TPEManager.setOldLocAnimation(player.getUniqueId(), pa);
                    sendSuccessTheme(player, "Successfully set your old location particle animation to %s", pa.getAnimationName());
                } else {
                    sendErrorTheme(player, "Particle animation %s was not found", args[4]);
                }
            }
        }));
        emptySetParticleData.setLooped(true);
        EmptyCommand emptySetParticle = new EmptyCommand();
        emptySetParticle.setCommandName("particleAnimation", ArgumentType.REQUIRED);
        emptySetParticle.setCommandDescription(textComponent("This command is used to change your old location particle animation", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.particleAnimation.old.set", ColorTheme.ColorType.varInfoColor));
        emptySetParticle.setTabRunnable((args, player) -> ParticleAnimation.getNewAnimation(args[3]).tabList(player, Arrays.copyOfRange(args, 4, args.length)));
        emptySetParticle.setRunnable((emptySetParticleData::run));
        emptySetParticle.addAction(emptySetParticleData);
        EmptyCommand emptySet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptySet.setCommandName("set", ArgumentType.FIXED);
        emptySet.setTabRunnable((args, player) -> ParticleAnimation.getAnimations());
        emptySet.setRunnable(((args, player) -> {
            if (args.length < 4) {
                sendErrorTheme(player, "Usage: %s", "/tport particleAnimation old set <particleAnimation> [data...]");
            } else {
                emptySetParticle.run(args, player);
            }
        }));
        emptySet.addAction(emptySetParticle);
        
        EmptyCommand emptyEditData = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyEditData.setCommandName("data", ArgumentType.REQUIRED);
        emptyEditData.setCommandDescription(textComponent("This command is used to edit your old location particle animation", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.particleAnimation.old.edit", ColorTheme.ColorType.varInfoColor));
        emptyEditData.setTabRunnable((args, player) -> TPEManager.getOldLocAnimation(player.getUniqueId()).tabList(player, Arrays.copyOfRange(args, 3, args.length)));
        emptyEditData.setRunnable((args, player) -> {
            if (hasPermission(player, true, "TPort.particleAnimation.old.edit")) {
                ParticleAnimation pa = TPEManager.getOldLocAnimation(player.getUniqueId());
                String[] data = Arrays.copyOfRange(args, 3, args.length);
                pa.edit(player, data);
                sendSuccessTheme(player, "Successfully edited your old particle animation with the data %s", String.join(" ", data));
            }
        });
        emptyEditData.setLooped(true);
        EmptyCommand emptyEdit = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyEdit.setCommandName("edit", ArgumentType.FIXED);
        emptyEdit.setTabRunnable((args, player) -> TPEManager.getOldLocAnimation(player.getUniqueId()).tabList(player, Arrays.copyOfRange(args, 3, args.length)));
        emptyEdit.setRunnable((args, player) -> {
            if (args.length < 4) {
                sendErrorTheme(player, "Usage: %s", "/tport particleAnimation old edit <data...>");
            } else {
                emptyEditData.run(args, player);
            }
        });
        emptyEdit.addAction(emptyEditData);
        
        EmptyCommand emptyTest = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyTest.setCommandName("test", ArgumentType.FIXED);
        emptyTest.setCommandDescription(textComponent("This command is used to test your old location particle animation", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.particleAnimation.old.test", ColorTheme.ColorType.varInfoColor));
        emptyTest.setRunnable(((args, player) -> {
            if (hasPermission(player, true, "TPort.particleAnimation.old.test"))
                TPEManager.getOldLocAnimation(player.getUniqueId()).show(player, player.getLocation());
        }));
        
        EmptyCommand emptyEnableState = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyEnableState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyEnableState.setCommandDescription(textComponent("This command is used to set the old location particle animation enabled or not", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.particleAnimation.old.enable.set", ColorTheme.ColorType.varInfoColor));
        emptyEnableState.setRunnable(((args, player) -> {
            if (args.length == 3) {
                if (hasPermission(player, true, "TPort.particleAnimation.old.enable.get")) {
                    ParticleAnimation pa = TPEManager.getOldLocAnimation(player.getUniqueId());
                    if (pa.isEnabled()) {
                        sendInfoTheme(player, "Your old location particle animation is %s", "enabled");
                    } else {
                        sendInfoTheme(player, "Your old location particle animation is %s", "disabled");
                    }
                }
            } else if (args.length == 4) {
                if (hasPermission(player, true, "TPort.particleAnimation.old.enable.set")) {
                    ParticleAnimation pa = TPEManager.getOldLocAnimation(player.getUniqueId());
                    pa.setEnabled(Boolean.parseBoolean(args[3]));
                    if (pa.isEnabled()) {
                        sendSuccessTheme(player, "Successfully set your old location particle animation %s", "enabled");
                    } else {
                        sendSuccessTheme(player, "Successfully set your old location particle animation %s", "disabled");
                    }
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport particleAnimation old enable [state]");
            }
        }));
        EmptyCommand emptyEnable = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyEnable.setCommandName("enable", ArgumentType.FIXED);
        emptyEnable.setCommandDescription(textComponent("This command is used to get if the old location particle animation is enabled or not", infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.particleAnimation.old.enable.get", ColorTheme.ColorType.varInfoColor));
        emptyEnable.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyEnable.setRunnable(emptyEnableState::run);
        
        addAction(emptySet);
        addAction(emptyEdit);
        addAction(emptyTest);
        addAction(emptyEnable);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport particleAnimation old set <particleAnimation> [data...]
        // tport particleAnimation old edit <data...>
        // tport particleAnimation old test
        // tport particleAnimation old enable [state]
        
        if (args.length > 2) {
            if (runCommands(getActions(), args[2], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage %s", "/tport particleAnimation old <set|edit|test|enable>");
    }
}
