package com.spaceman.tport.adapters;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AdaptiveAdapter extends AdaptiveFancyMessage {
    
    @Override
    public String getAdapterName() {
        return "adaptive";
    }
    
    @Override
    public void sendPlayerPacket(Player player, Object packet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        PlayerConnection pc = (PlayerConnection) getPlayerConnection(player);
        
        try {
            
            for (Method m : pc.getClass().getMethods()) {
                if (m.getParameterCount() != 1) continue;
                Parameter parameter = m.getParameters()[0];
                if (!parameter.getType().equals(Packet.class)) continue;
                m.invoke(pc, packet);
                break;
            }
            
            Class<?> packetSendListener = Class.forName("net.minecraft.network.PacketSendListener");
            for (Method m : pc.getClass().getMethods()) {
                if (m.getParameterCount() != 2) continue;
                Parameter parameter1 = m.getParameters()[0];
                if (!parameter1.getType().equals(Packet.class)) continue;
                Parameter parameter2 = m.getParameters()[1];
                if (!parameter2.getType().equals(packetSendListener))
                    continue; //todo in 1.18.2 PacketSendListener does not exist
                m.invoke(pc, packet, null);
                break;
            }
        } catch (ClassNotFoundException cnfe) {
            for (Method m : pc.getClass().getMethods()) {
                if (m.getParameterCount() != 1) continue;
                Parameter parameter = m.getParameters()[0];
                if (!parameter.getType().equals(Packet.class)) continue;
                m.invoke(pc, packet);
                return;
            }
        }
    }
}
