package com.spaceman.tport.fancyMessage.book;

import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Book {
    
    private final String title;
    private final String author;
    private final ArrayList<BookPage> pages = new ArrayList<>();
    
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }
    
    @Override
    public String toString() {
        return pages.stream().map(BookPage::toString).collect(Collectors.joining());
    }
    
    public BookPage createPage(TextComponent textComponent) {
        BookPage page = new BookPage(textComponent);
        addPage(page);
        return page;
    }
    
    public BookPage createPage() {
        BookPage page = new BookPage();
        addPage(page);
        return page;
    }
    
    @SuppressWarnings("deprecation")
    public ItemStack getWritableBook() {
        ItemStack stack = new ItemStack(Material.WRITABLE_BOOK);
        try {
            return Bukkit.getUnsafe().modifyItemStack(stack, translateString());
        } catch (Throwable localThrowable) {
            return stack;
        }
    }
    
    @SuppressWarnings("deprecation")
    public ItemStack getWrittenBook(@Nullable Player player) {
        ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
        try {
            return Bukkit.getUnsafe().modifyItemStack(stack, translateJSON(
                    player == null ? ColorTheme.getDefaultTheme(ColorTheme.getDefaultThemes().get(0)) : ColorTheme.getTheme(player)));
        } catch (Throwable localThrowable) {
            return stack;
        }
    }
    
    @SuppressWarnings("All")
    public static void openBook(ItemStack book, Player player) {
        
        if (!book.getType().equals(Material.WRITTEN_BOOK)) {
            throw new IllegalArgumentException("Given item is not a written book");
        }
        
        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, book);
        
        ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, (byte) 0);
        buf.writerIndex(1);
        
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            
            Class<?> packetDataSerializer = Class.forName("net.minecraft.server." + version + ".PacketDataSerializer");
            Constructor<?> packetDataSerializerConstructor = packetDataSerializer.getConstructor(ByteBuf.class);
            Class<?> packetPlayOutCustomPayload = Class.forName("net.minecraft.server." + version + ".PacketPlayOutCustomPayload");
            
            if (Integer.parseInt(version.split("_")[1]) >= 14) {
//                ((CraftPlayer)player).getHandle().a(new net.minecraft.server.v1_14_R1.ItemStack(Items.WRITTEN_BOOK), EnumHand.MAIN_HAND);
//                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenBook(EnumHand.MAIN_HAND));
                
                Field mainHand = Class.forName("net.minecraft.server." + version + ".EnumHand").getDeclaredField("MAIN_HAND");
                mainHand.setAccessible(true);
                
                Object openBook = Class.forName("net.minecraft.server." + version + ".PacketPlayOutOpenBook")
                        .getConstructor(Class.forName("net.minecraft.server." + version + ".EnumHand"))
                        .newInstance(mainHand.get(null));
                
                connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet"))
                        .invoke(connection, openBook);
                
            } else if (Integer.parseInt(version.split("_")[1]) > 12) {
                Constructor<?> minecraftKeyConstructor = Class.forName("net.minecraft.server." + version + ".MinecraftKey").getConstructor(String.class);
                
                Constructor packetPlayOutCustomPayloadConstructor = packetPlayOutCustomPayload.getConstructor(
                        Class.forName("net.minecraft.server." + version + ".MinecraftKey"), Class.forName("net.minecraft.server." + version + ".PacketDataSerializer"));
                
                connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet"))
                        .invoke(connection, packetPlayOutCustomPayloadConstructor.newInstance(minecraftKeyConstructor.newInstance("minecraft:book_open"),
                                packetDataSerializerConstructor.newInstance(buf)));
            } else {
                Constructor packetPlayOutCustomPayloadConstructor = packetPlayOutCustomPayload.getConstructor(String.class,
                        Class.forName("net.minecraft.server." + version + ".PacketDataSerializer"));
                
                connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet"))
                        .invoke(connection, packetPlayOutCustomPayloadConstructor.newInstance("MC|BOpen", packetDataSerializerConstructor.newInstance(buf)));
            }
        } catch (Exception ex) {
            player.getInventory().setItem(slot, old);
            player.getInventory().addItem(book);
            ex.printStackTrace();
            return;
        }
        player.getInventory().setItem(slot, old);
    }
    
    public void openBook(Player player) {
        openBook(getWrittenBook(player), player);
    }
    
    public String translateString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("author", author);
    
        JSONArray jsonArray = new JSONArray();
        pages.stream().map(BookPage::translateString).forEach(jsonArray::add);
        jsonObject.put("pages", jsonArray);
    
        return jsonObject.toString().replaceAll("\\\\/", "/").replace("\\n", "\n");
    }
    
    public String translateJSON(Player player) {
        return translateJSON(ColorTheme.getTheme(player));
    }
    
    public String translateJSON(ColorTheme theme) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("author", author);
    
        JSONArray jsonArray = new JSONArray();
        pages.stream().map(p -> p.translateJSON(theme)).forEach(jsonArray::add);
        jsonObject.put("pages", jsonArray);
    
        return jsonObject.toString().replaceAll("\\\\{3}/", "/");
    }
    
    public ArrayList<BookPage> getPages() {
        return pages;
    }
    
    public int getPageNumber(BookPage page) {
        int i = 1;
        for (BookPage tmpPage : pages) {
            if (tmpPage.equals(page)) {
                return i;
            }
            i++;
        }
        return -1;
    }
    
    public void addBook(Book book) {
        for (BookPage page : book.getPages()) {
            addPage(page);
        }
    }
    
    public void setPage(int page, BookPage bookPage) {
        pages.remove(bookPage);
        if (page > pages.size()) {
            pages.add(bookPage);
            bookPage.setPageNumber(pages.size());
        } else {
            if (page < 1) {
                page = 1;
            }
            BookPage newPage = bookPage;
            for (int i = page; i <= pages.size(); i++) {
                BookPage tmpPage = pages.get(i - 1);
                newPage.setPageNumber(i);
                pages.set(i - 1, newPage);
                newPage = tmpPage;
            }
            addPage(newPage);
        }
    }
    
    public void addPage(BookPage bookPage) {
        setPage(pages.size() + 1, bookPage);
    }
}
