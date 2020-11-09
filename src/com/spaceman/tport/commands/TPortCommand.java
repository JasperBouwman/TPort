package com.spaceman.tport.commands;

import com.spaceman.tport.Main;
import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHander.*;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.ColorThemeCommand;
import com.spaceman.tport.metrics.CommandCounter;
import com.spaceman.tport.tport.TPortManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.TPortInventories.addCommand;
import static com.spaceman.tport.TPortInventories.openMainTPortGUI;
import static com.spaceman.tport.fancyMessage.MessageUtils.ImageFrame.recursion;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class TPortCommand extends CommandTemplate {
    
    public TPortCommand() {
        super(Main.getInstance(), true, new CommandDescription("TPort", Main.getInstance().getDescription().getName(), "The command to teleport to saved locations and more", "/TPort <args...>"));
    }
    
    public static ItemStack getHead(UUID head, Player player) {
        return getHead(Bukkit.getOfflinePlayer(head), player);
    }
    
    public static ItemStack getHead(OfflinePlayer head, Player player) {
        if (head == null) {
            return null;
        }
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(head);
            meta.setDisplayName(ChatColor.YELLOW + head.getName());
            
            addCommand(meta, TPortInventories.Action.LEFT_CLICK, "open " + head.getName());
            
            ColorTheme theme = ColorTheme.getTheme(player.getUniqueId());
            
            meta.setLore(Arrays.asList("",
                    "§r" + theme.getInfoColor() + "TPorts: " + theme.getVarInfoColor() + TPortManager.getTPortList(head.getUniqueId()).size()));
            
            item.setItemMeta(meta);
        }
        return item;
    }
    
    public static boolean executeInternal(CommandSender sender, String args) {
        if (args == null) {
            return false;
        }
        return executeInternal(sender, args.split(" "));
    }
    
    public static boolean executeInternal(CommandSender sender, String[] args) {
        if (args == null) {
            return false;
        }
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            
            commandMap.getCommand("TPort").execute(sender, "tport", args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private EmptyCommand empty;
    
    @Override
    public void registerActions() {
        empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(textComponent("This command is used to open the main TPort gui with all the player to choose from", ColorTheme.ColorType.infoColor));
        empty.setPermissions("TPort.open", "TPort.basic");
        addAction(empty);
        addAction(new Open());
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
        addAction(new SetHome());
        addAction(new ColorThemeCommand());
        addAction(new Reload());
        addAction(new Cooldown());
        addAction(new RemovePlayer());
        if (Public.isEnabled()) addAction(new Public());
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
        addAction(new Permissions());
        addAction(new MetricsCommand());
        addAction(new DynmapCommand());
//        addAction(new SearchArea()); //todo uncomment for SearchArea
        addAction(new MainLayout());
        addAction(new WorldCommand());
        
        HelpCommand helpCommand = new HelpCommand(this);
        
        EmptyCommand emptyHelpQuickGuide = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyHelpQuickGuide.setCommandDescription(textComponent("This command is used to get the quick guide of this plugin", ColorTheme.ColorType.infoColor));
        emptyHelpQuickGuide.setRunnable((args, player) -> executeInternal(player, "quickGuide"));
        helpCommand.addExtraHelp("QuickGuide", emptyHelpQuickGuide);
        
        EmptyCommand emptyHelpDynmap = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyHelpDynmap.setCommandDescription(textComponent("This command is used to get the quick guide of this plugin", ColorTheme.ColorType.infoColor));
        emptyHelpDynmap.setRunnable((args, player) -> executeInternal(player, "dynmap"));
        helpCommand.addExtraHelp("Dynmap", emptyHelpDynmap);
        
        helpCommand.addExtraHelp("PLTP", new Message(textComponent("PLTP stands for PlayerTeleportation, this allows you to teleport to other players. " +
                "You can disable this, to disable use ", ColorTheme.ColorType.infoColor),
                textComponent("/tport PLTP off", ColorTheme.ColorType.varInfoColor),
                textComponent(". When this is off only players in your PLTP whitelist can teleport to you", ColorTheme.ColorType.infoColor)));
        
        Message redirects = new Message();
        redirects.addText(textComponent("A Redirect has the ability to redirect an event/command. For example when a player used the command ", ColorTheme.ColorType.infoColor));
        redirects.addText(textComponent("/tp <player>", ColorTheme.ColorType.varInfoColor));
        redirects.addText(textComponent(" it will stop the command, and it will run the command ", ColorTheme.ColorType.infoColor));
        redirects.addText(textComponent("/tport PLTP tp <player>", ColorTheme.ColorType.varInfoColor));
        redirects.addText(textComponent(" instead", ColorTheme.ColorType.infoColor));
        helpCommand.addExtraHelp("Redirects", redirects);
        
        addAction(helpCommand);
    }
    
    @Override
    public List<String> tabList(String[] args, Player player) {
        ArrayList<String> list = new ArrayList<>(super.tabList(args, player));
        if (!Public.isEnabled()) {
            list.remove("public");
        }
        return list;
    }
    
    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
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
        // tport edit <TPort name> private <statement>
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
        // tport PLTP accept [player...]
        // tport PLTP reject <player>
        // tport PLTP revoke <player>
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
        // tport biomeTP whitelist <biome...>
        // tport biomeTP blacklist <biome...>
        // tport biomeTP preset [preset]
        // tport biomeTP random
        // tport biomeTP searchTries [tries]
        // tport featureTP
        // tport featureTP search <feature> [mode]
        // tport featureTP mode [mode]
        // tport home
        // tport setHome <player> <TPort name>
        // tport colorTheme
        // tport colorTheme set <theme>
        // tport colorTheme set <type> <color>
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
        // tport redirect <redirect> [state]
        // tport tag create <tag> <permission>
        // tport tag delete <tag>
        // tport tag list
        // tport tag reset
        // tport search <type>
        // tport search <type> <query...>
        // tport search <type> <mode> <query...>
        // tport sort [sorter]
        // tport safetyCheck [default state]
        // tport metrics enable [state]
        // tport metrics viewStats
        // tport dynmap
        // tport dynmap enable [state]
        // tport dynmap show <player> [tport name]
        // tport dynmap IP [IP]
        // tport mainLayout players [state]
        // tport mainLayout TPorts [state]
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("You have to be a player to use this command");
            return false;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            if (!empty.hasPermissionToRun(player, true)) {
                return false;
            }
            openMainTPortGUI(player, 0);
        } else {
            if (this.runCommands(args[0], args, player)) {
                CommandCounter.add(args);
            } else {
                sendErrorTheme(player, "%s is not a valid sub-command", args[0]);
            }
        }
        return false;
    }
}
