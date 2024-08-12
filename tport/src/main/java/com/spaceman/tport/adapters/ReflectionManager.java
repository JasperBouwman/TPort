package com.spaceman.tport.adapters;

import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionManager {
    
    public static String getServerClassesVersion() {
        String pack = Bukkit.getServer().getClass().getPackage().getName();
        
        // org.bukkit.craftbukkit length == 22
        if (pack.length() == 22) {
            return "";
        } else {
            return pack.replace(".", ",").split(",")[3] + ".";
        }
    }
    
    public static String getServerVersion() {
        String s = Bukkit.getVersion();
        int startIndex = s.indexOf(":");
        return s.substring(startIndex+2, s.length()-1);
    }
    
    public static <R, I> R getPrivateField(Class<R> r, I invoked) throws IllegalAccessException {
        if (invoked == null) return null;
        for (Field f : invoked.getClass().getDeclaredFields()) {
            if (f.getType().equals(r)) {
                f.setAccessible(true);
                return (R) f.get(invoked);
            }
        }
        return null;
    }
    
    public static <R, I> R getPrivateField(Class<R> r, I invoked, Class<?> c) throws IllegalAccessException {
        if (invoked == null) return null;
        for (Field f : c.getDeclaredFields()) {
            if (f.getType().equals(r)) {
                f.setAccessible(true);
                return (R) f.get(invoked);
            }
        }
        return null;
    }
    
    public static <R, I> R getField(Class<R> r, I invoked) throws IllegalAccessException {
        if (invoked == null) return null;
        for (Field f : invoked.getClass().getFields()) {
            if (f.getType().equals(r)) return (R) f.get(invoked);
        }
        return null;
    }
    
    public static <R, I> R get(Class<R> r, I invoked) throws InvocationTargetException, IllegalAccessException {
        if (invoked == null) return null;
        for (Method m : invoked.getClass().getMethods()) {
            if (!m.getReturnType().equals(r)) continue;
            if (m.getParameterCount() == 0) {
                return (R) m.invoke(invoked);
            }
        }
        return null;
    }
    
    public static <R, I, P> R get(Class<R> returnClass, I invoked, P parameter) throws InvocationTargetException, IllegalAccessException {
        for (Method m : invoked.getClass().getMethods()) {
            if (!m.getReturnType().equals(returnClass)) continue;
            Class<?>[] parameterTypes = m.getParameterTypes();
            if (parameterTypes.length != 1) continue;
            if (!parameterTypes[0].equals(parameter.getClass())) continue;
            
            return (R) m.invoke(invoked, parameter);
        }
        return null;
    }
    
    public static <R, I, P> R get(Class<R> returnClass, I invoked, P parameter, Class<?> parameterClass) throws InvocationTargetException, IllegalAccessException {
        for (Method m : invoked.getClass().getMethods()) {
            if (!m.getReturnType().equals(returnClass)) continue;
            Class<?>[] parameterTypes = m.getParameterTypes();
            if (parameterTypes.length != 1) continue;
            if (!parameterTypes[0].equals(parameterClass)) continue;
            
            return (R) m.invoke(invoked, parameter);
        }
        return null;
    }
    
    public static <R, I> R get(Class<R> r, I invoked, @Nullable Annotation annotation) throws InvocationTargetException, IllegalAccessException {
        if (invoked == null) return null;
        for (Method m : invoked.getClass().getMethods()) {
            if (!m.getReturnType().equals(r)) continue;
            if (annotation == null) { if (m.getAnnotations().length != 0) continue;
            } else if (!m.getAnnotations()[0].equals(annotation)) continue;
            if (m.getParameterCount() == 0) return (R) m.invoke(invoked);
        }
        return null;
    }
}
