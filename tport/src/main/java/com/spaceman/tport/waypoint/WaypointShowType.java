package com.spaceman.tport.waypoint;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.tport.TPort;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import java.util.Arrays;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.inventories.SettingsInventories.*;

public enum WaypointShowType implements MessageUtils.MessageDescription {
    PublicTP((player, tport) -> tport.isPublicTPort(), settings_waypoints_show_type_public_tp_model),
    Public((player, tport) -> {
        if (tport.getPrivateState().equals(TPort.PrivateState.OPEN)) {
            return true;
        }
        if (tport.getPrivateState().equals(TPort.PrivateState.ONLINE)) {
            Player p = Bukkit.getPlayer(tport.getOwner());
            return p != null && p.isOnline();
        }
        return false;
    }, settings_waypoints_show_type_public_model),
    CanTP((player, tport) -> tport.canTeleport(player, false, false, false), settings_waypoints_show_type_can_tp_model),
    Own((player, tport) -> tport.getOwner().equals(player.getUniqueId()), settings_waypoints_show_type_own_model),
    All((player, tport) -> true, settings_waypoints_show_type_all_model),
    None((player, tport) -> false, settings_waypoints_show_type_none_model);
    
    private final TestShow testShow;
    private final InventoryModel inventoryModel;
    
    WaypointShowType(TestShow testShow, InventoryModel inventoryModel) {
        this.testShow = testShow;
        this.inventoryModel = inventoryModel;
    }
    
    public static WaypointShowType get(String name, @Nullable WaypointShowType def) {
        for (WaypointShowType showType : WaypointShowType.values()) {
            if (showType.name().equalsIgnoreCase(name)) {
                return showType;
            }
        }
        return def;
    }
    
    @FunctionalInterface
    public interface TestShow {
        boolean show(Player player, TPort tport);
    }
    
    public WaypointShowType getNext() {
        boolean next = false;
        for (WaypointShowType WaypointShowType : values()) {
            if (WaypointShowType.equals(this)) {
                next = true;
            } else if (next) {
                return WaypointShowType;
            }
        }
        return Arrays.asList(values()).get(0);
    }
    
    public boolean show(Player player, TPort tport) {
        return this.testShow.show(player, tport);
    }
    
    public InventoryModel getInventoryModel() {
        return inventoryModel;
    }
    
    @Override
    public Message getDescription() {
        return formatInfoTranslation("tport.waypoint.waypointShowType." + this.name() + ".description");
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