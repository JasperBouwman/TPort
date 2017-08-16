package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class JoinEvent implements Listener {

    private Main p;

    public JoinEvent(Main instance) {
        p = instance;
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void Join(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        setData(player, p);

    }

    public static void setData(Player player, Main p) {
        if (!p.getConfig().contains("tport")) {
            ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(player.getName());
            meta.setDisplayName(player.getName());
            item.setItemMeta(meta);
            ArrayList<String> list = new ArrayList<>();
            p.getConfig().set("tport." + player.getName() + ".gui", "-1");
            p.getConfig().set("tport." + player.getName() + ".tp.statement", "on");
            p.getConfig().set("tport." + player.getName() + ".tp.players", list);
            p.getConfig().set("tport." + player.getName() + ".item", item);
            p.saveConfig();
        } else if (!p.getConfig().contains("tport." + player.getName())) {
            ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(player.getName());
            meta.setDisplayName(player.getName());
            item.setItemMeta(meta);
            ArrayList<String> list = new ArrayList<>();
            p.getConfig().set("tport." + player.getName() + ".gui", "-1");
            p.getConfig().set("tport." + player.getName() + ".tp.statement", "on");
            p.getConfig().set("tport." + player.getName() + ".tp.players", list);
            p.getConfig().set("tport." + player.getName() + ".item", item);
            p.saveConfig();
        }
    }
}