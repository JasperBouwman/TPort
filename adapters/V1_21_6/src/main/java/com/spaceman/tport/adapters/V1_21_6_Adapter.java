package com.spaceman.tport.adapters;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_21_6_Adapter extends V1_21_6_FancyMessageAdapter {
    
    @Override
    public String getAdapterName() {
        return "1.21.6";
    }
    
    @Override
    public Object getPlayerConnection(Player player) {
        return ((CraftPlayer)player).getHandle().g;
    }
    
    @Override
    public void sendPlayerPacket(Player player, Object packet) {
        PlayerConnection pc = (PlayerConnection) getPlayerConnection(player);
        pc.b((Packet<?>) packet);
    }
    
}
