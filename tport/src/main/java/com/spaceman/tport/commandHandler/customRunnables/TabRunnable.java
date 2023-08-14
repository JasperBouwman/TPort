package com.spaceman.tport.commandHandler.customRunnables;

import org.bukkit.entity.Player;

import java.util.Collection;

@FunctionalInterface
public interface TabRunnable {
    Collection<String> run(String[] args, Player player);
}
