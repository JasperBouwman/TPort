package com.spaceman.tport.commands.tport.teleporter;

import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.Teleporter;
import com.spaceman.tport.commands.tport.teleporter.create.Back;
import com.spaceman.tport.commands.tport.teleporter.create.Home;
import com.spaceman.tport.commands.tport.teleporter.create.PLTP;
import com.spaceman.tport.commands.tport.teleporter.create.Public;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Create extends SubCommand {
    
    public Create() {
        if (Features.Feature.BackTP.isEnabled()) addAction(new Back());
        if (Features.Feature.BiomeTP.isEnabled()) addAction(new com.spaceman.tport.commands.tport.teleporter.create.BiomeTP());
        if (Features.Feature.FeatureTP.isEnabled()) addAction(new com.spaceman.tport.commands.tport.teleporter.create.FeatureTP());
        if (Features.Feature.PLTP.isEnabled()) addAction(new PLTP());
        if (Features.Feature.PublicTP.isEnabled()) addAction(new Public());
        addAction(new Home());
        addAction(new com.spaceman.tport.commands.tport.teleporter.create.TPort());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport teleporter create <type> [data...]
        
        if (!hasPermission(player, true, true, "TPort.teleporter.create")) {
            return;
        }
        
        /*
         * /tport teleporter create <type> [data]
         *
         * /tport teleporter create TPort [player] [TPort name]
         * /tport teleporter create biomeTP whitelist [mode] <biome...>
         * /tport teleporter create biomeTP blacklist [mode] <biome...>
         * /tport teleporter create biomeTP preset [preset] [mode]
         * /tport teleporter create biomeTP random
         * /tport teleporter create biomeTP
         * /tport teleporter create featureTP [mode] [featureType]
         * /tport teleporter create back
         * /tport teleporter create PLTP <player>
         * /tport teleporter create home
         * /tport teleporter create public [TPort name]
         * */
        
        if (args.length > 2) {
            ItemStack is = player.getInventory().getItemInMainHand();
            if (is.getType().isAir()) {
                sendErrorTranslation(player, "tport.command.teleporter.create.noItem");
                return;
            }
            if (is.getType().isEdible()) {
                sendErrorTranslation(player, "tport.command.teleporter.create.noEdible");
                return;
            }
            
            if (!runCommands(this.getActions(), args[2], args, player)) {
                sendErrorTranslation(player, "tport.command.teleporter.create.noValidType", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport teleporter create <type> [data...]");
        }
    }
    
    public static void createTeleporter(Player player, String type, String command) {
        createTeleporter(player, type, command, null, Collections.emptyList());
    }
    
    public static void createTeleporter(Player player, String type, String command, Collection<Message> addedLore) {
        createTeleporter(player, type, command, null, addedLore);
    }
    
    public static void createTeleporter(Player player, String type, String command, Collection<Pair<String, String>> addedData, Collection<Message> addedLore) {
        ItemStack is = player.getInventory().getItemInMainHand();
        Teleporter.removeTeleporter(is);
        
        int size = 3;
        ArrayList<Message> fancyLore = new ArrayList<>();
        
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        if (playerLang != null) { //if player has no custom language, translate it
            fancyLore.add(MessageUtils.translateMessage(formatTranslation(titleColor, titleColor, "tport.command.teleporter.create.format.title"), playerLang));
            fancyLore.add(new Message());
            fancyLore.add(MessageUtils.translateMessage(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.type", type), playerLang));
            
            addedLore = MessageUtils.translateMessage(addedLore, playerLang);
        } else {
            fancyLore.add(formatTranslation(titleColor, titleColor, "tport.command.teleporter.create.format.title"));
            fancyLore.add(new Message());
            fancyLore.add(formatTranslation(infoColor, varInfoColor, "tport.command.teleporter.create.format.type", type));
        }
        
        for (Message m : addedLore) {
            fancyLore.add(m);
            size++;
        }
        
        ColorTheme theme = ColorTheme.getTheme(player);
        MessageUtils.setCustomItemData(is, theme, null, fancyLore);
        
        ItemMeta im;
        if ((im = is.getItemMeta()) == null) {
            sendErrorTranslation(player, "tport.command.teleporter.create.error");
            return;
        }
        
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "teleporterVersion"), PersistentDataType.INTEGER, 2);
        
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "teleporterCommand"), PersistentDataType.STRING, StringUtils.normalizeSpace(command));
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "teleporterSize"), PersistentDataType.INTEGER, size);
        if (addedData != null) {
            for (Pair<String, String> addedDatum : addedData) {
                if (addedDatum.getRight() != null)
                    im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), addedDatum.getLeft()), PersistentDataType.STRING, addedDatum.getRight());
            }
        }
        
        is.setItemMeta(im);
    }
}
