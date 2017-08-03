package com.spaceman.tport;

import com.spaceman.tport.commandStuff.CommandHandler;
import com.spaceman.tport.commands.tport.TPort;
import com.spaceman.tport.commands.tport.actions.*;
import com.spaceman.tport.events.InventoryClick;
import com.spaceman.tport.events.JoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public void onEnable() {

        // For convenience' sake, we will initialize a variable.
        CommandHandler handler = new CommandHandler();

        // Registers the command /tport which has no arguments.
        handler.register("tport", new TPort(this));

        // Registers the command /tport args based on args[0] (args)
        handler.register("add", new Add(this));
        handler.register("remove", new Remove(this));
        handler.register("edit", new Edit(this));
        handler.register("help", new Help());
        handler.register("whitelist", new Whitelist(this));
        handler.register("open", new Open(this));
        handler.register("extra", new Extra(this));
        getCommand("tport").setExecutor(handler);
        getCommand("tport").setTabCompleter(new TabComplete(this));

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(this), this);
        pm.registerEvents(new JoinEvent(this), this);

        if (!getConfig().contains("tport")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                JoinEvent.setData(player, this);
            }

        }
    }
}
