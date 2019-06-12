package com.spaceman.tport.commands;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.HeadCommand;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.NumberConversions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static com.spaceman.tport.Permissions.hasPermission;
import static com.spaceman.tport.TPortInventories.openMainTPortGUI;

public class TPort extends HeadCommand {

    public static Open open;
    public static PLTP pltp;

    public TPort() {
        open = new Open();
        pltp = new PLTP();

        addAction(new Add());
        addAction(new Edit());
        addAction(pltp);
        addAction(new Help());
        addAction(open);
        addAction(new Remove());
        addAction(new RemovePlayer());
        addAction(new Compass());
        addAction(new Own());
        addAction(new Back());
        addAction(new BiomeTP());
        addAction(new FeatureTP());
        addAction(new Reload());
        addAction(new Cooldown());
        addAction(new Log());
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
    public List<String> tabList(String[] args, Player player) {
        ArrayList<String> list = new ArrayList<>(super.tabList(args, player));

        if (!Permissions.hasPermission(player, "TPort.admin.reload", false)) {
            list.remove("reload");
        }
        if (!Permissions.hasPermission(player, "TPort.admin.removePlayer", false)) {
            list.remove("removePlayer");
        }
        if (!hasPermission(player, "tport.command.cooldown")) {
            list.remove("cooldown");
        }
        return list;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] strings) {

        // tport
        // tport add <TPort name> [lore of TPort]
        // tport compass [player] [TPort name]
        // tport edit <TPort name> lore set <lore>
        // tport edit <TPort name> lore remove
        // tport edit <TPort name> name <new TPort name>
        // tport edit <TPort name> item
        // tport edit <TPort name> location
        // tport edit <TPort name> private
        // tport edit <TPort name> private [on:off:online]
        // tport edit <TPort name> whitelist <add:remove> <players names...>
        // tport edit <TPort name> whitelist list
        // tport edit <TPort name> move <slot>
        // tport PLTP [on:off]
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
        // tport cooldown <cooldown> [value]

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You have to be a player to use this command");
            return false;
        }

        Player player = (Player) commandSender;

        if (strings.length == 0) {
            if (!Permissions.hasPermission(player, "TPort.open", false)) {
                if (!Permissions.hasPermission(player, "TPort.basic", false)) {
                    Permissions.sendNoPermMessage(player, "TPort.open", "TPort.basic");
                    return false;
                }
            }
            openMainTPortGUI(player, 0);
        } else {

            if (!this.runCommands(strings[0], strings, player)) {
                player.sendMessage(ChatColor.RED + "This is not a sub-command");
            }

        }

        return false;
    }
}
