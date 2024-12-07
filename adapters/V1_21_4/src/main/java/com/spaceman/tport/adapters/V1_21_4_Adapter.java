package com.spaceman.tport.adapters;

import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_21_4_Adapter extends V1_21_4_FancyMessageAdapter {
    
    @Override
    public String getAdapterName() {
        return "1.21.4";
    }
    
    @Override
    public Object getPlayerConnection(Player player) {
        return ((CraftPlayer)player).getHandle().f;
    }
    
    @Override
    public void sendPlayerPacket(Player player, Object packet) {
        PlayerConnection pc = (PlayerConnection) getPlayerConnection(player);
        pc.a((Packet<?>) packet, (PacketSendListener) null);
    }
    
}
