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
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.pageDataName;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.inventories.TPortInventories.openFeatureTP;
import static com.spaceman.tport.reflection.ReflectionManager.*;

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
    
    public static List<String> getFeatures(World world) {
        try {
            WorldServer worldServer = getWorldServer(world);
            IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);

            return structureRegistry.e().stream().map(MinecraftKey::a).map(String::toLowerCase).toList();
        } catch (Exception | Error ex) {
            Features.Feature.printSmallNMSErrorInConsole("FeatureTP feature list", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static List<com.spaceman.tport.Pair<String, List<String>>> getTags(World world) {
        List<com.spaceman.tport.Pair<String, List<String>>> list = new ArrayList<>();
        
        try {
            WorldServer worldServer = getWorldServer(world);
            IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);
            
//            List<String> tags = structureRegistry.i().map((tagKey) -> tagKey.b().a()).toList();
            List<String> tags = structureRegistry.i().map((tagKey) -> tagKey.getFirst().b().a()).toList();
            
            for (String tagKeyName : tags) {
                TagKey<Structure> tagKey = TagKey.a(getStructureResourceKey(), new MinecraftKey(tagKeyName));
                
                Optional<HolderSet.Named<Structure>> optional = structureRegistry.b(tagKey);
                if (optional.isPresent()) {
                    HolderSet.Named<Structure> named = optional.get();
                    Stream<Holder<Structure>> values = named.a();
                    
                    List<String> features = values.map((holder) -> {
                        return holder.a(); //Holder -> StructureFeature
                    }).map((structureFeature) -> {
//                        structureFeature.a(); //biomes
                        MinecraftKey key = structureRegistry.b(structureFeature);
                        if (key != null) {
                            return key.a().toLowerCase();
                        }
                        return null;
                    }).filter(Objects::nonNull).toList();
                    
                    list.add(new com.spaceman.tport.Pair<>("#" + tagKeyName.toLowerCase(), features));
                }
            }
        } catch (Exception | Error ex) {
            Features.Feature.printSmallNMSErrorInConsole("FeatureTP tags", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
        }
        
        return list;
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
    public static List<ItemStack> getItems(Player player, ArrayList<String> featureSelection) {
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
            Message featureLClick = formatInfoTranslation("tport.tportInventories.openFeatureTP.feature.LClick", ClickType.LEFT, selectedMessage);
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
            
            FancyClickEvent.addFunction(im, ClickType.LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
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
                case "on_woodland_explorer_maps", "on_ocean_explorer_maps", "on_treasure_maps" -> "MAP";
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
            lore.add(formatInfoTranslation("tport.commands.tport.featureTP.getItems.selectFeatures.additive." + ((featureList.size() == 1) ? "singular" : "multiple"), ClickType.LEFT));
            lore.add(formatInfoTranslation("tport.commands.tport.featureTP.getItems.selectFeatures.overwrite." + ((featureList.size() == 1) ? "singular" : "multiple"), ClickType.SHIFT_LEFT));
            lore.add(formatInfoTranslation("tport.commands.tport.featureTP.getItems.selectFeatures.run", ClickType.RIGHT));
            
            if (playerLang != null) { //if player has no custom language, translate it
                lore = MessageUtils.translateMessage(lore, playerLang);
            }
            Message featureTitle = formatTranslation(varInfo2Color, varInfo2Color, "%s", pair.getLeft());
            MessageUtils.setCustomItemData(is, theme, featureTitle, lore);
            
            ItemMeta im = is.getItemMeta();
            String featuresAsString = String.join("|", pair.getRight());
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "featureTag/name"), PersistentDataType.STRING, pair.getLeft());
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "featureTag/features"), PersistentDataType.STRING, featuresAsString);
            
            FancyClickEvent.addFunction(im, ClickType.LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey tagKey = new NamespacedKey(Main.getInstance(), "featureTag/features");
                if (pdc.has(tagKey, PersistentDataType.STRING)) {
                    String innerFeatures = pdc.get(tagKey, PersistentDataType.STRING);
                    String[] featureArray = innerFeatures.split("\\|");
                    ArrayList<String> innerFeatureSelection = fancyInventory.getData("featureSelection", ArrayList.class, new ArrayList<String>());
                    Arrays.stream(featureArray).filter(s -> !innerFeatureSelection.contains(s)).forEach(innerFeatureSelection::add);
                    fancyInventory.setData("featureSelection", innerFeatureSelection);
                    openFeatureTP(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                }
            }));
            FancyClickEvent.addFunction(im, ClickType.SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey tagKey = new NamespacedKey(Main.getInstance(), "featureTag/features");
                if (pdc.has(tagKey, PersistentDataType.STRING)) {
                    String innerFeatures = pdc.get(tagKey, PersistentDataType.STRING);
                    String[] featureArray = innerFeatures.split("\\|");
                    ArrayList<String> innerFeatureSelection = Arrays.stream(featureArray).collect(Collectors.toCollection(ArrayList::new));
                    fancyInventory.setData("featureSelection", innerFeatureSelection);
                    openFeatureTP(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                }
            }));
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
                
                if (SafetyCheck.isSafe(tempFeet)) {
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
            return new Location(world, originalX, world.getHighestBlockYAt(originalX, originalZ), originalZ);
        } else {
            int y = world.getHighestBlockYAt(x, z);
            Location l = new Location(world, x, y + 1, z);
            Random random = new Random();
            int spread = 10;
            int searches = 0;
            
            while (y <= 1 || !SafetyCheck.isSafe(l)) {
                if (searches == 20) {
                    return null;
                }
                x += (int) (random.nextInt(spread) - spread * 0.5);
                z += (int) (random.nextInt(spread) - spread * 0.5);
                y = world.getHighestBlockYAt(x, z);
                l = new Location(world, x, y + 1, z);
                searches++;
            }
            return l;
        }
    }
}
