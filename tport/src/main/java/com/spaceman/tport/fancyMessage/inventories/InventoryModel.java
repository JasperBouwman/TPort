package com.spaceman.tport.fancyMessage.inventories;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

import static com.spaceman.tport.commands.tport.ResourcePack.getResourcePackState;

public class InventoryModel {
    
    private final Material material;
    private String subDir = "";
    
    private final int customModelData;
    private NamespacedKey namespacedKey;
    
    public InventoryModel(Material material, int customModelData, String nameSpace, String key) {
        this.material = material;
        this.customModelData = customModelData;
        createNameSpaceKey(nameSpace, key);
    }
    public InventoryModel(Material material, InventoryModel previousModel, String nameSpace, String key) {
        this.material = material;
        this.customModelData = previousModel.getCustomModelData() + 1;
        createNameSpaceKey(nameSpace, key);
    }
    public InventoryModel(Material material, int customModelData, String nameSpace, String key, String subDir) {
        this.material = material;
        this.customModelData = customModelData;
        createNameSpaceKey(nameSpace, key);
        this.subDir = subDir;
    }
    public InventoryModel(Material material, InventoryModel previousModel, String nameSpace, String key, String subDir) {
        this.material = material;
        this.customModelData = previousModel.getCustomModelData() + 1;
        createNameSpaceKey(nameSpace, key);
        this.subDir = subDir;
    }
    
    private void createNameSpaceKey(String nameSpace, String key) {
        this.namespacedKey = NamespacedKey.fromString( (nameSpace + ":" + key).toLowerCase() );
    }
    
    public ItemStack getItem(Player player) {
        return getItem(player.getUniqueId());
    }
    public ItemStack getItem(UUID playerUUID) {
        ItemStack item = new ItemStack(material);
        applyModelData(item, this, playerUUID);
        return item;
    }
    
    public ItemStack setItem(Player player, ItemStack is) {
        applyModelData(is, this, player);
        return is;
    }
    
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public int getCustomModelData() {
        return customModelData;
    }
    
    public boolean hasSubDir() {
        return !subDir.isEmpty();
    }
    public String getSubDir() {
        return subDir;
    }
    
    public String getName() {
        return this.namespacedKey.getKey();
    }
    
    private ItemStack applyModelData(ItemStack is, InventoryModel modelData, Player player) {
        return applyModelData(is, modelData, player.getUniqueId());
    }
    private ItemStack applyModelData(ItemStack is, InventoryModel modelData, UUID uuid) {
        if (!getResourcePackState(uuid)) return is;
        
        if (!is.getType().equals(modelData.getMaterial())) {
            throw new IllegalArgumentException("Type of item stack is not the same as the model data: " + is.getType().name() + ", {" + modelData.getMaterial() + "}");
        }
        
        ItemMeta im = is.getItemMeta();
        if (im == null) return is;
        
        try {
            im.setCustomModelData(modelData.getCustomModelData());
        } catch (Exception ignore) { }
        
        try {
            im.setItemModel(namespacedKey);
        } catch (Exception ignore) { }
        
        is.setItemMeta(im);
        
        return is;
    }
    
}
