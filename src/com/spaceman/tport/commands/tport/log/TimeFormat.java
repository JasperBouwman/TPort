package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class TimeFormat extends SubCommand {
    
    public TimeFormat() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("format...", ArgumentType.OPTIONAL);
        emptyCommand.setCommandDescription(textComponent("This command is used to set the time format, the time format is used for the read command", ColorType.infoColor));
        addAction(emptyCommand);
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the time format, the time format is used for the read command", ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport log timeFormat [format...]
        
        if (args.length == 2) {
            sendInfoTheme(player, "Your time format is %s", getFile("TPortData").getConfig().getString("tport." + player.getUniqueId() + ".timeFormat", "EEE MMM dd HH:mm:ss zzz yyyy"));
            Message message = new Message();
            message.addText(textComponent("To reset click ", ColorType.infoColor));
            message.addText(textComponent("here", ColorType.varInfoColor,
                    ClickEvent.runCommand("/tport log timeFormat EEE MMM dd HH:mm:ss zzz yyyy"),
                    new HoverEvent(textComponent("/tport log timeFormat EEE MMM dd HH:mm:ss zzz yyyy", ColorType.infoColor))));
            message.sendMessage(player);
        } else if (args.length > 2) {
            StringBuilder str = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                str.append(args[i]).append(" ");
            }
            String format = str.toString().trim();
            try {
                new SimpleDateFormat(format);
            } catch (IllegalArgumentException iae) {
                Message message = new Message();
                message.addText(textComponent("Given format is invalid, for help click ", ColorType.infoColor));
                message.addText(textComponent("here", ColorType.varInfoColor,
                        ClickEvent.openUrl("https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html"),
                        new HoverEvent(textComponent("https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html", ColorType.infoColor))));
                message.sendMessage(player);
                return;
            }
            Files tportData = getFile("TPortData");
            tportData.getConfig().set("tport." + player.getUniqueId() + ".timeFormat", format);
            tportData.saveConfig();
            
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Calendar calendar = Calendar.getInstance();
            sdf.setTimeZone(
                    TimeZone.getTimeZone(getFile("TPortData").getConfig().getString("tport." + player.getUniqueId() + ".timeZone", java.util.TimeZone.getDefault().getID())));
            
            Message message = new Message();
            message.addText(textComponent("Successfully set time format to ", ColorType.successColor));
            message.addText(textComponent(format, ColorType.varSuccessColor, new HoverEvent(
                    textComponent("Example: ", ColorType.infoColor),
                    textComponent(sdf.format(calendar.getTime()), ColorType.infoColor))));
            message.sendMessage(player);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport log timeFormat [format...]");
        }
    }
}
