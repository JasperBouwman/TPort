package com.spaceman.tport.commands;

import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.TPortInventories.openMainTPortGUI;

public class TPort implements CommandExecutor {

    private List<CmdHandler> actions = new ArrayList<>();
    public static Open open;
    public static PLTP pltp;

    public TPort() {
        open = new Open();
        pltp = new PLTP();

        actions.add(new Add());
        actions.add(new Edit());
        actions.add(pltp);
        actions.add(new Help());
        actions.add(open);
        actions.add(new Remove());
        actions.add(new RemovePlayer());
        actions.add(new Compass());
        actions.add(new Own());
        actions.add(new Back());
        actions.add(new BiomeTP());
        actions.add(new FeatureTP());
        actions.add(new Reload());
    }

    public static ItemStack getHead(UUID uuid) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        meta.setDisplayName(PlayerUUID.getPlayerName(uuid.toString()));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
        meta.setDisplayName(player.getName());
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] strings) {

        // tport add <TPort name> [lore of TPort]
        // tport compass [player] [TPort name]
        // tport edit <TPort name> lore set <lore>
        // tport edit <TPort name> lore remove
        // tport edit <TPort name> name <new TPort name>
        // tport edit <TPort name> item
        // tport edit <TPort name> location
        // tport edit <TPort name> private
        // tport edit <TPort name> private [true:false]
        // tport edit <TPort name> whitelist <add:remove> <players names...>
        // tport edit <TPort name> whitelist list
        // tport PLTP tp [on:off]
        // tport PLTP whitelist list
        // tport PLTP whitelist <add:remove> <playername>
        // tport help
        // tport open <playername> [TPort name]
        // tport own [TPort name]
        // tport remove <TPort name>
        // tport removePlayer <playerName>
        // tport back
        // tport biomeTP
        // tport biomeTP [biome]
        // tport featureTP
        // tport featureTP [featureType]
        // tport reload

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You have to be a player to use this command");
            return false;
        }

        Player player = (Player) commandSender;

        if (strings.length == 0) {
            openMainTPortGUI(player, 0);
        } else {

            for (CmdHandler action : this.actions) {
                if (strings[0].equalsIgnoreCase(action.getClass().getSimpleName())) {
                    action.run(strings, player);
                    return false;
                }
            }
            player.sendMessage(ChatColor.RED + "This is not a sub-command");

        }

        return false;
    }
}
