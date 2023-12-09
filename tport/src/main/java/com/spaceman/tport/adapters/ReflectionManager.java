package com.spaceman.tport.adapters;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionManager {
    
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
