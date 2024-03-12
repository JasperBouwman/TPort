package com.spaceman.textureGenerator;

import com.google.gson.*;
import com.spaceman.tport.Pair;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.fancyMessage.inventories.keyboard.KeyboardGUI;
import com.spaceman.tport.inventories.QuickEditInventories;
import com.spaceman.tport.inventories.SettingsInventories;
import com.spaceman.tport.inventories.TPortInventories;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.gson.JsonParser.parseReader;

public class Main {
    
    private static final boolean deleteFolders = false;
    
    // .../texture_generator/target/classes/
    private static final String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    
    private static final String outputDir = path + "/../../texture_output";
    
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
    
    record Model(String name, Material material, int model_data, String subDir) {}
    private static final ArrayList<Integer> collectedModelData = new ArrayList<>();
    private static int lastModelData = 0;
    private static ArrayList<Model> collectModels(Class<?> clazz) {
        ArrayList<Model> models = new ArrayList<>();
        final String modelSuffix = "_model";
        
        for (Field modelField : clazz.getFields()) {
            if (modelField.getType() == InventoryModel.class) {
                if (modelField.getName().endsWith(modelSuffix)) {
                    try {
                        InventoryModel inventoryModel = (InventoryModel) modelField.get(null);
                        int modelData = inventoryModel.getCustomModelData();
                        lastModelData = Math.max(lastModelData, modelData);
                        Material material = inventoryModel.getMaterial();
                        String subDir = inventoryModel.getSubDir();
                        
                        if (collectedModelData.contains(modelData)) {
                            throw new IllegalArgumentException("Model data is already used: " + modelData + " (" + modelField.getName() + ")");
                        }
                        collectedModelData.add(modelData);
                        System.out.println(modelField.getName() + " " + modelData);
                        
                        String modelName = modelField.getName();
                        modelName = modelName.substring(0, modelName.length() - modelSuffix.length());
                        
                        models.add( new Model(modelName.toLowerCase(), material, modelData, subDir));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
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
    private static void createMinecraftModels(ArrayList<Model> models) {
        
        for (Model model : models) {
            JsonObject json = getJsonObject(model.material);
            
            JsonObject custom_model_data = new JsonObject();
            custom_model_data.add("custom_model_data", new JsonPrimitive(model.model_data));
            
            JsonObject predicate = new JsonObject();
            predicate.add("predicate", custom_model_data);
            predicate.add("model", new JsonPrimitive("tport:item/" + model.name));
            
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
    
    private static void createTPortModels(ArrayList<Model> models) {
        HashMap<Model, JsonObject> tportJson = new HashMap<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        for (Model model : models) {
            JsonObject json = new JsonObject();
            
            json.add("parent", new JsonPrimitive("minecraft:item/generated"));
            
            JsonObject textures = new JsonObject();
            if (model.subDir == null) {
                textures.add("layer0", new JsonPrimitive("tport:item/" + model.name));
            } else {
                textures.add("layer0", new JsonPrimitive("tport:item/" + model.subDir + "/" + model.name));
            }
            json.add("textures", textures);
            
            tportJson.put(model, json);
        }
        
        File outputDirectory = new File(outputDir + "/assets/tport/models/item");
        try {
            if (deleteFolders) FileUtils.deleteDirectory(outputDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outputDirectory.mkdirs();
        
        for (Map.Entry<Model, JsonObject> modelEntry : tportJson.entrySet()) {
            
            File outputFile = new File(outputDirectory.getAbsolutePath() + "/" + modelEntry.getKey().name + ".json");
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
    
    private static void createPack_mcmeta(String packDir, boolean lightMode) throws URISyntaxException, IOException {
        InputStream inputStream = com.spaceman.tport.Main.class.getResourceAsStream("/plugin.yml");
        FileConfiguration plugin_yml = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject pack_mcmeta = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.add("pack_format", new JsonPrimitive(22));
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
    private static void copyTextures(ArrayList<Model> models, String packDir, boolean lightMode) {
        File outputDirectory = new File(outputDir + "/assets/tport/textures/item");
        try {
            if (deleteFolders) FileUtils.deleteDirectory(outputDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outputDirectory.mkdirs();
        boolean missingTextures = false;
        
        for (Model model : models) {
            File texture;
            texture = new File(path + "/icons/" + packDir + "/" + model.name + ".png");
            if (texture.exists()) {
                try {
                    File newFile;
                    if (model.subDir == null) {
                        newFile = new File(outputDirectory, model.name + ".png");
                    } else {
                        newFile = new File(outputDirectory, model.subDir + "/" + model.name + ".png");
                    }
                    FileUtils.copyFile(texture, newFile);
                    if (lightMode) lightModeConverter(newFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
//                throw new IllegalArgumentException("Texture '" + texture.getName() + "' does not exist");
                System.out.println("Texture '" + texture.getName() + "' does not exist");
                missingTextures = true;
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
    
    private static void lightModeConverter(File file) {
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
    
    private static void createPack(ArrayList<Model> models, String srcDir, String outputDir, boolean lightMode) {
        try {
            copyTextures(models, srcDir, lightMode);
            System.out.println(new File(outputDir).getAbsolutePath());
            copyTexturePack(new File(outputDir));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not create texture pack " + srcDir);
        }
    }
    
    public static void main(String[] args) {
        
        if (false) {
            System.out.println("running");
            
            System.out.println(path);
            
            File file1 = new File("/classes/model_json/hopper.json");
            File file2 = new File(path + "/../../texture_output");
            file2.mkdir();
            URL url1 = ClassLoader.getSystemResource("/model_json/hopper.json");
            URL url2 = ClassLoader.getSystemResource("/src/main/resources/model_json/hopper.json");
            
            System.out.println(file1.exists());
            System.out.println(file1);
            System.out.println(file2.exists());
            System.out.println(file2.getPath());
            System.out.println(url1);
            System.out.println(url2);
            
            return;
        }
        
        ArrayList<Model> models = collectModels(FancyInventory.class);
        models.addAll( collectModels(KeyboardGUI.class) );
        models.addAll( collectModels(TPortInventories.class) );
        models.addAll( collectModels(QuickEditInventories.class) );
        models.addAll( collectModels(SettingsInventories.class) );
//        models.addAll( collectDynmapModels() );
        
        createMinecraftModels(models);
        createTPortModels(models);
        
        createPack(models, "x32", "..\\resource pack\\src (32x)_dark", false);
        createPack(models, "x32", "..\\resource pack\\src (32x)_light", true);
        createPack(models, "x16", "..\\resource pack\\src (16x)_dark", false);
        createPack(models, "x16", "..\\resource pack\\src (16x)_light", true);
        
        //delete texture_output
        try {
            if (deleteFolders) FileUtils.deleteDirectory(new File(outputDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        System.out.println("done");
    }
}
