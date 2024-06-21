package com.spaceman.tport.adapters;

import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_20_6_Adapter extends V1_20_6_FancyMessageAdapter {
    
    @Override
    public String getAdapterName() {
        return "1.20.6";
    }
    
    @Override
    public Object getPlayerConnection(Player player) {
        return ((CraftPlayer)player).getHandle().c;
    }
    
    @Override
    public void sendPlayerPacket(Player player, Object packet) {
        PlayerConnection pc = (PlayerConnection) getPlayerConnection(player);
        pc.a((Packet<?>) packet, (PacketSendListener) null);
    }
    
}
