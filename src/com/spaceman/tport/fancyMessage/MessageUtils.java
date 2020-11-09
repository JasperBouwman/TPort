package com.spaceman.tport.fancyMessage;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

@SuppressWarnings("unchecked")
public class MessageUtils {
    
    public static JSONObject toString(ItemStack is) {
        JSONObject item = new JSONObject();
        
        item.put("id", is.getType().name().toLowerCase());
        item.put("Count", is.getAmount());
        
        if (is.hasItemMeta()) {
            ItemMeta im = is.getItemMeta();
            item.put("tag", toString(im));
        }
        
        return item;
    }
    
    @SuppressWarnings("rawtypes")
    public static JSONObject toString(ItemMeta im) {
        JSONObject tags = new JSONObject();
        if (im instanceof Damageable) {
            tags.put("Damage", ((Damageable) im).getDamage());
        }
        
        if (im instanceof Repairable) {
            tags.put("repairCost", ((Repairable) im).getRepairCost());
        }
        
        if (im instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) im;
            tags.put("Potion", potionMeta.getBasePotionData().getType().getEffectType().getName());
            
            if (potionMeta.hasColor()) {
                tags.put("CustomPotionColor", potionMeta.getColor().asRGB());
            }
            
            JSONArray customPotionEffects = new JSONArray();
            for (PotionEffect potionEffect : potionMeta.getCustomEffects()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", potionEffect.getType().getId());
                jsonObject.put("Duration", potionEffect.getDuration());
                jsonObject.put("Amplifier", potionEffect.getAmplifier());
                jsonObject.put("ShowParticles", (potionEffect.hasParticles() ? 1 : 0));
                customPotionEffects.add(jsonObject);
            }
            tags.put("CustomPotionEffects", customPotionEffects);
        }
        
        if (im instanceof CrossbowMeta) {
            CrossbowMeta crossbowMeta = (CrossbowMeta) im;
            if (crossbowMeta.hasChargedProjectiles()) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.addAll(crossbowMeta.getChargedProjectiles());
                tags.put("Charged", 1);
            }
        }
        
        if (im.isUnbreakable()) {
            tags.put("Unbreakable", 1);
        }
        
        if (im.hasDisplayName() || im.hasLore() || im instanceof LeatherArmorMeta) {
            JSONObject display = new JSONObject();
            
            if (im.hasDisplayName()) {
                JSONObject displayName = new JSONObject(Main.asMap(new Pair("text", im.getDisplayName())));
                display.put("Name", displayName.toString());
            }
            if (im.hasLore()) {
                JSONArray jsonArray = new JSONArray();
                //noinspection ConstantConditions
                im.getLore().forEach(lore -> jsonArray.add(new JSONObject(Main.asMap(new Pair("text", lore))).toString()));
                display.put("Lore", jsonArray);
            }
            if (im instanceof LeatherArmorMeta) {
                display.put("color", ((LeatherArmorMeta) im).getColor().asRGB());
            }
            
            tags.put("display", display);
        }
        
        if (im.hasEnchants()) {
            JSONArray jsonArray = new JSONArray();
            for (Enchantment enchantment : im.getEnchants().keySet()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", enchantment.getKey().toString());
                jsonObject.put("lvl", im.getEnchantLevel(enchantment));
                jsonArray.add(jsonObject);
            }
            tags.put("Enchantments", jsonArray);
        }
        
        if (im.hasCustomModelData()) {
            tags.put("CustomModelData", im.getCustomModelData());
        }
        
        byte flag = 0;
        for (ItemFlag itemFlag : ItemFlag.values()) {
            if (im.hasItemFlag(itemFlag)) {
                flag += 1 << itemFlag.ordinal();
            }
        }
        if (flag != 0) {
            tags.put("HieFlags", flag);
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
}
