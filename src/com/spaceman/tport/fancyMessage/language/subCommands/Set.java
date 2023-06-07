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
        EmptyCommand emptySetCustom = new EmptyCommand();
        emptySetCustom.setCommandName("custom", ArgumentType.FIXED);
        emptySetCustom.setCommandDescription(formatInfoTranslation("tport.command.language.set.custom.commandDescription"));
        
        EmptyCommand emptySetServer = new EmptyCommand();
        emptySetServer.setCommandName("server", ArgumentType.FIXED);
        emptySetServer.setCommandDescription(formatInfoTranslation("tport.command.language.set.server.commandDescription"));
        
        EmptyCommand emptySetServerLanguage = new EmptyCommand();
        emptySetServerLanguage.setCommandName("language", ArgumentType.REQUIRED);
        emptySetServerLanguage.setCommandDescription(formatInfoTranslation("tport.command.language.set.language.commandDescription"));
        
        addAction(emptySetCustom);
        addAction(emptySetServer);
        addAction(emptySetServerLanguage);
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
