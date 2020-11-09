package com.spaceman.tport.commandHander;

import com.spaceman.tport.commandHander.customRunnables.RunRunnable;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class HelpCommand extends SubCommand {
    
    private int listSize = 10;
    private final CommandTemplate template;
    private final Message commandMessage;
    private final List<String> extraHelp = new ArrayList<>();
    
    public HelpCommand(CommandTemplate template) {
        this(template, new Message(textComponent("This command is used to get all the help you need for this command", infoColor)));
        template.getPlugin().getLogger().info("No help command description given, using default one for " + template.getName());
    }
    
    public HelpCommand(CommandTemplate template, Message commandMessage) {
        this.template = template;
        this.commandMessage = commandMessage;
        
        EmptyCommand commandList = new EmptyCommand() {
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
        commandList.setCommandName("page", ArgumentType.REQUIRED);
        commandList.setCommandDescription(textComponent("This command is used to get the help page", infoColor));
        commandList.setRunnable((args, player) -> {
            
            int startIndex;
            try {
                startIndex = (Integer.parseInt(args[1]) - 1) * listSize;
            } catch (NumberFormatException nfe) {
                //player.sendMessage(formatError(args[1] + " is not a number"));
                sendErrorTheme(player, "%s is not a number", args[1]);
                return;
            }
            
            Message message = new Message();
            HashMap<String, SubCommand> commandMap = template.collectActions();
            
            if (startIndex > commandMap.size()) {
                startIndex = (commandMap.size() / listSize) * listSize;
            }
            if (startIndex < 0) {
                startIndex = 0;
            }
            
            ColorTheme theme = ColorTheme.getTheme(player);
            
            message.addText(textComponent("/" + template.getName(), theme.getVarInfoColor()));
            message.addText(textComponent(" help page ", theme.getInfoColor()));
            int page = startIndex / 10 + 1;
            message.addText(textComponent(String.valueOf(page), theme.getVarInfoColor()));
            
            List<String> commandArrayList = new ArrayList<>(commandMap.keySet());
            if (commandArrayList.size() > listSize) {
                
                message.addText(textComponent(" (", theme.getInfoColor()));
                HoverEvent forwardHover = new HoverEvent(textComponent("/" + template.getName() + " help " + (page + 1), theme.getInfoColor()));
                ClickEvent forwardClick = ClickEvent.runCommand("/" + template.getName() + " help " + (page + 1));
                HoverEvent backwardHover = new HoverEvent(textComponent("/" + template.getName() + " help " + (page - 1), theme.getInfoColor()));
                ClickEvent backwardClick = ClickEvent.runCommand("/" + template.getName() + " help " + (page - 1));
                if (page != 1) {
                    message.addText(textComponent("<-", theme.getVarInfoColor(), backwardHover, backwardClick));
                }
                if (tabList(player, new String[]{}).contains(String.valueOf(page + 1))) {
                    if (page != 1) message.addText(" ");
                    message.addText(textComponent("->", theme.getVarInfoColor(), forwardHover, forwardClick));
                }
                message.addText(textComponent(")", theme.getInfoColor()));
            }
            message.addText(":\n", theme.getVarInfoColor());
            
            boolean color = true;
            
            for (int i = startIndex; i < startIndex + listSize && i < commandArrayList.size(); i++) {
                String command = commandArrayList.get(i);
                SubCommand subCommand = commandMap.get(command);
                Message description = subCommand.getCommandDescription();
    
                if (!subCommand.getPermissions().isEmpty()) {
                    description.addText("\n\n");
                    description.addMessage(subCommand.permissionsHover());
                    if (subCommand.getPermissions().stream().anyMatch(p -> p.contains("<") || p.contains("["))) {
                        description.addText(textComponent("\nCan't measure if you have permission or not (variable not known)", infoColor));
                    } else {
                        description.addText(textComponent("\nYou ", infoColor));
                        if (subCommand.hasPermissionToRun(player, false)) {
                            description.addText(textComponent("do", varInfoColor));
                        } else {
                            description.addText(textComponent("don't", varInfoColor));
                        }
                        description.addText(textComponent(" have permission to use this command", infoColor));
                    }
                }
                
                TextComponent commandComponent = commandToComponent(command, description, color);
                message.addText(commandComponent);
                
                message.addText("\n");
                color = !color;
            }
            message.removeLast();
            
            message.sendMessage(player);
        });
        
        EmptyCommand commandHelp = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return template.getName();
            }
        };
        commandHelp.setCommandName(template.getName() + " command", ArgumentType.REQUIRED);
        commandHelp.setCommandDescription(textComponent("This command is used to get the help of the specified command", infoColor));
        commandHelp.setRunnable((args, player) -> {
            HashMap<String, SubCommand> commandMap = template.collectActions();
            String command = "/" + StringUtils.join(args, " ", 1, args.length);
            
            ColorTheme theme = ColorTheme.getTheme(player);
            Message message = new Message();
            
            message.addText(textComponent("Results for ", theme.getInfoColor()));
            message.addText(textComponent(command, theme.getVarInfoColor()));
            message.addText(":\n", theme.getInfoColor());
            message.addText("");
            
            boolean color = true;
            
            for (String tmpCommand : commandMap.keySet()) {
                if (tmpCommand.toLowerCase().startsWith(command.toLowerCase()) || command.equalsIgnoreCase(tmpCommand)) {
                    SubCommand subCommand = commandMap.get(tmpCommand);
                    Message description = subCommand.getCommandDescription();
    
                    if (!subCommand.getPermissions().isEmpty()) {
                        description.addText("\n\n");
                        description.addMessage(subCommand.permissionsHover());
                        if (subCommand.getPermissions().stream().anyMatch(p -> p.contains("<") || p.contains("["))) {
                            description.addText(textComponent("\nCan't measure if you have permission or not (variable not known)", infoColor));
                        } else {
                            description.addText(textComponent("\nYou ", infoColor));
                            if (subCommand.hasPermissionToRun(player, false)) {
                                description.addText(textComponent("do", varInfoColor));
                            } else {
                                description.addText(textComponent("don't", varInfoColor));
                            }
                            description.addText(textComponent(" have permission to use this command", infoColor));
                        }
                    }
                    
                    TextComponent commandComponent = commandToComponent(tmpCommand, description, color);
                    message.addText(commandComponent);
                    
                    message.addText("\n");
                    color = !color;
                }
            }
            message.removeLast();
            message.sendMessage(player);
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
        
        if (commandMessage != null) {
            addAction(new EmptyCommand(){
                @Override
                public String getCommandName() {
                    return "";
                }
    
                @Override
                public Message getCommandDescription() {
                    return commandMessage;
                }
    
                @Override
                public String getName(String argument) {
                    return "";
                }
            });
        }
        addAction(commandList);
        addAction(commandHelp);
    }
    
    private TextComponent commandToComponent(String command, Message message, boolean color) {
        TextComponent textComponent = new TextComponent(command);
        textComponent.setColor(color ? varInfoColor.name() : varInfo2Color.name());
        HoverEvent hEvent = new HoverEvent();
        hEvent.addMessage(message);
        textComponent.addTextEvent(hEvent);
        return textComponent;
    }
    
    public void addExtraHelp(String helpName, Message message) {
        addExtraHelp(helpName, (args, player) -> message.sendMessage(player));
    }
    
    public void addExtraHelp(String helpName, RunRunnable command) {
        EmptyCommand helpCommand = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return helpName;
            }
        };
        helpCommand.setRunnable(command);
        helpCommand.setCommandDescription(textComponent("To get additional information about this topic", infoColor));
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
                commandMessage.sendMessage(player);
                return;
            }
        } else {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/" + template.getName() + " help " + (commandMessage == null ? "<" : "[") + "page|" + template.getName() + " command..." +
                extraHelp.stream().collect(Collectors.joining("|", (extraHelp.size() == 0 ? "" : "|"), "")) + (commandMessage == null ? ">" : "]"));
    }
}
