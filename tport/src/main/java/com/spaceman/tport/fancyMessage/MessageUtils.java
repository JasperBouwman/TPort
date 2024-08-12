package com.spaceman.tport.fancyMessage;

import com.google.common.base.CharMatcher;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.adapters.TPortAdapter;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.resourcePack.ResolutionCommand;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.encapsulation.Encapsulation;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.language.Language;
import com.spaceman.tport.fancyMessage.markdown.FancyNodeRenderer;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import com.spaceman.tport.tpEvents.TPRestriction;
import com.spaceman.tport.tport.TPort;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class MessageUtils {
    
    public static JsonObject toString(ItemStack is) {
        JsonObject item = new JsonObject();
        
        item.addProperty("id", is.getType().name().toLowerCase());
        item.addProperty("Count", is.getAmount());
        
        if (is.hasItemMeta()) {
            ItemMeta im = is.getItemMeta();
            item.add("tag", toString(im));
        }
        
        return item;
    }
    
    public static JsonObject toString(ItemMeta im) {
        JsonObject tags = new JsonObject();
        if (im instanceof Damageable) {
            tags.addProperty("Damage", ((Damageable) im).getDamage());
        }
        
        if (im instanceof Repairable) {
            tags.addProperty("repairCost", ((Repairable) im).getRepairCost());
        }
        
        if (im instanceof CrossbowMeta crossbowMeta) {
            if (crossbowMeta.hasChargedProjectiles()) {
//                JsonArray JsonArray = new JsonArray();
//                JsonArray.addAll(crossbowMeta.getChargedProjectiles());
                tags.addProperty("Charged", 1);
            }
        }
        
        if (im.isUnbreakable()) {
            tags.addProperty("Unbreakable", 1);
        }
        
        if (im.hasDisplayName() || im.hasLore() || im instanceof LeatherArmorMeta) {
            JsonObject display = new JsonObject();
            
            if (im.hasDisplayName()) {
                JsonObject displayName = new JsonObject();
                displayName.addProperty("text", im.getDisplayName());
                display.addProperty("Name", displayName.toString());
            }
            if (im.hasLore()) {
                JsonArray JsonArray = new JsonArray();
                //noinspection ConstantConditions
                im.getLore().forEach(lore -> {
                    JsonObject lorePiece = new JsonObject();
                    lorePiece.addProperty("text", lore);
                    JsonArray.add(lorePiece.toString());
                });
                display.add("Lore", JsonArray);
            }
            if (im instanceof LeatherArmorMeta) {
                display.addProperty("color", ((LeatherArmorMeta) im).getColor().asRGB());
            }
            
            tags.add("display", display);
        }
        
        if (im.hasEnchants()) {
            JsonArray JsonArray = new JsonArray();
            for (Enchantment enchantment : im.getEnchants().keySet()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", enchantment.getKey().toString());
                jsonObject.addProperty("lvl", im.getEnchantLevel(enchantment));
                JsonArray.add(jsonObject);
            }
            tags.add("Enchantments", JsonArray);
        }
        
        if (im.hasCustomModelData()) {
            tags.addProperty("CustomModelData", im.getCustomModelData());
        }
        
        byte flag = 0;
        for (ItemFlag itemFlag : ItemFlag.values()) {
            if (im.hasItemFlag(itemFlag)) {
                flag += 1 << itemFlag.ordinal();
            }
        }
        if (flag != 0) {
            tags.addProperty("HideFlags", flag);
        }
        
        return tags;
    }
    
    public static Message createColorGradient(String text, List<MultiColor> colorList) {
        Message message = new Message();
        
        char[] chars = text.toCharArray();
        List<MultiColor> gradient = createGradient(chars.length, colorList);
        
        for (int i = 0; i < chars.length; i++) {
            message.addText(textComponent(String.valueOf(chars[i]), gradient.get(i)));
        }
        
        return message;
    }
    private static List<MultiColor> createGradient(int size, List<MultiColor> colorList) {
        if (colorList.size() == 2) {
            List<MultiColor> newColorList = new ArrayList<>();
            
            java.awt.Color c1 = colorList.get(0).getColor();
            java.awt.Color c2 = colorList.get(1).getColor();
            for (int i = 0; i < size; i++) {
                float ratio = (float) i / (float) size;
                int red = (int) (c2.getRed() * ratio + c1.getRed() * (1 - ratio));
                int green = (int) (c2.getGreen() * ratio + c1.getGreen() * (1 - ratio));
                int blue = (int) (c2.getBlue() * ratio + c1.getBlue() * (1 - ratio));
                java.awt.Color c = new java.awt.Color(red, green, blue);
                newColorList.add(new MultiColor(c));
            }
            
            return newColorList;
        } else if (colorList.size() > 2) {
            float[] fractions = new float[colorList.size()];
            
            double fractionSize = ((double) size) / (colorList.size() - 1);
            for (int i = 1; i < colorList.size() - 1; i++) {
                fractions[i] = (float) ((fractionSize * i) / size);
            }
            fractions[0] = 0;
            fractions[fractions.length - 1] = 1;
            
            LinearGradientPaint linearGradientPaint = new LinearGradientPaint(0, 0, size, 0, fractions,
                    colorList.stream().map(MultiColor::getColor).toArray(java.awt.Color[]::new));
            
            BufferedImage image = new BufferedImage(size, 1, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            Rectangle2D rectangle2D = new Rectangle2D.Double(0, 0, size, 1);
            g.setPaint(linearGradientPaint);
            g.fill(rectangle2D);
            g.dispose();
            
            List<MultiColor> newColorList = new ArrayList<>();
            
            for (int x = 0; x < image.getWidth(); x++) {
                newColorList.add(new MultiColor(new java.awt.Color(image.getRGB(x, 0))));
            }
            return newColorList;
        } else if (colorList.size() == 1) {
            return IntStream.range(0, size).mapToObj(i -> colorList.get(0)).collect(Collectors.toList());
        } else {
            return IntStream.range(0, size).mapToObj(i -> new MultiColor(ChatColor.WHITE)).collect(Collectors.toList());
        }
    }
    
    public static Message toMessage(BufferedImage image) {
        Message message = new Message();
        
        if (image.getWidth() > 34) {
            image = getScaledImage(image, 34, image.getHeight());
        }
        if (image.getHeight() > 20) {
            image = getScaledImage(image, image.getWidth(), 20);
        }
        
        for (int height = 0; height < image.getHeight(); height++) {
            for (int width = 0; width < image.getWidth(); width++) {
                message.addText(textComponent("█", new MultiColor(new Color(image.getRGB(width, height)))));
            }
            message.addText(textComponent("\n"));
        }
        message.removeLast();
        
        return message;
    }
    private static BufferedImage getScaledImage(BufferedImage src, int w, int h) {
        int original_width = src.getWidth();
        int original_height = src.getHeight();
        int bound_width = w;
        int bound_height = h;
        int new_width = original_width;
        int new_height = original_height;
        
        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }
        
        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }
        
        BufferedImage resizedImg = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, new_width, new_height);
        g2.drawImage(src, 0, 0, new_width, new_height, null);
        g2.dispose();
        return resizedImg;
    }
    
    public static HashMap<String, ArgumentTranslator> argumentTranslator = new HashMap<>();
    static {
        argumentTranslator.put("null", (text, object, color, varColor) -> {
            if (object == null) {
                text.addTranslateWith(textComponent("null", varColor));
                return true;
            }
            return false;
        });
        argumentTranslator.put("message", (text, object, color, varColor) -> {
            if (object instanceof Message) {
                text.addTranslateWith((Message) object);
                return true;
            }
            return false;
        });
        argumentTranslator.put("textComponent", (text, object, color, varColor) -> {
            if (object instanceof TextComponent) {
                text.addTranslateWith((TextComponent) object);
                return true;
            }
            return false;
        });
        argumentTranslator.put("tport", (text, object, color, varColor) -> {
            if (object instanceof TPort tport) {
                Encapsulation encapsulation = asTPort(tport);
                
                Message component = encapsulation.toMessage(color, varColor);
                component.addTextEvent(encapsulation.getHoverEvent());
                component.addTextEvent(encapsulation.getClickEvent());
                component.setInsertion(encapsulation.getInsertion());
                text.addTranslateWith(component);
                return true;
            }
            return false;
        });
        argumentTranslator.put("player", (text, object, color, varColor) -> {
            if (object instanceof Player player) {
                Encapsulation encapsulation = asPlayer(player);
                
                Message component = encapsulation.toMessage(color, varColor);
                component.addTextEvent(encapsulation.getHoverEvent());
                component.addTextEvent(encapsulation.getClickEvent());
                component.setInsertion(encapsulation.getInsertion());
                text.addTranslateWith(component);
                return true;
            }
            return false;
        });
        argumentTranslator.put("encapsulation", (text, object, color, varColor) -> {
            if (object instanceof Encapsulation encapsulation) {
                Message component = encapsulation.toMessage(color, varColor);
                component.addTextEvent(encapsulation.getHoverEvent());
                component.addTextEvent(encapsulation.getClickEvent());
                component.setInsertion(encapsulation.getInsertion());
                text.addTranslateWith(component);
                return true;
            }
            return false;
        });
        argumentTranslator.put("messageDescription", (text, object, color, varColor) -> {
            if (object instanceof MessageDescription messageDescription) {
                Message component = messageDescription.getName(color, varColor);
                Message description = messageDescription.getDescription();
                if (description != null && !description.isEmpty()) component.addTextEvent(new HoverEvent(description));
                component.setInsertion(messageDescription.getInsertion());
                text.addTranslateWith(component);
                return true;
            }
            return false;
        });
        argumentTranslator.put("restriction", (text, object, color, varColor) -> {
            if (object instanceof TPRestriction restriction) {
                TextComponent component = new TextComponent(restriction.getRestrictionName(), varColor);
                component.addTextEvent(new HoverEvent(restriction.getDescription()));
                component.setInsertion(restriction.getRestrictionName());
                text.addTranslateWith(component);
                return true;
            }
            return false;
        });
        argumentTranslator.put("clickType", (text, object, color, varColor) -> {
            if (object instanceof ClickType clickType) {
                TextComponent component;
                if (clickType == ClickType.CONTROL_DROP || clickType == ClickType.DROP) {
                    component = textComponent("tport.fancyMessage.MessageUtils.clickType." + clickType.name(), varColor)
                            .addTranslateWith(textComponent(Keybinds.DROP, varColor).setType(TextType.KEYBIND))
                            .setType(TextType.TRANSLATE);
                } else {
                    component = textComponent("tport.fancyMessage.MessageUtils.clickType." + clickType.name(), varColor).setType(TextType.TRANSLATE);
                }
                component.setInsertion(clickType.name());
                text.addTranslateWith(component);
                return true;
            }
            return false;
        });
        argumentTranslator.put("world", (text, object, color, varColor) -> {
            if (object instanceof World world) {
                TextComponent component = new TextComponent(world.getName(), varColor);
                String command = "/tport world " + world.getName();
                component.addTextEvent(hoverEvent(command, ColorTheme.ColorType.infoColor));
                component.addTextEvent(ClickEvent.runCommand(command));
                component.setInsertion(world.getName());
                text.addTranslateWith(component);
                return true;
            }
            return false;
        });
        argumentTranslator.put("particleAnimation", (text, object, color, varColor) -> {
            if (object instanceof ParticleAnimation particleAnimation) {
                TextComponent component = new TextComponent(particleAnimation.getAnimationName(), varColor);
                component.addTextEvent(new HoverEvent(particleAnimation.getDescription()));
                component.setInsertion(particleAnimation.getAnimationName());
                text.addTranslateWith(component);
                return true;
            }
            return false;
        });
        argumentTranslator.put("itemStack", (text, object, color, varColor) -> {
            if (object instanceof ItemStack item) {
                TextComponent component = new TextComponent(item.getType().name(), varColor);
                
                component.addTextEvent(new HoverEvent(textComponent(item.toString(), color)));
                component.setInsertion(component.getText());
                text.addTranslateWith(component);
                return true;
            }
            return false;
        });
        argumentTranslator.put("resourcePackResolution", (text, object, color, varColor) -> {
            if (object instanceof ResolutionCommand.Resolution resolution) {
                TextComponent component = new TextComponent(resolution.getName(), varColor);
                component.setInsertion(resolution.getName());
                
                component.addTextEvent(new HoverEvent(resolution.getDescription()));
                if (resolution.getUrl() != null) component.addTextEvent(new ClickEvent(ClickEvent.OPEN_URL, resolution.getUrl()));
                
                text.addTranslateWith(component);
                return true;
            }
            return false;
        });
        argumentTranslator.put("default", (text, object, color, varColor) -> {
            String textPiece = object.toString();
            TextComponent component = textComponent(textPiece, varColor);
            if (textPiece.startsWith("/") && !Pattern.compile("[\\[\\]<>]").matcher(textPiece).find()) {
                component.addTextEvent(hoverEvent(textPiece, ColorTheme.ColorType.infoColor));
                component.addTextEvent(ClickEvent.runCommand(textPiece));
            }
            try {
                new URL(textPiece);
                component.addTextEvent(hoverEvent(textPiece, ColorTheme.ColorType.infoColor));
                component.addTextEvent(ClickEvent.openUrl(textPiece));
            } catch (Exception ignore) {}
            component.setTextAsInsertion();
            text.addTranslateWith(component);
            return true;
        });
    }
    
    @FunctionalInterface
    public interface ArgumentTranslator {
        boolean format(TextComponent text, Object object, String color, String varColor);
    }
    
    public static ItemStack getSign(@Nullable ItemStack signItem, Player player, List<Message> lines) {
        return getSign(signItem, player, lines, null);
    }
    public static ItemStack getSign(@Nullable ItemStack signItem, ColorTheme theme, List<Message> lines) {
        return getSign(signItem, theme, lines, null);
    }
    public static ItemStack getSign(@Nullable ItemStack signItem, Player player, List<Message> lines, @Nullable DyeColor glow) {
        return getSign(signItem, ColorTheme.getTheme(player), lines, glow);
    }
    public static ItemStack getSign(@Nullable ItemStack signItem, ColorTheme theme, List<Message> lines, @Nullable DyeColor glow) {
        ItemStack itemStack = Main.getOrDefault(signItem, new ItemStack(Material.OAK_SIGN));
//        try {
//            TPortAdapter adapter = Main.getInstance().adapter;
//            String version = ReflectionManager.getServerClassesVersion();
//            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + version + "inventory.CraftItemStack");
//
//            Class<?> isClass = Class.forName("org.bukkit.inventory.ItemStack");
//            Object nmsStack = craftItemStack.getMethod("asNMSCopy", isClass).invoke(craftItemStack, itemStack);
//
//            Object tag = adapter.getNBTTag(nmsStack);
//            Object blockTag = adapter.getCompound(tag, "BlockEntityTag");
//            adapter.putString(blockTag, "id", "minecraft:sign");
//            for (int i = 0; i < 4 && i < lines.size(); i++) {
//                Message line = lines.get(i);
//                if (line != null) {
//                    adapter.putString(blockTag, "Text" + (i + 1), line.translateJSON(theme));
//                }
//            }
//            if (glow != null) {
//                adapter.putBoolean(blockTag, "GlowingText", true);
//                adapter.putString(blockTag, "Color", glow.name().toLowerCase());
//            }
//            adapter.put(tag, "BlockEntityTag", blockTag);
//
//            itemStack = (ItemStack) craftItemStack.getMethod("asCraftMirror", nmsStack.getClass()).invoke(null, nmsStack);
////            itemStack = CraftItemStack.asCraftMirror(item);
//        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
        
        //todo fix signs
        return itemStack;
    }
    
    public static Message setIgnoreTranslator(Message message, boolean ignore) {
        message.getText().forEach(t -> t.ignoreTranslator(ignore));
        return message;
    }
    
    public static String getOrDefaultCaseInsensitive(JsonObject json, String toFind, String defaultValue) {
        for (String s : json.keySet()) {
            if (s != null) {
                if (toFind.equalsIgnoreCase(s)) {
                    return json.get(s).getAsString();
                }
            }
        }
        return defaultValue;
    }
    
    public static HoverEvent translateHoverEvent(HoverEvent message, JsonObject translateFile) {
        HoverEvent hEvent = new HoverEvent();
        message.getText().stream().map(t -> translateTextComponent(t, translateFile)).forEach(hEvent::addMessage);
        return hEvent;
    }
    public static Message translateTextComponent(TextComponent textComponent, JsonObject translateFile) {
        return translateMessage(new Message(textComponent), translateFile);
    }
    public static List<Message> translateMessage(List<Message> toTranslate, JsonObject translateFile) {
        if (translateFile == null) return toTranslate;
        List<Message> returnCollection = new ArrayList<>();
        for (Message message : toTranslate) {
            returnCollection.add(translateMessage(message, translateFile));
        }
        return returnCollection;
    }
    public static Message translateMessage(@Nullable Message toTranslate, @Nullable JsonObject translateFile) {
        if (translateFile == null) return toTranslate;
        if (toTranslate == null) return null;
        if (toTranslate.isTranslated()) return toTranslate;
        
        Message message = new Message();
        
        for (TextComponent textComponent : toTranslate.getText()) {
            
            if (textComponent.hasHoverEvent()) {
                textComponent.addTextEvent(translateHoverEvent(textComponent.getHoverEvent(), translateFile));
            }
            
//            String color = null;
//            List<Attribute> attributes = ((TextComponent) textComponent.clone()).getAttributes();
            
            if (TextType.TRANSLATE.equals(textComponent.getType()) && !textComponent.ignoreTranslator()) {
                
                String text = getOrDefaultCaseInsensitive(translateFile, textComponent.getText(), textComponent.getText());
                
                ArrayList<Message> translateWith = textComponent.getTranslateWith();
                String delimiter = "((%\\p{Digit}\\$s)|(%s))";
                int withIndex = 0;
                
                for (String textPiece : text.split(String.format("((?<=%1$s)|(?=%1$s))", delimiter))) {
                    
                    if (textPiece.equals("%s")) {
                        if (withIndex < translateWith.size()) {
                            Message withMessage = translateWith.get(withIndex);
                            for (TextComponent withMessageComponent : withMessage.getText()) {
                                if (withMessageComponent.hasHoverEvent()) {
                                    withMessageComponent.addTextEvent(translateHoverEvent(withMessageComponent.getHoverEvent(), translateFile));
                                } else if (textComponent.hasHoverEvent()) {
                                    withMessageComponent.addTextEvent(textComponent.getHoverEvent());
                                }
                                if (!withMessageComponent.hasClickEvent() && textComponent.hasClickEvent()) {
                                    withMessageComponent.addTextEvent(textComponent.getClickEvent());
                                }//todo transfer all other elements
//                                for (Attribute attribute : textComponent.getAttributes()) {
//                                    withMessageComponent.addAttribute(attribute);
//                                }
                                
                                if (TextType.TRANSLATE.equals(withMessageComponent.getType())) {
                                    message.addMessage(translateTextComponent(withMessageComponent, translateFile));
                                } else {
                                    message.addText(withMessageComponent);
                                }
                            }
                            withIndex++;
                        }
                    } else if (textPiece.matches("%\\d++\\$s")) {
                        int index = Integer.parseInt(CharMatcher.inRange('0', '9').retainFrom(textPiece)) - 1;
                        if (index < translateWith.size()) {
                            Message withMessage = translateWith.get(index);
                            for (TextComponent withMessageComponent : withMessage.getText()) {
                                if (withMessageComponent.hasHoverEvent()) {
                                    withMessageComponent.addTextEvent(translateHoverEvent(withMessageComponent.getHoverEvent(), translateFile));
                                } else if (textComponent.hasHoverEvent()) {
                                    withMessageComponent.addTextEvent(textComponent.getHoverEvent());
                                }
                                if (!withMessageComponent.hasClickEvent() && textComponent.hasClickEvent()) {
                                    withMessageComponent.addTextEvent(textComponent.getClickEvent());
                                }
                                for (Attribute attribute : textComponent.getAttributes()) {
                                    withMessageComponent.addAttribute(attribute);
                                }
                                
                                if (TextType.TRANSLATE.equals(withMessageComponent.getType())) {
                                    message.addMessage(translateTextComponent(withMessageComponent, translateFile));
                                } else {
                                    message.addText(withMessageComponent);
                                }
                            }
                        }
                    } else {
                        TextComponent newComponent = ((TextComponent) textComponent.clone()).setText(textPiece).setType(TextType.TEXT);
                        newComponent.resetTranslateWith();
                        message.addText(newComponent);
                    }
                }
                
            } else {
                message.addText(textComponent);
            }
        }
        
        message.setTranslated(true);
        return message;
    }
    
    public static ItemStack setCustomName(ItemStack is, Player player, Message displayName) {
        return setCustomItemData(is, ColorTheme.getTheme(player), displayName, null);
    }
    public static ItemStack setCustomName(ItemStack is, ColorTheme theme, Message displayName) {
        return setCustomItemData(is, theme, displayName, null);
    }
    public static ItemStack setCustomLore(ItemStack is, Player player, Message... lore) {
        return setCustomItemData(is, ColorTheme.getTheme(player), null, Arrays.asList(lore));
    }
    public static ItemStack setCustomLore(ItemStack is, Player player, Collection<Message> lore) {
        return setCustomItemData(is, ColorTheme.getTheme(player), null, lore);
    }
    public static ItemStack setCustomLore(ItemStack is, ColorTheme theme, Message... lore) {
        return setCustomItemData(is, theme, null, Arrays.asList(lore));
    }
    public static ItemStack setCustomLore(ItemStack is, ColorTheme theme, Collection<Message> lore) {
        // {display:{Lore:['[{"text":"testA"},{"text":"testB"}]','[{"text":"testC"}]']}}
        return setCustomItemData(is, theme, null, lore);
    }
    public static ItemStack setCustomItemData(ItemStack is, ColorTheme theme, @Nullable Message displayName, @Nullable Collection<Message> lore) {
        try {
            TPortAdapter adapter = Main.getInstance().adapter;
            
            if (displayName != null) {
                adapter.setDisplayName(is, displayName, theme);
            }
            if (lore != null) {
                adapter.setLore(is, lore, theme);
            }
            return is;
        } catch (Throwable ex) {
            Features.Feature.printSmallNMSErrorInConsole("Fancy item stacks", true);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                JsonObject serverLang = Language.getServerLang();
                if (displayName != null) {
                    im.setDisplayName(displayName.translateMessage(serverLang).toColoredString(theme));
                }
                if (lore != null) {
                    ArrayList<String> lorePlaceholder = new ArrayList<>();
                    for (Message line : lore) {
                        if (line != null) {
                            lorePlaceholder.add(line.translateMessage(serverLang).toColoredString(theme));
                        }
                    }
                    im.setLore(lorePlaceholder);
                }
                is.setItemMeta(im);
            }
        }
        return is;
    }
    public static boolean hasCustomName(ItemStack is) {
        return hasCustomName(is.getItemMeta());
    }
    public static boolean hasCustomName(ItemMeta im) {
        return im.hasDisplayName();
    }
    
    public static ArrayList<Message> transformColoredTextToMessage(String coloredText, @Nullable String defaultColor) {
        //valid color inputs:
        // #123456
        // &3
        // $1$39$255
        
        ArrayList<Message> messages = new ArrayList<>();
        
        String lastHexColor = Main.getOrDefault(defaultColor, "");
        Message lastMessage = new Message();
        
        for (String element : transformColoredTextToArray(coloredText)) {
            if (MultiColor.isColor(element)) {
                lastHexColor = new MultiColor(element).getColorAsValue();
            } else if (element.equals("\n") || element.equals("\\n")) {
                messages.add(lastMessage);
                lastMessage = new Message();
                lastHexColor = Main.getOrDefault(defaultColor, "");
            } else {
                lastMessage.addText(new TextComponent(element, lastHexColor));
            }
        }
        if (!lastMessage.isEmpty()) {
            messages.add(lastMessage);
        }
        
        return messages;
    }
    public static ArrayList<String> transformColoredTextToArray(String coloredText) {
        //valid color inputs:
        // #123456
        // &3
        // $1$39$255
        
        ArrayList<String> textElements = new ArrayList<>();
        textElements.add("");
        
        char[] charArray = coloredText.toCharArray();
        charLoop:
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            String lastElement = textElements.get(textElements.size()-1);
            
            if (c == '\n') {
                if (lastElement.isEmpty()) {
                    textElements.remove(textElements.size() -1);
                }
                textElements.add("\n");
                textElements.add("");
                continue charLoop;
            }
            else if (c == '\\') {
                if (i + 1 < charArray.length) { //check if notation fits after index i
                    if (coloredText.startsWith("\\n", i)) { //check notation
                        
                        if (lastElement.isEmpty()) {
                            textElements.remove(textElements.size() -1);
                        }
                        textElements.add("\\n");
                        textElements.add("");
                        
                        i += 1;
                        continue charLoop;
                    }
                }
            }
            if (c == '#') { //look for # (start of hex notation)
                if (i + 6 < charArray.length) { //check if notation fits after index i
                    String possibleHexNotation = coloredText.substring(i, i + 7); //extract notation
                    if (MultiColor.isHexColor(possibleHexNotation)) { //check notation
                        //color found
                        if (lastElement.isEmpty()) {
                            textElements.remove(textElements.size() -1);
                        }
                        textElements.add(possibleHexNotation);
                        textElements.add("");
                        
                        i += 6;
                        continue charLoop;
                    }
                }
            }
            else if (c == '&') {
                if (i + 1 < charArray.length) { //check if notation fits after index i
                    String possibleChatColor = coloredText.substring(i, i + 2); //extract notation
                    if (MultiColor.isColorCode(possibleChatColor)) { //check notation
                        //color found
                        if (lastElement.isEmpty()) {
                            textElements.remove(textElements.size() -1);
                        }
                        textElements.add(possibleChatColor);
                        textElements.add("");
                        
                        i += 1;
                        continue charLoop;
                    }
                }
            }
            else if (c == '$') { //find first dollar
                try {
                    int redIndex = i;                              //red dollar found
                    int greenIndex = -1;
                    int blueIndex = -1;
                    int endIndex = -1;
                    
                    for (int j = 1; j < 5; j++) {                   //find green dollar
                        if (redIndex + j >= charArray.length) break;
                        char greenDollar = charArray[redIndex + j];
                        if (greenDollar == '$') {
                            greenIndex = redIndex + j;
                            break;
                        }
                    }
                    if (greenIndex == -1) throw new IllegalArgumentException();
                    for (int j = 1; j < 5; j++) {                   //find blue dollar
                        if (greenIndex + j >= charArray.length) break;
                        char blueDollar = charArray[greenIndex + j];
                        if (blueDollar == '$') {
                            blueIndex = greenIndex + j;
                            break;
                        }
                    }
                    if (blueIndex == -1) throw new IllegalArgumentException();
                    for (int j = 1; j < 5; j++) {                   //find end of blue
                        if (blueIndex + j >= charArray.length) {
                            endIndex = blueIndex + j;
                            break;
                        }
                        char end = charArray[blueIndex + j];
                        if (!String.valueOf(end).matches("\\d")) {
                            endIndex = blueIndex + j;
                            break;
                        }
                    }
                    if (endIndex == -1) throw new IllegalArgumentException();
                    
                    String redString = coloredText.substring(redIndex, greenIndex);
                    String greenString = coloredText.substring(greenIndex, blueIndex);
                    String blueString = coloredText.substring(blueIndex, endIndex);
                    for (String colorString : List.of(redString, greenString, blueString)) {
                        try {
                            int color = Integer.parseInt(colorString.substring(1));
                            if (color > 255) {
                                throw new IllegalArgumentException();
                            }
                        } catch (NumberFormatException nfe) {
                            throw new IllegalArgumentException();
                        }
                    }
                    
                    if (lastElement.isEmpty()) {
                        textElements.remove(textElements.size() -1);
                    }
                    textElements.add(redString + greenString + blueString);
                    textElements.add("");
                    
                    i = endIndex - 1;
                    continue charLoop;
                }
                catch (IllegalArgumentException ignore) {
                }
            }
            
            textElements.set(textElements.size() -1, lastElement + c);
//            singleColorLine.append(c);
        }

