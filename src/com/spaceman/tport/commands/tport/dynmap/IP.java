package com.spaceman.tport.commands.tport.dynmap;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.DynmapCommand;
import com.spaceman.tport.dynmap.DynmapHandler;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.openUrl;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public class IP extends SubCommand {
    
    private final EmptyCommand emptyIP;
    
    @Override
    public String getName(String arg) {
        return this.getClass().getSimpleName();
    }
    
    public IP() {
        emptyIP = new EmptyCommand();
        emptyIP.setCommandName("IP", ArgumentType.OPTIONAL);
        emptyIP.setCommandDescription(formatInfoTranslation("tport.command.dynmapCommand.ip.ip.commandDescription",
                formatTranslation(varInfoColor, varInfo2Color, "tport.command.dynmapCommand.searchAsText"),
                "http://0.0.0.0:PORT/", "http://example.com/"));
        emptyIP.setPermissions("TPort.dynmap.ip", "TPort.admin.dynmap");
        addAction(emptyIP);
        
        setCommandDescription(formatInfoTranslation("tport.command.dynmapCommand.ip.commandDescription",
                formatTranslation(varInfoColor, varInfo2Color, "tport.command.dynmapCommand.searchAsText")));
    }
    
    public static String getIP() {
        String setIP = tportConfig.getConfig().getString("dynmap.ip", null);
        if (setIP == null) {
            try {
                URL url = new URL("http://checkip.amazonaws.com");
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                setIP = "http://" +  in.readLine() + ":8123/";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return setIP;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport dynmap IP [IP]
        
        if (!DynmapHandler.isEnabled()) {
            DynmapCommand.sendDisableError(player);
            return;
        }
        
        if (args.length == 2) {
            if (tportConfig.getConfig().contains("dynmap.ip")) {
                String ip = tportConfig.getConfig().getString("dynmap.ip");
                
                sendInfoTranslation(player, "tport.command.dynmapCommand.ip.succeeded",
                        textComponent(ip, varInfoColor, hoverEvent(textComponent(ip, infoColor)), openUrl(ip)).setInsertion(ip));
            } else {
                String autoIP = getIP();
                if (autoIP == null) {
                    sendErrorTranslation(player, "tport.command.dynmapCommand.ip.ipNotSet");
                    return;
                }
                
                sendInfoTranslation(player, "tport.command.dynmapCommand.ip.automaticSucceeded",
                        textComponent(autoIP, varInfoColor, hoverEvent(textComponent(autoIP, infoColor)), openUrl(autoIP)).setInsertion(autoIP),
                        "/tport dynmap IP <IP>", varInfoColor);
            }
        } else if (args.length == 3) {
            if (!emptyIP.hasPermissionToRun(player, true)) {
                return;
            }
            tportConfig.getConfig().set("dynmap.ip", args[2]);
            tportConfig.saveConfig();
            sendSuccessTranslation(player, "tport.command.dynmapCommand.ip.ip.succeeded", args[2]);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport dynmap IP [IP]");
        }
    }
}
