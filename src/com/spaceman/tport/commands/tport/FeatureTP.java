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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
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
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spaceman.tport.TPortInventories.openFeatureTP;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;

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
            Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
            WorldServer worldServer = (WorldServer) nmsWorld;

            IRegistryCustom registry = worldServer.s();
            IRegistry<Structure> structureRegistry = registry.d(IRegistry.aN);

            return structureRegistry.d().stream().map(MinecraftKey::a).map(String::toLowerCase).toList();
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static List<com.spaceman.tport.Pair<String, List<String>>> getTags(World world) {
        List<com.spaceman.tport.Pair<String, List<String>>> list = new ArrayList<>();
        
        try {
            Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
            WorldServer worldServer = (WorldServer) nmsWorld;
            
            IRegistryCustom registry = worldServer.s();
            IRegistry<Structure> structureRegistry = registry.d(IRegistry.aN);
            
            List<String> tags = structureRegistry.i().map((tagKey) -> tagKey.b().a()).toList();
            
            for (String tagKeyName : tags) {
                TagKey<Structure> tagKey = TagKey.a(IRegistry.aN, new MinecraftKey(tagKeyName));
                
                Optional<HolderSet.Named<Structure>> optional = structureRegistry.c(tagKey);
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
        for (com.spaceman.tport.Pair<String, List<String>> pair : FeatureTP.getTags(player.getWorld())) {
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
    
    @Nullable
    public static Location setSafeY(@Nonnull World world, int x, int z) {
        if (world.getEnvironment().equals(World.Environment.NETHER)) {
            int originalX = x;
            int originalZ = z;
            int searches = 0;
            Random random = new Random();
            
            for (int y = 1; y < world.getMaxHeight(); y++) {
                Location tempFeet = new Location(world, x, y, z);
                
                if (SafetyCheck.isSafe(tempFeet)) {
                    return tempFeet;
                } else if (tempFeet.getBlock().getType().equals(Material.BEDROCK) && y > 10) {
                    //spread location, reset search
                    if (searches == 20) {
                        return null;
                    }
                    int spread = 10;
                    x += random.nextInt(spread) - spread * 0.5;
                    z += random.nextInt(spread) - spread * 0.5;
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
                x += random.nextInt(spread) - spread * 0.5;
                z += random.nextInt(spread) - spread * 0.5;
                y = world.getHighestBlockYAt(x, z);
                l = new Location(world, x, y + 1, z);
                searches++;
            }
            return l;
        }
    }
}
