package com.spaceman.tport.fancyMessage.language.subCommands;

import com.google.gson.JsonObject;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.fancyMessage.language.Language;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.spaceman.tport.fancyMessage.MessageUtils.getOrDefaultCaseInsensitive;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Test extends SubCommand {
    
    public Test() {
        EmptyCommand emptyID = new EmptyCommand();
        emptyID.setCommandName("ID", ArgumentType.REQUIRED);
        emptyID.setCommandDescription(formatInfoTranslation("tport.command.language.test.id.commandDescription"));
        
        addAction(emptyID);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        JsonObject translation = Language.getServerLang();
        
        List<String> list = new ArrayList<>();
        for (String s : translation.keySet()) {
            if (!s.startsWith("_")) {
                list.add(s);
            }
        }
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport language test <id>
        
        if (args.length == 3) {
            JsonObject translation = Language.getPlayerLang(player.getUniqueId());
            String raw = args[2];
            if (translation == null) {
                formatInfoTranslation("tport.command.language.test.id.succeeded", raw, textComponent(raw).setType(TextType.TRANSLATE)).sendMessage(player);
            } else if (getOrDefaultCaseInsensitive(translation, raw, null) != null) {
                String translated = translation.get(raw).toString();
                sendInfoTranslation(player, "tport.command.language.test.id.succeeded", raw, translated);
            } else {
                sendErrorTranslation(player, "tport.command.language.test.id.idNotFound", raw);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport language test <id>");
        }
    }
}
