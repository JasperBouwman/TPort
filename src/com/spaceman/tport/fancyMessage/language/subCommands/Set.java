package com.spaceman.tport.fancyMessage.language.subCommands;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.language.Language;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.language.Language.getAvailableLang;

public class Set extends SubCommand {
    
    public Set() {
        EmptyCommand emptyCustom = new EmptyCommand();
        emptyCustom.setCommandName("custom", ArgumentType.FIXED);
        emptyCustom.setCommandDescription(formatInfoTranslation("tport.command.language.set.custom.commandDescription"));
    
        EmptyCommand emptyServer = new EmptyCommand();
        emptyServer.setCommandName("server", ArgumentType.FIXED);
        emptyServer.setCommandDescription(formatInfoTranslation("tport.command.language.set.server.commandDescription"));
    
        EmptyCommand emptyServerLanguage = new EmptyCommand();
        emptyServerLanguage.setCommandName("language", ArgumentType.REQUIRED);
        emptyServerLanguage.setCommandDescription(formatInfoTranslation("tport.command.language.set.serverLanguage.commandDescription"));
        
        addAction(emptyCustom);
        addAction(emptyServer);
        addAction(emptyServerLanguage);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        Collection<String> list = new ArrayList<>(getAvailableLang());
        list.add("custom");
        list.add("server");
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport language set custom
        // tport language set server
        // tport language set <language>
        
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("custom")) {
                sendSuccessTranslation(player, "tport.command.language.set.custom.succeeded", "custom", "/tport help language");
                Language.setPlayerLang(player.getUniqueId(), "custom");
            } else if (args[2].equalsIgnoreCase("server")) {
                Language.setPlayerLang(player.getUniqueId(), "server");
                sendSuccessTranslation(player, "tport.command.language.set.server.succeeded", "server");
            } else {
                String currentLang = Language.getPlayerLangName(player.getUniqueId());
                
                if (currentLang.equals(args[2])) {
                    sendErrorTranslation(player, "tport.command.language.set.language.alreadySet", currentLang);
                    return;
                }
                
                if (Language.getAvailableLang().stream().noneMatch(args[2]::equals)) {
                    sendErrorTranslation(player, "tport.command.language.set.language.languageNotExist", args[2]);
                    return;
                }
                
                if (Language.setPlayerLang(player.getUniqueId(), args[2])) {
                    sendSuccessTranslation(player, "tport.command.language.set.language.succeeded", args[2]);
                } else {
                    //should not happen, but just to be sure
                    sendErrorTranslation(player, "tport.command.language.set.language.languageNotExist", args[2]);
                }
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport language set <custom|server|<language>>");
        }
    }
}
