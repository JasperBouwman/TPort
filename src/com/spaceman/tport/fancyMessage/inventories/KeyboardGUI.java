package com.spaceman.tport.fancyMessage.inventories;

import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.language.Language;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

import static com.spaceman.tport.fancyMessage.MessageUtils.setCustomItemData;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatTranslation;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.addFunction;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.getFunctionName;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static org.bukkit.event.inventory.ClickType.*;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class KeyboardGUI {
    
    public static final int TEXT_ONLY = 0b000;
    public static final int SPACE     = 0b001;
    public static final int NEWLINE   = 0b010;
    public static final int COLOR     = 0b100;
    
    private static final String outputStorage = "typedString";
    public static String getKeyboardOutput(FancyInventory inv) {
        return inv.getData(outputStorage, String.class, "");
    }
    
    private static final HashMap<Character, InventoryModel> keyModels = new HashMap<>();
    private static InventoryModel registerKeyModel(char forChar, Material material, InventoryModel previousModel) {
        InventoryModel p = new InventoryModel(material, previousModel, "keyboard");
        keyModels.put(forChar, p);
        return p;
    }
    private static InventoryModel getModel(char key) {
        return keyModels.getOrDefault(key, char_not_found_model);
    }
    
    public static final InventoryModel keyboard_accept_model = new InventoryModel(Material.OAK_BUTTON, FancyInventory.previous_model, "keyboard");
    public static final InventoryModel keyboard_reject_model = new InventoryModel(Material.OAK_BUTTON, keyboard_accept_model, "keyboard");
    public static final InventoryModel keyboard_change_layout_model = new InventoryModel(Material.OAK_BUTTON, keyboard_reject_model, "keyboard");
    public static final InventoryModel keyboard_color_model = new InventoryModel(Material.OAK_BUTTON, keyboard_change_layout_model, "keyboard");
    public static final InventoryModel keyboard_color_accept_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_model, "keyboard");
    public static final InventoryModel keyboard_color_reject_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_accept_model, "keyboard");
    
    public static final InventoryModel keyboard_color_red_add_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_reject_model, "keyboard");
    public static final InventoryModel keyboard_color_red_remove_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_red_add_model, "keyboard");
    public static final InventoryModel keyboard_color_green_add_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_red_remove_model, "keyboard");
    public static final InventoryModel keyboard_color_green_remove_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_green_add_model, "keyboard");
    public static final InventoryModel keyboard_color_blue_add_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_green_remove_model, "keyboard");
    public static final InventoryModel keyboard_color_blue_remove_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_blue_add_model, "keyboard");
    
    public static final InventoryModel char_not_found_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_blue_remove_model, "keyboard");
    public static final InventoryModel char_a_model = registerKeyModel('a', Material.OAK_BUTTON, char_not_found_model);
    public static final InventoryModel char_b_model = registerKeyModel('b', Material.OAK_BUTTON, char_a_model);
    public static final InventoryModel char_c_model = registerKeyModel('c', Material.OAK_BUTTON, char_b_model);
    public static final InventoryModel char_d_model = registerKeyModel('d', Material.OAK_BUTTON, char_c_model);
    public static final InventoryModel char_e_model = registerKeyModel('e', Material.OAK_BUTTON, char_d_model);
    public static final InventoryModel char_f_model = registerKeyModel('f', Material.OAK_BUTTON, char_e_model);
    public static final InventoryModel char_g_model = registerKeyModel('g', Material.OAK_BUTTON, char_f_model);
    public static final InventoryModel char_h_model = registerKeyModel('h', Material.OAK_BUTTON, char_g_model);
    public static final InventoryModel char_i_model = registerKeyModel('i', Material.OAK_BUTTON, char_h_model);
    public static final InventoryModel char_j_model = registerKeyModel('j', Material.OAK_BUTTON, char_i_model);
    public static final InventoryModel char_k_model = registerKeyModel('k', Material.OAK_BUTTON, char_j_model);
    public static final InventoryModel char_l_model = registerKeyModel('l', Material.OAK_BUTTON, char_k_model);
    public static final InventoryModel char_m_model = registerKeyModel('m', Material.OAK_BUTTON, char_l_model);
    public static final InventoryModel char_n_model = registerKeyModel('n', Material.OAK_BUTTON, char_m_model);
    public static final InventoryModel char_o_model = registerKeyModel('o', Material.OAK_BUTTON, char_n_model);
    public static final InventoryModel char_p_model = registerKeyModel('p', Material.OAK_BUTTON, char_o_model);
    public static final InventoryModel char_q_model = registerKeyModel('q', Material.OAK_BUTTON, char_p_model);
    public static final InventoryModel char_r_model = registerKeyModel('r', Material.OAK_BUTTON, char_q_model);
    public static final InventoryModel char_s_model = registerKeyModel('s', Material.OAK_BUTTON, char_r_model);
    public static final InventoryModel char_t_model = registerKeyModel('t', Material.OAK_BUTTON, char_s_model);
    public static final InventoryModel char_u_model = registerKeyModel('u', Material.OAK_BUTTON, char_t_model);
    public static final InventoryModel char_v_model = registerKeyModel('v', Material.OAK_BUTTON, char_u_model);
    public static final InventoryModel char_w_model = registerKeyModel('w', Material.OAK_BUTTON, char_v_model);
    public static final InventoryModel char_x_model = registerKeyModel('x', Material.OAK_BUTTON, char_w_model);
    public static final InventoryModel char_y_model = registerKeyModel('y', Material.OAK_BUTTON, char_x_model);
    public static final InventoryModel char_z_model = registerKeyModel('z', Material.OAK_BUTTON, char_y_model);
    public static final InventoryModel char_0_model = registerKeyModel('0', Material.OAK_BUTTON, char_z_model);
    public static final InventoryModel char_1_model = registerKeyModel('1', Material.OAK_BUTTON, char_0_model);
    public static final InventoryModel char_2_model = registerKeyModel('2', Material.OAK_BUTTON, char_1_model);
    public static final InventoryModel char_3_model = registerKeyModel('3', Material.OAK_BUTTON, char_2_model);
    public static final InventoryModel char_4_model = registerKeyModel('4', Material.OAK_BUTTON, char_3_model);
    public static final InventoryModel char_5_model = registerKeyModel('5', Material.OAK_BUTTON, char_4_model);
    public static final InventoryModel char_6_model = registerKeyModel('6', Material.OAK_BUTTON, char_5_model);
    public static final InventoryModel char_7_model = registerKeyModel('7', Material.OAK_BUTTON, char_6_model);
    public static final InventoryModel char_8_model = registerKeyModel('8', Material.OAK_BUTTON, char_7_model);
    public static final InventoryModel char_9_model = registerKeyModel('9', Material.OAK_BUTTON, char_8_model);
    public static final InventoryModel char_minus_model = registerKeyModel('-', Material.OAK_BUTTON, char_9_model);
    public static final InventoryModel char_equals_model = registerKeyModel('=', Material.OAK_BUTTON, char_minus_model);
    public static final InventoryModel char_square_open_model = registerKeyModel('[', Material.OAK_BUTTON, char_equals_model);
    public static final InventoryModel char_square_close_model = registerKeyModel(']', Material.OAK_BUTTON, char_square_open_model);
    public static final InventoryModel char_semicolon_model = registerKeyModel(';', Material.OAK_BUTTON, char_square_close_model);
    public static final InventoryModel char_apostrophe_model = registerKeyModel('\'', Material.OAK_BUTTON, char_semicolon_model);
    public static final InventoryModel char_slash_model = registerKeyModel('/', Material.OAK_BUTTON, char_apostrophe_model);
    public static final InventoryModel char_dot_model = registerKeyModel('.', Material.OAK_BUTTON, char_slash_model);
    public static final InventoryModel char_comma_model = registerKeyModel(',', Material.OAK_BUTTON, char_dot_model);
    public static final InventoryModel char_space_model = registerKeyModel(' ', Material.OAK_BUTTON, char_comma_model);
    public static final InventoryModel char_backspace_model = registerKeyModel('\b', Material.OAK_BUTTON, char_space_model);
    public static final InventoryModel char_newline_model = registerKeyModel('\b', Material.OAK_BUTTON, char_backspace_model);
    public static final int last_model_id = char_newline_model.getCustomModelData();
    
    private static Message getKeyTitle(char key) {
        return switch (key) {
            case ' ' -> formatTranslation(varInfoColor, varInfoColor, "tport.fancyMessage.inventories.KeyboardGUI.key.space");
            case '\b' -> formatTranslation(varInfoColor, varInfoColor, "tport.fancyMessage.inventories.KeyboardGUI.key.backspace");
            case '\n' -> formatTranslation(varInfoColor, varInfoColor, "tport.fancyMessage.inventories.KeyboardGUI.key.newline");
            default -> formatTranslation(varInfoColor, varInfoColor, "tport.fancyMessage.inventories.KeyboardGUI.key.other", key);
        };
    }
    private static ItemStack getKey(char key, char alternate, ColorTheme colorTheme, JsonObject playerLang, UUID playerUUID) {
        if (key == '\0') { key = alternate; alternate = '\0'; }
        InventoryModel model = getModel(Character.toLowerCase(key));
        ItemStack item = model.getItem(playerUUID);
        if (key == '\0') return null;
        
        boolean hasRightClickAction = true;
        Message title;
        Message rightClick = null;
        if (alternate == '\0') { //has no right click alternate key
            title = formatInfoTranslation("tport.fancyMessage.inventories.KeyboardGUI.key.title.withoutAlternate", getKeyTitle(key));
            hasRightClickAction = false;
        } else {
            title = formatInfoTranslation("tport.fancyMessage.inventories.KeyboardGUI.key.title.withAlternate", getKeyTitle(key), getKeyTitle(alternate));
            rightClick = formatInfoTranslation("tport.fancyMessage.inventories.KeyboardGUI.key.alternate.click", ClickType.RIGHT, getKeyTitle(alternate));
            rightClick.translateMessage(playerLang);
        }
        title.translateMessage(playerLang);
        
        Message leftClick = formatInfoTranslation("tport.fancyMessage.inventories.KeyboardGUI.key.normal.click", LEFT, getKeyTitle(key));
        leftClick.translateMessage(playerLang);
        
        setCustomItemData(item, colorTheme, title, Arrays.asList(new Message(), leftClick, rightClick));
        
        NamespacedKey currentKey_normal = new NamespacedKey(Main.getInstance(), "keyValue_normal");
        NamespacedKey currentKey_alternate = new NamespacedKey(Main.getInstance(), "keyValue_alternate");
        FancyClickEvent.setStringData(item, currentKey_normal, String.valueOf(key));
        FancyClickEvent.setStringData(item, currentKey_alternate, String.valueOf(alternate));
        
        FancyClickEvent.FancyClickRunnable onClick = (whoClicked, clickType, pdc, fancyInventory) -> {
            NamespacedKey clickedCurrentKey = new NamespacedKey(Main.getInstance(), (clickType == LEFT ? "keyValue_normal" : "keyValue_alternate"));
            String typedString = getKeyboardOutput(fancyInventory);
            
            if (pdc.has(clickedCurrentKey, STRING)) {
                String clickedKey = Main.getOrDefault(pdc.get(clickedCurrentKey, STRING), "");
                if (clickedKey.charAt(0) == '\n') {
                    clickedKey = "\n";
                }
                typedString = clickedKey.equals("\b") ?
                        typedString.substring(0, Math.max(typedString.length() - 1, 0)) :
                        typedString + clickedKey;
                fancyInventory.setData(outputStorage, typedString);
                updateKeyboardTitle(whoClicked, fancyInventory);
            }
        };
        
        addFunction(item, LEFT, onClick);
        if (hasRightClickAction) addFunction(item, ClickType.RIGHT, onClick);
        
        return item;
    }
    private static void updateKeyboardTitle(Player player, @Nonnull FancyInventory inv) {
        String typedString = getKeyboardOutput(inv);
        
        String defColor = inv.getData("defColor", String.class, null);
        ArrayList<Message> coloredMessage = MessageUtils.transformColoredTextToMessage(typedString, defColor);
        
        Message coloredTitle = new Message();
        coloredMessage.stream()
                .flatMap(m -> Stream.of( new Message("\n"), m ))
                .skip(1)
                .forEachOrdered(coloredTitle::addMessage);
        int charCount = 0;
        Message newTitleMessage = new Message();
        messageLoop:
        for (int componentIndex = coloredTitle.getText().size() - 1; componentIndex >= 0; componentIndex--) {
            TextComponent component = coloredTitle.getText().get(componentIndex);
            
            for (int charIndex = component.getText().length() - 1; charIndex >= 0; charIndex--) {
                charCount++;
                if (charCount >= 25) {
                    String t = component.getText().substring(charIndex);
                    if (charIndex != 0) t = "…" + t;
                    newTitleMessage.getText().add(0, new TextComponent(t, component.getColor()));
                    break messageLoop;
                }
            }
            newTitleMessage.getText().add(0, component);
        }
        
        Message invTitle = formatInfoTranslation("tport.fancyMessage.inventories.KeyboardGUI.title", newTitleMessage);
        inv.setTitle(invTitle);
        
        ItemStack acceptItem = inv.getItem(inv.getSize() - 1);
        setCustomItemData(acceptItem, ColorTheme.getTheme(player), null, coloredMessage);
        
        inv.open(player);
    }
    
    private static void populateQWERTY(FancyInventory inv, ColorTheme colorTheme, JsonObject playerLang, UUID uuid) {
        inv.setItem(0, getKey('1', '!', colorTheme, playerLang, uuid));
        inv.setItem(1, getKey('2', '@', colorTheme, playerLang, uuid));
        inv.setItem(2, getKey('3', '#', colorTheme, playerLang, uuid));
        inv.setItem(3, getKey('4', '$', colorTheme, playerLang, uuid));
        inv.setItem(4, getKey('5', '%', colorTheme, playerLang, uuid));
        inv.setItem(5, getKey('6', '^', colorTheme, playerLang, uuid));
        inv.setItem(6, getKey('7', '&', colorTheme, playerLang, uuid));
        inv.setItem(7, getKey('8', '*', colorTheme, playerLang, uuid));
        inv.setItem(8, getKey('9', '(', colorTheme, playerLang, uuid));
        inv.setItem(9, getKey('0', ')', colorTheme, playerLang, uuid));
        inv.setItem(10, getKey('-', '_', colorTheme, playerLang, uuid));
        inv.setItem(11, getKey('=', '+', colorTheme, playerLang, uuid));
        inv.setItem(12, getKey('[', '{', colorTheme, playerLang, uuid));
        inv.setItem(13, getKey(']', '}', colorTheme, playerLang, uuid));
        inv.setItem(14, getKey(';', ':', colorTheme, playerLang, uuid));
        inv.setItem(15, getKey('\'', '"', colorTheme, playerLang, uuid));
        inv.setItem(16, getKey('/', '?', colorTheme, playerLang, uuid));
        inv.setItem(17, getKey('P', 'p', colorTheme, playerLang, uuid));
        
        inv.setItem(18, getKey('Q', 'q', colorTheme, playerLang, uuid));
        inv.setItem(19, getKey('W', 'w', colorTheme, playerLang, uuid));
        inv.setItem(20, getKey('E', 'e', colorTheme, playerLang, uuid));
        inv.setItem(21, getKey('R', 'r', colorTheme, playerLang, uuid));
        inv.setItem(22, getKey('T', 't', colorTheme, playerLang, uuid));
        inv.setItem(23, getKey('Y', 'y', colorTheme, playerLang, uuid));
        inv.setItem(24, getKey('U', 'u', colorTheme, playerLang, uuid));
        inv.setItem(25, getKey('I', 'i', colorTheme, playerLang, uuid));
        inv.setItem(26, getKey('O', 'o', colorTheme, playerLang, uuid));
        inv.setItem(27, getKey('A', 'a', colorTheme, playerLang, uuid));
        inv.setItem(28, getKey('S', 's', colorTheme, playerLang, uuid));
        inv.setItem(29, getKey('D', 'd', colorTheme, playerLang, uuid));
        inv.setItem(30, getKey('F', 'f', colorTheme, playerLang, uuid));
        inv.setItem(31, getKey('G', 'g', colorTheme, playerLang, uuid));
        inv.setItem(32, getKey('H', 'h', colorTheme, playerLang, uuid));
        inv.setItem(33, getKey('J', 'j', colorTheme, playerLang, uuid));
        inv.setItem(34, getKey('K', 'k', colorTheme, playerLang, uuid));
        inv.setItem(35, getKey('L', 'l', colorTheme, playerLang, uuid));
        inv.setItem(36, getKey('Z', 'z', colorTheme, playerLang, uuid));
        inv.setItem(37, getKey('X', 'x', colorTheme, playerLang, uuid));
        inv.setItem(38, getKey('C', 'c', colorTheme, playerLang, uuid));
        inv.setItem(39, getKey('V', 'v', colorTheme, playerLang, uuid));
        inv.setItem(40, getKey('B', 'b', colorTheme, playerLang, uuid));
        inv.setItem(41, getKey('N', 'n', colorTheme, playerLang, uuid));
        inv.setItem(42, getKey('M', 'm', colorTheme, playerLang, uuid));
        inv.setItem(43, getKey(',', '<', colorTheme, playerLang, uuid));
        inv.setItem(44, getKey('.', '>', colorTheme, playerLang, uuid));
    }
    private static void populateAlphabet(FancyInventory inv, ColorTheme colorTheme, JsonObject playerLang, UUID uuid) {
        inv.setItem(0, getKey('1', '!', colorTheme, playerLang, uuid));
        inv.setItem(1, getKey('2', '@', colorTheme, playerLang, uuid));
        inv.setItem(2, getKey('3', '#', colorTheme, playerLang, uuid));
        inv.setItem(3, getKey('4', '$', colorTheme, playerLang, uuid));
        inv.setItem(4, getKey('5', '%', colorTheme, playerLang, uuid));
        inv.setItem(5, getKey('6', '^', colorTheme, playerLang, uuid));
        inv.setItem(6, getKey('7', '&', colorTheme, playerLang, uuid));
        inv.setItem(7, getKey('8', '*', colorTheme, playerLang, uuid));
        inv.setItem(8, getKey('9', '(', colorTheme, playerLang, uuid));
        inv.setItem(9, getKey('0', ')', colorTheme, playerLang, uuid));
        inv.setItem(10, getKey('-', '_', colorTheme, playerLang, uuid));
        inv.setItem(11, getKey('=', '+', colorTheme, playerLang, uuid));
        inv.setItem(12, getKey('[', '{', colorTheme, playerLang, uuid));
        inv.setItem(13, getKey(']', '}', colorTheme, playerLang, uuid));
        inv.setItem(14, getKey(';', ':', colorTheme, playerLang, uuid));
        inv.setItem(15, getKey('\'', '"', colorTheme, playerLang, uuid));
        inv.setItem(16, getKey('/', '?', colorTheme, playerLang, uuid));
        inv.setItem(17, getKey('J', 'j', colorTheme, playerLang, uuid));
        
        inv.setItem(18, getKey('A', 'a', colorTheme, playerLang, uuid));
        inv.setItem(19, getKey('B', 'b', colorTheme, playerLang, uuid));
        inv.setItem(20, getKey('C', 'c', colorTheme, playerLang, uuid));
        inv.setItem(21, getKey('D', 'd', colorTheme, playerLang, uuid));
        inv.setItem(22, getKey('E', 'e', colorTheme, playerLang, uuid));
        inv.setItem(23, getKey('F', 'f', colorTheme, playerLang, uuid));
        inv.setItem(24, getKey('G', 'g', colorTheme, playerLang, uuid));
        inv.setItem(25, getKey('H', 'h', colorTheme, playerLang, uuid));
        inv.setItem(26, getKey('I', 'i', colorTheme, playerLang, uuid));
        inv.setItem(27, getKey('K', 'k', colorTheme, playerLang, uuid));
        inv.setItem(28, getKey('L', 'l', colorTheme, playerLang, uuid));
        inv.setItem(29, getKey('M', 'm', colorTheme, playerLang, uuid));
        inv.setItem(30, getKey('N', 'n', colorTheme, playerLang, uuid));
        inv.setItem(31, getKey('O', 'o', colorTheme, playerLang, uuid));
        inv.setItem(32, getKey('P', 'p', colorTheme, playerLang, uuid));
        inv.setItem(33, getKey('Q', 'q', colorTheme, playerLang, uuid));
        inv.setItem(34, getKey('R', 'r', colorTheme, playerLang, uuid));
        inv.setItem(35, getKey('T', 's', colorTheme, playerLang, uuid));
        inv.setItem(36, getKey('S', 't', colorTheme, playerLang, uuid));
        inv.setItem(37, getKey('U', 'u', colorTheme, playerLang, uuid));
        inv.setItem(38, getKey('V', 'v', colorTheme, playerLang, uuid));
        inv.setItem(39, getKey('W', 'w', colorTheme, playerLang, uuid));
        inv.setItem(40, getKey('X', 'x', colorTheme, playerLang, uuid));
        inv.setItem(41, getKey('Y', 'y', colorTheme, playerLang, uuid));
        inv.setItem(42, getKey('Z', 'z', colorTheme, playerLang, uuid));
        inv.setItem(43, getKey(',', '<', colorTheme, playerLang, uuid));
        inv.setItem(44, getKey('.', '>', colorTheme, playerLang, uuid));
    }
    
    private static void openColorKeyboard(Player player, @Nonnull FancyInventory keyboard) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Color color = keyboard.getData("color", Color.class, new Color(0, 0, 0));
        MultiColor multiColor = new MultiColor(color);
        
        Message colorTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.title.color");
        FancyInventory inv = new FancyInventory(3 * 9, formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.title", colorTitle));
        inv.setData("color", color);
        
        inv.transferData(keyboard);
        
        inv.setData(outputStorage, getKeyboardOutput(keyboard));
        
        ItemStack acceptColor = keyboard_color_accept_model.getItem(player);
        setCustomItemData(acceptColor, colorTheme, formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.acceptColor"), null);
        addFunction(acceptColor, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            String accFuncName = fancyColorInventory.getData("acceptFuncName", String.class);
            FancyClickEvent.FancyClickRunnable accFunc = FancyClickEvent.getFunction(accFuncName);
            if (accFunc == null) accFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String rejFuncName = fancyColorInventory.getData("rejectFuncName", String.class);
            FancyClickEvent.FancyClickRunnable rejFunc = FancyClickEvent.getFunction(rejFuncName);
            if (rejFunc == null) rejFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String typedString = getKeyboardOutput(fancyColorInventory);
            
            Color c = fancyColorInventory.getData("color", Color.class, new Color(0, 0, 0));
            typedString += new MultiColor(c).getColorAsValue();
            
            int keyboardSettings = fancyColorInventory.getData("keyboardSettings", Integer.class);
            FancyInventory newKeyboard = openKeyboard(whoClicked, accFunc, rejFunc, typedString, keyboardSettings);
            newKeyboard.transferData(fancyColorInventory);
            newKeyboard.setData(outputStorage, typedString);
            updateKeyboardTitle(whoClicked, newKeyboard);
        }));
        inv.setItem(inv.getSize() - 1, acceptColor);
        
        ItemStack rejectColor = keyboard_color_reject_model.getItem(player);
        setCustomItemData(rejectColor, colorTheme, formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.cancel"), null);
        addFunction(rejectColor, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            String accFuncName = fancyColorInventory.getData("acceptFuncName", String.class);
            FancyClickEvent.FancyClickRunnable accFunc = FancyClickEvent.getFunction(accFuncName);
            if (accFunc == null) accFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String rejFuncName = fancyColorInventory.getData("rejectFuncName", String.class);
            FancyClickEvent.FancyClickRunnable rejFunc = FancyClickEvent.getFunction(rejFuncName);
            if (rejFunc == null) rejFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String typedString = getKeyboardOutput(fancyColorInventory);
            
            int keyboardSettings = fancyColorInventory.getData("keyboardSettings", Integer.class);
            FancyInventory newKeyboard = openKeyboard(whoClicked, accFunc, rejFunc, typedString, keyboardSettings);
            newKeyboard.transferData(fancyColorInventory);
            updateKeyboardTitle(whoClicked, newKeyboard);
        }));
        inv.setItem(inv.getSize() - 9, rejectColor);
        
        
        Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.hex", new MultiColor(color).getColorAsValue());
        Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.rgb", color.getRed(), color.getGreen(), color.getBlue());
        Message valueAdd1      = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.add",    LEFT, 1);
        Message valueAdd10     = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.add",    RIGHT, 10);
        Message valueAdd25     = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.add",    SHIFT_LEFT, 25);
        Message valueAdd100    = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.add",    SHIFT_RIGHT, 100);
        Message valueRemove1   = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.remove", LEFT, 1);
        Message valueRemove10  = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.remove", RIGHT, 10);
        Message valueRemove25  = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.remove", SHIFT_LEFT, 25);
        Message valueRemove100 = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.remove", SHIFT_RIGHT, 100);
        
        ItemStack redAdd = keyboard_color_red_add_model.getItem(player);
        Message redAddTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.red.add");
        MessageUtils.setCustomItemData(redAdd, colorTheme, redAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
        addFunction(redAdd, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(Math.min(c.getRed() + 1, 255), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(redAdd, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(Math.min(c.getRed() + 10, 255), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(redAdd, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(Math.min(c.getRed() + 25, 255), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(redAdd, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(Math.min(c.getRed() + 100, 255), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        inv.setItem(10, redAdd);
        
        ItemStack redRem = keyboard_color_red_remove_model.getItem(player);
        Message redRemTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.red.remove");
        MessageUtils.setCustomItemData(redRem, colorTheme, redRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        addFunction(redRem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(Math.max(c.getRed() - 1, 0), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(redRem, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(Math.max(c.getRed() - 10, 0), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(redRem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(Math.max(c.getRed() - 25, 0), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(redRem, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(Math.max(c.getRed() - 100, 0), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        inv.setItem(11, redRem);
        
        ItemStack greenAdd = keyboard_color_green_add_model.getItem(player);
        Message greenAddTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.green.add");
        MessageUtils.setCustomItemData(greenAdd, colorTheme, greenAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
        addFunction(greenAdd, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), Math.min(c.getGreen() + 1, 255), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(greenAdd, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), Math.min(c.getGreen() + 10, 255), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(greenAdd, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), Math.min(c.getGreen() + 25, 255), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(greenAdd, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), Math.min(c.getGreen() + 100, 255), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        inv.setItem(12, greenAdd);
        
        ItemStack greenRem = keyboard_color_green_remove_model.getItem(player);
        Message greenRemTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.green.remove");
        MessageUtils.setCustomItemData(greenRem, colorTheme, greenRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        addFunction(greenRem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), Math.max(c.getGreen() - 1, 0), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(greenRem, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), Math.max(c.getGreen() - 10, 0), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(greenRem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), Math.max(c.getGreen() - 25, 0), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(greenRem, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), Math.max(c.getGreen() - 100, 0), c.getBlue()));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        inv.setItem(13, greenRem);
        
        ItemStack blueAdd = keyboard_color_blue_add_model.getItem(player);
        Message blueAddTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.blue.add");
        MessageUtils.setCustomItemData(blueAdd, colorTheme, blueAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
        addFunction(blueAdd, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), c.getGreen(), Math.min(c.getBlue() + 1, 255)));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(blueAdd, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), c.getGreen(), Math.min(c.getBlue() + 10, 255)));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(blueAdd, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), c.getGreen(), Math.min(c.getBlue() + 25, 255)));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(blueAdd, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), c.getGreen(), Math.min(c.getBlue() + 100, 255)));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        inv.setItem(14, blueAdd);
        
        ItemStack blueRem = keyboard_color_blue_remove_model.getItem(player);
        Message blueRemTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.blue.remove");
        MessageUtils.setCustomItemData(blueRem, colorTheme, blueRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        addFunction(blueRem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), c.getGreen(), Math.max(c.getBlue() - 1, 0)));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(blueRem, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), c.getGreen(), Math.max(c.getBlue() - 10, 0)));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(blueRem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), c.getGreen(), Math.max(c.getBlue() - 25, 0)));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        addFunction(blueRem, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            Color c = fancyInventory.getData("color", Color.class);
            fancyInventory.setData("color", new Color(c.getRed(), c.getGreen(), Math.max(c.getBlue() - 100, 0)));
            openColorKeyboard(whoClicked, fancyInventory);
        }));
        inv.setItem(15, blueRem);
        
        inv.open(player);
    }
    
    public static FancyInventory openKeyboard(Player player, @Nonnull FancyClickEvent.FancyClickRunnable onAccept, @Nullable FancyClickEvent.FancyClickRunnable onReject) {
        return openKeyboard(player, onAccept, onReject, 0);
    }
    public static FancyInventory openKeyboard(Player player, @Nonnull FancyClickEvent.FancyClickRunnable onAccept, @Nullable FancyClickEvent.FancyClickRunnable onReject, int keyboardSettings) {
        return openKeyboard(player, onAccept, onReject, "", keyboardSettings);
    }
    public static FancyInventory openKeyboard(Player player, @Nonnull FancyClickEvent.FancyClickRunnable onAccept, @Nullable FancyClickEvent.FancyClickRunnable onReject, String startInput) {
        return openKeyboard(player, onAccept, onReject, startInput, 0);
    }
    public static FancyInventory openKeyboard(Player player, @Nonnull FancyClickEvent.FancyClickRunnable onAccept, @Nullable FancyClickEvent.FancyClickRunnable onReject, String startInput, int keyboardSettings) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player.getUniqueId());
        
        Message invTitle = formatInfoTranslation("tport.fancyMessage.inventories.KeyboardGUI.title", startInput);
        FancyInventory inv = new FancyInventory(6*9, invTitle);
        inv.setData(outputStorage, startInput);
        inv.setData("layout", "qwerty");
        inv.setData("keyboardSettings", keyboardSettings);
        
        populateQWERTY(inv, colorTheme, playerLang, player.getUniqueId());
        
        ItemStack changeLayout = keyboard_change_layout_model.getItem(player);
        Message changeLayoutTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.changeLayout.title", LEFT);
        setCustomItemData(changeLayout, colorTheme, changeLayoutTitle, null);
        addFunction(changeLayout, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            if (fancyInventory.getData("layout", String.class).equals("qwerty")) {
                populateAlphabet(fancyInventory, ColorTheme.getTheme(whoClicked), Language.getPlayerLang(whoClicked.getUniqueId()), whoClicked.getUniqueId());
                fancyInventory.setData("layout", "alphabet");
            } else {
                populateQWERTY(fancyInventory, ColorTheme.getTheme(whoClicked), Language.getPlayerLang(whoClicked.getUniqueId()), whoClicked.getUniqueId());
                fancyInventory.setData("layout", "qwerty");
            }
            fancyInventory.open(whoClicked);
        }));
        inv.setItem(47, changeLayout);
        
        if ((keyboardSettings & COLOR) == COLOR) {
            ItemStack openColor = keyboard_color_model.getItem(player);
            Message openColorTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColor.title", LEFT);
            setCustomItemData(openColor, colorTheme, openColorTitle, null);
            addFunction(openColor, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openColorKeyboard(whoClicked, fancyInventory)));
            inv.setItem(48, openColor);
        }
        
        char space = (keyboardSettings & SPACE) == SPACE ? ' ' : '\0';
        char newLine = (keyboardSettings & NEWLINE) == NEWLINE ? '\n' : '\0';
        inv.setItem(49, getKey(space, newLine, colorTheme, playerLang, player.getUniqueId()));
        inv.setItem(50, getKey('\b', '\0', colorTheme, playerLang, player.getUniqueId()));
        
        ItemStack acceptButton = keyboard_accept_model.getItem(player);
        Message acceptTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.accept.title");
        setCustomItemData(acceptButton, colorTheme, acceptTitle, null);
        addFunction(acceptButton, LEFT, onAccept);
        inv.setData("acceptFuncName", getFunctionName(acceptButton, LEFT));
        inv.setItem(inv.getSize() - 1, acceptButton);
        
        ItemStack rejectButton = keyboard_reject_model.getItem(player);
        Message rejectTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.reject.title");
        setCustomItemData(rejectButton, colorTheme, rejectTitle, null);
        if (onReject == null) onReject = ((whoClicked, clickType, pdc, fancyInventory) -> whoClicked.closeInventory());
        addFunction(rejectButton, LEFT, onReject);
        inv.setData("rejectFuncName", getFunctionName(rejectButton, LEFT));
        inv.setItem(inv.getSize() - 9, rejectButton);
        
        inv.open(player);
        return inv;
    }
}
