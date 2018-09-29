package com.spaceman.tport.fancyMessage.book;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class Book {

    private final String title;
    private final String author;
    private final String defaultChatColor;

    private ArrayList<BookPage> pages = new ArrayList<>();

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.defaultChatColor = "BLACK";
    }
    public Book(String title, String author, ChatColor defaultColor) {
        this.title = title;
        this.author = author;
        this.defaultChatColor = defaultColor.name();
    }

    public BookPage createPage(TextComponent textComponent) {
        BookPage page = BookPage.newBookPage(textComponent, pages.size());
        pages.add(page);
        return page;
    }

    public BookPage createPage() {
        BookPage page = BookPage.newBookPage(pages.size());
        pages.add(page);
        return page;
    }

    @SuppressWarnings("deprecation")
    public ItemStack getBook() {

        ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);

        try {
            return Bukkit.getUnsafe().modifyItemStack(stack, translateBook());
        } catch (Throwable localThrowable) {
            return stack;
        }
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

    @SuppressWarnings("All")
    public void openBook(ItemStack book, Player player) {

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

            if (Integer.parseInt(version.split("_")[1]) > 12) {
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
        openBook(getBook(), player);
    }

    public String translateBook() {
        return new Message.Translate(title, author, pages, defaultChatColor).translate.toString();
    }
}
