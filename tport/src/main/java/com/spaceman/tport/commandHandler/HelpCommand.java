package com.spaceman.tport.commandHandler;

import com.spaceman.tport.commandHandler.customRunnables.RunRunnable;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class HelpCommand extends SubCommand {
    
    private int listSize = 10;
    private final CommandTemplate template;
    private final Message commandMessage;
    private final List<String> extraHelp = new ArrayList<>();
    
    public HelpCommand(CommandTemplate template, @Nullable Message commandMessage, boolean shortenFirst) {
        this.template = template;
        if (commandMessage == null) commandMessage = formatInfoTranslation("tport.command.help.defaultDescription");
        this.commandMessage = commandMessage;
        
        EmptyCommand commandPage = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                try {
                    Integer.parseInt(argument);
                    return argument;
                } catch (NumberFormatException nfe) {
                    return "";
                }
            }
        };
        commandPage.setCommandName("page", ArgumentType.REQUIRED);
        commandPage.setCommandDescription(formatInfoTranslation("tport.command.help.page.commandDescription"));
        commandPage.setRunnable((args, player) -> {
            
            int page;
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                sendErrorTranslation(player, "tport.command.help.page.notANumber", args[1]);
                return;
            }
            
            HashMap<String, SubCommand> commandMap = template.collectActions();
            List<String> commandArrayList = new ArrayList<>(commandMap.keySet());
            
            int maxPages = commandArrayList.size() / listSize;
            if (commandArrayList.size() % listSize != 0) maxPages++;
            if (page > maxPages) page = maxPages;
            if (page <= 0) page = 1;
            
            int startIndex = (page - 1) * listSize;
            
            ColorTheme theme = ColorTheme.getTheme(player);
            
            Message buttons = new Message();
            if (commandArrayList.size() > listSize) {
                buttons.addText(textComponent(" (", theme.getInfoColor()));
                if (page != 1) {
                    HoverEvent backwardHover = new HoverEvent(textComponent("/" + template.getName() + " help " + (page - 1), theme.getInfoColor()));
                    ClickEvent backwardClick = ClickEvent.runCommand("/" + template.getName() + " help " + (page - 1));
                    buttons.addText(textComponent("<-", theme.getVarInfoColor(), backwardHover, backwardClick));
                }
                
                if (page < maxPages) {
                    if (page != 1) buttons.addText(" ");
                    
                    HoverEvent forwardHover = new HoverEvent(textComponent("/" + template.getName() + " help " + (page + 1), theme.getInfoColor()));
                    ClickEvent forwardClick = ClickEvent.runCommand("/" + template.getName() + " help " + (page + 1));
                    buttons.addText(textComponent("->", theme.getVarInfoColor(), forwardHover, forwardClick));
                }
                buttons.addText(textComponent(")", theme.getInfoColor()));
            }
            
            Message commands = new Message();
            boolean color = true;
            for (int i = startIndex; i < startIndex + listSize && i < commandArrayList.size(); i++) {
                String command = commandArrayList.get(i);
                SubCommand subCommand = commandMap.get(command);
                
                TextComponent commandComponent = commandToComponent(command, subCommand, player, color);
                commands.addText(commandComponent);
                
                commands.addNewLine();
                color = !color;
            }
            commands.removeLast();
            
            sendInfoTranslation(player, "tport.command.help.page.succeeded", "/" + template.getName(), page, buttons, commands);
        });
        
        EmptyCommand commandHelp = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return template.getName();
            }
        };
        commandHelp.setCommandName(template.getName() + " command", ArgumentType.REQUIRED);
        commandHelp.setCommandDescription(formatInfoTranslation("tport.command.help.command.commandDescription"));
        commandHelp.setRunnable((args, player) -> {
            //command help <command...>
            if (shortenFirst && args.length == 2) {
                String command = "/" + StringUtils.join(args, " ", 1, args.length);
                Message commands = new Message();
                boolean color = true;
                
                for (SubCommand subCommand : template.getActions()) {
                    TextComponent commandComponent;
                    if (subCommand.getActions().size() == 0) {
                        commandComponent = commandToComponent("/" + template.getName() + " " + subCommand.getCommandName(), subCommand, player, color);
                    } else {
                        if (subCommand.getCommandDescription().getText().get(0).getText().equals(descriptionNotGiven)) {
                            EmptyCommand descriptionHack = new EmptyCommand();
                            descriptionHack.setCommandDescription(formatInfoTranslation("tport.commandHandler.subCommand.shorten.generalDescription",
                                    "/" + template.getName() + " help " + template.getName() + " " + subCommand.getCommandName()));
                            commandComponent = commandToComponent("/" + template.getName() + " " + subCommand.getCommandName() + " <...>", descriptionHack, player, color);
                        } else {
                            commandComponent = commandToComponent("/" + template.getName() + " " + subCommand.getCommandName() + " [...]", subCommand, player, color);
                        }
                    }
                    commands.addText(commandComponent);
                    commands.addNewLine();
                    color = !color;
                }
                commands.removeLast();
                
                sendInfoTranslation(player, "tport.command.help.command.succeeded", command, commands);
                return;
            }
            
            HashMap<String, SubCommand> commandMap = template.collectActions();
            String command = "/" + StringUtils.join(args, " ", 1, args.length);
            
            Message commands = new Message();
            boolean color = true;
            for (String tmpCommand : commandMap.keySet()) {
                if (tmpCommand.toLowerCase().startsWith(command.toLowerCase()) || command.equalsIgnoreCase(tmpCommand)) {
                    SubCommand subCommand = commandMap.get(tmpCommand);
                    TextComponent commandComponent = commandToComponent(tmpCommand, subCommand, player, color);
                    commands.addText(commandComponent);
                    commands.addNewLine();
                    color = !color;
                }
            }
            commands.removeLast();
            
            sendInfoTranslation(player, "tport.command.help.command.succeeded", command, commands);
        });
        commandHelp.setTabRunnable((args, player) -> {
            String s = StringUtils.join(args, " ", 1, args.length - 1) + " ";
            return template.collectActions().keySet().stream()
                    .map(c -> c.substring(1).toLowerCase())
                    .filter(c -> c.startsWith(s.toLowerCase()))
                    .map(c -> c.replaceFirst("(?i)" + s, ""))
                    .collect(Collectors.toList());
        });
        commandHelp.setLooped(true);
        
        Message finalCommandMessage = commandMessage;
        addAction(new EmptyCommand(){
            @Override
            public String getCommandName() {
                return "";
            }
            
            @Override
            public Message getCommandDescription() {
                return finalCommandMessage;
            }
            
            @Override
            public String getName(String argument) {
                return "";
            }
        });
        addAction(commandPage);
        addAction(commandHelp);
    }
    
    private TextComponent commandToComponent(String command, SubCommand subCommand, Player player, boolean color) {
        TextComponent textComponent = new TextComponent(command);
        textComponent.setColor(color ? varInfoColor : varInfo2Color);
        
        Message hover = new Message();
        hover.addMessage(subCommand.getCommandDescription());
        if (!subCommand.getPermissions().isEmpty()) {
            hover.addNewLine();
            hover.addNewLine();
            hover.addMessage(subCommand.permissionsHover());
            hover.addNewLine();
            if (subCommand.getPermissions().stream().anyMatch(p -> p.contains("<") || p.contains("["))) {
                hover.addMessage(formatErrorTranslation("tport.command.help.page.cantMeasurePerm"));
            } else {
                hover.addMessage(formatInfoTranslation("tport.command.help.page.perm",
                        (subCommand.hasPermissionToRun(player, false) ?
                                formatTranslation(goodColor, goodColor, "tport.command.help.page.do") :
                                formatTranslation(badColor, badColor, "tport.command.help.page.dont")
                        )));
            }
        }
        textComponent.addTextEvent(new HoverEvent(hover)).setInsertion(command);
        return textComponent;
    }
    
    public void addExtraHelp(String helpName, Message message) {
        addExtraHelp(helpName, (args, player) -> message.sendMessage(player));
    }
    
    public void addExtraTranslatedHelp(String helpName, Message message) {
        addExtraHelp(helpName, (args, player) -> message.sendAndTranslateMessage(player));
    }
    
    public void addExtraHelp(String helpName, RunRunnable command) {
        EmptyCommand helpCommand = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return helpName;
            }
        };
        helpCommand.setRunnable(command);
        helpCommand.setCommandDescription(formatInfoTranslation("tport.command.help.extraHelp"));
        addExtraHelp(helpName, helpCommand);
    }
    
    public void addExtraHelp(String helpName, EmptyCommand helpCommand) {
        extraHelp.add(helpName);
        helpCommand.setCommandName(helpName, ArgumentType.FIXED);
        addAction(helpCommand);
    }
    
    public boolean removeExtraHelp(String dataName) {
        extraHelp.remove(dataName);
        return removeAction(dataName) != null;
    }
    
    @Override
    public String getName(String arg) {
        return "help";
    }
    
    public int getListSize() {
        return listSize;
    }
    
    public void setListSize(int listSize) {
        this.listSize = Math.max(1, listSize);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add(template.getName());
        list.addAll(extraHelp);
        int commandSize = template.collectActions().size();
        IntStream.range(0, commandSize / listSize).mapToObj(i -> String.valueOf(i + 1)).forEach(list::add);
        if (commandSize % listSize != 0) {
            list.add(String.valueOf(commandSize / listSize + 1));
        }
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        if (args.length <= 1) {
            if (commandMessage != null) {
                commandMessage.sendAndTranslateMessage(player);
                return;
            }
        } else {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage",
                "/" + template.getName() + " help " + (commandMessage == null ? "<" : "[") + "page|" + template.getName() + " command..." +
                extraHelp.stream().collect(Collectors.joining("|", (extraHelp.size() == 0 ? "" : "|"), "")) + (commandMessage == null ? ">" : "]"));
    }
}
