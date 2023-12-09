package com.spaceman.tport.adapters;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class V1_19_4_Adapter extends V1_19_4_NBTAdapter {
    
    static {
        adapters.put("V1_19_4", new V1_19_4_Adapter());
    }
    
    @Override
    public void sendPlayerPacket(Player player, Object packet) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        PlayerConnection pc = (PlayerConnection) getPlayerConnection(player);
        pc.a((Packet<?>) packet);
    }
    
}
