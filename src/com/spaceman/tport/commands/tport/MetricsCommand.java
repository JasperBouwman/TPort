package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.openUrl;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class MetricsCommand extends SubCommand {
    
    public MetricsCommand() {
        EmptyCommand emptyViewStats = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyViewStats.setCommandName("viewStats", ArgumentType.FIXED);
        emptyViewStats.setCommandDescription(formatInfoTranslation("tport.command.metricsCommand.viewStats.commandDescription"));
        
        addAction(emptyViewStats);
    }
    
    @Override
    public String getName(String arg) {
        return "metrics";
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport metrics viewStats
        
        if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("viewStats"))) {
            if (!Features.Feature.Metrics.isEnabled()) {
                sendErrorTranslation(player, "tport.command.metricsCommand.viewStats.notEnabled", "/tport features Metrics state true");
                return;
            }
            Message hereMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.metricsCommand.here");
            hereMessage.getText().forEach(t -> t
                    .setInsertion("https://bstats.org/plugin/bukkit/TPort/8061")
                    .addTextEvent(hoverEvent("https://bstats.org/plugin/bukkit/TPort/8061"))
                    .addTextEvent(openUrl("https://bstats.org/plugin/bukkit/TPort/8061")));
    
            sendInfoTranslation(player, "tport.command.metricsCommand.viewStats.succeeded",
                    hereMessage,
                    textComponent("bStats", varInfoColor)
                            .setInsertion("https://bstats.org/")
                            .addTextEvent(hoverEvent("https://bstats.org/"))
                            .addTextEvent(openUrl("https://bstats.org/")));
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport metrics viewStats");
        }
    }
}
