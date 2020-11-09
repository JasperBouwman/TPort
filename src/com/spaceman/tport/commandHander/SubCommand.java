package com.spaceman.tport.commandHander;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.permissions.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;

public abstract class SubCommand {
    
    private ArrayList<SubCommand> actions = new ArrayList<>();
    private ArgumentType argumentType = ArgumentType.FIXED;
    private String commandName = getName("");
    private Message commandDescription = new Message(textComponent("sub-command description not given"));
    private boolean linked = false;
    
    private List<String> permissions = new ArrayList<>();
    private boolean permissionsOR = true;
    
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
    
    public List<String> getPermissions() {
        return permissions;
    }
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
        PluginManager pm = Bukkit.getPluginManager();
        for (String permission : permissions) {
            try { pm.addPermission(new Permission(permission)); } catch (Exception ignore) { }
        }
    }
    public void setPermissions(String... permissions) {
        setPermissions(Arrays.asList(permissions));
    }
    public boolean permissionsOR() {
        return permissionsOR;
    }
    public void permissionsOR(boolean OR) {
        permissionsOR = OR;
    }
    public boolean hasPermissionToRun(Player player, boolean sendMessage) {
        return PermissionHandler.hasPermission(player, sendMessage, permissionsOR, permissions);
    }
    public Message permissionsHover() {
        Message message = new Message();
        boolean multiple = permissions.size() > 1;
        message.addText(textComponent("Permission" + (multiple ? "s: " : ": "), ColorTheme.ColorType.infoColor));
        message.addText("");
        
        for (int i = 0; i < permissions.size() - 1; i++) {
            message.addText(textComponent(permissions.get(i), varInfoColor));
            message.addText(textComponent(", ", infoColor));
        }
        message.removeLast();
        
        if (multiple) {
            if (permissionsOR) {
                message.addText(textComponent(" or ", infoColor));
            } else {
                message.addText(textComponent(" and ", infoColor));
            }
        }
        
        message.addText(textComponent(permissions.get(permissions.size() - 1), varInfoColor));
        return message;
    }
    
    public abstract void run(String[] args, Player player);
}
