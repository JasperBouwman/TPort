package com.spaceman.tport.waypoint;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.tport.TPort;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.inventories.QuickEditInventories.*;

public enum WaypointSpecificShowType implements MessageUtils.MessageDescription {
    
    SHOW((player, tport) -> true, quick_edit_waypoint_show_show_model),
    HIDE((player, tport) -> false, quick_edit_waypoint_show_hide_model),
    WHITELIST((player, tport) -> tport.getWhitelist().contains(player.getUniqueId()) || tport.getOwner().equals(player.getUniqueId()), quick_edit_waypoint_show_whitelist_model);
    
    private final TestShow testShow;
    private final InventoryModel inventoryModel;
    
    WaypointSpecificShowType(TestShow testShow, InventoryModel inventoryModel) {
        this.testShow = testShow;
        this.inventoryModel = inventoryModel;
    }
    
    public boolean showForPlayer(Player player, TPort tport) {
        return testShow.show(player, tport);
    }
    
    public InventoryModel getInventoryModel() {
        return inventoryModel;
    }
    
    @FunctionalInterface
    public interface TestShow {
        boolean show(Player player, TPort tport);
    }
    
    public static WaypointSpecificShowType get(String name, @Nullable WaypointSpecificShowType def) {
        for (WaypointSpecificShowType showType : WaypointSpecificShowType.values()) {
            if (showType.name().equalsIgnoreCase(name)) {
                return showType;
            }
        }
        return def;
    }
    
    public WaypointSpecificShowType getNext() {
        boolean next = false;
        for (WaypointSpecificShowType showType : values()) {
            if (showType.equals(this)) {
                next = true;
            } else if (next) {
                return showType;
            }
        }
        return Arrays.asList(values()).get(0);
    }
    
    @Override
    public Message getDescription() {
        return formatInfoTranslation("tport.waypoint.waypointSpecificShowType." + this.name() + ".description");
    }
    
    @Override
    public Message getName(String color, String varColor) {
        return new Message(new TextComponent(name(), varColor));
    }
    
    @Override
    public String getInsertion() {
        return name();
    }
}
