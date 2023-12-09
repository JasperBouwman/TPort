package com.spaceman.tport.adapters;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.commands.tport.biomeTP.Accuracy;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

import static com.spaceman.tport.adapters.ReflectionManager.getField;

public abstract class TPortAdapter {
    
    public static HashMap<String, TPortAdapter> adapters = new HashMap<>();
    
    public String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }
    
    public /*WorldServer*/ Object getWorldServer(World world) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
    }
    
    public /*EntityPlayer*/ Object getEntityPlayer(Player player) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return player.getClass().getMethod("getHandle").invoke(player);
    }
    public /*PlayerConnection*/ Object getPlayerConnection(Player player) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
//        return getField(PlayerConnection.class, getEntityPlayer(player));
        return getField(Class.forName("net.minecraft.server.network.PlayerConnection"), getEntityPlayer(player));
    }
    
    public abstract void sendPlayerPacket(Player player, Object packet) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException;

    //fancyMessage
    public void sendMessage(Player player, String message) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        try {
            Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game.ClientboundSystemChatPacket", false, this.getClass().getClassLoader());
            @Nullable IChatMutableComponent chatComponent = IChatBaseComponent.ChatSerializer.a(message);
            Packet<?> packet = (Packet<?>) packetClass.getConstructor(IChatBaseComponent.class, boolean.class).newInstance(chatComponent, false);
            sendPlayerPacket(player, packet);
        } catch (ClassNotFoundException cnfe) { //1.18 versions
            Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutChat");
            Object messageType = Class.forName("net.minecraft.network.chat.ChatMessageType").getDeclaredField("a").get(null);
            @Nullable IChatMutableComponent chatComponent = IChatBaseComponent.ChatSerializer.a(message);
            Packet<?> packet = (Packet<?>) packetClass.getConstructor(IChatBaseComponent.class, messageType.getClass(), UUID.class).newInstance(chatComponent, messageType, player.getUniqueId());
            sendPlayerPacket(player, packet);
        }
    }
    public void sendInventory(Player player, String stringTitle, Inventory inventory) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, InstantiationException {

        IChatMutableComponent chatSerializer = IChatBaseComponent.ChatSerializer.a(stringTitle);
        EntityPlayer entityPlayer = (EntityPlayer) getEntityPlayer(player);
        
        String version = Main.getInstance().adapter.getServerVersion();
//            Container c = new CraftContainer(inventory, entityPlayer, entityPlayer.nextContainerCounter());
        Class<?> craftContainer = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftContainer");
        Container container = (Container) craftContainer
                .getConstructor(Class.forName("org.bukkit.inventory.Inventory"), entityPlayer.getClass().getSuperclass(), int.class)
                .newInstance(inventory, entityPlayer, entityPlayer.nextContainerCounter());

//            Containers<?> windowType = CraftContainer.getNotchInventoryType(inventory);
        Containers<?> windowType = (Containers<?>) craftContainer.getMethod("getNotchInventoryType", Inventory.class).invoke(null, inventory);
//            getPlayerConnection(entityPlayer).a(new PacketPlayOutOpenWindow(container.j, windowType, chatSerializer));
        Main.getInstance().adapter.sendPlayerPacket(player, new PacketPlayOutOpenWindow(container.j, windowType, chatSerializer));

//            entityPlayer.bR = container;
        for (Field f : EntityHuman.class.getFields()) {
            if (f.getType().equals(Container.class)) {
                f.set(entityPlayer, container);
                break;
            }
        }
        
        entityPlayer.a(container); //todo reflection
    }
    //fancyMessage
    
    //NBT tags
    public abstract Object /*NBTTagCompound*/ getNBTTag(Object nmsStackObject) throws InvocationTargetException, IllegalAccessException;
    public abstract Object /*NBTTagCompound*/ getCompound(Object tagObject, String name);
    public abstract void putString(Object tagObject, String name, String data);
    public abstract void putBoolean(Object tagObject, String name, boolean data);
    public abstract Object put(Object tagObject, String name, Object tagData);
    public abstract Object new_NBTTagList();
    public abstract Object stringTag_valueOf(String value);
    public abstract boolean listTag_addTag(Object tagListObject, int index, Object toAdd);
    //NBT tags
    
    //biomeTP
    public abstract List<String> availableBiomes();
    public abstract List<String> availableBiomes(World world);
    public abstract Pair<Location, String> biomeFinder(Player player, List<String> biomes, @Nonnull Location startLocation, Accuracy.AccuracySettings accuracy);
    public abstract ArrayList<BiomeTP.BiomeTPPresets.BiomePreset> loadPresetsFromWorld(World world);
    Pair<Location, String> legacyBiomeTP(int quartSize, int quartX, int quartZ, Rectangle searchArea, int increment, List<Integer> yLevels, World world, List<String> biomes) {
        
        //legacy biomeTP
        for (int squareSize = 0; squareSize <= quartSize; squareSize += increment) {
            for (int zOffset = -squareSize; zOffset <= squareSize; zOffset += increment) {
                boolean zEnd = Math.abs(zOffset) == squareSize;
                
                for (int xOffset = -squareSize; xOffset <= squareSize; xOffset += increment) {
                    boolean xEnd = Math.abs(zOffset) == squareSize;
                    if (!zEnd && !xEnd) continue;
                    
                    int newX = (quartX + xOffset) << 2;
                    int newZ = (quartZ + zOffset) << 2;
                    
                    if (!searchArea.contains(newX, newZ)) {
                        continue;
                    }
                    
                    for (int y : yLevels) {
                        Location testLocation = new Location(world, newX, y, newZ);
                        Biome biome = world.getBiome(testLocation);
                        
                        if (biomes.stream().anyMatch(b -> biome.name().equalsIgnoreCase(b))) {
                            return new Pair<>(testLocation, biome.name());
                        }
                    }
                }
            }
        }
        return null;
    }
    //biomeTP
    
    //featureTP
    public abstract List<String> availableFeatures() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException;
    public abstract List<String> availableFeatures(World world) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException;
    public abstract List<com.spaceman.tport.Pair<String, List<String>>> getFeatureTags(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException;
    public abstract Pair<Location, String> searchFeature(Player player, Location startLocation, List<String> features) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException;
    //featureTP
    
}
