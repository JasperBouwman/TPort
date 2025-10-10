package com.spaceman.tport.adapters;

import com.spaceman.tport.Pair;
import com.spaceman.tport.commands.tport.ResourcePack;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.waypoint.WaypointModels;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public abstract class V1_21_10_WaypointAdapter extends V1_21_10_FancyMessageAdapter {
    
    @Override
    public boolean supportsWaypoints() {
        return true;
    }
    
    @Override
    public void sendWaypoint(Player player, TPort tport) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        
        Waypoint.a icon = new Waypoint.a();
        Pair<String, String> iconPair = tport.getWaypointIcon();
        
        if (ResourcePack.getResourcePackState(player.getUniqueId())) {
            if (WaypointModels.exists(iconPair.getRight()))
              icon.d = ResourceKey.a(WaypointStyleAssets.a, MinecraftKey.a(iconPair.getLeft(), iconPair.getRight()));
        }
        icon.e = Optional.of(tport.getWaypointColor().getColor().getRGB());
        
        Packet<?> packet = ClientboundTrackedWaypointPacket.a(tport.getTportID(), icon, newBlockPosition(tport.getLocation()));
        
        sendPlayerPacket(player, packet);
    }
    
    @Override
    public void removeWaypoint(Player player, TPort tport) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Packet<?> packet = ClientboundTrackedWaypointPacket.a(tport.getTportID());
        
        sendPlayerPacket(player, packet);
    }
}
