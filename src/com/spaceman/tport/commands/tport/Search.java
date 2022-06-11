package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Search extends SubCommand {
    
    private Search() { }
    private static final Search instance = new Search();
    public static Search getInstance() {
        return instance;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport search <type>
        // tport search <type> <query...>
        // tport search <type> <mode> <query...>
        
        /*
         * type:
         * canTP
         * player <mode> <player>
         * TPort <mode> <TPort name>
         * description <mode> <TPort description...>
         * dimension <dimension>
         * biome <biome...>
         * biomePreset <preset>
         * tag <tag>
         */
        
        /*
         * mode:
         * equals
         * contains
         * starts
         */
        
        if (!CooldownManager.Search.hasCooled(player, true)) {
            return;
        }
        
        if (args.length > 1) {
            for (SubCommand action : getActions()) {
                if (action.getName(args[1]).equalsIgnoreCase(args[1]) || action.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(args[1]))) {
                    if (hasPermission(player, true, "TPort.search." + action.getName(args[1]))) {
                        action.run(args, player);
                    }
                    return;
                }
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport search <type> [mode] <query...>");
        
    }
    
    public enum SearchMode {
        EQUALS(String::contentEquals),
        CONTAINS(String::contains),
        STARTS(String::startsWith);
        
        private final Fitter fitter;
        
        SearchMode(Fitter fitter) {
            this.fitter = fitter;
        }
        
        public static SearchMode get(String s) {
            try {
                return valueOf(s.toUpperCase());
            } catch (IllegalArgumentException iae) {
                return null;
            }
        }
        
        public boolean fits(String s, String query) {
            return this.fitter.fit(s.toLowerCase(), query.toLowerCase());
        }
        
        @FunctionalInterface
        private interface Fitter {
            boolean fit(String s, String a);
        }
    }
    
    public static class Searchers {
        
        private static final HashMap<String, Pair<Searcher, SubCommand>> searchers = new HashMap<>();
        
        public static boolean addSearcher(Searcher searcher, SubCommand subCommand) {
            if (searcher != null && !searchers.containsKey(subCommand.getCommandName())) {
                searchers.put(subCommand.getCommandName(), new Pair<>(searcher, subCommand));
                setPermission(subCommand.getCommandName(), subCommand);
                instance.addAction(subCommand);
                return true;
            }
            return false;
        }
        
        private static void setPermission(String name, SubCommand subCommand) {
            subCommand.setPermissions("TPort.search." + name);
            for (SubCommand sub : subCommand.getActions()) {
                if (!(sub instanceof EmptyCommand) || !((EmptyCommand) sub).isLooped()) {
                    setPermission(name, sub);
                } else {
                    sub.setPermissions("TPort.search." + name);
                }
            }
        }
        
        public static Set<String> getSearchers() {
            return searchers.keySet();
        }
        
        public static Searcher getSearcher(String name) {
            Pair<Searcher, SubCommand> s = searchers.getOrDefault(name, null);
            return s == null ? null : s.getLeft();
        }
        
        public static SubCommand getSubCommand(String name) {
            Pair<Searcher, SubCommand> s = searchers.getOrDefault(name, null);
            return s == null ? null : s.getRight();
        }
        
        @FunctionalInterface
        public interface Searcher {
            List<ItemStack> search(SearchMode searchMode, String query, Player player);
        }
    }
}
