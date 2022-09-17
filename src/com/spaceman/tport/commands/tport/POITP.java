package com.spaceman.tport.commands.tport;

import com.google.gson.JsonObject;
import com.spaceman.tport.Glow;
import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spaceman.tport.TPortInventories.openFeatureTP;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatTranslation;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;

public class POITP extends SubCommand {
    
    @Override
    public String getName(String arg) {
        return "poiTP";
    }
    
    @Override
    public void run(String[] args, Player player) {
    
    }
    
    public static List<String> getPOIs(World world) {
        try {
            Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
            WorldServer worldServer = (WorldServer) nmsWorld;
            
            IRegistryCustom registry = worldServer.s();
            IRegistry<VillagePlaceType> structureRegistry = registry.d(IRegistry.C);
            
            return structureRegistry.d().stream().map(MinecraftKey::a).map(String::toLowerCase).toList();
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static List<com.spaceman.tport.Pair<String, List<String>>> getPOITags(World world) {
        List<com.spaceman.tport.Pair<String, List<String>>> list = new ArrayList<>();
        
        try {
            Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
            WorldServer worldServer = (WorldServer) nmsWorld;
            
            IRegistryCustom registry = worldServer.s();
            IRegistry<VillagePlaceType> structureRegistry = registry.d(IRegistry.C);
            
            List<String> tags = structureRegistry.i().map((tagKey) -> tagKey.b().a()).toList();
            
            for (String tagKeyName : tags) {
                TagKey<VillagePlaceType> tagKey = TagKey.a(IRegistry.C, new MinecraftKey(tagKeyName));
                
                Optional<HolderSet.Named<VillagePlaceType>> optional = structureRegistry.c(tagKey);
                if (optional.isPresent()) {
                    HolderSet.Named<VillagePlaceType> named = optional.get();
                    Stream<Holder<VillagePlaceType>> values = named.a();
                    
                    List<String> pois = values.map((holder) -> {
                        return holder.a(); //Holder -> StructureFeature
                    }).map((structureFeature) -> {
//                        structureFeature.a(); //pois
                        MinecraftKey key = structureRegistry.b(structureFeature);
                        if (key != null) {
                            return key.a().toLowerCase();
                        }
                        return null;
                    }).filter(Objects::nonNull).toList();
                    
                    list.add(new com.spaceman.tport.Pair<>("#" + tagKeyName.toLowerCase(), pois));
                }
            }
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    public static Material getMaterial(String feature) {
        return switch (feature) {
            case "desert_pyramid" -> Material.SANDSTONE;
            case "end_city" -> Material.PURPUR_BLOCK;
            case "nether_fossil" -> Material.BONE_BLOCK;
            case "buried_treasure" -> Material.CHEST;
            case "bastion_remnant" -> Material.GILDED_BLACKSTONE;
            case "swamp_hut" -> Material.CAULDRON;
            case "jungle_pyramid", "jungle_temple" -> Material.TRIPWIRE_HOOK;
            case "igloo" -> Material.SNOW_BLOCK;
            case "fortress", "nether_fortress" -> Material.NETHER_BRICKS;
            case "mansion", "woodland_mansion" -> Material.TOTEM_OF_UNDYING;
            case "pillager_outpost" -> Material.CROSSBOW;
            case "monument", "ocean_monument" -> Material.PRISMARINE_BRICKS;
            case "stronghold" -> Material.END_PORTAL_FRAME;
            case "mineshaft", "mineshaft_mesa" -> Material.CHEST_MINECART;
            case "ocean_ruin_warm", "ocean_ruin_cold" -> Material.TRIDENT;
            case "shipwreck", "shipwreck_beached" -> Material.OAK_BOAT;
            case "ancient_city" -> Material.SCULK;
            
            case "village_taiga", "village_snowy" -> Material.SPRUCE_DOOR;
            case "village_desert" -> Material.BIRCH_DOOR;
            case "village_plains" -> Material.OAK_DOOR;
            case "village_savanna" -> Material.ACACIA_DOOR;
            
            case "ruined_portal",
                    "ruined_portal_swamp",
                    "ruined_portal_nether",
                    "ruined_portal_mountain",
                    "ruined_portal_standard",
                    "ruined_portal_jungle",
                    "ruined_portal_ocean",
                    "ruined_portal_desert" -> Material.CRYING_OBSIDIAN;
            
            default -> Material.DIAMOND_BLOCK;
        };
    }
    public static List<ItemStack> getItems(Player player, ArrayList<String> featureSelection) {
        ColorTheme theme = ColorTheme.getTheme(player);
        ArrayList<ItemStack> features = new ArrayList<>();
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        for (String feature : getPOIs(player.getWorld())) {
            ItemStack item = new ItemStack(getMaterial(feature));
            
            boolean selected = false;
            Message selectedMessage;
            if (featureSelection.contains(feature)) {
                selected = true;
                selectedMessage = formatTranslation(varInfoColor, varInfoColor, "tport.tportInventories.openFeatureTP.feature.unselect");
            } else {
                selectedMessage = formatTranslation(varInfoColor, varInfoColor, "tport.tportInventories.openFeatureTP.feature.select");
            }
            Message featureLClick = formatInfoTranslation("tport.tportInventories.openFeatureTP.feature.LClick", ClickType.LEFT, selectedMessage);
            Message featureRClick = formatInfoTranslation("tport.tportInventories.openFeatureTP.feature.RClick", ClickType.RIGHT);
            if (playerLang != null) { //if player has no custom language, translate it
                featureLClick = MessageUtils.translateMessage(featureLClick, playerLang);
                featureRClick = MessageUtils.translateMessage(featureRClick, playerLang);
            }
            MessageUtils.setCustomItemData(item, theme, null, Arrays.asList(featureLClick, featureRClick));
            
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(theme.getVarInfoColor() + feature);
            
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "feature"), PersistentDataType.STRING, feature.toLowerCase());
            if (selected) Glow.addGlow(im);
            
            FancyClickEvent.addFunction(im, ClickType.LEFT, "featureTP_select", ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey featureKey = new NamespacedKey(Main.getInstance(), "feature");
                if (pdc.has(featureKey, PersistentDataType.STRING)) {
                    ArrayList<String> innerFeatureSelection = fancyInventory.getData("featureSelection", ArrayList.class, new ArrayList<String>());
                    String innerFeature = pdc.get(featureKey, PersistentDataType.STRING);
                    if (innerFeatureSelection.contains(innerFeature)) {
                        innerFeatureSelection.remove(innerFeature);
                    } else {
                        innerFeatureSelection.add(innerFeature);
                    }
                    fancyInventory.setData("featureSelection", innerFeatureSelection);
                    openFeatureTP(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                }
            }));
            FancyClickEvent.addCommand(im, ClickType.RIGHT, "tport featureTP search " + feature);
            
            im.setDisplayName(theme.getVarInfoColor() + feature);
            item.setItemMeta(im);
            if (selected) features.add(0, item);
            else features.add(item);
        }
        for (com.spaceman.tport.Pair<String, List<String>> pair : getPOITags(player.getWorld())) {
            Material m = switch (pair.getLeft().substring(1)) { //remove #
                case "ruined_portal" -> Material.CRYING_OBSIDIAN;
                case "dolphin_located" -> Material.DOLPHIN_SPAWN_EGG;
                case "on_woodland_explorer_maps", "on_ocean_explorer_maps", "on_treasure_maps" -> Material.MAP;
                case "ocean_ruin" -> Material.TRIDENT;
                case "village" -> Material.EMERALD;
                case "eye_of_ender_located" -> Material.ENDER_EYE;
                case "mineshaft" -> Material.CHEST_MINECART;
                case "shipwreck" -> Material.OAK_BOAT;
                case "cats_spawn_as_black", "cats_spawn_in" -> Material.CAT_SPAWN_EGG;
                default -> Material.DIAMOND_BLOCK;
            };
            ItemStack is = new ItemStack(m);
            
            List<String> featureList = pair.getRight();
            
            List<Message> lore = new LinkedList<>();
            
            lore.add(formatInfoTranslation("tport.commands.tport.featureTP.getItems.list." + ((featureList.size() == 1) ? "singular" : "multiple")));
            
            Message lorePiece = new Message();
            boolean color = true;
            
            for (int i = 0; i < featureList.size(); i++) {
                if ((i % 2) == 0 && i != 0) {
                    lore.add(lorePiece);
                    lorePiece = new Message();
                }
                
                if (color) {
                    lorePiece.addText((textComponent(featureList.get(i), varInfoColor)));
                } else {
                    lorePiece.addText((textComponent(featureList.get(i), varInfo2Color)));
                }
                lorePiece.addText(textComponent(", ", infoColor));
                color = !color;
            }
            lorePiece.removeLast();
            lore.add(lorePiece);
            
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.commands.tport.featureTP.getItems.selectFeatures.additive." + ((featureList.size() == 1) ? "singular" : "multiple"), ClickType.LEFT));
            lore.add(formatInfoTranslation("tport.commands.tport.featureTP.getItems.selectFeatures.overwrite." + ((featureList.size() == 1) ? "singular" : "multiple"), ClickType.SHIFT_LEFT));
            lore.add(formatInfoTranslation("tport.commands.tport.featureTP.getItems.selectFeatures.run", ClickType.RIGHT));
            
            if (playerLang != null) { //if player has no custom language, translate it
                lore = MessageUtils.translateMessage(lore, playerLang);
            }
            MessageUtils.setCustomItemData(is, theme, null, lore);
            
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(theme.getVarInfo2Color() + pair.getLeft());
            String featuresAsString = String.join("|", pair.getRight());
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "featureTag/name"), PersistentDataType.STRING, pair.getLeft());
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "featureTag/features"), PersistentDataType.STRING, featuresAsString);
            
            FancyClickEvent.addFunction(im, ClickType.LEFT, "featureTP_selectAdditive", ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey tagKey = new NamespacedKey(Main.getInstance(), "featureTag/features");
                if (pdc.has(tagKey, PersistentDataType.STRING)) {
                    String innerFeatures = pdc.get(tagKey, PersistentDataType.STRING);
                    String[] featureArray = innerFeatures.split("\\|");
                    ArrayList<String> innerFeatureSelection = fancyInventory.getData("featureSelection", ArrayList.class, new ArrayList<String>());
                    Arrays.stream(featureArray).filter(s -> !innerFeatureSelection.contains(s)).forEach(innerFeatureSelection::add);
                    fancyInventory.setData("featureSelection", innerFeatureSelection);
                    openFeatureTP(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                }
            }));
            FancyClickEvent.addFunction(im, ClickType.SHIFT_LEFT, "featureTP_selectOverwrite", ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey tagKey = new NamespacedKey(Main.getInstance(), "featureTag/features");
                if (pdc.has(tagKey, PersistentDataType.STRING)) {
                    String innerFeatures = pdc.get(tagKey, PersistentDataType.STRING);
                    String[] featureArray = innerFeatures.split("\\|");
                    ArrayList<String> innerFeatureSelection = Arrays.stream(featureArray).collect(Collectors.toCollection(ArrayList::new));
                    fancyInventory.setData("featureSelection", innerFeatureSelection);
                    openFeatureTP(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                }
            }));
            FancyClickEvent.addCommand(im, ClickType.RIGHT, "tport featureTP search " + pair.getLeft());
            
            is.setItemMeta(im);
            features.add(is);
        }
        return features;
    }
}
