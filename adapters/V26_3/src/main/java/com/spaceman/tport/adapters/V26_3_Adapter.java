package com.spaceman.tport.adapters;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V26_3_Adapter extends V26_3_WaypointAdapter {
    
    @Override
    public String getAdapterName() {
        return "26.3";
    }
    
    @Override
    public Object getPlayerConnection(Player player) {
        return ((CraftPlayer)player).getHandle().connection;
    }
    
    @Override
    public void sendPlayerPacket(Player player, Object packet) {
        ServerGamePacketListenerImpl pc = (ServerGamePacketListenerImpl) getPlayerConnection(player);
        pc.send((Packet<?>) packet);
    }
    
}
