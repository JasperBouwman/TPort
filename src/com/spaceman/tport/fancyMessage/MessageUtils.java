package com.spaceman.tport.fancyMessage;

import com.google.common.base.CharMatcher;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.encapsulation.Encapsulation;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import com.spaceman.tport.tpEvents.TPRestriction;
import com.spaceman.tport.tport.TPort;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.Color;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        
        if (im instanceof PotionMeta potionMeta) {
            tags.addProperty("Potion", potionMeta.getBasePotionData().getType().getEffectType().getName());
            
            if (potionMeta.hasColor()) {
                tags.addProperty("CustomPotionColor", potionMeta.getColor().asRGB());
            }
            
            JsonArray customPotionEffects = new JsonArray();
            for (PotionEffect potionEffect : potionMeta.getCustomEffects()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", potionEffect.getType().getId());
                jsonObject.addProperty("Duration", potionEffect.getDuration());
                jsonObject.addProperty("Amplifier", potionEffect.getAmplifier());
                jsonObject.addProperty("ShowParticles", (potionEffect.hasParticles() ? 1 : 0));
                customPotionEffects.add(jsonObject);
            }
            tags.add("CustomPotionEffects", customPotionEffects);
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
    
    public static class ImageFrame {
        private final int delay;
        private final BufferedImage image;
        private final String disposal;
        private final int width, height;
        
        public ImageFrame(BufferedImage image, int delay, String disposal, int width, int height) {
            this.image = image;
            this.delay = delay;
            this.disposal = disposal;
            this.width = width;
            this.height = height;
        }
        
        public BufferedImage getImage() {
            return image;
        }
        
        public int getDelay() {
            return delay;
        }
        
        public String getDisposal() {
            return disposal;
        }
        
        public int getWidth() {
            return width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public static void recursion(int index, MessageUtils.ImageFrame[] frames, Player player) {
            MessageUtils.ImageFrame frame = frames[index];
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                MessageUtils.toMessage(frame.getImage()).sendMessage(player);
                if (frames.length - 1 != index) recursion(index + 1, frames, player);
            }, (frame.getDelay() == 0 ? 1 : frame.getDelay()));
        }
        
        public static ImageFrame[] readGif(InputStream stream) throws IOException {
            ArrayList<ImageFrame> frames = new ArrayList<>(2);
            
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            reader.setInput(ImageIO.createImageInputStream(stream));
            
            int lastx = 0;
            int lasty = 0;
            
            int width = -1;
            int height = -1;
            
            IIOMetadata metadata = reader.getStreamMetadata();
            
            Color backgroundColor = null;
            
            if (metadata != null) {
                IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());
                
                NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
                NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");
                
                if (globalScreeDescriptor != null && globalScreeDescriptor.getLength() > 0) {
                    IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreeDescriptor.item(0);
                    
                    if (screenDescriptor != null) {
                        width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
                        height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
                    }
                }
                
                if (globalColorTable != null && globalColorTable.getLength() > 0) {
                    IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);
                    
                    if (colorTable != null) {
                        String bgIndex = colorTable.getAttribute("backgroundColorIndex");
                        
                        IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
                        while (colorEntry != null) {
                            if (colorEntry.getAttribute("index").equals(bgIndex)) {
                                int red = Integer.parseInt(colorEntry.getAttribute("red"));
                                int green = Integer.parseInt(colorEntry.getAttribute("green"));
                                int blue = Integer.parseInt(colorEntry.getAttribute("blue"));
                                
                                backgroundColor = new Color(red, green, blue);
                                break;
                            }
                            
                            colorEntry = (IIOMetadataNode) colorEntry.getNextSibling();
                        }
                    }
                }
            }
            
            BufferedImage master = null;
            boolean hasBackground = false;
            
            for (int frameIndex = 0; ; frameIndex++) {
                BufferedImage image;
                try {
                    image = reader.read(frameIndex);
                } catch (IndexOutOfBoundsException io) {
                    break;
                }
                
                if (width == -1 || height == -1) {
                    width = image.getWidth();
                    height = image.getHeight();
                }
                
                IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
                IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
                NodeList children = root.getChildNodes();
                
                int delay = Integer.parseInt(gce.getAttribute("delayTime"));
                
                String disposal = gce.getAttribute("disposalMethod");
                
                if (master == null) {
                    master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    master.createGraphics().setColor(backgroundColor);
                    master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());
                    
                    hasBackground = image.getWidth() == width && image.getHeight() == height;
                    
                    master.createGraphics().drawImage(image, 0, 0, null);
                } else {
                    int x = 0;
                    int y = 0;
                    
                    for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
                        Node nodeItem = children.item(nodeIndex);
                        
                        if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                            NamedNodeMap map = nodeItem.getAttributes();
                            
                            x = Integer.parseInt(map.getNamedItem("imageLeftPosition").getNodeValue());
                            y = Integer.parseInt(map.getNamedItem("imageTopPosition").getNodeValue());
                        }
                    }
                    
                    if (disposal.equals("restoreToPrevious")) {
                        BufferedImage from = null;
                        for (int i = frameIndex - 1; i >= 0; i--) {
                            if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
                                from = frames.get(i).getImage();
                                break;
                            }
                        }
                        
                        {
                            assert from != null;
                            ColorModel model = from.getColorModel();
                            boolean alpha = from.isAlphaPremultiplied();
                            WritableRaster raster = from.copyData(null);
                            master = new BufferedImage(model, raster, alpha, null);
                        }
                    } else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null) {
                        if (!hasBackground || frameIndex > 1) {
                            master.createGraphics().fillRect(lastx, lasty, frames.get(frameIndex - 1).getWidth(), frames.get(frameIndex - 1).getHeight());
                        }
                    }
                    master.createGraphics().drawImage(image, x, y, null);
                    
                    lastx = x;
                    lasty = y;
                }
                
                {
                    BufferedImage copy;
                    
                    {
                        ColorModel model = master.getColorModel();
                        boolean alpha = master.isAlphaPremultiplied();
                        WritableRaster raster = master.copyData(null);
                        copy = new BufferedImage(model, raster, alpha, null);
                    }
                    frames.add(new ImageFrame(copy, delay, disposal, image.getWidth(), image.getHeight()));
                }
                
                master.flush();
            }
            reader.dispose();
            
            return frames.toArray(new ImageFrame[0]);
        }
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
                
                TextComponent component = new TextComponent(encapsulation.asString(), varColor);
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
                
                TextComponent component = new TextComponent(encapsulation.asString(), varColor);
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
                TextComponent component = encapsulation.asText(varColor);
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
                TextComponent component = new TextComponent(messageDescription.getName(), varColor);
                component.addTextEvent(new HoverEvent(messageDescription.getDescription()));
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
                TextComponent component = textComponent("tport.fancyMessage.MessageUtils.clickType." + clickType.name(), varColor).setType(TextType.TRANSLATE);
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
                component.addTextEvent(new HoverEvent(textComponent(item.toString())));
                component.setInsertion(component.getText());
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
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            
            Class<?> isClass = Class.forName("org.bukkit.inventory.ItemStack");
            net.minecraft.world.item.ItemStack item = (net.minecraft.world.item.ItemStack) craftItemStack.getMethod("asNMSCopy", isClass).invoke(craftItemStack, itemStack);
            
