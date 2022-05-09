package com.spaceman.tport.commands.tport.pa;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import com.spaceman.tport.tpEvents.TPEManager;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;

public class New extends SubCommand {
    
    public New() {
        EmptyCommand emptySetParticleData = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptySetParticleData.setCommandName("data", ArgumentType.OPTIONAL);
        emptySetParticleData.setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand.new.set.particleAnimation.data.commandDescription"));
        emptySetParticleData.setTabRunnable((args, player) -> ParticleAnimation.getNewAnimation(args[3]).tabList(player, Arrays.copyOfRange(args, 4, args.length)));
        emptySetParticleData.setRunnable(((args, player) -> {
            // tport particleAnimation new set <particleAnimation> [data...]
            if (args.length < 4) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation new set <particleAnimation> [data...]");
            } else {
                if (emptySetParticleData.hasPermissionToRun(player, true)) {
                    ParticleAnimation pa = ParticleAnimation.getNewAnimation(args[3], Arrays.copyOfRange(args, 4, args.length), player);
                    if (pa != null) {
                        TPEManager.setNewLocAnimation(player.getUniqueId(), pa);
                        sendSuccessTranslation(player, "tport.command.particleAnimationCommand.new.set.particleAnimation.data.succeeded", pa);
                    } else {
                        sendErrorTranslation(player, "tport.command.particleAnimationCommand.new.set.particleAnimation.data.animationNotFound", args[4]);
                    }
                }
            }
        }));
        emptySetParticleData.setLooped(true);
        emptySetParticleData.setPermissions("TPort.particleAnimation.new.set");
        EmptyCommand emptySetParticle = new EmptyCommand();
        emptySetParticle.setCommandName("particleAnimation", ArgumentType.REQUIRED);
        emptySetParticle.setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand.new.set.particleAnimation.commandDescription"));
        emptySetParticle.setTabRunnable((args, player) -> ParticleAnimation.getNewAnimation(args[3]).tabList(player, Arrays.copyOfRange(args, 4, args.length)));
        emptySetParticle.setRunnable((emptySetParticleData::run));
        emptySetParticle.addAction(emptySetParticleData);
        emptySetParticle.setPermissions(emptySetParticleData.getPermissions());
        EmptyCommand emptySet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptySet.setCommandName("set", ArgumentType.FIXED);
        emptySet.setTabRunnable((args, player) -> ParticleAnimation.getAnimations());
        emptySet.setRunnable(emptySetParticleData::run);
        emptySet.addAction(emptySetParticle);
        
        EmptyCommand emptyEditData = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyEditData.setCommandName("data", ArgumentType.REQUIRED);
        emptyEditData.setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand.new.edit.data.commandDescription"));
        emptyEditData.setTabRunnable((args, player) -> TPEManager.getNewLocAnimation(player.getUniqueId()).tabList(player, Arrays.copyOfRange(args, 3, args.length)));
        emptyEditData.setRunnable((args, player) -> {
            // tport particleAnimation new edit <data...>
            if (emptyEditData.hasPermissionToRun(player, true)) {
                ParticleAnimation pa = TPEManager.getNewLocAnimation(player.getUniqueId());
                String[] data = Arrays.copyOfRange(args, 3, args.length);
                if (pa.edit(player, data)) {
                    sendSuccessTranslation(player, "tport.command.particleAnimationCommand.new.edit.data.succeeded", String.join(" ", data));
                }
            }
        });
        emptyEditData.setLooped(true);
        emptyEditData.setPermissions("TPort.particleAnimation.new.edit");
        EmptyCommand emptyEdit = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyEdit.setCommandName("edit", ArgumentType.FIXED);
        emptyEdit.setTabRunnable((args, player) -> TPEManager.getNewLocAnimation(player.getUniqueId()).tabList(player, Arrays.copyOfRange(args, 3, args.length)));
        emptyEdit.setRunnable((args, player) -> {
            // tport particleAnimation new edit <data...>
            if (args.length < 4) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation new edit <data...>");
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
        emptyTest.setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand.new.test"));
        emptyTest.setRunnable(((args, player) -> {
            if (emptyTest.hasPermissionToRun(player, true))
                TPEManager.getNewLocAnimation(player.getUniqueId()).show(player, player.getLocation());
        }));
        emptyTest.setPermissions("TPort.particleAnimation.new.test");
        
        EmptyCommand emptyEnableState = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyEnableState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyEnableState.setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand.new.enable.state.commandDescription"));
        emptyEnableState.setPermissions("TPort.particleAnimation.new.enable.set");
        EmptyCommand emptyEnable = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyEnable.setCommandName("enable", ArgumentType.FIXED);
        emptyEnable.setCommandDescription(formatInfoTranslation("tport.command.particleAnimationCommand.new.enable.commandDescription"));
        emptyEnable.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyEnable.setRunnable(((args, player) -> {
            // tport particleAnimation new enable [state]
            if (args.length == 3) {
                if (emptyEnable.hasPermissionToRun(player, true)) {
                    ParticleAnimation pa = TPEManager.getNewLocAnimation(player.getUniqueId());
                    if (pa.isEnabled()) {
                        sendInfoTranslation(player, "tport.command.particleAnimationCommand.new.enable.succeeded",
                                formatTranslation(goodColor, varInfoColor, "tport.command.particleAnimationCommand.new.enable.state.enabled"));
                    } else {
                        sendInfoTranslation(player, "tport.command.particleAnimationCommand.new.enable.succeeded",
                                formatTranslation(badColor, varInfoColor, "tport.command.particleAnimationCommand.new.enable.state.disabled"));
                    }
                }
            } else if (args.length == 4) {
                if (emptyEnableState.hasPermissionToRun(player, true)) {
                    ParticleAnimation pa = TPEManager.getNewLocAnimation(player.getUniqueId());
                    Boolean state = Main.toBoolean(args[3]);
                    if (state == null) {
                        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation new enable [true|false]");
                        return;
                    }
                    pa.setEnabled(state);
                    if (pa.isEnabled()) {
                        sendSuccessTranslation(player, "tport.command.particleAnimationCommand.new.enable.state.succeeded",
                                formatTranslation(goodColor, varInfoColor, "tport.command.particleAnimationCommand.new.enable.state.enabled"));
                    } else {
                        sendSuccessTranslation(player, "tport.command.particleAnimationCommand.new.enable.state.succeeded",
                                formatTranslation(badColor, varInfoColor, "tport.command.particleAnimationCommand.new.enable.state.disabled"));
                    }
                }
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation new enable [state]");
            }
        }));
        emptyEnable.setPermissions("TPort.particleAnimation.new.enable.get");
        
        addAction(emptySet);
        addAction(emptyEdit);
        addAction(emptyTest);
        addAction(emptyEnable);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport particleAnimation new set <particleAnimation> [data...]
        // tport particleAnimation new edit <data...>
        // tport particleAnimation new test
        // tport particleAnimation new enable [state]
        
        if (args.length > 2) {
            if (runCommands(getActions(), args[2], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport particleAnimation new <set|edit|test|enable>");
    }
}
