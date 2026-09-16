package com.spaceman.tport.commands.tport.docs;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Docs;
import com.spaceman.tport.fancyMessage.book.Book;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Chat extends SubCommand {
    
    public Chat() {
        EmptyCommand emptyPage = new EmptyCommand();
        emptyPage.setCommandName("page", ArgumentType.OPTIONAL);
        addAction(emptyPage);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        Book books = Docs.docs.getOrDefault(args[1], null).getRight();
        if (books == null) return Collections.emptyList();
        return IntStream.range(1, books.getPages().size() + 1).mapToObj(String::valueOf).toList();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport docs <file> <chat> [page]
        
        if (args.length == 3) {
        
        } else if (args.length == 4) {
            
            Book pages = Docs.docs.getOrDefault(args[1], null).getRight();
            if (pages == null) {
                player.sendMessage("no pages found");
                return;
            }
            
            int page;
            try {
                page = Integer.parseInt(args[3]);
            } catch (NumberFormatException nfe) {
                player.sendMessage("is not a number");
                return;
            }
            
            if (page > pages.getPages().size()) {
                player.sendMessage("book has no page " + page);
                return;
            }
            
            pages.getPages().get(page - 1).getMessage().sendMessage(player);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport docs <file> chat [page]");
        }
        
    }
}
