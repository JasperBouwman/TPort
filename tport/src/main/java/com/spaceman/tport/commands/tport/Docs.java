package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.docs.Chat;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.book.Book;
import com.spaceman.tport.fancyMessage.markdown.FancyNodeRenderer;
import org.apache.commons.io.IOUtils;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.IntStream;

import static com.spaceman.tport.commandHandler.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Docs extends SubCommand {
    
    // String:              markdown file name
    // ArrayList<String>:   markdown chapter names
    // ArrayList<Message>:  markdown chapters
    public static final HashMap<String, Pair<List<Book>, Book>> docs = new HashMap<>();
    
    EmptyCommand emptyFile;
    
    public Docs(CommandTemplate mainTemplate) {
        loadMD("quickStart.md", "/docs/quickStart.md", mainTemplate);
//        loadMD("readme.md", "/docs/readme.md", mainTemplate);
//        loadMD("changelog.md", "/docs/changelog.md", mainTemplate);

//        loadMD("testMarkdown.md", "/testMarkdown.md", mainTemplate);
        //loadMD("book.md", "/book.md", mainTemplate);
        
        emptyFile = new EmptyCommand();
        emptyFile.setCommandName("file", ArgumentType.OPTIONAL);
        emptyFile.addAction(new com.spaceman.tport.commands.tport.docs.Book());
//        emptyFile.addAction(new Chat());
        
        addAction(emptyFile);
    }
    
    private void loadMD(String name, String mdFile, CommandTemplate mainTemplate) {
        try (InputStream md = Main.class.getResourceAsStream(mdFile)) {
            if (md != null) {
                String mdText = IOUtils.toString(md, StandardCharsets.UTF_8);
                FancyNodeRenderer nodeRenderer = MessageUtils.fromSplitMarkdown(mdText, mainTemplate);
                nodeRenderer.markdownName = name;
                
                docs.put(name, new Pair<>(MessageUtils.renderVolumes(nodeRenderer), MessageUtils.renderChatPages(nodeRenderer)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return docs.keySet();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport docs <file> <book> [volume]
        // tport docs <file> <chat> [page]
        
        if (args.length == 1) {
            // todo open docs GUI
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport docs <file> " + convertToArgs(emptyFile.getActions(), false));
            return;
        } else if (args.length > 2) {
            if (runCommands(emptyFile.getActions(), args[2], args, player)) {
                return;
            }
        }
        
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport docs <file> " + convertToArgs(emptyFile.getActions(), false));
    }
}
