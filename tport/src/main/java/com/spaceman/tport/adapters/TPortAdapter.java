package com.spaceman.tport.adapters;

import com.spaceman.tport.Pair;
import com.spaceman.tport.biomeTP.BiomePreset;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.commands.tport.biomeTP.Accuracy;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.spaceman.tport.adapters.ReflectionManager.getField;

public abstract class TPortAdapter {
    
    public static HashMap<String, TPortAdapter> adapters = new HashMap<>();
    
    public abstract String getAdapterName();
    
    public /*WorldServer*/ Object getWorldServer(World world) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
    }
    
    public /*EntityPlayer*/ Object getEntityPlayer(Player player) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return player.getClass().getMethod("getHandle").invoke(player);
    }
    public /*PlayerConnection*/ Object getPlayerConnection(Player player) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        return getField(Class.forName("net.minecraft.server.network.PlayerConnection"), getEntityPlayer(player));
    }
    
    public abstract void sendPlayerPacket(Player player, Object packet) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException;

    //fancyMessage
    public abstract void sendMessage(Player player, String message) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, NoSuchFieldException;
    public abstract void sendTitle(Player player, String message, Message.TitleTypes titleType, int fadeIn, int displayTime, int fadeOut) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException;
    public abstract void sendInventory(Player player, String stringTitle, Inventory inventory) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, InstantiationException;
    public abstract void sendSignEditor(Player player, Location loc) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException;
    public abstract void setQuickTypeSignHandler(Player player) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException;
    public abstract void removeQuickTypeSignHandler(Player player) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException;
    public abstract void sendBlockChange(Player player, Location blockLoc, Material material) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;
    public abstract void sendBlockChange(Player player, Location blockLoc, Block block) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;
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
    public abstract List<String> availableBiomes() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException;
    public abstract List<String> availableBiomes(World world) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;
    public abstract Pair<Location, String> biomeFinder(Player player, List<String> biomes, @Nonnull Location startLocation, Accuracy.AccuracySettings accuracy) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException;
    public abstract ArrayList<BiomePreset> loadPresetsFromWorld(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException;
    //biomeTP
    
    //featureTP
    public abstract List<String> availableFeatures() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException;
    public abstract List<String> availableFeatures(World world) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException;
    public abstract List<com.spaceman.tport.Pair<String, List<String>>> getFeatureTags(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException;
    public abstract Pair<Location, String> searchFeature(Player player, Location startLocation, List<String> features) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException;
    //featureTP
    
}
