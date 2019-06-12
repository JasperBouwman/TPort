package com.spaceman.tport.commandHander;

import com.spaceman.tport.commandHander.customRunnables.RunRunnable;
import com.spaceman.tport.commandHander.customRunnables.TabRunnable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EmptyCommand extends SubCommand {

    private ArrayList<SubCommand> action = new ArrayList<>();
    private RunRunnable runnable = (args, player) -> {
    };
    private TabRunnable tabRunnable = (args, player) -> {
        ArrayList<String> list = new ArrayList<>();
        for (SubCommand action : this.action) {
            if (!action.getName().equals("")) {
                list.add(action.getName());
            }
        }
        return list;
    };
    private boolean looped = false;

    @Override
    public void addAction(SubCommand subCommand) {
        action.add(subCommand);
    }

    @Override
    public ArrayList<SubCommand> getActions() {
        if (looped) {
            return new ArrayList<>(Collections.singletonList(this));
        }
        return new ArrayList<>(action);
    }

    public Collection<String> tabList(Player player, String[] args) {
        return tabRunnable.run(args, player);
    }

    @Override
    public String getName() {
        return "";
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
