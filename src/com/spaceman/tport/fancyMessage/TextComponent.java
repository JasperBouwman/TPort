package com.spaceman.tport.fancyMessage;

import com.google.common.base.Strings;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.fancyMessage.book.BookPage;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.events.ScoreEvent;
import com.spaceman.tport.fancyMessage.events.TextEvent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.Main.getOrDefault;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public class TextComponent {
    
    public final static String BOOK_APOSTROPHE = "\\\\\\\"";
    public final static String MESSAGE_APOSTROPHE = "\\\"";
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
        this("", "white", new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text) {
        this(text, "white");
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
    
    public TextComponent(String text, String color, List<TextEvent> textEvents, List<Attribute> attribute) {
        this.text = text;
        this.color = color;
        this.textEvents = getOrDefault(textEvents, new ArrayList<>());
        this.attributes = getOrDefault(attribute, new ArrayList<>());
    }
    
    public TextComponent(String text, ChatColor color, List<TextEvent> textEvents, List<Attribute> attribute) {
        this(text, color.name().toLowerCase(), textEvents, attribute);
    }
    
    public static TextComponent textComponent() {
        return new TextComponent("", "white", null, null);
    }
    
    public static TextComponent textComponent(String text) {
        return new TextComponent(text, "white");
    }
    
    public static TextComponent textComponent(String text, String color) {
        return new TextComponent(text, color);
    }
    
    public static TextComponent textComponent(String text, ChatColor color) {
        return new TextComponent(text, color);
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type) {
        return new TextComponent(text, type.name());
    }
    
    public static TextComponent textComponent(String text, String color, String insertion) {
        return new TextComponent(text, color).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, ChatColor color, String insertion) {
        return new TextComponent(text, color).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, String insertion) {
        return new TextComponent(text, type.name()).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, String color, TextEvent... textEvents) {
        return new TextComponent(text, color, Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, ChatColor color, TextEvent... textEvents) {
        return new TextComponent(text, color, Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, TextEvent... textEvents) {
        return new TextComponent(text, type.name(), Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, String color, Attribute... attributes) {
        return new TextComponent(text, color, new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, ChatColor color, Attribute... attributes) {
        return new TextComponent(text, color, new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, Attribute... attributes) {
        return new TextComponent(text, type.name(), new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, String color, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, color, textEvents, attributes);
    }
    
    public static TextComponent textComponent(String text, ChatColor color, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, color, textEvents, attributes);
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, type.name(), textEvents, attributes);
    }
    
    public String translateJSON(Message.TranslateMode mode, ColorTheme theme) {
        String q = mode.getQuote();
        return (String.format("{%s%s%s%s%s%s}",
                String.format("%s%s%s:%s%s%s", q, "color", q, q, translateColor(theme), q),
                hasScoreEvent() ? "" : String.format(",%s%s%s:%s%s%s", q, type, q, q, getText(), q),
                Strings.isNullOrEmpty(insertion) ? "" : String.format(",%s%s%s:%s%s%s", q, "insertion", q, q, insertion, q),
                attributes.stream().map(attribute -> String.format(",%s%s%s:%s%s%s", q, attribute.name().toLowerCase(), q, q, "true", q)).collect(Collectors.joining()),
                textEvents.stream().map(textEvent -> "," + textEvent.translateJSON(mode, theme)).collect(Collectors.joining()),
                with.isEmpty() ? "" : "," + q + "with" + q + ":[" + with.getText().stream().map(s -> s.translateJSON(mode, theme)).collect(Collectors.joining(",")) + "]"
        ));
    }
    
    private String translateColor(ColorTheme theme) {
        return Arrays.stream(ColorTheme.ColorType.values())
                .filter(type -> type.name().equals(color))
                .findFirst().map(type -> type.getColor(theme).name().toLowerCase()).orElse(color);
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
    
    public TextComponent setText(String text) {
        this.text = text;
        return this;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(ChatColor color) {
        setColor(color.name().toLowerCase());
    }
    
    public void setColor(String color) {
        this.color = color;
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
    
    public Message getTranslateWith() {
        return with;
    }
    
    public void setTranslateWith(TextComponent... text) {
        with = new Message(text);
    }
    
    public void setTranslateWith(Message message) {
        with = message;
    }
    
}
