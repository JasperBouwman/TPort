package com.spaceman.tport.commands;

import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.HelpCommand;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.cooldown.CooldownCommand;
import com.spaceman.tport.events.PreviewEvents;
import com.spaceman.tport.fancyMessage.*;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.ColorThemeCommand;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.language.Language;
import com.spaceman.tport.metrics.CommandCounter;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.TPortInventories.openMainTPortGUI;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.openUrl;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;

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
        
        hoverData.add(new Message());
        hoverData.add(formatInfoTranslation("tport.command.headDisplay.tportAmount", TPortManager.getTPortList(uuid).size()));
        
        return hoverData;
    }
    
    public static ItemStack getHead(UUID head, Player player) {
        return getHead(Bukkit.getOfflinePlayer(head), player);
    }
    
    public static ItemStack getHead(OfflinePlayer head, Player player) {
        if (head == null) {
            return null;
        }
        
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        
        ColorTheme theme = ColorTheme.getTheme(player.getUniqueId());
        
        Message title;
        if (head.getName() == null) {
            title = formatTranslation(ColorType.titleColor, ColorType.titleColor, "tport.command.headDisplay.title", head.getUniqueId());
        } else {
            title = formatTranslation(ColorType.titleColor, ColorType.titleColor, "tport.command.headDisplay.title", head.getName());
        }
        
        List<Message> lore = getPlayerData(head.getUniqueId());
        lore.add(new Message());
        lore.add(formatInfoTranslation("tport.command.headDisplay.leftClick", ClickType.LEFT));
        
        if (head.isOnline()) {
            lore.add(formatInfoTranslation("tport.command.headDisplay.whenOnline.PLTP", ClickType.RIGHT, head.getName()));
            lore.add(formatInfoTranslation("tport.command.headDisplay.whenOnline.preview",
                    textComponent(Keybinds.DROP, varInfoColor).setType(TextType.KEYBIND), head.getName()));
        }
        
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        if (playerLang != null) { //if player has no custom language, translate it
            title = MessageUtils.translateMessage(title, playerLang);
            lore = MessageUtils.translateMessage(lore, playerLang);
        }
        MessageUtils.setCustomItemData(item, theme, title, lore);
        
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(head);
            
            FancyClickEvent.addCommand(meta, ClickType.LEFT, "tport open " + head.getName());
            FancyClickEvent.addCommand(meta, ClickType.RIGHT, "tport pltp tp " + head.getName());
            FancyClickEvent.addCommand(meta, ClickType.DROP, "tport preview " + head.getName());
            item.setItemMeta(meta);
        }
        return item;
    }
    
    public static boolean executeInternal(CommandSender sender, String args) {
        if (args == null) {
            return false;
        }
        if (args.isEmpty()) {
            return executeInternal(sender, new String[]{});
        }
        return executeInternal(sender, args.split(" "));
    }
    
    public static boolean executeInternal(CommandSender sender, String[] args) {
        if (args == null) {
            return false;
        }
        return TPortCommand.getInstance().execute(sender, "tport", args);
    }
    
    public static void reRegisterActions() {
        TPortCommand.getInstance().registerActions();
    }
    
    private EmptyCommand empty;
    
    @Override
    public void registerActions() {
        this.actions.clear();
        PreviewEvents.unregister();
        
        empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.commandDescription"));
        empty.setPermissions("TPort.open", "TPort.basic");
        addAction(empty);
        addAction(Open.getInstance());
        addAction(new Add());
        addAction(new Remove());
        addAction(new Edit());
        if (Features.Feature.PLTP.isEnabled()) addAction(new PLTP());
        addAction(new Teleporter());
        addAction(new Own());
        if (Features.Feature.BackTP.isEnabled()) addAction(new Back());
        if (Features.Feature.BiomeTP.isEnabled()) addAction(new BiomeTP());
        if (Features.Feature.FeatureTP.isEnabled()) addAction(new FeatureTP());
        addAction(new Home());
        addAction(new ColorThemeCommand());
        addAction(new Reload());
        addAction(CooldownCommand.getInstance());
        addAction(new RemovePlayer());
        if (Features.Feature.PublicTP.isEnabled()) addAction(new Public());
        addAction(new Transfer());
        addAction(new Version());
        addAction(new Log());
        addAction(new Backup());
        if (Features.Feature.ParticleAnimation.isEnabled()) addAction(new ParticleAnimationCommand());
        addAction(new Delay());
        addAction(new Restriction());
        addAction(new Cancel());
        if (Features.Feature.Redirects.isEnabled()) addAction(new Redirect());
        addAction(Search.getInstance());
        addAction(new Sort());
        addAction(new Tag());
        addAction(new SafetyCheck());
        addAction(new MetricsCommand());
        if (Features.Feature.Dynmap.isEnabled()) addAction(new DynmapCommand());
        addAction(new MainLayout());
        if (Features.Feature.WorldTP.isEnabled()) addAction(new WorldCommand());
        addAction(new Requests());
        if (Features.Feature.FeatureSettings.isEnabled()) addAction(new Features());
        addAction(new Language());
        if (Features.Feature.Preview.isEnabled()) addAction(new Preview());
        addAction(new ResourcePack());
        
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
            emptyHelpDynmap.setRunnable((args, player) -> executeInternal(player, "dynmap"));
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
            if (!empty.hasPermissionToRun(player, true)) {
                return false;
            }
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
