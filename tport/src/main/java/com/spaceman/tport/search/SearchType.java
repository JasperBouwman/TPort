package com.spaceman.tport.search;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commandHandler.customRunnables.TabRunnable;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.inventories.TPortInventories;
import com.spaceman.tport.permissions.PermissionHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class SearchType implements MessageUtils.MessageDescription {
    
    private final String searchTypeName;
    
    private TabRunnable tabQuery = null;
    private InventoryQuery inventoryQuery = null;
    private String queryName = null;
    private boolean loopedQuery = false;
    private boolean intSearch = false;
    private SubCommand command = null;
    
    private boolean hasSearchMode = false;
    
    private InventoryModel inventoryModel = null;
    
    private Searcher searcher = null;
    
    public SearchType(String searchTypeName) {
        this.searchTypeName = searchTypeName;
    }
    
    public String getSearchTypeName() {
        return searchTypeName;
    }
    
    public void hasSearchMode(boolean hasSearchMode) {
        this.hasSearchMode = hasSearchMode;
    }
    public boolean hasSearchMode() {
        return hasSearchMode;
    }
    
    public void setInventoryModel(InventoryModel inventoryModel) {
        this.inventoryModel = inventoryModel;
    }
    public ItemStack getDisplayItem(Player player) {
        if (this.inventoryModel == null) {
            return new ItemStack(Material.DIAMOND_BLOCK);
        } else {
            return this.inventoryModel.getItem(player);
        }
    }
    
    @Override
    public Message getDescription() {
        return formatInfoTranslation("tport.search.SearchType." + searchTypeName + ".description");
    }
    
    @Override
    public Message getName(String color, String varColor) {
        return new Message(new TextComponent(searchTypeName, varColor));
    }
    
    @Override
    public String getInsertion() {
        return searchTypeName;
    }
    
    @FunctionalInterface
    public interface InventoryQuery {
        ArrayList<ItemStack> queryItems(Player player);
    }
    
    @FunctionalInterface
    public interface Searcher {
        List<ItemStack> search(SearchMode searchMode, String query, Player player);
    }
    
    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }
    public boolean hasSearcher() {
        return this.searcher != null;
    }
    public List<ItemStack> search(SearchMode searchMode, String query, Player player) {
        return this.searcher.search(searchMode, query, player);
    }
    
    public void removeQuery() {
        this.queryName = null;
        this.tabQuery = null;
        this.inventoryQuery = null;
        this.loopedQuery = false;
        this.intSearch = false;
    }
    public void setQuery(String name, boolean looped, boolean intSearch, @Nullable /*when null, free typing*/ TabRunnable tabQuery, @Nullable /*when null, use FancyKeyboard*/ InventoryQuery inventoryQuery) {
        this.queryName = name;
        this.loopedQuery = looped;
        this.intSearch = intSearch;
        this.tabQuery = Objects.requireNonNullElseGet(tabQuery, () -> ((args, player) -> List.of()));
        this.inventoryQuery = inventoryQuery;
    }
    public boolean hasQuery() {
        return queryName != null;
    }
    public boolean keyboardQuery() {
        return inventoryQuery == null;
    }
    public ArrayList<ItemStack> queryItems(Player player) {
        return inventoryQuery.queryItems(player);
    }
    public boolean isLoopedQuery() {
        return loopedQuery;
    }
    public boolean isIntSearch() {
        return intSearch;
    }
    
    public boolean hasPermission(Player player, boolean sendMessage) {
        return PermissionHandler.hasPermission(player, sendMessage, "TPort.search." + searchTypeName);
    }
    
    public EmptyCommand buildCommand() {
        EmptyCommand emptyQuery = null;
        if (hasQuery()) {
            emptyQuery = new EmptyCommand();
            emptyQuery.setCommandName(queryName, ArgumentType.REQUIRED);
            emptyQuery.setPermissions("TPort.search." + searchTypeName);
            emptyQuery.setCommandDescription(formatInfoTranslation("tport.command.search." + searchTypeName + ".commandDescription"));
            if (loopedQuery) {
                emptyQuery.setTabRunnable(tabQuery);
            }
            emptyQuery.setLooped(loopedQuery);
        }
        
        EmptyCommand emptyMode = null;
        if (hasSearchMode) {
            if (!hasQuery()) throw new IllegalArgumentException("No query type is set up for " + searchTypeName + ", this is required for a search mode");
            
            emptyMode = new EmptyCommand();
            emptyMode.setCommandName("mode", ArgumentType.REQUIRED);
            emptyMode.setPermissions("TPort.search." + searchTypeName);
            emptyMode.setTabRunnable(tabQuery);
            emptyMode.addAction(emptyQuery);
        }
        if (intSearch && isLoopedQuery()) {
            throw new IllegalArgumentException("Int search does not support looped query");
        }
        
        EmptyCommand searchCommand = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        searchCommand.setCommandName(searchTypeName, ArgumentType.FIXED);
        searchCommand.setPermissions("TPort.search." + searchTypeName);
        searchCommand.setCommandDescription(formatInfoTranslation("tport.command.search." + searchTypeName + ".commandDescription"));
        if (emptyMode != null) {
            if (intSearch) {
                searchCommand.setTabRunnable(((args, player) -> Arrays.stream(SearchMode.values()).filter(SearchMode::hasIntegerFitter).map(Enum::name).collect(Collectors.toList())));
            } else {
                searchCommand.setTabRunnable(((args, player) -> Arrays.stream(SearchMode.values()).map(Enum::name).collect(Collectors.toList())));
            }
        } else searchCommand.setTabRunnable(tabQuery);
        searchCommand.setRunnable(((args, player) -> {
            int completeCommandLength = 2;
            if (hasSearchMode) completeCommandLength++;
            if (hasQuery()) completeCommandLength++;
            
            if (loopedQuery && args.length >= completeCommandLength || args.length == completeCommandLength) {
                SearchMode searchMode = null;
                if (hasSearchMode) {
                    searchMode = SearchMode.get(args[2]);
                    if (searchMode == null) {
                        sendErrorTranslation(player, "tport.command.search.modeNotExist", args[2]);
                        return;
                    }
                    if (intSearch && !searchMode.hasIntegerFitter()) {
                        sendErrorTranslation(player, "tport.command.search.modeNotIntSearch", args[2]);
                        return;
                    }
                }
                
                String searchedQuery;
                if (loopedQuery) {
                    searchedQuery = StringUtils.join(args, " ", completeCommandLength-1, args.length);
                } else {
                    searchedQuery = args[completeCommandLength - 1];
                    
                    if (this.isIntSearch()) {
                        try {
                            Integer.parseInt(searchedQuery);
                        } catch (NumberFormatException nfe) {
                            sendErrorTranslation(player, "tport.command.search.inputIsNotInteger", this, searchedQuery);
                            return;
                        }
                    }
                }
                
                if (inventoryQuery != null) {
                    ArrayList<String> accepted = inventoryQuery.queryItems(player).stream()
                            .map(is -> is.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "query"), STRING))
                            .collect(Collectors.toCollection(ArrayList::new));
                    for (int i = completeCommandLength - 1; i < args.length; i++) {
                        String givenQuery = args[i];
                        if (accepted.stream().noneMatch(tmpQuery -> tmpQuery.equalsIgnoreCase(givenQuery))) {
                            sendErrorTranslation(player, "tport.command.search.queryNotExist", this, args[i]);
                            return;
                        }
                    }
                }
                
                TPortInventories.openSearchGUI(player, 0, searchMode, searchTypeName, searchedQuery);
            } else {
                String command = "/tport search " + searchTypeName;
                if (hasSearchMode) command += " <mode>";
                if (hasQuery()) {
                    command += " <";
                    command += queryName;
                    if (loopedQuery) command += "...";
                    command += ">";
                }
                sendErrorTranslation(player, "tport.command.wrongUsage",command);
            }
        }));
        
        if (emptyMode != null) searchCommand.addAction(emptyMode);
        else searchCommand.addAction(emptyQuery);
        
        this.command = searchCommand;
        return searchCommand;
    }
    public SubCommand getCommand() {
        if (command == null) {
            return buildCommand();
        }
        return command;
    }
}