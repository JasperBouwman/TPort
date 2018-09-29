package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commands.CmdHandler;
import com.spaceman.tport.fancyMessage.book.Book;
import com.spaceman.tport.fancyMessage.book.BookPage;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.spaceman.tport.events.InventoryClick.NEXT;
import static com.spaceman.tport.events.InventoryClick.PREVIOUS;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class Help extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {
        if (args.length == 2) {
            openB(player);
        } else {
            openMainBook(player);
        }
    }


    @SuppressWarnings("deprecation")
    private void openB(Player player) {
        String json =
                "{pages:[\"[\\\"\\\",{\\\"text\\\":\\\"Welcome in the \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"TPort \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"help book,\\\\nin here you can find all the usages and help for all the commands.\\\\nyou can click in the register in the commands to go to the right page.\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\"these are all the sub-commands, all are explained of the usage and what they do.\\\\nextra info: <between there is necessary> [there are optional] \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":\\\"3\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"pages: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"3\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\", \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"4\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"/tport add \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":\\\"5\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"page: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"5\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"/tport edit \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":\\\"6\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"pages: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"6\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\", \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"7\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\", \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"8\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"/tport extra \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":\\\"9\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"pages: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"9\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\", \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"10\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\", \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"11\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"/tport open \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":\\\"12\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"page: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"12\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"/tport remove \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":\\\"13\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"page: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"13\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"/tport whitelist\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":\\\"14\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"pages: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"14\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\", \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"15\\\",\\\"color\\\":\\\"blue\\\"}]}}}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"usage: \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"this opens the TPort player list, when in this list you click on a playerhead and that takes you to their TPort list. click on an item to go to the location.\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\"when you click on their head you will teleport to that player when he/she is online. the barrier item will take you back the the TPort player list, use the hopper and fern to scroll through all the players\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport add \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"usage: \\\\n \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport add <TPort> [lore of TPort]\\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport add home this is my home\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"what this does is it adds the location of where you are standing in your TPort list. the lore is something like the comment of that TPort. the name is 1 word, the lore can be more\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport edit \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"usage: \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport edit <lore:name:item:location:private>\\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"you can edit a TPort using this.\\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport edit <TPort> lore set <new lore of TPort> \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport edit home lore set this is my old home\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this will edit the lore of the item\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport edit <TPort> lore remove \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport edit home lore remove\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this will remove the lore of the item \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport edit <TPort>  name <new name> \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport edit home name oldHome\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this will change the name of the TPort \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport edit <TPort> item \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport edit home item\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this will edit the item material to the item material in your main hand\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport edit <TPort> location \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport edit home location\\\",\\\"color\\\":\\\"blue\\\"}]}}}" +
                        ",{\\\"text\\\":\\\"this will edit the location of the TPort \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport edit <TPort> private <true:false> \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport edit home private true\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this will set the TPort to private or not, people that are in the whitelist are still able to teleport to that location.\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport extra \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"usage: \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport extra <item:tp:whitelist>\\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"this will edit some extra functions.\\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport extra item \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"this will set your item in the TPort player list. note: there is no undo.\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport extra tp <on:off> \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"this will toggle the ability to let players teleport to you, players in your whitelist are still able to. \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport extra whitelist list \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"this shows all the players in your player teleportation whitelist.\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport extra whitelist add <player name> \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport extra whitelist add %player%\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this adds a player in your player teleportation whitelist \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport extra whitelist remove <player name> \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport extra whitelist remove %player%\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this removes a player in your player teleportation whitelist.\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport open \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"usage: \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport open <player name> [TPort] \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"examples: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"\\\\n/tport open %player%\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"\\\\n/tport open %player% home\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this will open the TPort list of the selected player, when giving a TPort you will directly teleport to that location.\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport remove \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"usage: \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport remove <TPort> \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport remove home\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this will remove the TPort from your TPort list.\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport whitelist \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"usage: \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport whitelist <TPort> <add:remove:list> \\\\n\\\",\\\"color\\\":\\\"blue\\\"}," +
                        "{\\\"text\\\":\\\"this will edit the whitelist of that TPort \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport whitelist <TPort> add <player name> \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport whitelist home add %player%\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this will add the player in your whitelist\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"," +

                        "\"[\\\"\\\",{\\\"text\\\":\\\" /tport whitelist <TPort> remove <player name> \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport whitelist home remove %player%\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this will remove the player in your whitelist \\\\n\\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\" /tport whitelist <TPort> list \\\\n\\\",\\\"color\\\":\\\"blue\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\"\\\",\\\"extra\\\":[{\\\"text\\\":\\\"example: \\\",\\\"color\\\":\\\"dark_aqua\\\"}," +
                        "{\\\"text\\\":\\\"/tport whitelist home list\\\",\\\"color\\\":\\\"blue\\\"}]}}}," +
                        "{\\\"text\\\":\\\"this will show the whitelist list of that TPort\\\",\\\"color\\\":\\\"dark_aqua\\\"}]\"]" +

                        ",\"title\":\"The TPort Help Book\",\"author\":\"The_Spaceman\"}";

        try {
            ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
            stack = Bukkit.getUnsafe().modifyItemStack(stack, json.replaceAll("%player%", player.getName()));
            player.getInventory().addItem(stack);
        } catch (Throwable ignored) {
        }
    }

    private void openMainBook(Player player) {
        Book book = new Book("TPort", "The_Spaceman | " + player.getName(), ChatColor.BLACK);

        BookPage cover = book.createPage();
        BookPage index = book.createPage();
        BookPage aboutP1 = book.createPage();
        BookPage aboutP2 = book.createPage();
        BookPage aboutP3 = book.createPage();
        BookPage aboutP4 = book.createPage();
        BookPage aboutP5 = book.createPage();
        BookPage aboutP6 = book.createPage();
        BookPage aboutP7 = book.createPage();
        BookPage aboutP8 = book.createPage();
        BookPage commandPage1 = book.createPage();
        BookPage commandPage2 = book.createPage();
        BookPage commandPage3 = book.createPage();
        BookPage commandPage4 = book.createPage();
        BookPage cp1 = book.createPage();
        BookPage cp2 = book.createPage();
        BookPage cp3 = book.createPage();
        BookPage cp4 = book.createPage();
        BookPage cp5 = book.createPage();
        BookPage cp6 = book.createPage();
        BookPage cp7 = book.createPage();
        BookPage cp8 = book.createPage();
        BookPage cp9 = book.createPage();
        BookPage cp10 = book.createPage();
        BookPage cp11 = book.createPage();
        BookPage cp12 = book.createPage();
        BookPage cp13 = book.createPage();
        BookPage cp14 = book.createPage();
        BookPage cp15 = book.createPage();
        BookPage author1 = book.createPage();
        BookPage author2 = book.createPage();

        cover.addText("TPort", ChatColor.BLUE);
        cover.addText(textComponent("\n\nIn this book you can find a description of this plugin and all of it usages and commands", ChatColor.DARK_AQUA));

        index.addText("Index", ChatColor.DARK_AQUA);
        index.addText("\n\n");
        index.addText(textComponent("About the plugin", ChatColor.BLUE, ClickEvent.changePage(aboutP1.getPageNumber()), hoverEvent(textComponent("Page: " + aboutP1.getPageNumber(), ChatColor.DARK_AQUA))));
        index.addText(textComponent("\nCommands", ChatColor.BLUE, ClickEvent.changePage(commandPage1.getPageNumber()), hoverEvent(textComponent("Page: " + commandPage1.getPageNumber(), ChatColor.DARK_AQUA))));
        index.addText(textComponent("\nAbout the author", ChatColor.BLUE, ClickEvent.changePage(author1.getPageNumber()), hoverEvent(textComponent("Page: " + author1.getPageNumber(), ChatColor.DARK_AQUA))));

        aboutP1.addText(textComponent("About the plugin", ChatColor.DARK_AQUA));
        aboutP1.addText(textComponent("\n\nThis plugin lets you save locations to teleport to. It will save the given location under the item that is in your hand, and this is called a ", ChatColor.BLUE));
        HoverEvent hEvent1 = hoverEvent("The word TPort comes from the word ", ChatColor.BLUE);
        hEvent1.addText(textComponent("T", ChatColor.DARK_AQUA));
        hEvent1.addText(textComponent("ele", ChatColor.BLUE));
        hEvent1.addText(textComponent("Port", ChatColor.DARK_AQUA));
        aboutP1.addText(textComponent("TPort.", ChatColor.DARK_GREEN, hEvent1));
        aboutP1.addText(textComponent(" You can easily teleport to your TPort by using just 1 command", ChatColor.BLUE, hoverEvent("/tport own [TPort name]", ChatColor.BLUE)));

        aboutP2.addText(textComponent("Choose a player", ChatColor.DARK_AQUA));
        aboutP2.addText(textComponent("\n\nIn this inventory you can select a player. All players that have been online after the install of TPort are in this inventory." +
                " When clicking a player head you will open their TPort gui. When not all players can be displayed a hopper called '" + NEXT  + ChatColor.BLUE +
                "' will", ChatColor.BLUE));
        aboutP3.addText(textComponent("appear. Click on the hopper to show the next line of players. The fern called '" + PREVIOUS + ChatColor.BLUE +  "' will go back 1 line. This inventory is called" +
                " the ", ChatColor.BLUE));
        aboutP3.addText(textComponent("Main TPort gui", ChatColor.DARK_PURPLE));
        aboutP3.addText(textComponent("\n\nTPort: <player name>", ChatColor.DARK_AQUA));
        aboutP3.addText(textComponent("\n\nThis inventory is called the ", ChatColor.BLUE));
        aboutP3.addText(textComponent("TPort gui", ChatColor.DARK_PURPLE));
        aboutP3.addText(textComponent(", in here you can find ", ChatColor.BLUE));
        aboutP4.addText(textComponent("all the saved TPort of the owning player. When hovering over a TPort you can see of the TPort is private or not. If you are the owner of the TPort gui you can right-click" +
                " a TPort to toggle to set the TPort private or open. When clicking the player head you can teleport to that", ChatColor.BLUE));
        aboutP5.addText(textComponent("player, but only if his PLTP is set to open or when you are whitelisted. When you click on your own head in your own TPort gui you will toggle the PLTP" +
                " to open or private.", ChatColor.BLUE));
        aboutP6.addText(textComponent("When left-click the elytra you teleport back you your previous location, when right-click the elytra you open the BiomeTP gui." +
                " With the barrier you go back to the ", ChatColor.BLUE));
        aboutP6.addText(textComponent("Main TPort gui", ChatColor.DARK_PURPLE));
        aboutP6.addText(textComponent(".", ChatColor.BLUE));

        aboutP7.addText(textComponent("Select a Biome", ChatColor.DARK_AQUA));
        aboutP7.addText(textComponent("\n\nIn this inventory you can select a biome to use as a random teleporter, when selecting a biome the plugin is trying 100 times to find your selected biome." +
                " When not found you will be notified. When succeed you will teleport to that biome.", ChatColor.BLUE));

        aboutP8.addText(textComponent("Select a Feature"));
        aboutP8.addText(textComponent("\n\nIn this inventory you can select a feature to teleport to, when selecting a feature the plugin search the selected feature." +
                " When not found you will be notified. When succeed you will teleport to that feature.", ChatColor.BLUE));

        HoverEvent commandTitleHoverEvent = hoverEvent("");
        commandTitleHoverEvent.addText(textComponent("Command arguments between ", ChatColor.BLUE));
        commandTitleHoverEvent.addText(textComponent("<>", ChatColor.DARK_AQUA));
        commandTitleHoverEvent.addText(textComponent(" are necessary, between ", ChatColor.BLUE));
        commandTitleHoverEvent.addText(textComponent("[]", ChatColor.DARK_AQUA));
        commandTitleHoverEvent.addText(textComponent(" are optional and '", ChatColor.BLUE));
        commandTitleHoverEvent.addText(textComponent("...", ChatColor.DARK_AQUA));
        commandTitleHoverEvent.addText(textComponent("' means that you can use more arguments for your command", ChatColor.BLUE));

        commandPage1.addText(textComponent("Commands:", ChatColor.DARK_AQUA, commandTitleHoverEvent));
        commandPage1.addText(textComponent("\n/tport add <TPort name> [lore of TPort]", ChatColor.BLUE, ClickEvent.changePage(cp1.getPageNumber()), hoverEvent(textComponent("Page: " + cp1.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage1.addText(textComponent("\n/tport compass [player] [TPort name]", ChatColor.DARK_GREEN, ClickEvent.changePage(cp2.getPageNumber()), hoverEvent(textComponent("Page: " + cp2.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage1.addText(textComponent("\n/tport edit <TPort name> lore set <lore...>", ChatColor.BLUE, ClickEvent.changePage(cp3.getPageNumber()), hoverEvent(textComponent("Page: " + cp3.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage1.addText(textComponent("\n/tport edit <TPort name> lore remove", ChatColor.DARK_GREEN, ClickEvent.changePage(cp4.getPageNumber()), hoverEvent(textComponent("Page: " + cp4.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage1.addText(textComponent("\n/tport edit <TPort name> name <new TPort name>", ChatColor.BLUE, ClickEvent.changePage(cp4.getPageNumber()), hoverEvent(textComponent("Page: " + cp4.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage1.addText(textComponent("\n/tport edit <TPort name> item", ChatColor.DARK_GREEN, ClickEvent.changePage(cp5.getPageNumber()), hoverEvent(textComponent("Page: " + cp5.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage2.addText(textComponent("Commands:", ChatColor.DARK_AQUA, commandTitleHoverEvent));
        commandPage2.addText(textComponent("\n/tport edit <TPort name> location", ChatColor.BLUE, ClickEvent.changePage(cp5.getPageNumber()), hoverEvent(textComponent("Page: " + cp5.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage2.addText(textComponent("\n/tport edit <TPort name> private", ChatColor.DARK_GREEN, ClickEvent.changePage(cp6.getPageNumber()), hoverEvent(textComponent("Page: " + cp6.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage2.addText(textComponent("\n/tport edit <TPort name> private <true:false>", ChatColor.BLUE, ClickEvent.changePage(cp6.getPageNumber()), hoverEvent(textComponent("Page: " + cp6.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage2.addText(textComponent("\n/tport edit <TPort name> whitelist <add:remove> <player names...>", ChatColor.DARK_GREEN, ClickEvent.changePage(cp7.getPageNumber()), hoverEvent(textComponent("Page: " + cp7.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage2.addText(textComponent("\n/tport edit <TPort name> whitelist list", ChatColor.BLUE, ClickEvent.changePage(cp8.getPageNumber()), hoverEvent(textComponent("Page: " + cp8.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage3.addText(textComponent("Commands:", ChatColor.DARK_AQUA, commandTitleHoverEvent));
        commandPage3.addText(textComponent("\n/tport PLTP <on:off>", ChatColor.BLUE, ClickEvent.changePage(cp8.getPageNumber()), hoverEvent(textComponent("Page: " + cp8.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage3.addText(textComponent("\n/tport PLTP whitelist list", ChatColor.DARK_GREEN, ClickEvent.changePage(cp9.getPageNumber()), hoverEvent(textComponent("Page: " + cp9.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage3.addText(textComponent("\n/tport PLTP whitelist <add:remove> <playername>", ChatColor.BLUE, ClickEvent.changePage(cp10.getPageNumber()), hoverEvent(textComponent("Page: " + cp10.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage3.addText(textComponent("\n/tport help", ChatColor.DARK_GREEN, ClickEvent.changePage(cp10.getPageNumber()), hoverEvent(textComponent("Page: " + cp10.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage3.addText(textComponent("\n/tport open <playername> [TPort name]", ChatColor.BLUE, ClickEvent.changePage(cp11.getPageNumber()), hoverEvent(textComponent("Page: " + cp11.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage3.addText(textComponent("\n/tport own [TPort name]", ChatColor.DARK_GREEN, ClickEvent.changePage(cp12.getPageNumber()), hoverEvent(textComponent("Page: " + cp12.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage4.addText(textComponent("Commands:", ChatColor.DARK_AQUA, commandTitleHoverEvent));
        commandPage4.addText(textComponent("\n/tport remove <TPort name>", ChatColor.BLUE, ClickEvent.changePage(cp12.getPageNumber()), hoverEvent(textComponent("Page: " + cp12.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage4.addText(textComponent("\n/tport removePlayer <playerName>", ChatColor.DARK_GREEN, ClickEvent.changePage(cp13.getPageNumber()), hoverEvent(textComponent("Page: " + cp13.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage4.addText(textComponent("\n/tport back", ChatColor.BLUE, ClickEvent.changePage(cp13.getPageNumber()), hoverEvent(textComponent("Page: " + cp13.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage4.addText(textComponent("\n/tport biomeTP", ChatColor.DARK_GREEN, ClickEvent.changePage(cp15.getPageNumber()), hoverEvent(textComponent("Page: " + cp15.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage4.addText(textComponent("\n/tport biomeTP [biome]", ChatColor.BLUE, ClickEvent.changePage(cp15.getPageNumber()), hoverEvent(textComponent("Page: " + cp15.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage4.addText(textComponent("\n/tport featureTP", ChatColor.DARK_GREEN, ClickEvent.changePage(cp15.getPageNumber()), hoverEvent(textComponent("Page: " + cp15.getPageNumber(), ChatColor.DARK_AQUA))));
        commandPage4.addText(textComponent("\n/tport featureTP [featureType]", ChatColor.BLUE, ClickEvent.changePage(cp15.getPageNumber()), hoverEvent(textComponent("Page: " + cp15.getPageNumber(), ChatColor.DARK_AQUA))));

        cp1.addText(textComponent("/tport add <TPort name> [lore of TPort]", ChatColor.DARK_AQUA));
        cp1.addText(textComponent("\n\nUse this command to create a new TPort. It will take the item from your main hand and that item will be used to display the TPort in your TPort gui." +
                " The lore you can add is like a description for your TPort, use ", ChatColor.BLUE));
        cp1.addText(textComponent("\\\\\\\\n", ChatColor.DARK_AQUA));
        cp1.addText(textComponent(" to create a new line", ChatColor.BLUE));

        cp2.addText(textComponent("/tport compass [player] [TPort name]", ChatColor.DARK_AQUA));
        cp2.addText(textComponent("\n\nThis will give you a compass item, when right-click with the compass you will open the main TPort gui. If you used a player in the command you will get a compass" +
                " with a player linked to it, ", ChatColor.BLUE));
        cp3.addText(textComponent("when right-click you will open their TPort gui. And if you even have a TPort linked to it you will teleport to that TPort", ChatColor.BLUE));
        cp3.addText(textComponent("\n\n/tport edit <TPort name> lore set <lore...>", ChatColor.DARK_AQUA));
        cp3.addText(textComponent("\n\nThis will edit the lore of the given TPort", ChatColor.BLUE));

        cp4.addText(textComponent("/tport edit <TPort name> lore remove", ChatColor.DARK_AQUA));
        cp4.addText(textComponent("\n\nThis will remove the lore of the given TPort", ChatColor.BLUE));
        cp4.addText(textComponent("\n\n/tport edit <TPort name> name <new TPort name>", ChatColor.DARK_AQUA));
        cp4.addText(textComponent("\n\nThis will edit the given TPorts name to the new given name", ChatColor.BLUE));

        cp5.addText(textComponent("/tport edit <TPort name> item", ChatColor.DARK_AQUA));
        cp5.addText(textComponent("\n\nThis will edit the item of the given TPort to the item in your main hand", ChatColor.BLUE));
        cp5.addText(textComponent("\n\n/tport edit <TPort name> location", ChatColor.DARK_AQUA));
        cp5.addText(textComponent("\n\nThis will edit the location of the given TPort to your location", ChatColor.BLUE));

        cp6.addText(textComponent("/tport edit <TPort name> private", ChatColor.DARK_AQUA));
        cp6.addText(textComponent("\n\nThis tell you the state of people can teleport to you", ChatColor.BLUE));
        cp6.addText(textComponent("\n\n/tport edit <TPort name> private <true:false>", ChatColor.DARK_AQUA));
        cp6.addText(textComponent("\n\nThis will edit the private state to the given value", ChatColor.BLUE));

        cp7.addText(textComponent("/tport edit <TPort name> whitelist <add:remove> <player names...>", ChatColor.DARK_AQUA));
        cp7.addText(textComponent("\n\nThis will allow you to edit the whitelist, when a TPort is set to private only people in that whitelist are able to teleport to that TPort", ChatColor.BLUE));

        cp8.addText(textComponent("/tport edit <TPort name> whitelist list", ChatColor.DARK_AQUA));
        cp8.addText(textComponent("\n\nThis will give you all the people that are in the given whitelist", ChatColor.BLUE));
        cp8.addText(textComponent("\n\n/tport PLTP <on:off>", ChatColor.DARK_AQUA));
        cp8.addText(textComponent("\n\nThis will set the PLTP to the given value. ", ChatColor.BLUE));
        cp8.addText(textComponent("\nWhen ", ChatColor.BLUE));
        HoverEvent PLTPHoverEvent = hoverEvent("PLTP stands for: ", ChatColor.BLUE);
        PLTPHoverEvent.addText(textComponent("PL", ChatColor.DARK_AQUA));
        PLTPHoverEvent.addText(textComponent("ayer", ChatColor.BLUE));
        PLTPHoverEvent.addText(textComponent("T", ChatColor.DARK_AQUA));
        PLTPHoverEvent.addText(textComponent("ele", ChatColor.BLUE));
        PLTPHoverEvent.addText(textComponent("P", ChatColor.DARK_AQUA));
        PLTPHoverEvent.addText(textComponent("ortation", ChatColor.BLUE));
        cp8.addText(textComponent("PLTP", ChatColor.DARK_GREEN, PLTPHoverEvent));
        cp8.addText(textComponent(" is off only players in your", ChatColor.BLUE));

        cp9.addText(textComponent("whitelist are able to teleport to you, and when on all players can teleport to you", ChatColor.BLUE));
        cp9.addText(textComponent("\n\n/tport PLTP whitelist list", ChatColor.DARK_AQUA));
        cp9.addText(textComponent("\n\nThis will give you all the people that are in your PLTP whitelist", ChatColor.BLUE));

        cp10.addText(textComponent("/tport PLTP whitelist <add:remove> <playername>", ChatColor.DARK_AQUA));
        cp10.addText(textComponent("\n\nThis will add/remove given players to you PLTP whitelist", ChatColor.BLUE));
        cp10.addText(textComponent("\n\n/tport help", ChatColor.DARK_AQUA));
        cp10.addText(textComponent("\n\nThis will open this book", ChatColor.BLUE));

        cp11.addText(textComponent("/tport open <playername> [TPort name]", ChatColor.DARK_AQUA));
        cp11.addText(textComponent("\n\nThis will open the TPort gui of the given player, when a TPort is given you will teleport to that TPort", ChatColor.BLUE));

        cp12.addText(textComponent("/tport own [TPort name]", ChatColor.DARK_AQUA));
        cp12.addText(textComponent("\n\nThis will open your own TPort gui, when a TPort is given you will teleport to that TPort", ChatColor.BLUE));
        cp12.addText(textComponent("\n\n/tport remove <TPort name>", ChatColor.DARK_AQUA));
        cp12.addText(textComponent("\n\nThis removes the given TPort", ChatColor.BLUE));

        cp13.addText(textComponent("/tport removePlayer <playerName>", ChatColor.DARK_AQUA));
        cp13.addText(textComponent("\n\nThis will remove a player from the main TPort gui and removes all his/her data", ChatColor.BLUE));
        cp13.addText(textComponent("\n\n/tport back", ChatColor.DARK_AQUA));
        cp13.addText(textComponent("\n\nThis will teleport you back. If you are teleporting to a ", ChatColor.BLUE));
        cp14.addText(textComponent("'from ...' location you will teleport back to your last known location when teleporting to your latest TPort teleportation or PLTP." +
                " If you are teleporting to a 'to ...' location you will teleport back to that given TPort or player", ChatColor.BLUE));

        cp15.addText(textComponent("/tport biomeTP", ChatColor.DARK_AQUA));
        cp15.addText(textComponent("\n\nOpen biomeTP gui", ChatColor.BLUE));
        cp15.addText(textComponent("\n\n/tport biomeTP <biome>", ChatColor.DARK_AQUA));
        cp15.addText(textComponent("\n\nTeleport to a random location or biome", ChatColor.BLUE));

        author1.addText(textComponent("About the author", ChatColor.DARK_AQUA));
        author1.addText(textComponent("\n\nI'm ", ChatColor.BLUE));
        author1.addText(textComponent("The_Spaceman", ChatColor.DARK_AQUA, ClickEvent.openUrl("https://dev.bukkit.org/members/the_spaceman2000/projects"),
                hoverEvent(textComponent("https://dev.bukkit.org/members/the_spaceman2000/projects", ChatColor.DARK_GREEN))));
        author1.addText(textComponent(" and I created this plugin. I wanted to create a plugin that visualized the common known command /warp, so I created TPort, a visual plugin " +
                "to teleport to saved locations.", ChatColor.BLUE));
        author2.addText(textComponent("When I can see whats available I can recognise earlier which locations I have saved. I created this plugin on my own and used a little help from friends to test" +
                " the plugin, but the code is fully made by me. I use this plugin to create other plugins", ChatColor.BLUE));

        book.openBook(player);
    }
}
