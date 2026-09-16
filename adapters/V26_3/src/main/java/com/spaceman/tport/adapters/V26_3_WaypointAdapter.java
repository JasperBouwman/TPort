package com.spaceman.tport.adapters;

import com.spaceman.tport.commands.tport.ResourcePack;
import com.spaceman.tport.fancyMessage.inventories.WaypointModel;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.waypoint.WaypointModels;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public abstract class V26_3_WaypointAdapter extends V26_3_FancyMessageAdapter {
    
    @Override
    public boolean supportsWaypoints() {
        return true;
    }
    
    @Override
    public void sendWaypoint(Player player, TPort tport) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        
        Waypoint.Icon icon = new Waypoint.Icon();
        WaypointModel iconPair = tport.getWaypointIcon();
        
        if (ResourcePack.getResourcePackState(player.getUniqueId())) {
            if (iconPair != null && WaypointModels.exists(iconPair.getNamespacedKey()))
                icon.style = ResourceKey.create(WaypointStyleAssets.ROOT_ID, Identifier.fromNamespaceAndPath(iconPair.getNamespacedKey().getNamespace(), iconPair.getNamespacedKey().getKey()));
        }
        icon.color = Optional.of(tport.getWaypointColor().getColor().getRGB());
        
        Packet<?> packet = ClientboundTrackedWaypointPacket.addWaypointPosition(tport.getTportID(), icon, newBlockPosition(tport.getLocation()));
        
        sendPlayerPacket(player, packet);
    }
    
    @Override
    public void removeWaypoint(Player player, TPort tport) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Packet<?> packet = ClientboundTrackedWaypointPacket.removeWaypoint(tport.getTportID());
        
        sendPlayerPacket(player, packet);
    }
}
