package com.spaceman.tport.commandHandler;

import org.bukkit.entity.Player;

public abstract class CmdHandler {
    /*
     * This is the simple command handler
     * In the code below is a class written with the implementation of this class
     *
     * public class Command {
     *     List<CmdHandler> actions = new ArrayList<>();
     *
     *     pubic Constructor() {
     *         actions.add(new SubCommand());
     *     }
     *
     *     public void onCommand(Command arguments) {
     *         for (CmdHandler action : actions) {
     *             if (actions.name().equals(args[1])) {
     *                 action.run(args, player);
     *             }
     *         }
     *     }
     * }
     *
     * public class SubCommand extends CmdHandler {
     *     @override
     *     public void run(String[] args, Player player) {
     *         //do something
     *     }
     * }
     */
    
    
    public abstract void run(String[] args, Player player);
    
    public String name() {
        return this.getClass().getSimpleName();
    }
}
