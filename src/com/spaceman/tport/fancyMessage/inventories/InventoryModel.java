package com.spaceman.tport.fancyMessage.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

import static com.spaceman.tport.commands.tport.ResourcePack.getResourcePackState;

public class InventoryModel {
    
    private final Material material;
    private final int customModelData;
    private String subDir = "";
    
    public InventoryModel(Material material, int customModelData) {
        this.material = material;
        this.customModelData = customModelData;
    }
    public InventoryModel(Material material, InventoryModel previousModel) {
        this.material = material;
        this.customModelData = previousModel.getCustomModelData() + 1;
    }
    public InventoryModel(Material material, int customModelData, String subDir) {
        this.material = material;
        this.customModelData = customModelData;
        this.subDir = subDir;
    }
    public InventoryModel(Material material, InventoryModel previousModel, String subDir) {
        this.material = material;
        this.customModelData = previousModel.getCustomModelData() + 1;
        this.subDir = subDir;
    }
    
    public ItemStack getItem(Player player) {
        return getItem(player.getUniqueId());
    }
    public ItemStack getItem(UUID playerUUID) {
        ItemStack item = new ItemStack(material);
        applyModelData(item, this, playerUUID);
        return item;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public int getCustomModelData() {
        return customModelData;
    }
    
    public boolean hasSubDir() {
        return !subDir.equals("");
    }
    public String getSubDir() {
        return subDir;
    }
    
    private static ItemStack applyModelData(ItemStack is, InventoryModel modelData, Player player) {
        return applyModelData(is, modelData, player.getUniqueId());
    }
    private static ItemStack applyModelData(ItemStack is, InventoryModel modelData, UUID uuid) {
        if (!getResourcePackState(uuid)) return is;
        
        if (!is.getType().equals(modelData.getMaterial())) {
            throw new IllegalArgumentException("Type of item stack is not the same as the model data: " + is.getType().name() + ", {" + modelData.getMaterial() + "}");
        }
        
        ItemMeta im = is.getItemMeta();
        if (im == null) return is;
        
        im.setCustomModelData(modelData.getCustomModelData());
        is.setItemMeta(im);
        
        return is;
    }
    
}
