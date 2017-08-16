package com.spaceman.tport.commands;

import com.spaceman.tport.Main;
import org.bukkit.entity.Player;

public abstract class CmdHandler {

    protected Main p;

    public abstract void run(String[] args, Player player);

    public void setMain(Main main) {
        this.p = main;
    }

}
