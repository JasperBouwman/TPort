package com.spaceman.tport.adapters;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_20_R4.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.adapters.ReflectionManager.getPrivateField;
import static com.spaceman.tport.fancyMessage.inventories.keyboard.QuickType.onSignEdit;

public abstract class V1_20_5_FancyMessageAdapter extends V1_20_5_BiomeTPAdapter {
    
    private BlockPosition newBlockPosition(Location l) {
        return new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }
    
    @Override
    public void setDisplayName(org.bukkit.inventory.ItemStack itemStack, Message title, ColorTheme theme) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        ItemMeta im = itemStack.getItemMeta();
        
        Field displayNameField = Class.forName("org.bukkit.craftbukkit.v1_20_R4.inventory.CraftMetaItem").getDeclaredField("displayName");
        displayNameField.setAccessible(true);
        
        displayNameField.set(im, CraftChatMessage.fromJSON(title.translateJSON(theme)));
        itemStack.setItemMeta(im);
    }
    
    @Override
    public void setLore(org.bukkit.inventory.ItemStack itemStack, Collection<Message> lore, ColorTheme theme) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        
        ItemMeta im = itemStack.getItemMeta();
        
        Field loreField = Class.forName("org.bukkit.craftbukkit.v1_20_R4.inventory.CraftMetaItem").getDeclaredField("lore");
        loreField.setAccessible(true);
        
        List<IChatBaseComponent> l = new ArrayList<>();
        lore.forEach(line -> l.add(CraftChatMessage.fromJSON(line.translateJSON(theme))));
        loreField.set(im, l);
        
        itemStack.setItemMeta(im);
    }
    
    @Override
    public void sendMessage(Player player, String message) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        @Nullable IChatBaseComponent chatComponent = CraftChatMessage.fromJSON(message);
        Packet<?> packet = new ClientboundSystemChatPacket(chatComponent, false);
        sendPlayerPacket(player, packet);
    }
    
    @Override
    public void sendTitle(Player player, String message, Message.TitleTypes titleType, int fadeIn, int displayTime, int fadeOut) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        @Nullable IChatBaseComponent text = CraftChatMessage.fromJSON(message);
        
        Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game." + titleType.getMCClass());
        Class<?> chatComponent = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
        Packet<?> packetObject = (Packet<?>) packetClass.getConstructor(chatComponent).newInstance(text);
        
        if (fadeIn != -1 || displayTime != -1 || fadeOut != -1) {
            ClientboundSetTitlesAnimationPacket clientboundSetTitlesAnimationPacket = new ClientboundSetTitlesAnimationPacket(fadeIn, displayTime, fadeOut);
            this.sendPlayerPacket(player, clientboundSetTitlesAnimationPacket);
        }
        this.sendPlayerPacket(player, packetObject);
    }
    
    @Override
    public void sendInventory(Player player, String stringTitle, Inventory inventory) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        @Nullable IChatBaseComponent chatComponent = CraftChatMessage.fromJSON(stringTitle);
        EntityPlayer entityPlayer = (EntityPlayer) getEntityPlayer(player);
        Container container = new CraftContainer(inventory, entityPlayer, entityPlayer.nextContainerCounter());
        Containers<?> windowType = CraftContainer.getNotchInventoryType(inventory);
        sendPlayerPacket(player, new PacketPlayOutOpenWindow(container.j, windowType, chatComponent));
        entityPlayer.cb = container;
        entityPlayer.a(container);
    }
    
    @Override
    public void sendSignEditor(Player player, Location loc) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.sendPlayerPacket(player, new PacketPlayOutOpenSignEditor(newBlockPosition(loc), false));
    }
    
    @Override
    public void setQuickTypeSignHandler(Player player) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        ChannelDuplexHandler channelDuplexHandler = getChannelDuplexHandler(player);
        
        PlayerConnection playerConnection = (PlayerConnection) getPlayerConnection(player);
        NetworkManager networkManager = getPrivateField(NetworkManager.class, playerConnection, ServerCommonPacketListenerImpl.class);
        Channel channel = networkManager.n;
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.context("fancyMessage_quickType") != null) {
            this.removeQuickTypeSignHandler(player);
        }
        pipeline.addBefore("packet_handler", "fancyMessage_quickType", channelDuplexHandler);
    }
    
    @Nonnull
    private static ChannelDuplexHandler getChannelDuplexHandler(Player player) {
        UUID uuid = player.getUniqueId();
        return new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                if (packet instanceof PacketPlayInUpdateSign inUpdateSign) {
                    String[] lines = inUpdateSign.f();
                    if (onSignEdit(lines, uuid)) {
                        return;
                    }
                }
                super.channelRead(ctx, packet);
            }
        };
    }
    
    @Override
    public void removeQuickTypeSignHandler(Player player) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        PlayerConnection playerConnection = (PlayerConnection) getPlayerConnection(player);
        NetworkManager networkManager = getPrivateField(NetworkManager.class, playerConnection, ServerCommonPacketListenerImpl.class);
        Channel channel = networkManager.n;
        channel.eventLoop().submit(() -> channel.pipeline().remove("fancyMessage_quickType"));
    }
    
    @Override
    public void sendBlockChange(Player player, Location blockLoc, Block block) {
        player.sendBlockChange(blockLoc, block.getBlockData());
    }
    
    @Override
    public void sendBlockChange(Player player, Location blockLoc, Material material) {
        player.sendBlockChange(blockLoc, material.createBlockData());
    }
    
}
