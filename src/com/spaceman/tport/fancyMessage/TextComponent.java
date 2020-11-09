package com.spaceman.tport.fancyMessage;

import com.google.common.base.Strings;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.book.BookPage;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.events.ScoreEvent;
import com.spaceman.tport.fancyMessage.events.TextEvent;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.Main.getOrDefault;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public class TextComponent {
    
    public final static String APOSTROPHE = "\"";
    public final static String NEW_LINE = "\n";
    
    private String type = "text";
    private BookPage pageNumber = null;
    private String text;
    private String color;
    private String insertion = null;
    private List<Attribute> attributes;
    private List<TextEvent> textEvents;
    private Message with = new Message();
    
    public TextComponent() {
        this("", new MultiColor("#ffffff").getColorAsValue(), new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text) {
        this(text, new MultiColor("#ffffff").getColorAsValue());
    }
    
    public TextComponent(String text, String color) {
        this(text, color, new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text, ChatColor color) {
        this(text, color, new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text, ColorTheme.ColorType color) {
        this(text, color.name(), new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text, Color color) {
        this(text, new MultiColor(color).getColorAsValue(), new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text, MultiColor color) {
        this(text, color.getColorAsValue(), new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text, String color, List<TextEvent> textEvents, List<Attribute> attribute) {
        this.text = text;
        setColor(color);
        this.textEvents = getOrDefault(textEvents, new ArrayList<>());
        this.attributes = getOrDefault(attribute, new ArrayList<>());
    }
    
    public TextComponent(String text, ChatColor color, List<TextEvent> textEvents, List<Attribute> attribute) {
        this(text, color.name().toLowerCase(), textEvents, attribute);
    }
    
    @Override
    public String toString() {
        return getRawText();
    }
    
    public static TextComponent textComponent() {
        return new TextComponent("", new MultiColor("#ffffff").getColorAsValue(), null, null);
    }
    
    public static TextComponent textComponent(String text) {
        return new TextComponent(text, new MultiColor("#ffffff").getColorAsValue());
    }
    
    public static TextComponent textComponent(String text, String color) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue());
    }
    
    public static TextComponent textComponent(String text, ChatColor color) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue());
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type) {
        return new TextComponent(text, type.name());
    }
    
    public static TextComponent textComponent(String text, Color color) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue());
    }
    
    public static TextComponent textComponent(String text, MultiColor color) {
        return new TextComponent(text, color.getColorAsValue());
    }
    
    public static TextComponent textComponent(String text, String color, String insertion) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue()).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, ChatColor color, String insertion) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue()).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, String insertion) {
        return new TextComponent(text, type.name()).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, Color color, String insertion) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue()).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, MultiColor color, String insertion) {
        return new TextComponent(text, color.getColorAsValue()).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, String color, TextEvent... textEvents) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, ChatColor color, TextEvent... textEvents) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, TextEvent... textEvents) {
        return new TextComponent(text, type.name(), Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, Color color, TextEvent... textEvents) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, MultiColor color, TextEvent... textEvents) {
        return new TextComponent(text, color.getColorAsValue(), Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, String color, Attribute... attributes) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, ChatColor color, Attribute... attributes) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, Attribute... attributes) {
        return new TextComponent(text, type.name(), new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, Color color, Attribute... attributes) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, MultiColor color, Attribute... attributes) {
        return new TextComponent(text, color.getColorAsValue(), new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, String color, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), textEvents, attributes);
    }
    
    public static TextComponent textComponent(String text, ChatColor color, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, color, textEvents, attributes);
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, type.name(), textEvents, attributes);
    }
    
    public static TextComponent textComponent(String text, Color color, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), textEvents, attributes);
    }
    
    public static TextComponent textComponent(String text, MultiColor color, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, color.getColorAsValue(), textEvents, attributes);
    }
    
    public JSONObject translateJSON(ColorTheme theme) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(type, getText());
        jsonObject.put("color", translateColor(theme));
        if (!Strings.isNullOrEmpty(insertion)) jsonObject.put("insertion", insertion);
        attributes.forEach(attribute -> jsonObject.put(attribute.name().toLowerCase(), "true"));
        textEvents.forEach(event -> jsonObject.put(event.name(), event.translateJSON(theme)));
        if (!with.isEmpty()) {
            jsonObject.put("with", with.getText().stream().map(s -> s.translateJSON(theme)).collect(Collectors.toCollection(JSONArray::new)));
        }
        
        return jsonObject;
    }
    
    private String translateColor(ColorTheme theme) {
        return Arrays.stream(ColorTheme.ColorType.values())
                .filter(type -> type.name().equalsIgnoreCase(color))
                .findFirst().map(type -> type.getColor(theme).getColorAsValue()).orElse(color);
    }
    
    public void clearEvents() {
        textEvents = new ArrayList<>();
    }
    
    public void clearInteractiveEvents() {
        if (textEvents != null) {
            textEvents = textEvents.stream()
                    .filter(event -> event.getClass().getAnnotation(TextEvent.InteractiveTextEvent.class) == null)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }
    
    public String getText() {
        if (pageNumber != null) {
            return text.replace(BookPage.getActivePageReplacer(), String.valueOf(pageNumber.getPageNumber()));
        }
        return text;
    }
    
    public String getRawText() {
        return text;
    }
    
    public TextComponent setText(String text) {
        this.text = text;
        return this;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(ChatColor color) {
        this.color = new MultiColor(color).getColorAsValue();
    }
    
    public void setColor(String color) {
        if (Arrays.stream(ColorTheme.ColorType.values()).anyMatch(type -> type.name().equalsIgnoreCase(color))) {
            this.color = color;
        } else if (color.matches("#[0-9a-fA-F]{6}")) {
            this.color = color;
        } else {
            this.color = new MultiColor(color).getColorAsValue();
        }
    }
    
    public void setColor(Color color) {
        this.color = new MultiColor(color).getColorAsValue();
    }
    
    public void setColor(MultiColor color) {
        this.color = color.getColorAsValue();
    }
    
    public void setColor(ColorTheme.ColorType type) {
        this.color = type.name();
    }
    
    public List<Attribute> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
    
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }
    
    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }
    
    public List<TextEvent> getTextEvents() {
        return textEvents;
    }
    
    public <E extends TextEvent> E getTextEvent(Class<E> textEvent) {
        //noinspection unchecked
        return (E) textEvents.stream().filter(textEvent::isInstance).findFirst().orElse(null);
    }
    
    public ScoreEvent getScoreEvent() {
        return getTextEvent(ScoreEvent.class);
    }
    
    public ClickEvent getClickEvent() {
        return getTextEvent(ClickEvent.class);
    }
    
    public HoverEvent getHoverEvent() {
        return getTextEvent(HoverEvent.class);
    }
    
    public boolean hasTextEvent(Class<? extends TextEvent> textEvent) {
        return textEvents.stream().anyMatch(textEvent::isInstance);
    }
    
    public boolean hasScoreEvent() {
        return hasTextEvent(ScoreEvent.class);
    }
    
    public boolean hasClickEvent() {
        return hasTextEvent(ClickEvent.class);
    }
    
    public boolean hasHoverEvent() {
        return hasTextEvent(HoverEvent.class);
    }
    
    public <E extends TextEvent> E removeTextEvent(Class<E> textEvent) {
        TextEvent toRemove = textEvents.stream().filter(textEvent::isInstance).findFirst().orElse(null);
        if (toRemove != null) {
            textEvents.remove(toRemove);
        }
        return (E) toRemove;
    }
    
    public void addTextEvent(TextEvent textEvent) {
        removeTextEvent(textEvent.getClass());
        textEvents.add(textEvent);
    }
    
    public String getType() {
        return type;
    }
    
    public TextComponent setType(String type) {
        this.type = type.toLowerCase();
        return this;
    }
    
    public TextComponent setType(TextType type) {
        this.type = type.name().toLowerCase();
        return this;
    }
    
    public String getInsertion() {
        return insertion;
    }
    
    public TextComponent setInsertion(String insertion) {
        this.insertion = insertion;
        return this;
    }
    
    public TextComponent setTextAsInsertion() {
        this.insertion = getText();
        return this;
    }
    
    public BookPage getBookPage() {
        return pageNumber;
    }
    
    public TextComponent setBookPage(BookPage pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }
    
    public TextComponent addTranslateWith(TextComponent... text) {
        with.addText(text);
        return this;
    }
    
    public TextComponent addTranslateWith(Message message) {
        with.addMessage(message);
        return this;
    }
    
    public TextComponent removeTranslateWith(String simpleText) {
        with.removeText(simpleText);
        return this;
    }
    
    public TextComponent removeTranslateWith(TextComponent text) {
        with.removeText(text);
        return this;
    }
    
    public TextComponent removeTranslateWith(Message message) {
        with.removeMessage(message);
        return this;
    }
    
    public TextComponent setTranslateWith(TextComponent... text) {
        with = new Message(text);
        return this;
    }
    
    public TextComponent setTranslateWith(Message message) {
        with = message;
        return this;
    }
    
    public Message getTranslateWith() {
        return with;
    }
}
