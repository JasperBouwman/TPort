package com.spaceman.textureGenerator;

import com.google.gson.*;
import com.spaceman.tport.Pair;
import com.spaceman.tport.advancements.TPortAdvancementsModels;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.fancyMessage.inventories.WaypointModel;
import com.spaceman.tport.fancyMessage.inventories.keyboard.KeyboardGUI;
import com.spaceman.tport.inventories.QuickEditInventories;
import com.spaceman.tport.inventories.SettingsInventories;
import com.spaceman.tport.inventories.TPortInventories;
import com.spaceman.tport.waypoint.WaypointModels;
import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.gson.JsonParser.parseReader;

public class Main {
    
    private static final boolean deleteFolders = true;
    
    // .../texture_generator/target/classes/
    private static final String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    
    private static final String outputDir = path + "/../../texture_output";
    
    private static final int pack_format = 46;
    
    private static final HashMap<Color, Color> lightColorMap = com.spaceman.tport.Main.asMap(
            new Pair<>(
                    new Color(61, 61, 61),
                    new Color(100, 100, 100)
            ),
            new Pair<>(
                    new Color(31, 31, 31),
                    new Color(50, 50, 50)
            )
    );
    
//    record Model(String name, Material material, int model_data, String subDir) {}
    private static final ArrayList<Integer> collectedModelData = new ArrayList<>();
    private static int lastModelData = 0;
    private static ArrayList<InventoryModel> collectModels(Class<?> clazz) {
        ArrayList<InventoryModel> models = new ArrayList<>();
        final String modelSuffix = "_model";
        
        for (Field modelField : clazz.getFields()) {
            if (modelField.getType() == InventoryModel.class) {
                try {
                    InventoryModel inventoryModel = (InventoryModel) modelField.get(null);
                    int modelData = inventoryModel.getCustomModelData();
                    lastModelData = Math.max(lastModelData, modelData);
                    
                    String modelName = inventoryModel.getNamespacedKey().getKey();
                    if (modelName.endsWith(modelSuffix)) {
                        modelName = modelName.substring(0, modelName.length() - modelSuffix.length());
                    }
                    
                    if (collectedModelData.contains(modelData)) {
                        throw new IllegalArgumentException("Model data is already used: " + modelData + " (" + modelName + ")");
                    }
                    collectedModelData.add(modelData);
                    
                    System.out.println(modelName + " " + modelData);
                    
                    models.add(inventoryModel);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return models;
    }
    
    private static ArrayList<WaypointModel> collectWaypointModels(Class<?> clazz) {
        ArrayList<WaypointModel> models = new ArrayList<>();
        final String modelSuffix = "_model";
        
        for (Field modelField : clazz.getFields()) {
            if (modelField.getType() == WaypointModel.class) {
                try {
                    WaypointModel waypointModel = (WaypointModel) modelField.get(null);
                    
                    String waypointName = waypointModel.getNamespacedKey().getKey();
                    if (waypointName.endsWith(modelSuffix)) {
                        waypointName = waypointName.substring(0, waypointName.length() - modelSuffix.length());
                    }
                    
                    System.out.println(waypointName);
                    
                    models.add(waypointModel);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return models;
    }
//    private static ArrayList<Model> collectDynmapModels() {
//    //import org.dynmap.markers.impl.MarkerAPIImpl;
//        ArrayList<Model> models = new ArrayList<>(85);
//
//        File outputDirectory = new File(outputDir + "/assets/tport/textures/item/dynmap_markers");
//        try {
//            if (deleteFolders) FileUtils.deleteDirectory(outputDirectory);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        outputDirectory.mkdirs();
//
//        try {
//            Field builtin_icons_field = org.dynmap.markers.impl.MarkerAPIImpl.class.getDeclaredField("builtin_icons");
//            builtin_icons_field.setAccessible(true);
//
//            String[] markerArray = (String[]) builtin_icons_field.get(null);
//
//            for (String markerName : markerArray) {
//                Model model = new Model(markerName, Material.OAK_BUTTON, lastModelData++, "");
//
//                File outputFile = new File(outputDir + "/assets/tport/textures/item/dynmap_markers/dynmap_" + markerName + ".png");
//                InputStream in = org.dynmap.markers.impl.MarkerAPIImpl.class.getResourceAsStream("/markers/" + markerName + ".png");
//
//                java.nio.file.Files.copy(in, outputFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
//                in.close();
//
//                models.add(model);
//            }
//
//        } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return models;
//    }
    
    private static final HashMap<Material, JsonObject> minecraftJson = new HashMap<>();
    private static JsonObject getJsonObject(Material material) {
        JsonObject j = minecraftJson.get(material);
        if (j == null) {
            File jsonFile = new File(path + "/model_json/" + material.name().toLowerCase() + ".json");
            InputStream jsonStream;
            try {
                jsonStream = new FileInputStream(jsonFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            
            JsonObject json = parseReader(new InputStreamReader(jsonStream, StandardCharsets.UTF_8)).getAsJsonObject();
            minecraftJson.put(material, json);
            return json;
        } else {
            return j;
        }
    }
    private static void createMinecraftModels(ArrayList<InventoryModel> models) {
        
        for (InventoryModel model : models) {
            
            JsonObject json = getJsonObject(model.getMaterial());
            
            JsonObject custom_model_data = new JsonObject();
            custom_model_data.add("custom_model_data", new JsonPrimitive(model.getCustomModelData()));
            
            JsonObject predicate = new JsonObject();
            predicate.add("predicate", custom_model_data);
            predicate.add("model", new JsonPrimitive(model.getNamespacedKey().getNamespace() + ":item/" + model.getName()));
            
            JsonArray overrides = json.getAsJsonArray("overrides");
            if (overrides == null) {
                overrides = new JsonArray();
            }
            overrides.add(predicate);
            
            json.add("overrides", overrides);
        }
        
        File outputDirectory = new File(outputDir + "/assets/minecraft/models/item");
        try {
            if (deleteFolders) FileUtils.deleteDirectory(outputDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outputDirectory.mkdirs();
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        for (Map.Entry<Material, JsonObject> modelEntry : minecraftJson.entrySet()) {
            
            File outputFile = new File(outputDirectory.getAbsolutePath() + "/" + modelEntry.getKey().name().toLowerCase() + ".json");
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            try {
                FileWriter fileWriter = new FileWriter(outputFile);
                try {
                    fileWriter.write(gson.toJson(modelEntry.getValue()));
                } finally {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    private static void defineTPortModels(ArrayList<InventoryModel> models) {
//        HashMap<InventoryModel, JsonObject> tportJson = new HashMap<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        File outputDirectory = new File(outputDir + "/assets/tport/items");
        try {
            if (deleteFolders) FileUtils.deleteDirectory(outputDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outputDirectory.mkdirs();
        
        for (InventoryModel model : models) {
            
            JsonObject json = new JsonObject();
            
            JsonObject modelObject = new JsonObject();
            modelObject.add("type", new JsonPrimitive("minecraft:model"));
            modelObject.add("model", new JsonPrimitive(model.getNamespacedKey().getNamespace() + ":item/" + model.getName()));
            json.add("model", modelObject);
//            tportJson.put(model, json);
            
            File outputFile = new File(outputDirectory.getAbsolutePath() + "/" + model.getName() + ".json");
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            try {
                FileWriter fileWriter = new FileWriter(outputFile);
                try {
                    fileWriter.write(gson.toJson(json));
                } finally {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
        
//        for (Map.Entry<InventoryModel, JsonObject> modelEntry : tportJson.entrySet()) {
//
//            File outputFile = new File(outputDirectory.getAbsolutePath() + "/" + modelEntry.getKey().getName() + ".json");
//            try {
//                outputFile.createNewFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            try {
//                FileWriter fileWriter = new FileWriter(outputFile);
//                try {
//                    fileWriter.write(gson.toJson(modelEntry.getValue()));
//                } finally {
//                    fileWriter.flush();
//                    fileWriter.close();
//                }
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            }
//        }
    }
    private static void createTPortModels(ArrayList<InventoryModel> models) {
//        HashMap<InventoryModel, JsonObject> tportJson = new HashMap<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        File outputDirectory = new File(outputDir + "/assets/tport/models/item");
        try {
            if (deleteFolders) FileUtils.deleteDirectory(outputDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outputDirectory.mkdirs();
        
        for (InventoryModel model : models) {
            
            JsonObject json = new JsonObject();
            
            json.add("parent", new JsonPrimitive("minecraft:item/generated"));
            
            JsonObject textures = new JsonObject();
            if (model.hasSubDir()) {
                textures.add("layer0", new JsonPrimitive(model.getNamespacedKey().getNamespace() + ":item/" + model.getSubDir() + "/" + model.getName()));
            } else {
                textures.add("layer0", new JsonPrimitive(model.getNamespacedKey().getNamespace() + ":item/" + model.getName()));
            }
            json.add("textures", textures);
//            tportJson.put(model, json);
            
            File outputFile = new File(outputDirectory.getAbsolutePath() + "/" + model.getName() + ".json");
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            try {
                FileWriter fileWriter = new FileWriter(outputFile);
                try {
                    fileWriter.write(gson.toJson(json));
                } finally {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
        
//        for (Map.Entry<InventoryModel, JsonObject> modelEntry : tportJson.entrySet()) {
//
//            File outputFile = new File(outputDirectory.getAbsolutePath() + "/" + modelEntry.getKey().getName() + ".json");
//            try {
//                outputFile.createNewFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            try {
//                FileWriter fileWriter = new FileWriter(outputFile);
//                try {
//                    fileWriter.write(gson.toJson(modelEntry.getValue()));
//                } finally {
//                    fileWriter.flush();
//                    fileWriter.close();
//                }
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            }
//        }
    }
    
    private static void createTPortWaypoints(ArrayList<WaypointModel> models) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        File outputDirectory = new File(outputDir + "/assets/tport/waypoint_style");
        try {
            if (deleteFolders) FileUtils.deleteDirectory(outputDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outputDirectory.mkdirs();
        
        for (WaypointModel model : models) {
            
            JsonObject json = new JsonObject();
            
            JsonArray jsonArray = new JsonArray();
            if (model.hasSubDir()) {
                jsonArray.add(model.getNamespacedKey().getNamespace() + ":" + model.getSubDir() + "/" + model.getName());
            } else {
                jsonArray.add(model.getNamespacedKey().getNamespace() + ":" + model.getName());
            }
            json.add("sprites", jsonArray);
            
            File outputFile = new File(outputDirectory.getAbsolutePath() + "/" + model.getName() + ".json");
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            try {
                FileWriter fileWriter = new FileWriter(outputFile);
                try {
                    fileWriter.write(gson.toJson(json));
                } finally {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    private static void createPack_mcmeta(String packDir, boolean lightMode) throws URISyntaxException, IOException {
        InputStream inputStream = com.spaceman.tport.Main.class.getResourceAsStream("/plugin.yml");
        FileConfiguration plugin_yml = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject pack_mcmeta = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.add("pack_format", new JsonPrimitive(pack_format));
        String description = "";
        if (lightMode) {
            description = String.format("TPort %s (v%s) (light), made by The_Spaceman", packDir, plugin_yml.get("version"));
        } else {
            description = String.format("TPort %s (v%s) (dark), made by The_Spaceman", packDir, plugin_yml.get("version"));
        }
        pack.add("description", new JsonPrimitive(description));
        pack_mcmeta.add("pack", pack);
        System.out.println(gson.toJson(pack_mcmeta));
        
        File outputFile = new File(outputDir + "/pack.mcmeta");
        
        FileWriter fileWriter = new FileWriter(outputFile);
        try {
            fileWriter.write(gson.toJson(pack_mcmeta));
        } finally {
            fileWriter.flush();
            fileWriter.close();
        }
    }
    private static void copyTextures(ArrayList<InventoryModel> models, ArrayList<WaypointModel> waypointModels, String packDir, boolean lightMode) {
        File outputDirectory = new File(outputDir + "/assets/tport/textures/item");
        File waypointOutputDirectory = new File(outputDir + "/assets/tport/textures/gui/sprites/hud/locator_bar_dot");
        try {
            if (deleteFolders) {
                FileUtils.deleteDirectory(outputDirectory);
                FileUtils.deleteDirectory(waypointOutputDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outputDirectory.mkdirs();
        waypointOutputDirectory.mkdir();
        boolean missingTextures = false;
        
        for (InventoryModel model : models) {
            File texture;
            texture = new File(path + "/icons/" + packDir + "/" + model.getName() + ".png");
            if (texture.exists()) {
                try {
                    File newFile;
                    
                    if (model.hasSubDir()) {
                        newFile = new File(outputDirectory, model.getSubDir() + "/" + model.getName() + ".png");
                    } else {
                        newFile = new File(outputDirectory, model.getName() + ".png");
                    }
                    FileUtils.copyFile(texture, newFile);
                    if (lightMode) colorConverter(newFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Texture '" + texture.getName() + "' does not exist");
                missingTextures = true;
            }
            
            File textureMCMeta = new File(path + "/icons/" + packDir + "/" + model.getName() + ".png.mcmeta");
            if (textureMCMeta.exists()) {
                try {
                    File newFile;
                    if (model.hasSubDir()) {
                        newFile = new File(outputDirectory, model.getSubDir() + "/" + model.getName() + ".png.mcmeta");
                    } else {
                        newFile = new File(outputDirectory, model.getName() + ".png.mcmeta");
                    }
                    FileUtils.copyFile(textureMCMeta, newFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            
        }
        
        for (WaypointModel model : waypointModels) {
            File texture;
            texture = new File(path + "/icons/" + packDir + "/" + model.getName() + ".png");
            if (texture.exists()) {
                try {
                    File newFile;
                    
                    if (model.hasSubDir()) {
                        newFile = new File(waypointOutputDirectory, model.getSubDir() + "/" + model.getName() + ".png");
                    } else {
                        newFile = new File(waypointOutputDirectory, model.getName() + ".png");
                    }
                    FileUtils.copyFile(texture, newFile);
                    if (lightMode) colorConverter(newFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Texture '" + texture.getName() + "' does not exist");
                missingTextures = true;
            }
            
            File textureMCMeta = new File(path + "/icons/" + packDir + "/" + model.getName() + ".png.mcmeta");
            if (textureMCMeta.exists()) {
                try {
                    File newFile;
                    if (model.hasSubDir()) {
                        newFile = new File(waypointOutputDirectory, model.getSubDir() + "/" + model.getName() + ".png.mcmeta");
                    } else {
                        newFile = new File(waypointOutputDirectory, model.getName() + ".png.mcmeta");
                    }
                    FileUtils.copyFile(textureMCMeta, newFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            
        }
        
        File packPNG = new File(path + "/icons/" + packDir + "/pack.png");
        if (packPNG.exists()) {
            try {
                FileUtils.copyFile(packPNG, new File(outputDir, "pack.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Texture 'pack.png' does not exist");
            missingTextures = true;
        }
        
        if (missingTextures) {
            throw new IllegalArgumentException("Missing textures, see log above");
        }
        
        try {
            createPack_mcmeta(packDir, lightMode);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void colorConverter(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            boolean hasChanged = false;
            
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    Color c = new Color(img.getRGB(x, y), true);
                    c = lightColorMap.getOrDefault(c, null);
                    if (c != null) {
                        img.setRGB(x, y, c.getRGB());
                        hasChanged = true;
                    }
                }
            }
            
            if (hasChanged) ImageIO.write(img, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void copyTexturePack(File packDir) {
        if (packDir.isFile()) {
            throw new IllegalArgumentException("Output file is a file, it must be a directory");
        }
        try {
            if (deleteFolders) FileUtils.deleteDirectory(packDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        File texture_output = new File(outputDir);
        
        try {
            FileUtils.copyDirectory(texture_output, packDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void createPack(ArrayList<InventoryModel> models, ArrayList<WaypointModel> waypointModels, String srcDir, String outputDir, boolean lightMode) {
        try {
            copyTextures(models, waypointModels, srcDir, lightMode);
            System.out.println(new File(outputDir).getAbsolutePath());
            copyTexturePack(new File(outputDir));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not create texture pack " + srcDir);
        }
    }
    
    public static void main(String[] args) {
        
        ArrayList<InventoryModel> models = collectModels(FancyInventory.class);
        models.addAll( collectModels(KeyboardGUI.class) );
        models.addAll( collectModels(TPortInventories.class) );
        models.addAll( collectModels(QuickEditInventories.class) );
        models.addAll( collectModels(SettingsInventories.class) );
        models.addAll( collectModels(TPortAdvancementsModels.class) );
        
        createMinecraftModels(models);
        defineTPortModels(models);
        createTPortModels(models);
        
        ArrayList<WaypointModel> waypointModels = collectWaypointModels(WaypointModels.class);
        createTPortWaypoints(waypointModels);
        
        createPack(models, waypointModels, "x32", "../resource pack/src (32x)_dark", false);
        createPack(models, waypointModels, "x32", "../resource pack/src (32x)_light", true);
        createPack(models, waypointModels, "x16", "../resource pack/src (16x)_dark", false);
        createPack(models, waypointModels, "x16", "../resource pack/src (16x)_light", true);
        
        //delete texture_output
        try {
            if (deleteFolders) FileUtils.deleteDirectory(new File(outputDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        System.out.println("done");
    }
}
