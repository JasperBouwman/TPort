package com.spaceman.tport.commandHandler;

import com.spaceman.tport.commandHandler.customRunnables.RunRunnable;
import com.spaceman.tport.commandHandler.customRunnables.TabRunnable;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class EmptyCommand extends SubCommand {
    
    private boolean looped = false;
    private final ArrayList<SubCommand> actions = new ArrayList<>();
    private RunRunnable runnable = (args, player) -> {};
    private TabRunnable tabRunnable = (args, player) -> {
        ArrayList<String> list = new ArrayList<>();
        for (SubCommand action : this.actions) {
            list.add(action.getName(""));
        }
        return list;
    };
    
    public EmptyCommand() {
        setCommandName(null);
    }
    
    @Override
    public void addAction(@Nullable SubCommand subCommand) {
        if (subCommand != null) actions.add(subCommand);
    }
    
    @Override
    public ArrayList<SubCommand> getActions() {
        if (looped) {
            return new ArrayList<>(Collections.singletonList(this));
        }
        return new ArrayList<>(actions);
    }
    
    public Collection<String> tabList(Player player, String[] args) {
        return tabRunnable.run(args, player);
    }
    
    @Override
    public String getName(String argument) {
        return argument;
    }
    
    public RunRunnable getRunnable() {
        return runnable;
    }
    
    public void setRunnable(RunRunnable runnable) {
        this.runnable = runnable;
    }
    
    public TabRunnable getTabRunnable() {
        return tabRunnable;
    }
    
    public void setTabRunnable(TabRunnable runnable) {
        this.tabRunnable = runnable;
    }
    
    public boolean isLooped() {
        return looped;
    }
    
    public void setLooped(boolean looped) {
        this.looped = looped;
    }
    
    @Override
    public void run(String[] args, Player player) {
        if (runnable != null) {
            runnable.run(args, player);
        }
    }
}
