package com.spaceman.tport.fancyMessage.language.subCommands;

import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.language.Language;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;

import static com.google.gson.JsonParser.parseReader;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Repair extends SubCommand {
    
    private final EmptyCommand emptyRepairLanguageRepairWith;
    
    public Repair() {
        emptyRepairLanguageRepairWith = new EmptyCommand();
        emptyRepairLanguageRepairWith.setCommandName("repair with", ArgumentType.OPTIONAL);
        emptyRepairLanguageRepairWith.setPermissions("TPort.language.repair", "TPort.admin.language");
        emptyRepairLanguageRepairWith.setCommandDescription(formatInfoTranslation("tport.command.language.repair.language.repairWith.commandDescription"));
        
        EmptyCommand emptyRepairLanguage = new EmptyCommand();
        emptyRepairLanguage.setCommandName("language", ArgumentType.REQUIRED);
        emptyRepairLanguage.setCommandDescription(formatInfoTranslation("tport.command.language.repair.language.commandDescription"));
        emptyRepairLanguage.setTabRunnable(((args, player) -> {
            if (!emptyRepairLanguageRepairWith.hasPermissionToRun(player, false)) {
                return Collections.emptyList();
            }
            return Language.getAvailableLang();
        }));
        emptyRepairLanguage.setPermissions(emptyRepairLanguageRepairWith.getPermissions());
        emptyRepairLanguage.addAction(emptyRepairLanguageRepairWith);
        
        addAction(emptyRepairLanguage);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyRepairLanguageRepairWith.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Language.getAvailableLang();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport language repair <language> [repair with]
        
        if (args.length == 3) {
            if (!emptyRepairLanguageRepairWith.hasPermissionToRun(player, true)) {
                return;
            }
            
            File langFile = new File(Main.getInstance().getDataFolder(), "lang/" + args[2]);
            
            if (!langFile.exists()) {
                sendErrorTranslation(player, "tport.command.language.repair.language.languageFileNotExist", args[2]);
                return;
            }
            
            try {
                JsonObject oldJSON = (JsonObject) parseReader(new FileReader(langFile));
                Pair<JsonObject, Integer> newJSON = Language.repairLanguage(oldJSON, Language.getLang("en_us.json"));
                if (newJSON == null) {
                    sendErrorTranslation(player, "tport.command.language.repair.language.couldNotRepair", args[2]);
                    return;
                }
                
                if (newJSON.getRight() == 0) {
                    sendInfoTranslation(player, "tport.command.language.repair.language.succeeded.none", args[2]);
                }
                
                Language.saveLanguage(newJSON.getLeft(), langFile);
                
                if (newJSON.getRight() == 1)     sendSuccessTranslation(player, "tport.command.language.repair.language.succeeded.singular", args[2], newJSON.getRight());
                else if (newJSON.getRight() > 1) sendSuccessTranslation(player, "tport.command.language.repair.language.succeeded.multiple", args[2], newJSON.getRight());
                
            } catch (FileNotFoundException e) {
                sendErrorTranslation(player, "tport.command.language.repair.language.couldNotRepair", args[2]);
            }
            
        } else if (args.length == 4) {
            if (!emptyRepairLanguageRepairWith.hasPermissionToRun(player, true)) {
                return;
            }
            
            File langFile = new File(Main.getInstance().getDataFolder(), "lang/" + args[2]);
            
            if (!langFile.exists()) {
                sendErrorTranslation(player, "tport.command.language.repair.language.repairWith.languageFileNotExist", args[2]);
                return;
            }
            
            JsonObject repairWith = Language.getLang(args[3]);
            if (repairWith == null) {
                sendErrorTranslation(player, "tport.command.language.repair.language.repairWith.languageNotExist", args[3]);
                return;
            }
            
            try {
                JsonObject oldJSON = (JsonObject) parseReader(new FileReader(langFile));
                Pair<JsonObject, Integer> newJSON = Language.repairLanguage(oldJSON, repairWith);
                if (newJSON == null) {
                    sendErrorTranslation(player, "tport.command.language.repair.language.repairWith.couldNotRepair", args[2]);
                    return;
                }
                
                if (newJSON.getRight() == 0) {
                    sendInfoTranslation(player, "tport.command.language.repair.language.repairWith.succeeded.none", args[2]);
                }
                
                Language.saveLanguage(newJSON.getLeft(), langFile);
    
                if (newJSON.getRight() == 1)     sendSuccessTranslation(player, "tport.command.language.repair.language.repairWith.succeeded.singular", args[2], args[3], newJSON.getRight());
                else if (newJSON.getRight() > 1) sendSuccessTranslation(player, "tport.command.language.repair.language.repairWith.succeeded.multiple", args[2], args[3], newJSON.getRight());
                
            } catch (FileNotFoundException e) {
                sendErrorTranslation(player, "tport.command.language.repair.language.repairWith.couldNotRepair", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport language repair <language> [repair with]");
        }
    }
}