//        if (!lastColor.isBlank()) textElements.add(lastColor);
//        if (!singleColorLine.isEmpty()) textElements.add(singleColorLine.toString());
        
        return textElements;
    }
    
    public static Message fromMarkdown(String markdown) {
        Parser markdownParser = Parser.builder().build();
        Node node = markdownParser.parse(markdown);
        
        Message message = new Message();
        FancyNodeRenderer fancyRenderer = new FancyNodeRenderer(message);
        fancyRenderer.render(node);
        
        return message;
    }
    public static Pair<ArrayList<String>, ArrayList<Message>> fromSplitMarkdown(String markdown, CommandTemplate... templates) {
        Parser markdownParser = Parser.builder().build();
        Node node = markdownParser.parse(markdown);
        
        ArrayList<String> chapterTitles = new ArrayList<>();
        ArrayList<Message> chapters = new ArrayList<>();
        FancyNodeRenderer fancyRenderer = new FancyNodeRenderer(chapterTitles, chapters);
        Stream.of(templates).forEach(fancyRenderer::addCommandLookup);
        
        fancyRenderer.render(node);
        
        return new Pair<>(chapterTitles, chapters);
    }
    
    public interface MessageDescription {
        @Nullable Message getDescription();
        Message getName(String color, String varColor);
        String getInsertion();
    }
}
