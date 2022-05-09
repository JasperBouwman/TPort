package com.spaceman.tport.commandHandler.customRunnables;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface RunRunnable {
    void run(String[] args, Player player);
}
