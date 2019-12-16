package com.spaceman.tport.commandHander;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SubCommand {
    
    private ArrayList<SubCommand> actions = new ArrayList<>();
    private ArgumentType argumentType = ArgumentType.FIXED;
    private String commandName = getName("");
    private Message commandDescription = new Message(TextComponent.textComponent("sub-command description not given"));
    private boolean linked = false;
    
    public static String lowerCaseFirst(String string) {
        return string == null || string.isEmpty() ? "" : Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }
    
    public void addAction(SubCommand subCommand) {
        actions.add(subCommand);
    }
    
    public boolean removeAction(SubCommand subCommand) {
        return actions.remove(subCommand);
    }
    
    public SubCommand removeAction(String actionName) {
        for (SubCommand sub : actions) {
            if (sub.getName(actionName).equals(actionName)) {
                if (actions.remove(sub)) {
                    return sub;
                }
            }
        }
        return null;
    }
    
    public ArrayList<SubCommand> getActions() {
        return new ArrayList<>(actions);
    }
    
    public SubCommand getAction(String action) {
        return getAction(action, null);
    }
    
    public SubCommand getAction(String action, SubCommand def) {
        for (SubCommand subCommand : this.getActions()) {
            if (subCommand.getName(action).equalsIgnoreCase(action)) {
                return subCommand;
            }
        }
        return def;
    }
    
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        for (SubCommand subCommand : actions) {
            list.add(subCommand.getName(""));
        }
        return list;
    }
    
    public void setLinked() {
        linked = true;
    }
    
    public boolean isLinked() {
        return linked;
    }
    
    public String getCommandName() {
        return commandName;
    }
    
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }
    
    public Message getCommandDescription() {
        return commandDescription;
    }
    
    public void setCommandDescription(Message commandDescription) {
        this.commandDescription = commandDescription;
    }
    
    public void setCommandDescription(TextComponent... textComponents) {
        setCommandDescription(new Message(textComponents));
    }
    
    public ArgumentType getArgumentType() {
        return argumentType;
    }
    
    public void setArgumentType(ArgumentType argumentType) {
        this.argumentType = argumentType;
    }
    
    public void setCommandName(String commandName, ArgumentType argumentType) {
        setCommandName(commandName);
        setArgumentType(argumentType);
    }
    
    public String getName(String arg) {
        return lowerCaseFirst(this.getClass().getSimpleName());
    }
    
    public List<String> getAliases() {
        return new ArrayList<>();
    }
    
    public abstract void run(String[] args, Player player);
}
