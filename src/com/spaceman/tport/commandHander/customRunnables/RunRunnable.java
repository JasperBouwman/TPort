package com.spaceman.tport.commandHander.customRunnables;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface RunRunnable {
    void run(String[] args, Player player);
}
