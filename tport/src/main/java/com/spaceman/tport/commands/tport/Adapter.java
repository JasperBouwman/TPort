package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.adapters.ReflectionManager;
import com.spaceman.tport.adapters.TPortAdapter;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public class Adapter extends SubCommand {
    
    public static HashMap<String/*adapter name*/, String/*adapter path*/> adapters = new HashMap<>();
    public static final String automatic = "automatic";
    
    public static boolean registerAdapter(String name, String path) {
        if (name.equalsIgnoreCase(automatic)) {
            throw new IllegalArgumentException("Adapter name can not be 'automatic'");
        }
        name = name.toLowerCase();
        try {
            Class.forName(path);
        } catch (ClassNotFoundException cnfe) {
            Main.getInstance().getLogger().log(Level.WARNING, "Adapter " + name + " was not found");
            return false;
        } catch (Throwable ignored) {
            // adapter won't load, most likely not supported
        }
        
        if (!adapters.containsKey(name)) {
            adapters.put(name, path);
            return true;
        }
        return false;
    }
    
    private static TPortAdapter initAdapter(String path) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (path == null) throw new ClassNotFoundException();
        return (TPortAdapter) Class.forName(path).getConstructor().newInstance();
    }
    
    public static String getSelectedAdapter() {
        return tportConfig.getConfig().getString("tport.adapter", automatic).toLowerCase();
    }
    public static String getLoadedAdapter() {
        return Main.getInstance().adapter.getAdapterName();
    }
    
    private static TPortAdapter loadAdaptive() {
        TPortAdapter adaptive = null;
        try {
            adaptive = initAdapter(adapters.get("adaptive"));
        } catch (Throwable e) {
            //this was the last line of defence, throw error in console and suggest updating TPort
        }
        return adaptive;
    }
    
    public static void loadAdapter(@Nullable Player player) {
        String adapterName = tportConfig.getConfig().getString("tport.adapter", automatic).toLowerCase();
        String serverVersion = ReflectionManager.getServerVersion();
        if (adapterName.equals(automatic)) {
            adapterName = serverVersion.toLowerCase();
        }
        
        TPortAdapter adapter = null;
        try {
            adapter = initAdapter(adapters.get(adapterName));
        } catch (ClassNotFoundException cnfe) {
            if (player == null) {
                Main.getInstance().getLogger().log(Level.WARNING, "Adapter '" + adapterName + "' was not found, attempting to load 'adaptive'");
            } else {
                sendErrorTranslation(player, "tport.command.adapter.adapter.notFound", adapterName, "adaptive");
            }
        } catch (Throwable e) {
            if (player == null) {
                Main.getInstance().getLogger().log(Level.WARNING, "Adapter '" + adapterName + "' is not loading on this server, attempting to load 'adaptive'");
            } else {
                sendErrorTranslation(player, "tport.command.adapter.adapter.notLoading", adapterName, "adaptive");
            }
        }
        
        if (adapter == null) {
            adapter = loadAdaptive();
        }
        
        Main.getInstance().adapter = adapter;
        
        if (player == null) {
            Main.getInstance().getLogger().log(Level.INFO, "Successfully loaded adapter '" + Main.getInstance().adapter.getAdapterName() + "'");
        } else {
            sendSuccessTranslation(player, "tport.command.adapter.adapter.loaded", Main.getInstance().adapter.getAdapterName());
        }
    }
    
    private final EmptyCommand emptyAdapter;
    public Adapter() {
        emptyAdapter = new EmptyCommand();
        emptyAdapter.setCommandName("adapter", ArgumentType.OPTIONAL);
        emptyAdapter.setCommandDescription(formatInfoTranslation("tport.command.adapter.adapter.commandDescription"));
        emptyAdapter.setPermissions("TPort.adapter", "TPort.admin");
        addAction(emptyAdapter);
        
        this.setCommandDescription(formatInfoTranslation("tport.command.adapter.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>(adapters.keySet());
        list.add(automatic);
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport adapter
        //tport adapter <adapter>
        
        if (args.length == 1) {
            String setTo = tportConfig.getConfig().getString("tport.adapter", automatic).toLowerCase();
            sendInfoTranslation(player, "tport.command.adapter.setTo", setTo);
            sendInfoTranslation(player, "tport.command.adapter.name", Main.getInstance().adapter.getAdapterName());
        }
        else if (args.length == 2) {
            if (!emptyAdapter.hasPermissionToRun(player, true)) {
                return;
            }
            if (adapters.containsKey(args[1].toLowerCase()) || automatic.equalsIgnoreCase(args[1])) {
                tportConfig.getConfig().set("tport.adapter", args[1]);
                tportConfig.saveConfig();
                loadAdapter(player);
            } else {
                sendErrorTranslation(player, "tport.command.adapter.adapter.adapterNotExist", args[1].toLowerCase());
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport adapter [adapter]");
        }
    }
}
