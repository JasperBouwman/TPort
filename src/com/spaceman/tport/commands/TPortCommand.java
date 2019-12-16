package com.spaceman.tport.commands;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.colorFormatter.ColorThemeCommand;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.CommandTemplate;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.HelpCommand;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.TPortInventories.openMainTPortGUI;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class TPortCommand extends CommandTemplate {
    
    public TPortCommand() {
        super(true, new CommandDescription("TPort", "The command to teleport to saved locations and more", "/TPort <args...>"));
    }
    
    public static ItemStack getHead(UUID uuid) {
        return getHead(Bukkit.getOfflinePlayer(uuid));
    }
    
    public static ItemStack getHead(OfflinePlayer player) {
        if (player == null) {
            return null;
        }
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) {
            meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(item.getType());
        }
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName(ChatColor.YELLOW + player.getName());
            item.setItemMeta(meta);
        }
        return item;
    }
    
    @Override
    public void registerActions() {
        EmptyCommand empty = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(textComponent("This command is used to open the main TPort gui with all the player to choose from", ColorTheme.ColorType.infoColor),
                textComponent("\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.open", ColorTheme.ColorType.varInfoColor));
        addAction(empty);
        addAction(new Open());
        addAction(new Add());
        addAction(new Remove());
        addAction(new Edit());
        addAction(new PLTP());
        addAction(new Compass());
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
        HelpCommand helpCommand = new HelpCommand(this);
        helpCommand.addExtraHelp("PLTP", new Message(textComponent("PLTP stands for PlayerTeleportation, this allows you to teleport to other players. " +
                "You can disable this, to disable use ", ColorTheme.ColorType.infoColor),
                textComponent("/tport PLTP off", ColorTheme.ColorType.varInfoColor),
                textComponent(". When this is off only players in your PLTP whitelist can teleport to you", ColorTheme.ColorType.infoColor)));
        
        addAction(helpCommand);
    }
    
    @Override
    public List<String> tabList(String[] args, Player player) {
        ArrayList<String> list = new ArrayList<>(super.tabList(args, player));
        
        if (!hasPermission(player, false, "TPort.admin.reload")) {
            list.remove("reload");
        }
        if (!hasPermission(player, false, "TPort.admin.removePlayer")) {
            list.remove("removePlayer");
        }
        if (!Public.isEnabled()) {
            list.remove("public");
        }
        return list;
    }
    
    public static boolean executeInternal(CommandSender sender, String args) {
        return executeInternal(sender, args.split(" "));
    }
    
    public static boolean executeInternal(CommandSender sender, String[] args) {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            
            commandMap.getCommand("TPort").execute(sender, "", args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
        // tport
        // tport open <player> [TPort name]
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
        // tport PLTP state [state]
        // tport PLTP consent [state]
        // tport PLTP accept [player...]
        // tport PLTP reject <player>
        // tport PLTP revoke <player>
        // tport PLTP whitelist list
        // tport PLTP whitelist <add|remove> <player...>
        // tport PLTP tp <player>
        // tport compass <type> [data...]
        // tport own [TPort name]
        // tport back
        // tport biomeTP
        // tport biomeTP [biome]
        // tport featureTP
        // tport featureTP [featureType]
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
        // tport log notify <TPort name> [state]
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
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("You have to be a player to use this command");
            return false;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            if (!hasPermission(player, true, true, "TPort.open", "TPort.basic")) {
                return false;
            }
            openMainTPortGUI(player, 0);
        } else {
            if (!this.runCommands(args[0], args, player)) {
                sendErrorTheme(player, "%s is not a valid sub-command", args[0]);
            }
        }
        return false;
    }
}
