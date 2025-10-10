package com.spaceman.tport.waypoint;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.tport.TPort;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;

public enum WaypointShowType implements MessageUtils.MessageDescription {
    PublicTP((player, tport) -> tport.isPublicTPort()),
    Public((player, tport) -> {
        if (tport.getPrivateState().equals(TPort.PrivateState.OPEN)) {
            return true;
        }
        if (tport.getPrivateState().equals(TPort.PrivateState.ONLINE)) {
            Player p = Bukkit.getPlayer(tport.getOwner());
            return p != null && p.isOnline();
        }
        return false;
    }),
    CanTP((player, tport) -> tport.canTeleport(player, false, false, false)),
    Own((player, tport) -> tport.getOwner().equals(player.getUniqueId())),
    All((player, tport) -> true),
    None((player, tport) -> false);
    
    private final TestShow testShow;
    
    WaypointShowType(TestShow testShow) {
        this.testShow = testShow;
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
    
    public boolean show(Player player, TPort tport) {
        return this.testShow.show(player, tport);
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