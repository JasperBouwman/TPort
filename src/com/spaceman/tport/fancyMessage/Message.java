package com.spaceman.tport.fancyMessage;

import com.spaceman.tport.fancyMessage.book.BookPage;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class Message {

    private ArrayList<TextComponent> components = new ArrayList<>();
    private String defaultChatColor = "WHITE";

    public Message() {
    }

    public Message(TextComponent text) {
        this.components.add(text);

    }
    public Message(ChatColor defaultChatColor) {
        this.defaultChatColor = defaultChatColor.name();
    }

    public Message(TextComponent text, ChatColor defaultChatColor) {
        this.components.add(text);
        this.defaultChatColor = defaultChatColor.name();
    }

    public static Message message() {
        return new Message();
    }

    public void removeLast() {
        this.components.remove(components.size() - 1);
    }

    public void addText(TextComponent... text) {
        this.components.addAll(Arrays.asList(text));
    }

    public void addText(String simpleText) {
        addText(textComponent(simpleText));
    }

    public void addText(String simpleText, ChatColor color) {
        addText(textComponent(simpleText, color));
    }

    public void addMessage(Message message) {
        this.components.addAll(message.getText());
    }

    public void addWhiteSpace() {
        addText(" ");
    }

    public ArrayList<TextComponent> getText() {
        return components;
    }

    public void clearMessage() {
        components.clear();
    }

    public void sendTitle(Player player, TitleTypes titleTypes) {
        sendTitle(player, titleTypes.name(), -1, -1, -1);
    }

    public void sendTitle(Player player, String titleTypes) {
        sendTitle(player, titleTypes, -1, -1, -1);
    }

    public void sendTitle(Player player, TitleTypes titleType, int fadeIn, int displayTime, int fadeOut) {
        sendTitle(player, titleType.name(), fadeIn, displayTime, fadeOut);
    }

    public void sendTitle(Player player, String titleType, int fadeIn, int displayTime, int fadeOut) {

        boolean b = true;
        for (TitleTypes type : TitleTypes.values()) {
            if (type.name().equalsIgnoreCase(titleType)) {
                b = false;
            }
        }
        if (b) {
            titleType = "title";
        }

        try {

            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            Class<?> chatSerializer = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");
            Class<?> chatComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");

            Class<?> packet = Class.forName("net.minecraft.server." + version + ".PacketPlayOutTitle");

            String message = new Translate(components, defaultChatColor).translate.toString();

            Class enumClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutTitle$EnumTitleAction");
            Field actionF = enumClass.getDeclaredField(titleType.toUpperCase());
            actionF.setAccessible(true);

            Constructor constructor = packet.getConstructor(enumClass, chatComponent, int.class, int.class, int.class);

            Object text = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, message);
            Object packetFinal = constructor.newInstance(actionF.get(null), text, fadeIn, displayTime, fadeOut);

            connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet")).invoke(connection, packetFinal);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public void sendMessage(Player player) {

        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            Class<?> chatSerializer = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");
            Class<?> chatComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");

            Class<?> packet = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");

            Constructor constructor = packet.getConstructor(chatComponent);

            String message = new Translate(components, defaultChatColor).translate.toString();

            Object text = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, message);
            Object packetFinal = constructor.newInstance(text);

            Field field = packetFinal.getClass().getDeclaredField("a");
            field.setAccessible(true);
            field.set(packetFinal, text);
            connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet")).invoke(connection, packetFinal);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void broadcast() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.sendMessage(player);
        }
    }

    public enum TitleTypes {
        TITLE,
        ACTIONBAR,
        SUBTITLE
    }

    public static class Translate {

        public StringBuilder translate;

        public Translate(String title, String author, ArrayList<BookPage> pages, String defaultChatColor) {

            translate = new StringBuilder("{pages:[");

            if (pages != null && !pages.isEmpty()) {

                int pageNumber = 1;
                for (BookPage page : pages) {
                    if (page.getText().isEmpty()) {
                        translate.append("\"[\\\"\\\",");
                        translate.append("{\\\"text\\\":\\\"\\\"}");
                        translate.append("]\"").append(pageNumber < pages.size() ? "," : "");
                        pageNumber++;
                        continue;
                    }
                    translate.append("\"[\\\"\\\",");
                    translate.append(translatePage(page, defaultChatColor));
                    translate.append("]\"").append(pageNumber < pages.size() ? "," : "");
                    pageNumber++;
                }
            }

            translate.append(String.format("],\"title\":\"%s\",\"author\":\"%s\"}", title, author));
        }

        private Translate(ArrayList<TextComponent> texts, String defaultChatColor) {
            translate = new StringBuilder();

            int textNumber = 1;

            for (TextComponent textComponent : texts) {

                if (textNumber == 1) {
                    translate.append("[\"\",");
                }

                translate.append(String.format("{\"" + textComponent.getType() + "\":\"%s\",\"color\":\"%s\"%s%s%s%s}",
                        textComponent.getText(),
                        textComponent.getColor(),

                        textComponent.getInsertion() == null ? "" : ",\"insertion\":\"" + textComponent.getInsertion() + "\"",

                        translateAttributes2(textComponent),

                        (textComponent.hasClickEvent() ? String.format(",\"clickEvent\":{\"action\":\"%s\",\"value\":\"%s\"}",
                                textComponent.getClickEvent().getClickEvent(), textComponent.getClickEvent().getValue()) : ""),

                        (textComponent.hasHoverEvent() ? String.format("%s", translateHoverEvent2(textComponent.getHoverEvent(), defaultChatColor)) : "")))

                        .append(textNumber < texts.size() ? "," : "");
                textNumber++;
            }
            translate.append("]");
        }

        private String translatePage(BookPage page, String defaultChatColor) {

            StringBuilder str = new StringBuilder();
            int textNumber = 1;

            for (TextComponent textComponent : page.getText()) {

                if (textNumber == 1) {
                    str.append("\\\"\\\",");
                }

                str.append(String.format("{\\\"text\\\":\\\"%s\\\",\\\"color\\\":\\\"%s\\\"%s%s%s}",
                        textComponent.getText(),
                        (textComponent.getColor() == null ? defaultChatColor : textComponent.getColor()),

                        translateAttributes(textComponent),

                        (textComponent.hasClickEvent() ? String.format(",\\\"clickEvent\\\":{\\\"action\\\":\\\"%s\\\",\\\"value\\\":\\\"%s\\\"}",
                                textComponent.getClickEvent().getClickEvent(), textComponent.getClickEvent().getValue()) : ""),

                        (textComponent.hasHoverEvent() ? String.format("%s", translateHoverEvent(textComponent.getHoverEvent(), defaultChatColor)) : "")))


                        .append(textNumber < page.getText().size() ? "," : "");
                textNumber++;
            }
            return str.toString();
        }

        private String translateAttributes(TextComponent textComponent) {
            StringBuilder str = new StringBuilder();
            for (Attribute attribute : textComponent.getAttributes()) {
                str.append(String.format(",\\\"%s\\\":\\\"true\\\"", attribute.name().toLowerCase()));
            }
            return str.toString();
        }

        private String translateAttributes2(TextComponent textComponent) {
            StringBuilder str = new StringBuilder();
            for (Attribute attribute : textComponent.getAttributes()) {
                str.append(String.format(",\"%s\":\"true\"", attribute.name().toLowerCase()));
            }
            return str.toString();
        }

        private String translateHoverEvent(HoverEvent hoverEvent, String defaultChatColor) {
            StringBuilder str = new StringBuilder(",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[");
            int textNumber = 1;

            for (TextComponent textComponent : hoverEvent.getText()) {
                str.append(String.format("{\\\"text\\\":\\\"%s\\\",\\\"color\\\":\\\"%s\\\"%s}", textComponent.getText(),
                        (textComponent.getColor() == null ? defaultChatColor : textComponent.getColor()), translateAttributes(textComponent)))

                        .append(textNumber < hoverEvent.getText().size() ? "," : "");
                textNumber++;
            }

            str.append("]}}");

            return str.toString();
        }

        private String translateHoverEvent2(HoverEvent hoverEvent, String defaultChatColor) {
            StringBuilder str = new StringBuilder(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[");
            int textNumber = 1;

            for (TextComponent textComponent : hoverEvent.getText()) {
                str.append(String.format("{\"text\":\"%s\",\"color\":\"%s\"%s}", textComponent.getText(),
                        (textComponent.getColor() == null ? defaultChatColor : textComponent.getColor()), translateAttributes2(textComponent)))

                        .append(textNumber < hoverEvent.getText().size() ? "," : "");
                textNumber++;
            }

            str.append("]}}");

            return str.toString();
        }
    }
}
