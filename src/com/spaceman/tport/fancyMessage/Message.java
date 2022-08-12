package com.spaceman.tport.fancyMessage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spaceman.tport.fancyMessage.book.Book;
import com.spaceman.tport.fancyMessage.book.BookPage;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.events.ScoreEvent;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;

public class Message implements Cloneable {
    
    private final ArrayList<TextComponent> components = new ArrayList<>();
    
    public Message() {
    }
    
    public Message(TextComponent text) {
        this.components.add(text);
    }
    
    public Message(TextComponent... text) {
        this.components.addAll(Arrays.asList(text));
    }
    
    public Message(List<TextComponent> message) {
        this.components.addAll(message);
    }
    
    public Message(String simpleText, ChatColor color) {
        this.components.add(textComponent(simpleText, color));
    }
    
    public Message(String simpleText) {
        this.components.add(textComponent(simpleText));
    }
    
    @Override
    public String toString() {
        return components.stream().map(TextComponent::toString).collect(Collectors.joining());
    }
    
    public String translateString() {
        return components.stream().map(TextComponent::getText).collect(Collectors.joining());
    }
    
    public static void testAll(Player player) { //todo check all functions
        
        /*
         * Be careful that you don't use a text component in a hover event that is already in use
         *
         * example:
         * TextComponent text = new TextComponent("test", ChatColor.GREEN);
         * text.addTextEvent(new HoverEvent(text));
         *
         * This causes the translator to not complete the translation to JSON, and it won't give an error
         * */
        
        Message message = new Message();
        
        TextComponent newLine = new TextComponent("\n");
        
        TextComponent color1 = new TextComponent("c", ChatColor.GREEN);
        TextComponent color2 = new TextComponent("o", ChatColor.RED);
        TextComponent color3 = new TextComponent("l", ChatColor.BLUE);
        TextComponent color4 = new TextComponent("o", ChatColor.LIGHT_PURPLE);
        TextComponent color5 = new TextComponent("r", ChatColor.YELLOW);
        color1.addAttribute(Attribute.BOLD);
        color2.addAttribute(Attribute.ITALIC);
        color3.addAttribute(Attribute.OBFUSCATED);
        color4.addAttribute(Attribute.STRIKETHROUGH);
        color5.addAttribute(Attribute.UNDERLINED);
        
        TextComponent color1RGB = new TextComponent("c", Color.fromBGR(100, 100, 100));
        TextComponent color2RGB = new TextComponent("o", Color.TEAL);
        TextComponent color3RGB = new TextComponent("l", Color.OLIVE);
        TextComponent color4RGB = new TextComponent("o", Color.fromRGB(696969));
        TextComponent color5RGB = new TextComponent("r", Color.fromRGB(123456));
        color1RGB.addAttribute(Attribute.BOLD);
        color2RGB.addAttribute(Attribute.ITALIC);
        color3RGB.addAttribute(Attribute.OBFUSCATED);
        color4RGB.addAttribute(Attribute.STRIKETHROUGH);
        color5RGB.addAttribute(Attribute.UNDERLINED);
        
        TextComponent hover = new TextComponent("hover event", ChatColor.RED);
        HoverEvent hoverEvent = new HoverEvent();
        hoverEvent.addText(color1, color2, color3, color4, color5);
        hover.addTextEvent(hoverEvent);
        
        TextComponent runCommand = new TextComponent("run command say hi", ChatColor.BLUE);
        runCommand.addTextEvent(new ClickEvent(ClickEvent.RUN_COMMAND, "say hi"));
        TextComponent openURL = new TextComponent("open url https://noot.space", ChatColor.DARK_RED);
        openURL.addTextEvent(new ClickEvent(ClickEvent.OPEN_URL, "https://noot.space"));
        TextComponent suggestCommand = new TextComponent("suggest command say hi", ChatColor.DARK_PURPLE);
        suggestCommand.addTextEvent(new ClickEvent(ClickEvent.SUGGEST_COMMAND, "say hi"));
        TextComponent changePage = new TextComponent("open page 2", ChatColor.GREEN);
        changePage.addTextEvent(new ClickEvent(ClickEvent.CHANGE_PAGE, "2"));
        TextComponent copyText = new TextComponent("click to copy this", ChatColor.GREEN);
        copyText.addTextEvent(new ClickEvent(ClickEvent.COPY_TO_CLIPBOARD, "copied this"));
        
        TextComponent scoreText = new TextComponent("score: ", ChatColor.DARK_GREEN);
        TextComponent score = new TextComponent();
        score.setColor(ChatColor.GREEN);
        score.addTextEvent(new ScoreEvent("The_Spaceman", "dummyObjective"));
        
        TextComponent keybind = new TextComponent(Keybinds.DROP, ChatColor.DARK_AQUA);
        keybind.setType(TextType.KEYBIND);
        TextComponent keybindText = new TextComponent(" is your drop key", ChatColor.AQUA);
        
        TextComponent diamond_sword = new TextComponent("hover for leather_helmet", ChatColor.GREEN);
        ItemStack swordItem = new ItemStack(Material.LEATHER_HELMET, 2);
        ItemMeta swordMeta = swordItem.getItemMeta();
        swordMeta.setDisplayName("helmet");
        Damageable d = (Damageable) swordMeta;
        d.setDamage(100);
        
        ((LeatherArmorMeta) swordMeta).setColor(Color.fromRGB(12, 34, 45));
        
        swordMeta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
        swordMeta.addEnchant(Enchantment.MENDING, 3, true);
        
        swordMeta.setLore(Arrays.asList(ChatColor.GREEN + "line 1",
                "line 2",
                new MultiColor("#123456").getStringColor() + "line 3"));
        
        swordItem.setItemMeta(swordMeta);
        diamond_sword.addTextEvent(hoverEvent(swordItem));
        
        TextComponent selector = new TextComponent("@p", ChatColor.GRAY).setType(TextType.SELECTOR);
        TextComponent selectorText = new TextComponent(" should be your name", ChatColor.DARK_GRAY);
        
        TextComponent translateTitle = new TextComponent("gui.toTitle", ChatColor.GOLD).setType(TextType.TRANSLATE);
        
        TextComponent translateWith = new TextComponent("translate with 1 '%s', with 2 '%s'", ChatColor.RED).setType(TextType.TRANSLATE);
        translateWith.addTranslateWith(new TextComponent("1", ChatColor.BLUE), new TextComponent("2", ChatColor.BLACK));
        
        message.addText(color1, color2, color3, color4, color5, newLine,
                color1RGB, color2RGB, color3RGB, color4RGB, color5RGB, newLine,
                hover, newLine,
                runCommand, newLine,
                openURL, newLine,
                suggestCommand, newLine,
                changePage, newLine,
                copyText, newLine,
                scoreText, score, newLine,
                keybind, keybindText, newLine,
                diamond_sword, newLine,
                selector, selectorText, newLine,
                translateTitle, newLine,
                translateWith
        );
        
        message.sendMessage(player);
        
        ItemStack is = new ItemStack(Material.STONE);
        Message displayName = new Message();
        displayName.addText(textComponent("key of forward: ", ChatColor.RED));
        displayName.addText(textComponent(Keybinds.FORWARD, ChatColor.BLUE).setType(TextType.KEYBIND));
        MessageUtils.setCustomItemData(is, ColorTheme.getTheme(player), displayName, null);
        Message lore1 = new Message();
        lore1.addText(textComponent("line 1, component 1", ChatColor.DARK_AQUA), textComponent("line 1, component 2", ChatColor.BLUE));
        Message lore2 = new Message();
        lore2.addText(textComponent("line 2", ChatColor.AQUA));
        MessageUtils.setCustomLore(is, ColorTheme.getTheme(player), lore1, lore2);
        player.getInventory().addItem(is);
        
        Message title = new Message();
        title.addText(textComponent("title", ChatColor.GREEN));
        title.sendTitle(player, TitleTypes.TITLE);
        title.removeLast();
        title.addText(textComponent("actionbar", ChatColor.GREEN));
        title.sendTitle(player, TitleTypes.ACTIONBAR);
        title.removeLast();
        title.addText(textComponent("subtitle", ChatColor.GREEN));
        title.sendTitle(player, TitleTypes.SUBTITLE);
        
        Book book = new Book("All functions", "The_Spaceman");
        BookPage page = book.createPage();
        page.addMessage(message);
        BookPage page2 = book.createPage();
        page2.addText(scoreText, score, newLine,
                keybind, keybindText, newLine,
                diamond_sword, newLine,
                selector, selectorText, newLine,
                translateTitle, newLine,
                translateWith);
        
        book.openBook(player);
        player.getInventory().addItem(book.getWritableBook());
        player.getInventory().addItem(book.getWrittenBook(player));
    }
    
