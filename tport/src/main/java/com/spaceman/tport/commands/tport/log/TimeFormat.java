package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fileHander.Files.tportData;

public class TimeFormat extends SubCommand {
    
    public TimeFormat() {
        EmptyCommand emptyFormat = new EmptyCommand();
        emptyFormat.setCommandName("format...", ArgumentType.OPTIONAL);
        emptyFormat.setCommandDescription(formatInfoTranslation("tport.command.log.timeFormat.format.commandDescription"));
        addAction(emptyFormat);
        
        setCommandDescription(formatInfoTranslation("tport.command.log.timeFormat.commandDescription"));
    }
    
    public static String getFormatExample(Player player, String timeFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        sdf.setTimeZone(java.util.TimeZone.getTimeZone(
                tportData.getConfig().getString("tport." + player.getUniqueId() + ".timeZone", TimeZone.getDefault().getID())));
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    public static String getTimeFormat(Player player) {
        return tportData.getConfig().getString("tport." + player.getUniqueId() + ".timeFormat", defaultTimeFormat);
    }
    public static void setTimeFormat(Player player, String format) {
        tportData.getConfig().set("tport." + player.getUniqueId() + ".timeFormat", format);
        tportData.saveConfig();
    }
    
    public static final String defaultTimeFormat = "EEE MMM dd HH:mm:ss zzz yyyy";
    
    @Override
    public void run(String[] args, Player player) {
        // tport log timeFormat [format...]
        
        if (args.length == 2) {
            String timeFormat = getTimeFormat(player);
            
            Message here = new Message();
            here.addText(textComponent("tport.command.log.timeFormat.here", varInfoColor, ClickEvent.runCommand("/tport log timeFormat " + defaultTimeFormat),
                    new HoverEvent(textComponent("/tport log timeFormat " + defaultTimeFormat, ColorType.infoColor))).setType(TextType.TRANSLATE));
            
            sendInfoTranslation(player, "tport.command.log.timeFormat.succeeded", timeFormat, getFormatExample(player, timeFormat), here);
        } else if (args.length > 2) {
            StringBuilder str = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                str.append(args[i]).append(" ");
            }
            String format = str.toString().trim();
            try {
                new SimpleDateFormat(format);
            } catch (IllegalArgumentException iae) {
                Message here = new Message();
                here.addText(textComponent("tport.command.log.timeFormat.format.here", varInfoColor,
                        ClickEvent.openUrl("https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html"),
                        new HoverEvent(textComponent("https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html", ColorType.infoColor))
                ).setType(TextType.TRANSLATE));
                
                sendErrorTranslation(player, "tport.command.log.timeFormat.format.invalidFormat", here);
                return;
            }
            setTimeFormat(player, format);
            
            sendSuccessTranslation(player, "tport.command.log.timeFormat.format.succeeded", format, getFormatExample(player, format));
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport log timeFormat [format...]");
        }
    }
}
