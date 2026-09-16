package com.spaceman.tport.adapters;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static com.spaceman.tport.adapters.ReflectionManager.getField;

public class AdaptiveAdapter extends AdaptiveFancyMessage {
    
    @Override
    public String getAdapterName() {
        return "adaptive";
    }
    
    public /*PlayerConnection*/ Object getPlayerConnection(Player player) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        return getField(Class.forName("net.minecraft.server.network.ServerGamePacketListenerImpl"), getEntityPlayer(player));
    }
    
    @Override
    public void sendPlayerPacket(Player player, Object packet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        ServerGamePacketListenerImpl pc = (ServerGamePacketListenerImpl) getPlayerConnection(player);
        
        try {
            pc.sendPacket((Packet<?>) packet);
        } catch (Throwable t) {
            try {
                Class<?> packetSendListener = Class.forName("net.minecraft.network.PacketSendListener");
                for (Method m : pc.getClass().getMethods()) {
                    if (m.getParameterCount() != 2) continue;
                    Parameter parameter1 = m.getParameters()[0];
                    if (!parameter1.getType().equals(Packet.class)) continue;
                    Parameter parameter2 = m.getParameters()[1];
                    if (!parameter2.getType().equals(packetSendListener)) {
                        continue; //todo in 1.18.2 PacketSendListener does not exist
                    }
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
}
