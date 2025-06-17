package com.spaceman.tport.commands.tport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.language.Language;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class GeneratePermFile extends SubCommand {
    
    private final CommandTemplate template;
    
    private final EmptyCommand emptyFilterFile;
    
    public GeneratePermFile(CommandTemplate template) {
        this.template = template;
        
        emptyFilterFile = new EmptyCommand();
        emptyFilterFile.setCommandName("file type", ArgumentType.OPTIONAL);
        emptyFilterFile.setPermissions("tport.generatePermFile", "tport.admin");
        emptyFilterFile.setCommandDescription(formatInfoTranslation("tport.command.generatePermFile.commandDescription"));
        
        EmptyCommand emptyFilter = new EmptyCommand();
        emptyFilter.setCommandName("filter none", ArgumentType.OPTIONAL);
        emptyFilter.setTabRunnable(((args, player) -> List.of("csv", "json")));
        emptyFilter.setPermissions("tport.generatePermFile", "tport.admin");
        emptyFilter.setCommandDescription(formatInfoTranslation("tport.command.generatePermFile.commandDescription"));
        emptyFilter.addAction(emptyFilterFile);
        
        addAction(emptyFilter);
        this.setPermissions("tport.generatePermFile", "tport.admin");
        this.setCommandDescription(formatInfoTranslation("tport.command.generatePermFile.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return List.of("true", "false");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport generatePermFile [filter none] [file type]
        
        if (!emptyFilterFile.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length > 3) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport generatePermFile [filter none] [file type]");
            return;
        }
        
        boolean filterNone = false;
        if (args.length >= 2) {
            Boolean filter = Main.toBoolean(args[1]);
            if (filter == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport generatePermFile [true|false] [file type]");
                return;
            } else if (filter) {
                filterNone = true;
            }
        }
        
        String fileType = ".csv";
        int fileID = 1;
        
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("csv")) {
                fileType = ".csv";
                fileID = 1;
            } else if (args[2].equalsIgnoreCase("json")) {
                fileType = ".json";
                fileID = 2;
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport generatePermFile [filter none] [csv|json]");
                return;
            }
        }
        File outputFile = new File(Main.getInstance().getDataFolder(), "TPort permissions " + Main.getInstance().getDescription().getVersion() + fileType);
        
        switch (fileID) {
            case 1:
                if (generateCSV(outputFile, filterNone)) {
                    sendSuccessTranslation(player, "tport.command.generatePermFile.succeeded");
                } else {
                    sendErrorTranslation(player, "tport.command.generatePermFile.error");
                }
                break;
            case 2:
                if (generateJson(outputFile, filterNone)) {
                    sendSuccessTranslation(player, "tport.command.generatePermFile.succeeded");
                } else {
                    sendErrorTranslation(player, "tport.command.generatePermFile.error");
                }
                break;
            default:
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport generatePermFile [filter none] [csv|json]");
                return;
        }
        
        
    }
    
    private boolean generateCSV(File outputFile, boolean filterNone) {
        HashMap<String, SubCommand> commandMap = template.collectActions();
        JsonObject lang = Language.getServerLang();
        
        try {
            FileWriter fileWriter = new FileWriter(outputFile);
            
            StringBuilder outputString = new StringBuilder();
            for (Map.Entry<String, SubCommand> entry : commandMap.entrySet()) {
                if (!entry.getValue().getPermissions().isEmpty()) {
                    String perm = entry.getValue().permissionsHover().translateMessage(lang).translateString();
                    outputString.append(entry.getKey()).append(",").append(perm).append("\n");
                } else if (!filterNone) {
                    outputString.append(entry.getKey()).append(",NONE").append("\n");
                }
            }
            
            fileWriter.write(outputString.toString());
            fileWriter.flush();
            fileWriter.close();
            return true;
            
        } catch (IOException ioE) {
            return false;
        }
    }
    
    private boolean generateJson(File outputFile, boolean filterNone) {
        HashMap<String, SubCommand> commandMap = template.collectActions();
        JsonObject lang = Language.getServerLang();
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        
        JsonObject json = new JsonObject();
        
        try {
            FileWriter fileWriter = new FileWriter(outputFile);
            
            for (Map.Entry<String, SubCommand> entry : commandMap.entrySet()) {
                if (!entry.getValue().getPermissions().isEmpty()) {
                    String perm = entry.getValue().permissionsHover().translateMessage(lang).translateString();
                    json.add(entry.getKey(), new JsonPrimitive(perm));
                } else if (!filterNone) {
                    json.add(entry.getKey(), new JsonPrimitive("NONE"));
                }
            }
            
            fileWriter.write(gsonBuilder.toJson(json));
            fileWriter.flush();
            fileWriter.close();
            return true;
            
        } catch (IOException ioE) {
            return false;
        }
    }
    
}
