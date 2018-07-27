package com.spaceman.tport.fancyMessage.book;

import com.spaceman.tport.fancyMessage.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

public class Book {

    private final String title;
    private final String author;

    private ArrayList<BookPage> pages = new ArrayList<>();

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public void addPage(BookPage page) {
        pages.add(page);
    }

    public void addPage(BookPage... pages) {
        this.pages.addAll(Arrays.asList(pages));
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

    @SuppressWarnings("All")
    public void openBook(ItemStack book, Player player) {

        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, book);

        ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, (byte) 0);
        buf.writerIndex(1);

        try {
            //get minecraft server version
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            //get player handle
            Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            //get player connection
            Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            Class<?> packetDataSerializer = Class.forName("net.minecraft.server." + version + ".PacketDataSerializer");
            Constructor<?> packetDataSerializerConstructor = packetDataSerializer.getConstructor(ByteBuf.class);

            Class<?> packetPlayOutCustomPayload = Class.forName("net.minecraft.server." + version + ".PacketPlayOutCustomPayload");
            Constructor packetPlayOutCustomPayloadConstructor = packetPlayOutCustomPayload.getConstructor(String.class,
                    Class.forName("net.minecraft.server." + version + ".PacketDataSerializer"));

            connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet"))
                    .invoke(connection, packetPlayOutCustomPayloadConstructor.newInstance("MC|BOpen", packetDataSerializerConstructor.newInstance(buf)));

        } catch (Exception ex) {
            player.getInventory().addItem(book);
        }
        player.getInventory().setItem(slot, old);
    }

    public void openBook(Player player) {
        openBook(getBook(), player);
    }

    public String translateBook() {
        return new Message.Translate(title, author, pages).translate.toString();
    }
}
