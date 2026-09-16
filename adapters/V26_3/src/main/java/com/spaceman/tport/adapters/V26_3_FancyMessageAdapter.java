package com.spaceman.tport.adapters;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.SignTextSlot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.adapters.ReflectionManager.getPrivateField;
import static com.spaceman.tport.adapters.ReflectionManager.getServerClassesVersion;
import static com.spaceman.tport.fancyMessage.inventories.keyboard.QuickType.onSignEdit;

public abstract class V26_3_FancyMessageAdapter extends V26_3_BiomeTPAdapter {
    
    BlockPos newBlockPosition(Location l) {
        return new BlockPos(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }
    
    @Override
    public int JSONVersion() {
        return 1;
    }
    
    @Override
    public void setDisplayName(org.bukkit.inventory.ItemStack itemStack, @Nonnull Message title, ColorTheme theme) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        ItemMeta im = itemStack.getItemMeta();
        
        String version = getServerClassesVersion();
        // reflection because CraftMetaItem is package private
        Field displayNameField = Class.forName("org.bukkit.craftbukkit." + version + "inventory.CraftMetaItem").getDeclaredField("displayName");
        displayNameField.setAccessible(true);
        
        displayNameField.set(im, CraftChatMessage.fromJSON(title.translateJSON(theme)));
        itemStack.setItemMeta(im);
    }
    
    @Override
    public void setLore(org.bukkit.inventory.ItemStack itemStack, @Nonnull Collection<Message> lore, ColorTheme theme) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        
        ItemMeta im = itemStack.getItemMeta();
        
        String version = getServerClassesVersion();
        // reflection because CraftMetaItem is package private
        Field loreField = Class.forName("org.bukkit.craftbukkit." + version + "inventory.CraftMetaItem").getDeclaredField("lore");
        loreField.setAccessible(true);
        
        List<Component> l = lore.stream()
                .filter(Objects::nonNull)
                .map(line -> CraftChatMessage.fromJSON(line.translateJSON(theme)))
                .collect(Collectors.toList());
        loreField.set(im, l);
        
        itemStack.setItemMeta(im);
    }
    
    @Override
    public void sendMessage(Player player, String message) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Component chatComponent = CraftChatMessage.fromJSON(message);
        Packet<?> packet = new ClientboundSystemChatPacket(chatComponent, false);
        sendPlayerPacket(player, packet);
    }
    
    @Override
    public void sendTitle(Player player, String message, Message.TitleTypes titleType, int fadeIn, int displayTime, int fadeOut) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Component text = CraftChatMessage.fromJSON(message);
        
        Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game." + titleType.getMCClass());
        Packet<?> packetObject = (Packet<?>) packetClass.getConstructor(Component.class).newInstance(text);
        
        if (fadeIn != -1 || displayTime != -1 || fadeOut != -1) {
            ClientboundSetTitlesAnimationPacket clientboundSetTitlesAnimationPacket = new ClientboundSetTitlesAnimationPacket(fadeIn, displayTime, fadeOut);
            this.sendPlayerPacket(player, clientboundSetTitlesAnimationPacket);
        }
        this.sendPlayerPacket(player, packetObject);
    }
    
    @Override
    public void sendInventory(Player player, String stringTitle, Inventory inventory) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        Component chatComponent = CraftChatMessage.fromJSON(stringTitle);
        ServerPlayer entityPlayer = (ServerPlayer) getEntityPlayer(player);
        AbstractContainerMenu container = new CraftContainer(inventory, entityPlayer, entityPlayer.nextContainerCounter());
        MenuType<?> windowType = CraftContainer.getNotchInventoryType(inventory);
        
        sendPlayerPacket(player, new ClientboundOpenScreenPacket(container.containerId, windowType, chatComponent));
        entityPlayer.containerMenu = container;
        
        entityPlayer.initMenu(container);
    }
    
    @Override
    public void sendSignEditor(Player player, Location loc) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.sendPlayerPacket(player, new ClientboundOpenSignEditorPacket(newBlockPosition(loc), SignTextSlot.BACK));
    }
    
    @Override
    public void setQuickTypeSignHandler(Player player) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        ChannelDuplexHandler channelDuplexHandler = getChannelDuplexHandler(player);
        
        ServerGamePacketListenerImpl playerConnection = (ServerGamePacketListenerImpl) getPlayerConnection(player);
        Connection networkManager = getPrivateField(Connection.class, playerConnection, ServerCommonPacketListenerImpl.class);
        Channel channel = networkManager.channel;
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
                if (packet instanceof ServerboundSignUpdatePacket inUpdateSign) {
                    String[] lines = inUpdateSign.lines().toArray(new String[0]);
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
        ServerGamePacketListenerImpl playerConnection = (ServerGamePacketListenerImpl) getPlayerConnection(player);
        Connection networkManager = getPrivateField(Connection.class, playerConnection, ServerCommonPacketListenerImpl.class);
        Channel channel = networkManager.channel;
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
