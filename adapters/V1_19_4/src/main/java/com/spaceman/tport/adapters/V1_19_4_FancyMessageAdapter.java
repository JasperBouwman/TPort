package com.spaceman.tport.adapters;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftContainer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.UUID;

import static com.spaceman.tport.adapters.ReflectionManager.getPrivateField;
import static com.spaceman.tport.fancyMessage.inventories.keyboard.QuickType.onSignEdit;

public abstract class V1_19_4_FancyMessageAdapter extends V1_19_4_BiomeTPAdapter {
    
    private BlockPosition newBlockPosition(Location l) {
        return new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }
    
    @Override
    public void setDisplayName(ItemStack itemStack, Message title, ColorTheme theme) throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        String version = ReflectionManager.getServerClassesVersion();
        Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
        
        Class<?> isClass = Class.forName("org.bukkit.inventory.ItemStack");
        net.minecraft.world.item.ItemStack nmsStack = (net.minecraft.world.item.ItemStack) craftItemStack.getMethod("asNMSCopy", isClass).invoke(craftItemStack, itemStack);
        Class<?> itemStackClass = nmsStack.getClass();
        
        NBTTagCompound tag = this.getNBTTag(nmsStack);
        
        NBTTagCompound display = this.getCompound(tag, "display");
        if (title != null) this.putString(display, "Name", title.translateJSON(theme));
        
        this.put(tag, "display", display);
        
        ItemMeta im = (ItemMeta) craftItemStack.getMethod("getItemMeta", itemStackClass).invoke(craftItemStack, nmsStack);
        
        itemStack.setItemMeta(im);
    }
    
    @Override
    public void setLore(ItemStack itemStack, Collection<Message> lore, ColorTheme theme) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String version = ReflectionManager.getServerClassesVersion();
        Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
        
        Class<?> isClass = Class.forName("org.bukkit.inventory.ItemStack");
        net.minecraft.world.item.ItemStack nmsStack = (net.minecraft.world.item.ItemStack) craftItemStack.getMethod("asNMSCopy", isClass).invoke(craftItemStack, itemStack);
        Class<?> itemStackClass = nmsStack.getClass();
        
        NBTTagCompound tag = this.getNBTTag(nmsStack);
        
        NBTTagCompound display = this.getCompound(tag, "display");
        
        if (lore != null) {
            NBTTagList loreT = new NBTTagList();
            int id = 0;
            for (Message line : lore) {
                if (line != null) {
                    this.listTag_addTag(loreT, id++, this.stringTag_valueOf(line.translateJSON(theme)));
                }
            }
            this.put(display, "Lore", loreT);
        }
        
        this.put(tag, "display", display);
        
        ItemMeta im = (ItemMeta) craftItemStack.getMethod("getItemMeta", itemStackClass).invoke(craftItemStack, nmsStack);
        
        itemStack.setItemMeta(im);
    }
    public NBTTagCompound getNBTTag(net.minecraft.world.item.ItemStack nmsStack) {
        return nmsStack.v(); //not Nullable
    }
    public NBTTagCompound getCompound(NBTTagCompound tag, String name) {
        return tag.p(name);
    }
    public void putString(NBTTagCompound tag, String name, String data) {
        tag.a(name, data);
    }
    public NBTBase put(NBTTagCompound tag, String name, NBTBase tagData) {
        return tag.a(name, tagData);
    }
    public NBTTagString stringTag_valueOf(String value) {
        return NBTTagString.a(value);
    }
    public void listTag_addTag(NBTTagList tagList, int index, NBTBase toAdd) {
        tagList.b(index, toAdd);
    }
    
    @Override
    public void sendMessage(Player player, String message) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        @Nullable IChatMutableComponent chatComponent = IChatBaseComponent.ChatSerializer.a(message);
        Packet<?> packet = new ClientboundSystemChatPacket(chatComponent, false);
        sendPlayerPacket(player, packet);
    }
    
    @Override
    public void sendTitle(Player player, String message, Message.TitleTypes titleType, int fadeIn, int displayTime, int fadeOut) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        IChatMutableComponent text = IChatBaseComponent.ChatSerializer.a(message);
        
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
        IChatMutableComponent chatSerializer = IChatBaseComponent.ChatSerializer.a(stringTitle);
        EntityPlayer entityPlayer = (EntityPlayer) getEntityPlayer(player);
        Container container = new CraftContainer(inventory, entityPlayer, entityPlayer.nextContainerCounter());
        Containers<?> windowType = CraftContainer.getNotchInventoryType(inventory);
        sendPlayerPacket(player, new PacketPlayOutOpenWindow(container.j, windowType, chatSerializer));
        entityPlayer.bP = container;
        entityPlayer.a(container);
    }
    
    @Override
    public void sendSignEditor(Player player, Location loc) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.sendPlayerPacket(player, new PacketPlayOutOpenSignEditor(newBlockPosition(loc)));
    }
    
    @Override
    public void setQuickTypeSignHandler(Player player) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        ChannelDuplexHandler channelDuplexHandler = getChannelDuplexHandler(player);
        
        PlayerConnection playerConnection = (PlayerConnection) getPlayerConnection(player);
        NetworkManager networkManager = getPrivateField(NetworkManager.class, playerConnection);
        Channel channel = networkManager.m;
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
                    String[] lines = inUpdateSign.c();
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
        Channel channel = networkManager.m;
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
