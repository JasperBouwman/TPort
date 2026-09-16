package com.spaceman.tport.commands.tport.docs;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Docs;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Book extends SubCommand {
    
    public Book() {
        EmptyCommand emptyVolume = new EmptyCommand();
        emptyVolume.setCommandName("volume", ArgumentType.OPTIONAL);
        addAction(emptyVolume);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        List<com.spaceman.tport.fancyMessage.book.Book> books = Docs.docs.getOrDefault(args[1], null).getLeft();
        if (books == null) return Collections.emptyList();
        return IntStream.range(1, books.size() + 1).mapToObj(String::valueOf).toList();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport docs <file> <book> [volume]
        
        if (args.length == 3) {
            List<com.spaceman.tport.fancyMessage.book.Book> books = Docs.docs.getOrDefault(args[1], null).getLeft();
            if (books == null) {
                player.sendMessage("no chapters found");
            } else {
                
                books.get(0).openTranslatedBook(player);
                
//                for (com.spaceman.tport.fancyMessage.book.Book book : books) {
//                    Main.giveItems(player, book.getWrittenBook(player));
//                }
            }
        } else if (args.length == 4) {
            
            List<com.spaceman.tport.fancyMessage.book.Book> volumes = Docs.docs.getOrDefault(args[1], null).getLeft();
            if (volumes == null) {
                player.sendMessage("no chapters found");
                return;
            }
            
            int volume;
            try {
                volume = Integer.parseInt(args[3]);
            } catch (NumberFormatException nfe) {
                player.sendMessage("is not a number");
                return;
            }
            
            if (volume > volumes.size()) {
                player.sendMessage("book has no volume " + volume);
                return;
            }
            
            volumes.get(volume - 1).openTranslatedBook(player);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport docs <file> book [volume]");
        }
    }
}
