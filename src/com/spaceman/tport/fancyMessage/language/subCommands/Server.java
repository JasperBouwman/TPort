package com.spaceman.tport.fancyMessage.language.subCommands;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varErrorColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.language.Language.getAvailableLang;

public class Server extends SubCommand {
    
    private final EmptyCommand language;
    
    public Server() {
        language = new EmptyCommand();
        language.setCommandName("language", ArgumentType.OPTIONAL);
        language.setCommandDescription(formatInfoTranslation("tport.command.language.server.language.commandDescription"));
        language.setPermissions("TPort.language.setServerLanguage", "TPort.admin.language");
        addAction(language);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getAvailableLang();
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.language.server.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport language server [language]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.language.server.succeeded", Language.getServerLangName());
        } else if (args.length == 3) {
            if (!language.hasPermissionToRun(player, true)) {
                return;
            }
            String currentServerLanguage = Language.getServerLangName();
            
            if (currentServerLanguage.equals(args[2])) {
                sendErrorTranslation(player, "tport.command.language.server.language.alreadySet", currentServerLanguage);
                return;
            }
            
            if (Language.getAvailableLang().stream().noneMatch(args[2]::equals)) {
                sendErrorTranslation(player, "tport.command.language.server.language.languageNotExist", args[2]);
                return;
            }
            
            Message hereMessage = new Message();
            hereMessage.addText(textComponent("tport.command.language.server.language.here", varErrorColor,
                    new HoverEvent(textComponent("/tport language set " + currentServerLanguage, varInfoColor)),
                    ClickEvent.runCommand("/tport language set " + currentServerLanguage)).setType(TextType.TRANSLATE));
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                sendInfoTranslation(p, "tport.command.language.server.language.succeededOtherPlayers", player, args[2], hereMessage);
            }
            
            if (Language.setServerLang(args[2])) {
                sendSuccessTranslation(player, "tport.command.language.server.language.succeeded", Language.getServerLangName());
            } else {
                //should not happen, but just to be sure
                sendErrorTranslation(player, "tport.command.language.server.language.languageNotExist", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport language server [language]");
        }
    }
}