//            NBTTagCompound nbt = item.u();
            NBTTagCompound nbt = item.v();
            NBTTagCompound blockTag = nbt.p("BlockEntityTag");
            blockTag.a("id", "minecraft:sign");
            for (int i = 0; i < 4 && i < lines.size(); i++) {
                Message line = lines.get(i);
                if (line != null) {
                    blockTag.a("Text" + (i + 1), line.translateJSON(theme));
                }
            }
            if (glow != null) {
                blockTag.a("GlowingText", true);
                blockTag.a("Color", glow.name().toLowerCase());
            }
            nbt.a("BlockEntityTag", blockTag);
            
            itemStack = (ItemStack) craftItemStack.getMethod("asCraftMirror", item.getClass()).invoke(null, item);
//            itemStack = CraftItemStack.asCraftMirror(item);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
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
    public static Collection<Message> translateMessage(Collection<Message> toTranslate, JsonObject translateFile) {
        Collection<Message> returnCollection = new ArrayList<>();
        for (Message message : toTranslate) {
            returnCollection.add(translateMessage(message, translateFile));
        }
        return returnCollection;
    }
    public static Message translateMessage(Message toTranslate, @Nullable JsonObject translateFile) {
        if (translateFile == null) return toTranslate;
        
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
                        
                        /*
                        String hexColorPattern = "\\{&x(&[0-9A-F]){6}}";
                        String formattingPattern = "\\{&[K-OR]}";
                        ArrayList<String> colorsList = new ArrayList<>();
                        Arrays.stream(ColorTheme.ColorType.values()).map(colorType -> "\\{" + colorType.name() + "}").forEach(colorsList::add);
                        Arrays.stream(ChatColor.values()).map(colorType -> "\\{" + colorType.name() + "}").forEach(colorsList::add);
                        String colorPattern = String.join("|", colorsList);
                        String innerDelimiter = "(" + hexColorPattern + "|" + colorPattern + "|" + formattingPattern + ")";
                        
                        for (String innerTextPiece : textPiece.split(String.format("(?i)((?<=%1$s)|(?=%1$s))", innerDelimiter))) {
                            if (innerTextPiece.matches("(?i)" + hexColorPattern)) { //hex colors
                                String newColor = "#" + innerTextPiece.substring(4, innerTextPiece.length() - 1).replace("&", "");
                                if (newColor.equals(color)) {
                                    color = null;
                                } else {
                                    color = newColor;
                                }
                            } else if (innerTextPiece.matches("(?i)" + colorPattern)) { //theme and chatColor name colors
                                String newColor = innerTextPiece.substring(1, innerTextPiece.length() - 1);
                                if (newColor.equals(color)) {
                                    color = null;
                                } else {
                                    color = newColor;
                                }
                            } else if (innerTextPiece.matches("(?i)" + formattingPattern)) { //formatting
                                switch (innerTextPiece.charAt(2)) {
                                    case 'k': //obfuscated
                                        if (!attributes.remove(Attribute.OBFUSCATED))
                                            attributes.add(Attribute.OBFUSCATED);
                                        break;
                                    case 'l': //bold
                                        if (!attributes.remove(Attribute.BOLD))
                                            attributes.add(Attribute.BOLD);
                                        break;
                                    case 'm': //strikethrough
                                        if (!attributes.remove(Attribute.STRIKETHROUGH))
                                            attributes.add(Attribute.STRIKETHROUGH);
                                        break;
                                    case 'n': //underline
                                        if (!attributes.remove(Attribute.UNDERLINED))
                                            attributes.add(Attribute.UNDERLINED);
                                        break;
                                    case 'o': //italic
                                        if (!attributes.remove(Attribute.ITALIC))
                                            attributes.add(Attribute.ITALIC);
                                        break;
                                    case 'r': //reset
                                    default:
                                        attributes.clear();
                                        break;
                                }
                            } else { //normal text
                                TextComponent newComponent = ((TextComponent) textComponent.clone()).setText(innerTextPiece).setType(TextType.TEXT);
                                if (color != null) newComponent.setColor(color);
                                newComponent.setAttributes(attributes);
                                message.addText(newComponent);
                            }
                        }
                        
                        */
                    }
                }
                
            } else {
                message.addText(textComponent);
            }
        }
        
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
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            
            Class<?> isClass = Class.forName("org.bukkit.inventory.ItemStack");
            net.minecraft.world.item.ItemStack nmsStack = (net.minecraft.world.item.ItemStack) craftItemStack.getMethod("asNMSCopy", isClass).invoke(craftItemStack, is);
            Class<?> itemStackClass = nmsStack.getClass();
            
//            NBTTagCompound tag = nmsStack.u(); //1.18.2
            NBTTagCompound tag = nmsStack.v(); //1.18.2
            
            NBTTagCompound display = tag.p("display");
            if (displayName != null) display.a("Name", displayName.translateJSON(theme));
            
            if (lore != null) {
                NBTTagList loreT = display.c("Lore", 8);
                int id = 0;
                for (Message line : lore) {
                    if (line != null) {
                        loreT.b(id++, NBTTagString.a(line.translateJSON(theme)));
                    }
                }
                display.a("Lore", loreT);
            }
            
            tag.a("display", display);
            
            ItemMeta im = (ItemMeta) craftItemStack.getMethod("getItemMeta", itemStackClass).invoke(craftItemStack, nmsStack);
            is.setItemMeta(im);
            return is;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return is;
    }
    
    public interface MessageDescription {
        Message getDescription();
        String getName();
        String getInsertion();
    }
}
