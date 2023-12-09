package com.spaceman.tport.fancyMessage.inventories.keyboard;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.adapters.ReflectionManager;
import com.spaceman.tport.commands.tport.Features;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

import static com.spaceman.tport.adapters.ReflectionManager.getField;
import static com.spaceman.tport.adapters.ReflectionManager.getPrivateField;

public class QuickType implements Listener {
    
    private static final HashMap<UUID, Pair<Callback, Location>> quickMap = new HashMap<>();
    
    public static void setQuickTypeSignHandler(Player player) {
        ChannelDuplexHandler channelDuplexHandler = getChannelDuplexHandler(player);
        
        try {
            PlayerConnection playerConnection = (PlayerConnection) Main.getInstance().adapter.getPlayerConnection(player);
            NetworkManager networkManager = getPrivateField(NetworkManager.class, playerConnection);
            if (networkManager == null) {
                networkManager = getPrivateField(NetworkManager.class, playerConnection, ServerCommonPacketListenerImpl.class);
            }
            Channel channel = getField(Channel.class, networkManager); //nm.m;
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addBefore("packet_handler", "fancyMessage_quickType", channelDuplexHandler);
        } catch (Error | Exception ex) {
            Features.Feature.printSmallNMSErrorInConsole("QuickType", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
        }
    }
    
    @Nonnull
    private static ChannelDuplexHandler getChannelDuplexHandler(Player player) {
        UUID uuid = player.getUniqueId();
        return new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                if (packet instanceof PacketPlayInUpdateSign inUpdateSign) {
                    String[] lines = ReflectionManager.get(String[].class, inUpdateSign);
                    if (onSignEdit(lines, uuid)) {
                        return;
                    }
                }
                super.channelRead(ctx, packet);
            }
        };
    }
    
    public static void removeQuickTypeSignHandler(Player player) {
        try {
            PlayerConnection playerConnection = (PlayerConnection) Main.getInstance().adapter.getPlayerConnection(player);
            NetworkManager networkManager = getPrivateField(NetworkManager.class, playerConnection);
            if (networkManager == null) {
                networkManager = getPrivateField(NetworkManager.class, playerConnection, ServerCommonPacketListenerImpl.class);
            }
            Channel channel = getField(Channel.class, networkManager); //nm.m;
            channel.eventLoop().submit(() -> channel.pipeline().remove("fancyMessage_quickType"));
        } catch (Error | Exception ex) {
            Features.Feature.printSmallNMSErrorInConsole("QuickType", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
        }
    }
    
    private static boolean onSignEdit(String[] lines, UUID uuid) {
        Pair<Callback, Location> pair = quickMap.remove(uuid);
        if (pair != null) {
            pair.getLeft().onDone(lines);
            
            try {
                Location l = pair.getRight();
                BlockPosition blockPos = new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                Player player = Bukkit.getPlayer(uuid);
                
                PacketPlayOutBlockChange blockPacket = new PacketPlayOutBlockChange(blockPos, getBlockData(l.getBlock()));
                if (player != null) Main.getInstance().adapter.sendPlayerPacket(player, blockPacket);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }
    
    @FunctionalInterface
    public interface Callback {
        void onDone(String[] lines);
    }
    
    /**
     Inspired by <a href="https://github.com/Cleymax/SignGUI">SignGUI</a>
     * */
    public static void open(Player player, Callback callback) {
        player.closeInventory();
        
        Location playerBlockLoc = player.getEyeLocation().clone();
        playerBlockLoc.add(playerBlockLoc.getDirection().multiply(-4));
//        playerBlockLoc.add(0, -1, 0);
        
        BlockPosition playerBlockPos = new BlockPosition(playerBlockLoc.getBlockX(), playerBlockLoc.getBlockY(), playerBlockLoc.getBlockZ());
        try {
            PacketPlayOutBlockChange blockPacket = new PacketPlayOutBlockChange(playerBlockPos, getBlockData(Material.OAK_SIGN));
            Main.getInstance().adapter.sendPlayerPacket(player, blockPacket);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        try {
            PacketPlayOutOpenSignEditor signPacket = newSignEditor(playerBlockPos);
            Main.getInstance().adapter.sendPlayerPacket(player, signPacket);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        quickMap.put(player.getUniqueId(), new Pair<>(callback, playerBlockLoc));
    }
    
    private static PacketPlayOutOpenSignEditor newSignEditor(BlockPosition blockPos) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        PacketPlayOutOpenSignEditor packet;
        
        Class<?> packetClass = PacketPlayOutOpenSignEditor.class;
        try {
            packet = (PacketPlayOutOpenSignEditor) packetClass.getConstructor(BlockPosition.class, boolean.class).newInstance(blockPos, false);
        } catch (NoSuchMethodException nsme) { //pre 1.20
            //noinspection JavaReflectionMemberAccess
            packet = (PacketPlayOutOpenSignEditor) packetClass.getConstructor(BlockPosition.class).newInstance(blockPos);
        }
        return packet;
    }
    private static IBlockData getBlockData(Material material) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        CraftMagicNumbers.getBlock(material, (byte)0);
        
        String version = Main.getInstance().adapter.getServerVersion();
        Class<?> craftMagicNumbersClass = Class.forName("org.bukkit.craftbukkit." + version + ".util.CraftMagicNumbers");
        
        Method getBlockMethod = craftMagicNumbersClass.getMethod("getBlock", Material.class, byte.class);
        return (IBlockData) getBlockMethod.invoke(null, material, (byte) 0);
    }
    private static IBlockData getBlockData(Block block) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        ((CraftBlock)block).getNMS();
        
        String version = Main.getInstance().adapter.getServerVersion();
        Class<?> craftBlockClass = Class.forName("org.bukkit.craftbukkit." + version + ".block.CraftBlock");
        return (IBlockData) craftBlockClass.getMethod("getNMS").invoke(block);
    }

}
