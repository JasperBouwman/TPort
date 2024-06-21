package com.spaceman.tport.commands.tport;

import com.google.gson.JsonObject;
import com.spaceman.tport.Glow;
import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.featureTP.Mode;
import com.spaceman.tport.commands.tport.featureTP.Search;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.pageDataName;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.inventories.TPortInventories.featureSelectionDataName;
import static com.spaceman.tport.inventories.TPortInventories.openFeatureTP;
import static org.bukkit.event.inventory.ClickType.LEFT;
import static org.bukkit.event.inventory.ClickType.SHIFT_LEFT;

public class FeatureTP extends SubCommand {
    
    private final EmptyCommand empty;
    
    public FeatureTP() {
        empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.featureTP.commandDescription"));
        empty.setPermissions("TPort.featureTP.open");
        addAction(empty);
        
        addAction(new Mode());
        addAction(new Search());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport featureTP
        // tport featureTP search [mode] <feature...>
        // tport featureTP mode [mode]
        
        if (Features.Feature.FeatureTP.isDisabled())  {
            Features.Feature.FeatureTP.sendDisabledMessage(player);
            return;
        }
        
        if (args.length == 1) {
            if (empty.hasPermissionToRun(player, true)) {
                openFeatureTP(player);
            }
            return;
        }
        if (runCommands(getActions(), args[1], args, player)) {
            return;
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport featureTP " + CommandTemplate.convertToArgs(getActions(), true));
    }
    
    public static List<String> getFeatures() {
        try {
            return Main.getInstance().adapter.availableFeatures();
        } catch (Throwable ex) {
            Features.Feature.printSmallNMSErrorInConsole("FeatureTP feature list", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static List<String> getFeatures(World world) {
        try {
            return Main.getInstance().adapter.availableFeatures(world);
        } catch (Throwable ex) {
            Features.Feature.printSmallNMSErrorInConsole("FeatureTP feature list", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static List<com.spaceman.tport.Pair<String, List<String>>> getTags(World world) {
        try {
            return Main.getInstance().adapter.getFeatureTags(world);
        } catch (Throwable ex) {
            Features.Feature.printSmallNMSErrorInConsole("FeatureTP tags", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public static Material getMaterial(String feature) {
        String materialName = switch (feature) {
            case "desert_pyramid" -> "SANDSTONE";
            case "end_city" -> "PURPUR_BLOCK";
            case "nether_fossil" -> "BONE_BLOCK";
            case "buried_treasure" -> "CHEST";
            case "bastion_remnant" -> "GILDED_BLACKSTONE";
            case "swamp_hut" -> "CAULDRON";
            case "jungle_pyramid", "jungle_temple" -> "TRIPWIRE_HOOK";
            case "igloo" -> "SNOW_BLOCK";
            case "fortress", "nether_fortress" -> "NETHER_BRICKS";
            case "mansion", "woodland_mansion" -> "TOTEM_OF_UNDYING";
            case "pillager_outpost" -> "CROSSBOW";
            case "monument", "ocean_monument" -> "PRISMARINE_BRICKS";
            case "stronghold" -> "END_PORTAL_FRAME";
            case "mineshaft", "mineshaft_mesa" -> "CHEST_MINECART";
            case "ocean_ruin_warm", "ocean_ruin_cold" -> "TRIDENT";
            case "shipwreck", "shipwreck_beached" -> "OAK_BOAT";
            case "ancient_city" -> "SCULK";
            case "trail_ruins" -> "SUSPICIOUS_GRAVEL";
            case "trial_chambers" -> "COPPER_GRATE";
            
            case "village_taiga", "village_snowy" -> "SPRUCE_DOOR";
            case "village_desert" -> "BIRCH_DOOR";
            case "village_plains" -> "OAK_DOOR";
            case "village_savanna" -> "ACACIA_DOOR";
            
            case "ruined_portal",
                    "ruined_portal_swamp",
                    "ruined_portal_nether",
                    "ruined_portal_mountain",
                    "ruined_portal_standard",
                    "ruined_portal_jungle",
                    "ruined_portal_ocean",
                    "ruined_portal_desert" -> "CRYING_OBSIDIAN";
            
            default -> "DIAMOND_BLOCK";
        };
        return Main.getOrDefault(Material.getMaterial(materialName), Material.DIAMOND_BLOCK);
    }
    public static List<ItemStack> getItems(Player player, Set<String> featureSelection) {
        ColorTheme theme = ColorTheme.getTheme(player);
        ArrayList<ItemStack> features = new ArrayList<>();
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        for (String feature : FeatureTP.getFeatures(player.getWorld())) {
            ItemStack item = new ItemStack(getMaterial(feature));
            
            boolean selected = false;
            Message selectedMessage;
            if (featureSelection.contains(feature)) {
                selected = true;
                selectedMessage = formatTranslation(varInfoColor, varInfoColor, "tport.tportInventories.openFeatureTP.feature.unselect");
            } else {
                selectedMessage = formatTranslation(varInfoColor, varInfoColor, "tport.tportInventories.openFeatureTP.feature.select");
            }
            Message featureLClick = formatInfoTranslation("tport.tportInventories.openFeatureTP.feature.LClick", LEFT, selectedMessage);
            Message featureRClick = formatInfoTranslation("tport.tportInventories.openFeatureTP.feature.RClick", ClickType.RIGHT);
            if (playerLang != null) { //if player has no custom language, translate it
                featureLClick = MessageUtils.translateMessage(featureLClick, playerLang);
                featureRClick = MessageUtils.translateMessage(featureRClick, playerLang);
            }
            Message featureTitle = formatTranslation(varInfoColor, varInfoColor, "%s", feature);
            MessageUtils.setCustomItemData(item, theme, featureTitle, Arrays.asList(featureLClick, featureRClick));
            
            ItemMeta im = item.getItemMeta();
            
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "feature"), PersistentDataType.STRING, feature.toLowerCase());
            if (selected) Glow.addGlow(im);
            
            FancyClickEvent.addFunction(im, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey featureKey = new NamespacedKey(Main.getInstance(), "feature");
                if (pdc.has(featureKey, PersistentDataType.STRING)) {
                    Set<String> innerFeatureSelection = fancyInventory.getData(featureSelectionDataName);
                    String innerFeature = pdc.get(featureKey, PersistentDataType.STRING);
                    if (innerFeatureSelection.contains(innerFeature)) {
                        innerFeatureSelection.remove(innerFeature);
                    } else {
                        innerFeatureSelection.add(innerFeature);
                    }
                    fancyInventory.setData(featureSelectionDataName, innerFeatureSelection);
                    openFeatureTP(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                }
            }));
            FancyClickEvent.addCommand(im, ClickType.RIGHT, "tport featureTP search " + feature);
            
            item.setItemMeta(im);
            if (selected) features.add(0, item);
            else features.add(item);
        }
        for (com.spaceman.tport.Pair<String, List<String>> pair : FeatureTP.getTags(player.getWorld())) {
            String m = switch (pair.getLeft().substring(1)) { //remove #
                case "ruined_portal" -> "CRYING_OBSIDIAN";
                case "dolphin_located" -> "DOLPHIN_SPAWN_EGG";
                case "on_woodland_explorer_maps", "on_ocean_explorer_maps", "on_treasure_maps", "on_trial_chambers_maps" -> "MAP";
                case "ocean_ruin" -> "TRIDENT";
                case "village" -> "EMERALD";
                case "eye_of_ender_located" -> "ENDER_EYE";
                case "mineshaft" -> "CHEST_MINECART";
                case "shipwreck" -> "OAK_BOAT";
                case "cats_spawn_as_black", "cats_spawn_in" -> "CAT_SPAWN_EGG";
                default -> "DIAMOND_BLOCK";
            };
            Material material = Main.getOrDefault(Material.getMaterial(m), Material.DIAMOND_BLOCK);
            ItemStack is = new ItemStack(material);
            
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
            lore.add(formatInfoTranslation("tport.commands.tport.featureTP.getItems.selectFeatures.additive." + ((featureList.size() == 1) ? "singular" : "multiple"), LEFT));
            lore.add(formatInfoTranslation("tport.commands.tport.featureTP.getItems.selectFeatures.overwrite." + ((featureList.size() == 1) ? "singular" : "multiple"), SHIFT_LEFT));
            lore.add(formatInfoTranslation("tport.commands.tport.featureTP.getItems.selectFeatures.run", ClickType.RIGHT));
            
            if (playerLang != null) { //if player has no custom language, translate it
                lore = MessageUtils.translateMessage(lore, playerLang);
            }
            Message featureTitle = formatTranslation(varInfo2Color, varInfo2Color, "%s", pair.getLeft());
            MessageUtils.setCustomItemData(is, theme, featureTitle, lore);
            
            ItemMeta im = is.getItemMeta();
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "featureTag/name"), PersistentDataType.STRING, pair.getLeft());
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "featureTag/featuresFromPreset"), PersistentDataType.LIST.strings(), pair.getRight());
            
            FancyClickEvent.addFunction(im, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey tagKey = new NamespacedKey(Main.getInstance(), "featureTag/featuresFromPreset");
                if (pdc.has(tagKey, PersistentDataType.LIST.strings())) {
                    List<String> featuresFromPreset = pdc.getOrDefault(tagKey, PersistentDataType.LIST.strings(), new ArrayList<>());
                    Set<String> innerFeatureSelection = clickType == LEFT ? fancyInventory.getData(featureSelectionDataName) : new HashSet<>();
                    int originalSelectionSize = innerFeatureSelection.size();
                    List<String> availableFeatures = FeatureTP.getFeatures(whoClicked.getWorld());
                    
                    for (String feature : featuresFromPreset) {
                        if (availableFeatures.contains(feature)) {
                            innerFeatureSelection.add(feature);
                        }
                    }
                    if (originalSelectionSize == innerFeatureSelection.size()) {
                        sendErrorTranslation(whoClicked, "tport.commands.tport.featureTP.getItems.selectFeatures.noBiomesLeft");
                    }
                    
                    fancyInventory.setData(featureSelectionDataName, innerFeatureSelection);
                    openFeatureTP(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                }
            }), LEFT, SHIFT_LEFT);
            FancyClickEvent.addCommand(im, ClickType.RIGHT, "tport featureTP search " + pair.getLeft());
            
            is.setItemMeta(im);
            features.add(is);
        }
        return features;
    }
    
    @Nullable
    public static Location setSafeY(@Nonnull World world, int x, int z) {
        if (world.getEnvironment().equals(World.Environment.NETHER)) {
            int originalX = x;
            int originalZ = z;
            int searches = 0;
            Random random = new Random();
            int spread = 10;
            
            for (int y = 1; y < world.getMaxHeight(); y++) {
                Location tempFeet = new Location(world, x, y, z);
                
                if (SafetyCheck.isSafe(tempFeet, true)) {
                    return tempFeet;
                } else if (tempFeet.getBlock().getType().equals(Material.BEDROCK) && y > 10) {
                    //spread location, reset search
                    if (searches == 20) {
                        return null;
                    }
                    x += (int) (random.nextInt(spread) - spread * 0.5);
                    z += (int) (random.nextInt(spread) - spread * 0.5);
                    y = 1;
                    searches++;
                }
            }
            return new Location(world, originalX, world.getHighestBlockYAt(originalX, originalZ) + 1, originalZ);
        } else {
            int y = world.getHighestBlockYAt(x, z);
            Location location = new Location(world, x, y + 1, z);
            Random random = new Random();
            int spread = 10;
            int searches = 0;
            
            while (y <= 1 || !SafetyCheck.isSafe(location)) {
                if (searches == 20) {
                    return null;
                }
                x += (int) (random.nextInt(spread) - spread * 0.5);
                z += (int) (random.nextInt(spread) - spread * 0.5);
                y = world.getHighestBlockYAt(x, z);
                location = new Location(world, x, y + 1, z);
                searches++;
            }
            return location;
        }
    }
}
