package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commands.CmdHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;

public class Help extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {
        ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);

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
            stack = Bukkit.getUnsafe().modifyItemStack(stack, json.replaceAll("%player%", player.getName()));
        } catch (Throwable localThrowable) {
            return;
        }
        book(stack, player);
    }

    private void book(ItemStack book, Player player) {

        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, book);

        ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, (byte) 0);
        buf.writerIndex(1);

        try {
            //get minecraft server version
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            //get player handle
            Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            //get player connection
            Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);

            Class<?> packetDataSerializer = Class.forName("net.minecraft.server." + version + ".PacketDataSerializer");
            Constructor<?> packetDataSerializerConstructor = packetDataSerializer.getConstructor(ByteBuf.class);

            Class<?> packetPlayOutCustomPayload = Class.forName("net.minecraft.server." + version + ".PacketPlayOutCustomPayload");
            Constructor packetPlayOutCustomPayloadConstructor = packetPlayOutCustomPayload.getConstructor(String.class,
                    Class.forName("net.minecraft.server." + version + ".PacketDataSerializer"));

            connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet"))
                    .invoke(connection, packetPlayOutCustomPayloadConstructor.newInstance("MC|BOpen", packetDataSerializerConstructor.newInstance(buf)));

        } catch (Exception ex) {
            player.getInventory().addItem(book);
        }
        player.getInventory().setItem(slot, old);
    }
}