    public void removeLast() {
        if (!this.components.isEmpty()) {
            this.components.remove(components.size() - 1);
        }
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
    
    public void addText(String simpleText, String color) {
        addText(textComponent(simpleText, color));
    }
    
    public void addText(String simpleText, Color color) {
        addText(textComponent(simpleText, color));
    }
    
    public void addText(String simpleText, MultiColor color) {
        addText(textComponent(simpleText, color));
    }
    
    public void addText(String simpleText, ColorTheme.ColorType color) {
        addText(textComponent(simpleText, color));
    }
    
    public void removeText(String simpleText) {
        ArrayList<TextComponent> toRemove = new ArrayList<>();
        for (TextComponent component : components) {
            if (component.getText().equals(simpleText)) {
                toRemove.add(component);
            }
        }
        components.removeAll(toRemove);
    }
    
    public void removeText(TextComponent textComponent) {
        components.remove(textComponent);
    }
    
    public void addMessage(Message message) {
        if (message != null) {
            this.components.addAll(message.getText());
        }
    }
    
    public void removeMessage(Message message) {
        for (TextComponent textComponent : message.getText()) {
            removeText(textComponent);
        }
    }
    
    public void addWhiteSpace() {
        addText(" ");
    }
    
    public void addNewLine() {
        addText(TextComponent.NEW_LINE);
    }
    
    public ArrayList<TextComponent> getText() {
        return components;
    }
    
    public void clearMessage() {
        components.clear();
    }
    
    public boolean isEmpty() {
        return components.isEmpty();
    }
    
    public void sendTitle(Player player, TitleTypes titleTypes) {
        sendTitle(player, titleTypes, -1, -1, -1);
    }
    
    public void sendTitle(Player player, TitleTypes titleType, int fadeIn, int displayTime, int fadeOut) {
        
        try {
            EntityPlayer entityPlayer = (EntityPlayer) player.getClass().getMethod("getHandle").invoke(player);
            
            IChatMutableComponent text = IChatBaseComponent.ChatSerializer.a(this.translateJSON(player));
            
            Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game." + titleType.mcClass);
            Class<?> chatComponent = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
            Packet<?> packetObject = (Packet<?>) packetClass.getConstructor(chatComponent).newInstance(text);
            
            if (fadeIn != -1 || displayTime != -1 || fadeOut != -1) {
                ClientboundSetTitlesAnimationPacket clientboundSetTitlesAnimationPacket = new ClientboundSetTitlesAnimationPacket(fadeIn, displayTime, fadeOut);
                entityPlayer.b.a(clientboundSetTitlesAnimationPacket);
            }
            entityPlayer.b.a(packetObject);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void sendAndTranslateMessage(Player player) {
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        if (playerLang == null) { //custom language
            sendMessage(player);
        } else {
            MessageUtils.translateMessage(this, playerLang).sendMessage(player);
        }
    }
    
    public void sendMessage(Player player) {
        if (player == null) {
            return;
        }
        try {
            EntityPlayer entityPlayer = (EntityPlayer) player.getClass().getMethod("getHandle").invoke(player);
            entityPlayer.b.a(new ClientboundSystemChatPacket(this.translateJSON(player), false));
        } catch (Exception ex) {
            ex.printStackTrace();
            player.sendMessage(this.translateString());
        }
    }
    
    public void broadcast() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.sendMessage(player);
        }
    }
    
    public String translateJSON(Player player) {
        return translateJSON(ColorTheme.getTheme(player));
    }
    
    public String translateJSON(ColorTheme theme) {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add("");
        components.stream().map(t -> t.translateJSON(theme)).forEach(jsonArray::add);
        return jsonArray.toString();
    }
    
    @Override
    public Object clone() {
        Message message = new Message();
        this.components.stream().map(component -> (TextComponent) component.clone()).forEach(message::addText);
        return message;
    }
    
    public enum TitleTypes {
        TITLE("ClientboundSetTitleTextPacket"),
        ACTIONBAR("ClientboundSetActionBarTextPacket"),
        SUBTITLE("ClientboundSetSubtitleTextPacket");
        
        private final String mcClass;
        
        TitleTypes(String mcClass) {
            this.mcClass = mcClass;
        }
    }
}
