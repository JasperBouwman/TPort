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
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.adapters.ReflectionManager.getField;
import static com.spaceman.tport.adapters.ReflectionManager.getPrivateField;
import static com.spaceman.tport.fancyMessage.inventories.keyboard.QuickType.onSignEdit;

public abstract class AdaptiveFancyMessage extends AdaptiveBiomeTP {
    
    private BlockPosition newBlockPosition(Location l) {
        return new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }
    
    @Override
    public void setDisplayName(ItemStack itemStack, @Nonnull Message title, ColorTheme theme) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String version = ReflectionManager.getServerClassesVersion();
        Field displayNameField = Class.forName("org.bukkit.craftbukkit." + version + "inventory.CraftMetaItem").getDeclaredField("displayName");
        displayNameField.setAccessible(true);
        
        ItemMeta im = itemStack.getItemMeta();
        if (displayNameField.getType().equals(IChatBaseComponent.class)) { // components
            Class<?> craftChatMessageClass = Class.forName("org.bukkit.craftbukkit." + version + "util.CraftChatMessage");
            IChatBaseComponent chatComponent = (IChatBaseComponent) craftChatMessageClass.getMethod("fromJSON", String.class).invoke(null, title.translateJSON(theme));
            displayNameField.set(im, chatComponent);
        } else if (displayNameField.getType().equals(String.class)) { // nbt
            displayNameField.set(im, title.translateJSON(theme));
        }
        itemStack.setItemMeta(im);
    }
    
    @Override
    public void setLore(ItemStack itemStack, @Nonnull Collection<Message> lore, ColorTheme theme) throws NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        String version = ReflectionManager.getServerClassesVersion();
        Field displayNameField = Class.forName("org.bukkit.craftbukkit." + version + "inventory.CraftMetaItem").getDeclaredField("displayName");
        Field loreField = Class.forName("org.bukkit.craftbukkit." + version + "inventory.CraftMetaItem").getDeclaredField("lore");
        loreField.setAccessible(true);
        
        ItemMeta im = itemStack.getItemMeta();
        if (displayNameField.getType().equals(IChatBaseComponent.class)) { // components
            Class<?> craftChatMessageClass = Class.forName("org.bukkit.craftbukkit." + version + "util.CraftChatMessage");
            Method fromJSONMethod = craftChatMessageClass.getMethod("fromJSON", String.class);
            List<IChatBaseComponent> l = new ArrayList<>();
            for (Message line : lore) {
                if (line != null) {
                    l.add((IChatBaseComponent) fromJSONMethod.invoke(null, line.translateJSON(theme)));
                }
            }
            
            loreField.set(im, l);
        } else { // nbt
            loreField.set(im, lore.stream()
                    .filter(Objects::nonNull)
                    .map(line -> line.translateJSON(theme))
                    .collect(Collectors.toList()));
        }
        itemStack.setItemMeta(im);
    }
    
    @Override
    public void sendMessage(Player player, String message) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        String version = ReflectionManager.getServerClassesVersion();
        Class<?> craftChatMessageClass = Class.forName("org.bukkit.craftbukkit." + version + "util.CraftChatMessage");
        IChatBaseComponent chatComponent = (IChatBaseComponent) craftChatMessageClass.getMethod("fromJSON", String.class).invoke(null, message);
//      @Nullable IChatMutableComponent chatComponent = IChatBaseComponent.ChatSerializer.a(message);
        
        try {
            Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game.ClientboundSystemChatPacket", false, this.getClass().getClassLoader());
            Packet<?> packet = (Packet<?>) packetClass.getConstructor(IChatBaseComponent.class, boolean.class).newInstance(chatComponent, false);
            sendPlayerPacket(player, packet);
        } catch (ClassNotFoundException cnfe) { //1.18 versions
            Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutChat");
            Object messageType = Class.forName("net.minecraft.network.chat.ChatMessageType").getDeclaredField("a").get(null);
            Packet<?> packet = (Packet<?>) packetClass.getConstructor(IChatBaseComponent.class, messageType.getClass(), UUID.class).newInstance(chatComponent, messageType, player.getUniqueId());
            sendPlayerPacket(player, packet);
        }
    }
    
    @Override
    public void sendTitle(Player player, String message, Message.TitleTypes titleType, int fadeIn, int displayTime, int fadeOut) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        String version = ReflectionManager.getServerClassesVersion();
        Class<?> craftChatMessageClass = Class.forName("org.bukkit.craftbukkit." + version + "util.CraftChatMessage");
        IChatBaseComponent text = (IChatBaseComponent) craftChatMessageClass.getMethod("fromJSON", String.class).invoke(null, message);
