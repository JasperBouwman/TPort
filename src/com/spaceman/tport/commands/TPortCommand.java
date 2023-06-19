package com.spaceman.tport.commands;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.HelpCommand;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.cooldown.CooldownCommand;
import com.spaceman.tport.events.PreviewEvents;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorThemeCommand;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.language.Language;
import com.spaceman.tport.metrics.CommandCounter;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.openUrl;
import static com.spaceman.tport.inventories.TPortInventories.openMainTPortGUI;

public class TPortCommand extends CommandTemplate {
    
    private static final TPortCommand instance = new TPortCommand();
    public static TPortCommand getInstance() {
        return instance;
    }
    
    private TPortCommand() {
        super(Main.getInstance(), false, new CommandDescription("tport", Main.getInstance().getDescription().getName(), "The command to teleport to saved locations and more", "/TPort <args...>"));
    }
    
    public static List<Message> getPlayerData(UUID uuid) {
        List<Message> hoverData = new ArrayList<>();
        
        hoverData.add(formatInfoTranslation("tport.command.headDisplay.tportAmount", TPortManager.getTPortList(uuid).size()));
        
        return hoverData;
    }
    
    public static boolean executeTPortCommand(CommandSender sender, String args) {
        if (args == null) {
            return false;
        }
        if (args.isEmpty()) {
            return executeTPortCommand(sender, new String[]{});
        }
        return executeTPortCommand(sender, args.split(" "));
    }
    
    public static boolean executeTPortCommand(CommandSender sender, String[] args) {
        if (args == null) {
            return false;
        }
        return TPortCommand.getInstance().execute(sender, "tport", args);
    }
    
    public static void reRegisterActions() {
        TPortCommand.getInstance().registerActions();
    }
    
    @Override
    public void registerActions() {
        this.actions.clear();
        PreviewEvents.unregister();
        
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.commandDescription"));
        addAction(empty);
        addAction(Open.getInstance());
        addAction(new Add());
        addAction(new Remove());
        addAction(new Edit());
        addAction(new PLTP());
        addAction(new Teleporter());
        addAction(new Own());
        addAction(new Back());
        addAction(new BiomeTP());
        addAction(new FeatureTP());
        addAction(new Home());
        addAction(new ColorThemeCommand());
        addAction(new Reload());
        addAction(CooldownCommand.getInstance());
        addAction(new RemovePlayer());
        addAction(new Public());
        addAction(new Transfer());
        addAction(new Version());
        addAction(new Log());
        addAction(new Backup());
        addAction(new ParticleAnimationCommand());
        addAction(new Delay());
        addAction(new Restriction());
        addAction(new Cancel());
        addAction(new Redirect());
        addAction(Search.getInstance());
        addAction(new Sort());
        addAction(new Tag());
        addAction(new SafetyCheck());
        addAction(new MetricsCommand());
        addAction(new DynmapCommand());
        addAction(new MainLayout());
        addAction(new WorldCommand());
        addAction(new Requests());
        addAction(new Features());
        addAction(new Language());
        addAction(new Preview());
        addAction(new ResourcePack());
        addAction(new Restore());
        addAction(new Settings());
//        addAction(new Look());
        
        TextComponent discordServer = new TextComponent("Discord Server", varInfoColor)
                .addTextEvent(new HoverEvent(new TextComponent(Main.discordLink, infoColor)))
                .addTextEvent(openUrl(Main.discordLink));
        Message helpMessage = formatInfoTranslation("tport.command.help.succeeded", discordServer);
        HelpCommand helpCommand = new HelpCommand(this, helpMessage, true);
        
        if (Features.Feature.Dynmap.isEnabled()) {
            EmptyCommand emptyHelpDynmap = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyHelpDynmap.setCommandDescription(formatInfoTranslation("tport.command.help.dynmap.commandDescription"));
            emptyHelpDynmap.setRunnable((args, player) -> executeTPortCommand(player, "dynmap"));
            helpCommand.addExtraHelp("Dynmap", emptyHelpDynmap);
        }
        
        if (Features.Feature.PLTP.isEnabled())
            helpCommand.addExtraTranslatedHelp("PLTP", formatInfoTranslation("tport.command.help.PLTP.description", "/tport PLTP state off"));
        
        helpCommand.addExtraTranslatedHelp("Redirects", formatInfoTranslation("tport.command.help.redirects.description", "/tp <player>", "/tport PLTP tp <player>"));
        helpCommand.addExtraTranslatedHelp("Language", formatInfoTranslation("tport.command.help.language.description", "server", "custom", "https://github.com/JasperBouwman/TPort/tree/master/src/lang"));
        helpCommand.addExtraTranslatedHelp("Cooldown", formatInfoTranslation("tport.command.help.cooldown.description", "/tport cooldown <cooldown> <value>", "permission", "TPort.cooldown.<cooldown>.<value>"));
        
        addAction(helpCommand);
    }
    
    @Override
    public boolean execute(@Nonnull CommandSender sender, @Nonnull String command, @Nonnull String[] args) {
        // for list of commands see the Permissions.txt
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You have to be a player to use this command");
            return false;
        }
        
        if (args.length == 0) {
            openMainTPortGUI(player);
        } else {
            if (this.runCommands(args[0], args, player)) {
                CommandCounter.add(args);
            } else {
                sendErrorTranslation(player, "tport.command.invalidSubCommand", args[0]);
            }
        }
        return false;
    }
}
