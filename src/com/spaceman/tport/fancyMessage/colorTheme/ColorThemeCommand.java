package com.spaceman.tport.fancyMessage.colorTheme;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.TextType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spaceman.tport.commandHandler.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class ColorThemeCommand extends SubCommand {
    
    public ColorThemeCommand() {
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("");
        empty.setCommandDescription(formatInfoTranslation("tport.colorTheme.commandDescription"));
        
        EmptyCommand emptySetTypeChat = new EmptyCommand();
        emptySetTypeChat.setCommandName("chat color", ArgumentType.REQUIRED);
        emptySetTypeChat.setCommandDescription(formatInfoTranslation("tport.colorTheme.set.type.chat.commandDescription"));
        EmptyCommand emptySetTypeHex = new EmptyCommand();
        emptySetTypeHex.setCommandName("hex color", ArgumentType.REQUIRED);
        emptySetTypeHex.setCommandDescription(formatInfoTranslation("tport.colorTheme.set.type.hex.commandDescription"));
        EmptyCommand emptySetType = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                if (Arrays.stream(ColorType.values()).anyMatch(type -> type.name().equalsIgnoreCase(argument)))
                    return argument;
                return "";
            }
        };
        emptySetType.setCommandName("type", ArgumentType.REQUIRED);
        emptySetType.setTabRunnable(((args, player) -> {
            if (args[3].startsWith("#")) {
                return args[3].length() < 8 ? Collections.singletonList(args[3] + "#ffffff".substring(args[3].length(), 7)) : Collections.singletonList(args[3].substring(0, 7));
            } else {
                List<String> list = Arrays.stream(ChatColor.values()).map(Enum::name).collect(Collectors.toList());
                list.add("#ffffff");
                list.add("#000000");
                list.add("#");
                return list;
            }
        }));
        emptySetType.setRunnable(((args, player) -> {
            if (args.length != 4) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport colorTheme set <type> <chat color|hex color>");
                return;
            }
            if (!ColorType.getTypes().contains(args[2])) {
                sendErrorTranslation(player, "tport.colorTheme.set.type.colorTypeNotFound", args[2]);
                return;
            }
            if (Arrays.stream(ChatColor.values()).map(ChatColor::name).anyMatch(c -> c.equalsIgnoreCase(args[3]))) { //tport colorTheme set <type> <chat color>
                ColorType.valueOf(args[2]).setColor(player, new MultiColor(ChatColor.valueOf(args[3].toUpperCase())));
                
                Message message = formatTranslation(ColorType.valueOf(args[2]), ColorType.varInfo2Color, "tport.colorTheme.this");
                message.getText().forEach(t -> t.setInsertion(ColorType.valueOf(args[2]).getColor(player).getColorAsValue()));
                sendSuccessTranslation(player, "tport.colorTheme.set.type.chat.succeeded", ColorType.valueOf(args[2]).name(), message);
            } else if (args[3].matches("#[0-9a-fA-F]{6}")) {//tport colorTheme set <type> <hex color>
                ColorType.valueOf(args[2]).setColor(player, new MultiColor(args[3]));
                
                Message message = formatTranslation(ColorType.valueOf(args[2]), ColorType.varInfo2Color, "tport.colorTheme.this");
                message.getText().forEach(t -> t.setInsertion(ColorType.valueOf(args[2]).getColor(player).getColorAsValue()));
                sendSuccessTranslation(player, "tport.colorTheme.set.type.hex.succeeded", ColorType.valueOf(args[2]).name(), message);
            } else {
                sendErrorTranslation(player, "tport.colorTheme.set.type.colorNotFound", args[3]);
            }
        }));
        emptySetType.addAction(emptySetTypeChat);
        emptySetType.addAction(emptySetTypeHex);
        EmptyCommand emptySetTheme = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                if (ColorTheme.getDefaultThemes().contains(argument)) return argument;
                return "";
            }
        };
        emptySetTheme.setCommandName("theme", ArgumentType.REQUIRED);
        emptySetTheme.setCommandDescription(formatInfoTranslation("tport.colorTheme.set.theme.commandDescription"));
        emptySetTheme.setRunnable(((args, player) -> {
            if (args.length == 3) {
                ColorTheme.setDefaultTheme(player, args[2]);
                sendSuccessTranslation(player, "tport.colorTheme.set.theme.succeeded", args[2]);
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport colorTheme set <theme>");
            }
        }));
        EmptyCommand emptySet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptySet.setCommandName("set", ArgumentType.FIXED);
        emptySet.setTabRunnable((args, player) -> Stream.concat(ColorTheme.getDefaultThemes().stream(), ColorType.getTypes().stream()).collect(Collectors.toList()));
        emptySet.setRunnable(((args, player) -> {
            if (args.length > 2 && runCommands(emptySet.getActions(), args[2], args, player)) {
                return;
            }
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport colorTheme set " + convertToArgs(emptySet.getActions(), false));
        }));
        emptySet.addAction(emptySetTheme);
        emptySet.addAction(emptySetType);
        
        EmptyCommand emptyGetType = new EmptyCommand();
        emptyGetType.setCommandName("type", ArgumentType.REQUIRED);
        emptyGetType.setCommandDescription(formatInfoTranslation("tport.colorTheme.get.commandDescription"));
        EmptyCommand emptyGet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyGet.setCommandName("get", ArgumentType.FIXED);
        emptyGet.setTabRunnable((args, player) -> Arrays.stream(ColorTheme.ColorType.values()).map(Enum::name).collect(Collectors.toList()));
        emptyGet.setRunnable(((args, player) -> {
            if (args.length != 3) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport colorTheme get <type>");
                return;
            }
            if (!ColorType.getTypes().contains(args[2])) {
                sendErrorTranslation(player, "tport.colorTheme.get.colorNotFound", args[2]);
                return;
            }
            Message message = formatTranslation(ColorType.valueOf(args[2]), ColorType.varInfo2Color, "tport.colorTheme.this");
            message.getText().forEach(t -> t.setInsertion(ColorType.valueOf(args[2]).getColor(player).getColorAsValue()));
            sendInfoTranslation(player, "tport.colorTheme.get.succeeded", ColorType.valueOf(args[2]).name(), message);
        }));
        emptyGet.addAction(emptyGetType);
        
        addAction(empty);
        addAction(emptySet);
        addAction(emptyGet);
    }
    
    @Override
    public String getName(String arg) {
        return "colorTheme";
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport colorTheme
        //tport colorTheme set <theme>
        //tport colorTheme set <type> <chat color>
        //tport colorTheme set <type> <hex color>
        //tport colorTheme get <type>
        
        if (args.length == 1) {
            ColorTheme colorTheme = ColorTheme.getTheme(player);
            
            TextComponent infoMessage             = textComponent("tport.colorTheme.info",         ColorType.varInfoColor)    .addTextEvent(hoverEvent(colorTheme.getVarInfoColor()    .getColorAsValue(), colorTheme.getInfoColor())).setInsertion(colorTheme.getVarInfoColor()    .getColorAsValue()).setType(TextType.TRANSLATE);
            TextComponent successMessage          = textComponent("tport.colorTheme.success",      ColorType.varSuccessColor) .addTextEvent(hoverEvent(colorTheme.getVarSuccessColor() .getColorAsValue(), colorTheme.getInfoColor())).setInsertion(colorTheme.getVarSuccessColor() .getColorAsValue()).setType(TextType.TRANSLATE);
            TextComponent errorMessage            = textComponent("tport.colorTheme.error",        ColorType.varErrorColor)   .addTextEvent(hoverEvent(colorTheme.getVarErrorColor()   .getColorAsValue(), colorTheme.getInfoColor())).setInsertion(colorTheme.getVarErrorColor()   .getColorAsValue()).setType(TextType.TRANSLATE);
            TextComponent secondaryInfoMessage    = textComponent("tport.colorTheme.secondary",    ColorType.varInfo2Color)   .addTextEvent(hoverEvent(colorTheme.getVarInfo2Color()   .getColorAsValue(), colorTheme.getInfoColor())).setInsertion(colorTheme.getVarInfo2Color()   .getColorAsValue()).setType(TextType.TRANSLATE);
            TextComponent secondarySuccessMessage = textComponent("tport.colorTheme.secondary",    ColorType.varSuccess2Color).addTextEvent(hoverEvent(colorTheme.getVarSuccess2Color().getColorAsValue(), colorTheme.getInfoColor())).setInsertion(colorTheme.getVarSuccess2Color().getColorAsValue()).setType(TextType.TRANSLATE);
            TextComponent secondaryErrorMessage   = textComponent("tport.colorTheme.secondary",    ColorType.varError2Color)  .addTextEvent(hoverEvent(colorTheme.getVarError2Color()  .getColorAsValue(), colorTheme.getInfoColor())).setInsertion(colorTheme.getVarError2Color()  .getColorAsValue()).setType(TextType.TRANSLATE);
            
            TextComponent info = textComponent("tport.colorTheme.succeeded", colorTheme.getInfoColor()).setType(TextType.TRANSLATE).addTranslateWith(infoMessage, secondaryInfoMessage).addTextEvent(hoverEvent(colorTheme.getInfoColor().getColorAsValue(), colorTheme.getInfoColor())).setInsertion(colorTheme.getInfoColor().getColorAsValue());
            TextComponent success = textComponent("tport.colorTheme.succeeded", colorTheme.getSuccessColor()).setType(TextType.TRANSLATE).addTranslateWith(successMessage, secondarySuccessMessage).addTextEvent(hoverEvent(colorTheme.getSuccessColor().getColorAsValue(), colorTheme.getInfoColor())).setInsertion(colorTheme.getSuccessColor().getColorAsValue());
            TextComponent error = textComponent("tport.colorTheme.succeeded", colorTheme.getErrorColor()).setType(TextType.TRANSLATE).addTranslateWith(errorMessage, secondaryErrorMessage).addTextEvent(hoverEvent(colorTheme.getErrorColor().getColorAsValue(), colorTheme.getInfoColor())).setInsertion(colorTheme.getErrorColor().getColorAsValue());
            
            Message message = new Message();
            message.addText(info);
            message.addNewLine();
            message.addText(success);
            message.addNewLine();
            message.addText(error);
            message.sendAndTranslateMessage(player);
        } else {
            if (!runCommands(getActions(), args[1], args, player)) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport colorTheme " + convertToArgs(getActions(), true));
            }
        }
    }
}