//      @Nullable IChatMutableComponent text = IChatBaseComponent.ChatSerializer.a(message);
        
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
    public void sendInventory(Player player, String stringTitle, Inventory inventory) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        
        String version = ReflectionManager.getServerClassesVersion();
        Class<?> craftChatMessageClass = Class.forName("org.bukkit.craftbukkit." + version + "util.CraftChatMessage");
        IChatBaseComponent chatSerializer = (IChatBaseComponent) craftChatMessageClass.getMethod("fromJSON", String.class).invoke(null, stringTitle);
//      IChatMutableComponent chatSerializer = IChatBaseComponent.ChatSerializer.a(stringTitle);
        
        EntityPlayer entityPlayer = (EntityPlayer) getEntityPlayer(player);
        
//      Container c = new CraftContainer(inventory, entityPlayer, entityPlayer.nextContainerCounter());
        Class<?> craftContainer = Class.forName("org.bukkit.craftbukkit." + version + "inventory.CraftContainer");
        Container container = (Container) craftContainer
                .getConstructor(Class.forName("org.bukkit.inventory.Inventory"), entityPlayer.getClass().getSuperclass(), int.class)
                .newInstance(inventory, entityPlayer, entityPlayer.nextContainerCounter());

//      Containers<?> windowType = CraftContainer.getNotchInventoryType(inventory);
        Containers<?> windowType = (Containers<?>) craftContainer.getMethod("getNotchInventoryType", Inventory.class).invoke(null, inventory);
//      getPlayerConnection(entityPlayer).a(new PacketPlayOutOpenWindow(container.j, windowType, chatSerializer));
        sendPlayerPacket(player, new PacketPlayOutOpenWindow(container.j, windowType, chatSerializer));

//      entityPlayer.bR = container;
        for (Field f : EntityHuman.class.getFields()) {
            if (f.getType().equals(Container.class)) {
                f.set(entityPlayer, container);
                break;
            }
        }
        
        entityPlayer.a(container); //todo reflection
    }
    
    @Override
    public void sendSignEditor(Player player, Location loc) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        PacketPlayOutOpenSignEditor packet;
        BlockPosition blockPos = newBlockPosition(loc);
        
        Class<?> packetClass = PacketPlayOutOpenSignEditor.class;
        try {
            packet = (PacketPlayOutOpenSignEditor) packetClass.getConstructor(BlockPosition.class, boolean.class).newInstance(blockPos, false);
        } catch (NoSuchMethodException nsme) { //pre 1.20
            //noinspection JavaReflectionMemberAccess
            packet = (PacketPlayOutOpenSignEditor) packetClass.getConstructor(BlockPosition.class).newInstance(blockPos);
        }
        this.sendPlayerPacket(player, packet);
    }
    
    @Override
    public void setQuickTypeSignHandler(Player player) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        ChannelDuplexHandler channelDuplexHandler = getChannelDuplexHandler(player);
        
        PlayerConnection playerConnection = (PlayerConnection) getPlayerConnection(player);
        NetworkManager networkManager = getPrivateField(NetworkManager.class, playerConnection);
        if (networkManager == null) {
            networkManager = getPrivateField(NetworkManager.class, playerConnection, ServerCommonPacketListenerImpl.class);
        }
        Channel channel = getField(Channel.class, networkManager);
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
                    String[] lines = ReflectionManager.get(String[].class, inUpdateSign);
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
        NetworkManager networkManager = getPrivateField(NetworkManager.class, playerConnection);
        if (networkManager == null) {
            networkManager = getPrivateField(NetworkManager.class, playerConnection, ServerCommonPacketListenerImpl.class);
        }
        Channel channel = getField(Channel.class, networkManager);
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
