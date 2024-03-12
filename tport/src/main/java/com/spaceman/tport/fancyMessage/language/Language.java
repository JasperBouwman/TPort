package com.spaceman.tport.fancyMessage.language;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.language.subCommands.*;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import static com.google.gson.JsonParser.parseReader;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public class Language extends SubCommand {
    
    private static HashMap<String, JsonObject> languages = new HashMap<>();
    
    public static boolean setServerLang(String lang) {
        if (languages.containsKey(lang)) {
            tportConfig.getConfig().set("language.server", lang);
            tportConfig.saveConfig();
            return true;
        }
        return false;
    }
    
    public static String getServerLangName() {
        String name = tportConfig.getConfig().getString("language.server", "en_us.json");
        if (!languages.containsKey(name)) {
            name = "en_us.json";
        }
        return name;
    }
    
    public static JsonObject getServerLang() {
        return languages.get(getServerLangName());
    }
    
    //returns false if language has not been set
    public static boolean setPlayerLang(UUID uuid, @Nonnull String lang) {
        if (!lang.equalsIgnoreCase("custom") && !lang.equalsIgnoreCase("server") && !languages.containsKey(lang)) {
            return false;
        }
        tportConfig.getConfig().set("language.players." + uuid.toString(), lang);
        tportConfig.saveConfig();
        return true;
    }
    
    public static String getPlayerLangName(UUID uuid) {
        String lang = tportConfig.getConfig().getString("language.players." + uuid.toString(), "server");
        if (!languages.containsKey(lang)) {
            if (lang.equals("custom") || lang.equals("server")) return lang;
            setPlayerLang(uuid, "en_us.json");
            return "en_us.json";
        }
        return lang;
    }
    
    @Nullable
    public static JsonObject getPlayerLang(Player player) {
        return getPlayerLang(player.getUniqueId());
    }
    @Nullable
    public static JsonObject getPlayerLang(UUID uuid) {
        String playerLang = getPlayerLangName(uuid);
        if (playerLang.equals("server")) {
            return getServerLang();
        } else if (playerLang.equals("custom")) {
            return null;
        } else {
            return languages.get(playerLang);
        }
    }
    
    @Nullable
    public static JsonObject getLang(String langName) {
        return languages.get(langName);
    }
    
    public static void loadLanguages() {
        languages = new HashMap<>();
        InputStream en_us_resource = Main.getInstance().getResource("lang/en_us.json");
        JsonObject defaultEN_US = (JsonObject) parseReader(new InputStreamReader(en_us_resource, StandardCharsets.UTF_8));
        languages.put("en_us.json", defaultEN_US);
        
        try {
            File langFile = new File(Main.getInstance().getDataFolder(), "lang/en_us.json");
            langFile.getParentFile().mkdir();
            langFile.createNewFile();
            
            saveLanguage(defaultEN_US, langFile);
        } catch (IOException ignore) {
            Main.getInstance().getLogger().log(Level.WARNING, "Could not update 'lang/en_us.json'");
        }
        
        File[] files = new File(Main.getInstance().getDataFolder().getAbsolutePath() + "\\lang").listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile() && !f.getName().contains(" ")) {
                    try {
                        if (FilenameUtils.getExtension(f.getName()).equals("json")) {
                            Main.getInstance().getLogger().log(Level.INFO, "Loading language " + f.getName());
                            Pair<JsonObject, Integer> json = loadLanguage(f);
                            if (json != null) {
                                languages.putIfAbsent(f.getName(), json.getLeft());
                                
                                if (json.getRight() == 1) Main.getInstance().getLogger().log(Level.INFO, String.format("Repaired language %s internally, missing %s item", f.getName(), 1));
                                if (json.getRight() > 1) Main.getInstance().getLogger().log(Level.INFO, String.format("Repaired language %s internally, missing %s items", f.getName(), json.getRight()));
                            } else {
                                Main.getInstance().getLogger().log(Level.WARNING, "Could not load language " + f.getName());
                            }
                        }
                    } catch (IllegalArgumentException ignore) {
                    }
                }
            }
        }
    }
    
    public static void saveLanguage(JsonObject json, File langFile) {
        try {
            FileWriter fileWriter = new FileWriter(langFile);
            try {
                fileWriter.write(json.toString());
            } finally {
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public static Pair<JsonObject, Integer> repairLanguage(JsonObject language, @Nullable JsonObject repairWith) {
        return repairLanguage(language, repairWith, false);
    }
    public static Pair<JsonObject, Integer> repairLanguage(JsonObject language, @Nullable JsonObject repairWith, boolean dump) {
        if (repairWith == null) return null;
        
        if (dump) Main.getInstance().getLogger().log(Level.INFO, "repairing language, missing ID's:");
        
        var ref = new Object() { int amountRepaired = 0; };
        repairWith.keySet().forEach(id -> {
            if (!language.has(id)) {
                language.add(id, repairWith.get(id));
                ref.amountRepaired++;
                if (dump) Main.getInstance().getLogger().log(Level.INFO, id);
            }
        });
        
        if (dump && ref.amountRepaired == 0) Main.getInstance().getLogger().log(Level.INFO, "No missing ID's");
        return new Pair<>(language, ref.amountRepaired);
    }
    
    public static Pair<JsonObject, Integer> loadLanguage(File langFile) {
        try {
            JsonObject oldJSON = (JsonObject) parseReader(new FileReader(langFile));
            return repairLanguage(oldJSON, languages.get("en_us.json"));
        } catch (JsonParseException | FileNotFoundException ignored) { }
        return null;
    }
    
    public static Collection<String> getAvailableLang() {
        return languages.keySet();
    }
    
    public Language() {
        addAction(new Server());
        addAction(new Get());
        addAction(new Set());
        addAction(new Test());
        addAction(new Repair());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport language server [language]
        // tport language get
        // tport language set custom
        // tport language set server
        // tport language set <server language>
        // tport language test <id>
        // tport language repair <language> [repair with] [dump]
        
        if (args.length > 1 && runCommands(getActions(), args[1], args, player)) {
            return;
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport language " + CommandTemplate.convertToArgs(getActions(), false));
    }
}
