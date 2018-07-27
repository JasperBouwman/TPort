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

    public Message() {
    }

    public Message(TextComponent text) {
        this.components.add(text);
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

    public void addWhiteSpace() {
        addText(" ");
    }

    public ArrayList<TextComponent> getText() {
        return components;
    }

    public void clearMessage() {
        components.clear();
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

            String message = new Translate(components).translate.toString();

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

    public static class Translate {

        public StringBuilder translate;

        public Translate(String title, String author, ArrayList<BookPage> pages) {

            translate = new StringBuilder("{pages:[");

            if (pages != null && !pages.isEmpty()) {

                int pageNumber = 1;
                for (BookPage page : pages) {
                    translate.append("\"[\\\"\\\",");
                    translate.append(translatePage(page));
                    translate.append("]\"").append(pageNumber < pages.size() ? "," : "");
                    pageNumber++;
                }
            }

            translate.append(String.format("],\"title\":\"%s\",\"author\":\"%s\"}", title, author));
        }

        private Translate(ArrayList<TextComponent> texts) {
            translate = new StringBuilder();

            int textNumber = 1;

            for (TextComponent textComponent : texts) {

                if (textNumber == 1) {
                    translate.append("[\"\",");
                }

                translate.append(String.format("{\"text\":\"%s\",\"color\":\"%s\"%s%s%s}",
                        textComponent.getText(),
                        textComponent.getColor(),

                        translateAttributes2(textComponent),

                        (textComponent.hasClickEvent() ? String.format(",\"clickEvent\":{\"action\":\"%s\",\"value\":\"%s\"}",
                                textComponent.getClickEvent().getClickEvent(), textComponent.getClickEvent().getValue()) : ""),

                        (textComponent.hasHoverEvent() ? String.format("%s", translateHoverEvent2(textComponent.getHoverEvent())) : "")))

                        .append(textNumber < texts.size() ? "," : "");
                textNumber++;
            }
            translate.append("]");
        }

        private String translatePage(BookPage page) {

            StringBuilder str = new StringBuilder();
            int textNumber = 1;

            for (TextComponent textComponent : page.getText()) {

                if (textNumber == 1) {
                    str.append("\\\"\\\",");
                }

                str.append(String.format("{\\\"text\\\":\\\"%s\\\",\\\"color\\\":\\\"%s\\\"%s%s%s}",
                        textComponent.getText(),
                        textComponent.getColor(),

                        translateAttributes(textComponent),

                        (textComponent.hasClickEvent() ? String.format(",\\\"clickEvent\\\":{\\\"action\\\":\\\"%s\\\",\\\"value\\\":\\\"%s\\\"}",
                                textComponent.getClickEvent().getClickEvent(), textComponent.getClickEvent().getValue()) : ""),

                        (textComponent.hasHoverEvent() ? String.format("%s", translateHoverEvent(textComponent.getHoverEvent())) : "")))


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

        private String translateHoverEvent(HoverEvent hoverEvent) {
            StringBuilder str = new StringBuilder(",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[");
            int textNumber = 1;

            for (TextComponent textComponent : hoverEvent.getText()) {
                str.append(String.format("{\\\"text\\\":\\\"%s\\\",\\\"color\\\":\\\"%s\\\"%s}", textComponent.getText(), textComponent.getColor(), translateAttributes(textComponent)))

                        .append(textNumber < hoverEvent.getText().size() ? "," : "");
                textNumber++;
            }

            str.append("]}}");

            return str.toString();
        }

        private String translateHoverEvent2(HoverEvent hoverEvent) {
            StringBuilder str = new StringBuilder(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[");
            int textNumber = 1;

            for (TextComponent textComponent : hoverEvent.getText()) {
                str.append(String.format("{\"text\":\"%s\",\"color\":\"%s\"%s}", textComponent.getText(), textComponent.getColor(), translateAttributes2(textComponent)))

                        .append(textNumber < hoverEvent.getText().size() ? "," : "");
                textNumber++;
            }

            str.append("]}}");

            return str.toString();
        }
    }
}
