package com.spaceman.tport.fancyMessage.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.language.Language;
import net.minecraft.network.chat.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftMetaBookSigned;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Book {
    
    private final String title;
    private final String author;
    private final ArrayList<BookPage> pages = new ArrayList<>();
    
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }
    
    @Override
    public String toString() {
        return pages.stream().map(BookPage::toString).collect(Collectors.joining());
    }
    
    public BookPage createPage(TextComponent textComponent) {
        BookPage page = new BookPage(textComponent);
        addPage(page);
        return page;
    }
    
    public BookPage createPage() {
        BookPage page = new BookPage();
        addPage(page);
        return page;
    }
    
    @SuppressWarnings("deprecation")
    public ItemStack getWritableBook() {
        ItemStack stack = new ItemStack(Material.WRITABLE_BOOK);
        
        BookMeta meta = (BookMeta) stack.getItemMeta();
        meta.setAuthor(author);
        meta.setTitle(title);
        
        meta.addPage("test");
        
        stack.setItemMeta(meta);
        
        if (true) return stack;
        
        try {
            return Bukkit.getUnsafe().modifyItemStack(stack, translateString());
        } catch (Throwable localThrowable) {
            return stack;
        }
    }
    
    public ItemStack getWrittenBook(@Nullable Player player) {
        return getWrittenBook(player, false);
    }
    
    public ItemStack getWrittenBook(@Nullable Player player, boolean translate) {
        ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
        
        ColorTheme colorTheme = player == null ? ColorTheme.getDefaultTheme(ColorTheme.getDefaultThemes().get(0)) : ColorTheme.getTheme(player);
        JsonObject playerLang = null;
        if (player != null) {
            playerLang = Language.getPlayerLang(player);
        }
        
        CraftMetaBookSigned meta = (CraftMetaBookSigned) stack.getItemMeta();
        meta.setAuthor(author);
        meta.setTitle(title);
        
        try {
            Method s = CraftMetaBookSigned.class.getDeclaredMethod("internalAddPage", Component.class);
            s.setAccessible(true);
            for (BookPage page : pages) {
                if (translate) {
                    s.invoke(meta, CraftChatMessage.fromJSON(page.translatePage(playerLang).translateJSON(colorTheme)));
                } else {
                    s.invoke(meta, CraftChatMessage.fromJSON(page.translateJSON(colorTheme)));
                }
            }
        } catch (Exception e) {
            Features.Feature.printSmallNMSErrorInConsole("Create book", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) e.printStackTrace();
        }
        
        stack.setItemMeta(meta);
        
        return stack;
    }
    
    @SuppressWarnings("All")
    public static void openBook(ItemStack book, Player player) {
        
        if (!book.getType().equals(Material.WRITTEN_BOOK)) {
            throw new IllegalArgumentException("Given item is not a written book");
        }
        
        player.openBook(book);
    }
    
    public void openBook(Player player) {
        openBook(getWrittenBook(player), player);
    }
    
    public void openTranslatedBook(Player player) {
        openBook(getWrittenBook(player, true), player);
    }
    
    public String translateString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("author", author);
        
        JsonArray jsonArray = new JsonArray();
        pages.stream().map(BookPage::translateString).forEach(jsonArray::add);
        jsonObject.add("pages", jsonArray);
        
        return jsonObject.toString().replaceAll("\\\\/", "/").replace("\\n", "\n");
    }
    
    public String translateJSON(Player player) {
        return translateJSON(ColorTheme.getTheme(player));
    }
    
    public String translateJSON(ColorTheme theme) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("author", author);
        
        JsonArray jsonArray = new JsonArray();
        pages.stream().map(p -> p.translateJSON(theme)).forEach(jsonArray::add);
        jsonObject.add("pages", jsonArray);
        
        return jsonObject.toString().replaceAll("\\\\{3}/", "/");
    }
    
    public ArrayList<BookPage> getPages() {
        return pages;
    }
    
    public int getPageNumber(BookPage page) {
        int i = 1;
        for (BookPage tmpPage : pages) {
            if (tmpPage.equals(page)) {
                return i;
            }
            i++;
        }
        return -1;
    }
    
    public void addBook(Book book) {
        for (BookPage page : book.getPages()) {
            addPage(page);
        }
    }
    
    public void setPage(int page, BookPage bookPage) {
        pages.remove(bookPage);
        if (page > pages.size()) {
            pages.add(bookPage);
            bookPage.setPageNumber(pages.size());
        } else {
            if (page < 1) {
                page = 1;
            }
            BookPage newPage = bookPage;
            for (int i = page; i <= pages.size(); i++) {
                BookPage tmpPage = pages.get(i - 1);
                newPage.setPageNumber(i);
                pages.set(i - 1, newPage);
                newPage = tmpPage;
            }
            addPage(newPage);
        }
    }
    
    public void addPage(BookPage bookPage) {
        setPage(pages.size() + 1, bookPage);
    }
}
