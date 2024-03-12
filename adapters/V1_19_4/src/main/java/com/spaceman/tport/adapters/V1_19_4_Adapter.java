package com.spaceman.tport.adapters;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_19_4_Adapter extends V1_19_4_FancyMessageAdapter {
    
    @Override
    public String getAdapterName() {
        return "1.19.4";
    }
    
    @Override
    public Object getPlayerConnection(Player player) {
        return ((CraftPlayer)player).getHandle().b;
    }
    
    @Override
    public void sendPlayerPacket(Player player, Object packet) {
        PlayerConnection pc = (PlayerConnection) getPlayerConnection(player);
        pc.a((Packet<?>) packet);
    }
    
}
