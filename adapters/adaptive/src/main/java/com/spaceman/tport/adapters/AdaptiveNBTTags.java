package com.spaceman.tport.adapters;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.InvocationTargetException;

public abstract class AdaptiveNBTTags extends AdaptiveBiomeTP {
    
    @Override
    public Object getNBTTag(Object nmsStackObject) throws InvocationTargetException, IllegalAccessException {
        net.minecraft.world.item.ItemStack nmsStack = (ItemStack) nmsStackObject;
        return ReflectionManager.get(NBTTagCompound.class, nmsStack, null); //search for not @Nullable
    }
    
    @Override
    public Object getCompound(Object tagObject, String name) {
        NBTTagCompound tag = (NBTTagCompound) tagObject;
        return tag.p(name);
    }
    
    @Override
    public void putString(Object tagObject, String name, String data) {
        NBTTagCompound tag = (NBTTagCompound) tagObject;
        tag.a(name, data);
    }
    
    @Override
    public void putBoolean(Object tagObject, String name, boolean data) {
        NBTTagCompound tag = (NBTTagCompound) tagObject;
        tag.a(name, data);
    }
    
    @Override
    public Object put(Object tagObject, String name, Object tagData) {
        NBTTagCompound tag = (NBTTagCompound) tagObject;
        return tag.a(name, (NBTBase) tagData);
    }
    
    @Override
    public Object new_NBTTagList() {
        return new NBTTagList();
    }
    
    @Override
    public Object stringTag_valueOf(String value) {
        return NBTTagString.a(value);
    }
    
    @Override
    public boolean listTag_addTag(Object tagListObject, int index, Object toAdd) {
        NBTTagList tagList = (NBTTagList) tagListObject;
        return tagList.b(index, (NBTBase) toAdd);
    }
}
