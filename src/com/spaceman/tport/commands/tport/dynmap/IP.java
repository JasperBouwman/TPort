package com.spaceman.tport.commands.tport.dynmap;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.openUrl;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class IP extends SubCommand {
    
    private final EmptyCommand emptyIP;
    
    @Override
    public String getName(String arg) {
        return this.getClass().getSimpleName();
    }
    
    public IP() {
        emptyIP = new EmptyCommand();
        emptyIP.setCommandName("IP", ArgumentType.OPTIONAL);
        emptyIP.setCommandDescription(textComponent("This command is used to set your IP prefix for the '", infoColor),
                textComponent("show", varInfoColor),
                textComponent("' command, it should look like: ", infoColor),
                textComponent("http://0.0.0.0:PORT/", varInfoColor),
                textComponent("' or '", infoColor),
                textComponent("http://YourWebsite.org/", varInfoColor),
                textComponent("'", infoColor));
        emptyIP.setPermissions("TPort.dynmap.ip", "TPort.admin.dynmap");
        addAction(emptyIP);
    }
    
    public static String getIP() {
        String setIP = getFile("TPortConfig").getConfig().getString("dynmap.ip", null);
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
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to see what your set IP prefix is for the '", infoColor),
                textComponent("show", varInfoColor),
                textComponent("' command", infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport dynmap IP [IP]
        
        if (!DynmapHandler.isEnabled()) {
            DynmapHandler.sendDisableError(player);
            return;
        }
        
        if (args.length == 2) {
            Files tportConfig = getFile("TPortConfig");
            if (tportConfig.getConfig().contains("dynmap.ip")) {
                String ip = tportConfig.getConfig().getString("dynmap.ip");
                
                Message message = new Message();
                message.addText(textComponent("Dynmap IP is set to ", infoColor));
                message.addText(textComponent(ip, varInfoColor, hoverEvent(textComponent(ip, infoColor)), openUrl(ip)));
                message.sendMessage(player);
            } else {
                String autoIP = getIP();
                if (autoIP != null) {
                    Message message = new Message();
                    message.addText(textComponent("Dynmap IP is set to ", infoColor));
                    message.addText(textComponent(autoIP, varInfoColor, hoverEvent(textComponent(autoIP, infoColor)), openUrl(autoIP)));
                    message.addText(textComponent(". This IP is set automatically and does not have to work, best is you set it manually using ", infoColor));
                    message.addText(textComponent("/tport dynmap IP <IP>", varInfoColor));
                    message.sendMessage(player);
                } else {
                    sendErrorTheme(player, "Dynmap IP is not set");
                }
            }
        } else if (args.length == 3) {
            if (!emptyIP.hasPermissionToRun(player, true)) {
                return;
            }
            Files tportConfig = getFile("TPortConfig");
            tportConfig.getConfig().set("dynmap.ip", args[2]);
            tportConfig.saveConfig();
            sendSuccessTheme(player, "Successfully set Dynmap IP to %s", args[2]);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport dynmap IP [IP]");
        }
    }
}
