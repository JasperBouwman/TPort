package com.spaceman.tport.commands;

import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.HelpCommand;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.cooldown.CooldownCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.ColorThemeCommand;
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
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.TPortInventories.openMainTPortGUI;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;

public class TPortCommand extends CommandTemplate {
    
    private static TPortCommand instance = null;
    public static TPortCommand getInstance() {
        if (instance == null) {
            instance = new TPortCommand();
        }
        return instance;
    }
    
    private TPortCommand() {
        super(Main.getInstance(), false, new CommandDescription("TPort", Main.getInstance().getDescription().getName(), "The command to teleport to saved locations and more", "/TPort <args...>"));
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
        
        Message title = formatTranslation(ColorType.titleColor, ColorType.titleColor, "tport.command.headDisplay.title", head.getName());
        Collection<Message> lore = getPlayerData(head.getUniqueId());
        
        if (head.isOnline()) {
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.command.headDisplay.whenOnline", ClickType.RIGHT, head.getName()));
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
        addAction(new Open());
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
        addAction(new SetHome());
        addAction(new ColorThemeCommand());
        addAction(new Reload());
        addAction(new CooldownCommand());
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
        addAction(new WorldCommand());
        addAction(new Requests());
        if (Features.Feature.FeatureSettings.isEnabled()) addAction(new Features());
        addAction(new Language());
        
        HelpCommand helpCommand = new HelpCommand(this, null, true);
        
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
        // tport
        // tport open <player> [TPort name] [safetyCheck]
        // tport add <TPort name> [description...]
        // tport remove <TPort name>
        // tport edit <TPort name> description set <description...>
        // tport edit <TPort name> description remove
        // tport edit <TPort name> description get
        // tport edit <TPort name> name <new TPort name>
        // tport edit <TPort name> item
        // tport edit <TPort name> location
        // tport edit <TPort name> private
        // tport edit <TPort name> private <state>
        // tport edit <TPort name> whitelist <add|remove> <player...>
        // tport edit <TPort name> whitelist list
        // tport edit <TPort name> whitelist clone <TPort name>
        // tport edit <TPort name> move <slot|TPort name>
        // tport edit <TPort name> range [range]
        // tport edit <TPort name> tag add <tag>
        // tport edit <TPort name> tag remove <tag>
        // tport edit <TPort name> dynmap show [state]
        // tport edit <TPort name> dynmap icon [icon]
        // tport PLTP state [state]
        // tport PLTP consent [state]
        // tport PLTP whitelist list
        // tport PLTP whitelist <add|remove> <player...>
        // tport PLTP tp <player>
        // tport PLTP offset
        // tport PLTP offset <offset>
        // tport teleporter create <type> [data...]
        // tport teleporter remove
        // tport own [TPort name] [safetyCheck]
        // tport back [safetyCheck]
        // tport biomeTP
        // tport biomeTP accuracy [accuracy]
        // tport biomeTP whitelist <biome...>
        // tport biomeTP blacklist <biome...>
        // tport biomeTP preset [preset]
        // tport biomeTP random
        // tport biomeTP searchTries [tries]
        // tport biomeTP mode [mode]
        // tport featureTP
        // tport featureTP search <feature> [mode]
        // tport featureTP mode [mode]
        // tport home [safetyCheck]
        // tport setHome <player> <TPort name> [safetyCheck]
        // tport colorTheme
        // tport colorTheme set <theme>
        // tport colorTheme set <type> <chat color>
        // tport colorTheme set <type> <hex color>
        // tport colorTheme get <type>
        // tport reload
        // tport cooldown <cooldown> [value]
        // tport removePlayer <player>
        // tport help
        // tport public
        // tport public open <TPort name>
        // tport public add <TPort name>
        // tport public remove <own TPort name|all TPort name>
        // tport public list [own|all]
        // tport public move <TPort name> <slot|TPort name>
        // tport public listSize [size]
        // tport transfer offer <player> <TPort name>
        // tport transfer revoke <TPort name>
        // tport transfer accept <player> <TPort name>
        // tport transfer reject <player> <TPort name>
        // tport transfer list
        // tport version
        // tport log read <TPort name>
        // tport log TimeZone [TimeZone]
        // tport log timeFormat [format...]
        // tport log clear <TPort name...>
        // tport log logData [TPort name] [player]
        // tport log add <TPort name> <player[:LogMode]...>
        // tport log remove <TPort name> <player...>
        // tport log default <TPort name> [default LogMode]
        // tport log notify [TPort name] [state]
        // tport log logSize [size]
        // tport backup save <name>
        // tport backup load <name>
        // tport backup auto [state|count]
        // tport particleAnimation new set <particleAnimation> [data...]
        // tport particleAnimation new edit <data...>
        // tport particleAnimation new test
        // tport particleAnimation new enable [state]
        // tport particleAnimation old set <particleAnimation> [data...]
        // tport particleAnimation old edit <data...>
        // tport particleAnimation old test
        // tport particleAnimation old enable [state]
        // tport delay permission [state]
        // tport delay set <player> <delay>
        // tport delay get [player]
        // tport restriction permission [state]
        // tport restriction set <player> <type>
        // tport restriction get [player]
        // tport cancel
        // tport quickGuide [guide]
        // tport redirect [redirect] [state]
        // tport tag create <tag> <permission>
        // tport tag delete <tag>
        // tport tag list
        // tport tag reset
        // tport search <type>
        // tport search <type> <query...>
        // tport search <type> <mode> <query...>
        // tport sort [sorter]
        // tport safetyCheck
        // tport safetyCheck <source> [state]
        // tport safetyCheck check
        // tport metrics viewStats
        // tport dynmap
        // tport dynmap show <player> [tport name]
        // tport dynmap IP [IP]
        // tport mainLayout players [state]
        // tport mainLayout TPorts [state]
        
        
        // tport language server [language]
        // tport language get
        // tport language set custom
        // tport language set server
        // tport language set <server language>
        // tport language test <id>
        // tport language repair <language> [repair with]
        // tport requests
        // tport requests accept [player...]
        // tport requests reject [player...]
        // tport requests revoke [player...]
        // tport features
        // tport features <feature>
        // tport features <feature> state
        // tport features <feature> state <state>
        
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
