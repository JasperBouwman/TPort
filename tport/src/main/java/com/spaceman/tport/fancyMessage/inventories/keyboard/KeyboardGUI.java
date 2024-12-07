package com.spaceman.tport.fancyMessage.inventories.keyboard;

import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.fancyMessage.language.Language;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spaceman.tport.fancyMessage.MessageUtils.setCustomItemData;
import static com.spaceman.tport.fancyMessage.MessageUtils.translateMessage;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatTranslation;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.getDynamicScrollableInventory;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.pageDataName;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static org.bukkit.event.inventory.ClickType.*;
import static org.bukkit.persistence.PersistentDataType.INTEGER;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class KeyboardGUI {
    
    public static final int ALL       = 0b1111111;
    public static final int TEXT_ONLY = 0b1111000;
    
    public static final int SPACE     = 0b0000001;
    public static final int NEWLINE   = 0b0000010;
    public static final int COLOR     = 0b0000100;
    public static final int NUMBERS   = 0b0001000; //accepts 0 though 9
    public static final int CHARS     = 0b0010000; //accepts a-z and A-Z
    public static final int SPECIAL   = 0b0100000; //accepts !"#$%'()*+-./ :;<=>?@ [\]^_` {|}~
    public static final int LINES     = 0b1000000; //accepts - and _, overrides SPECIAL
    
    private static final FancyInventory.DataName<String> outputStorage = new FancyInventory.DataName<>("typedString", String.class, "");
    private static final FancyInventory.DataName<String> layoutDataType = new FancyInventory.DataName<>("layout", String.class);
    private static final FancyInventory.DataName<String> acceptFuncNameDataType = new FancyInventory.DataName<>("acceptFuncName", String.class);
    private static final FancyInventory.DataName<String> rejectFuncNameDataType = new FancyInventory.DataName<>("rejectFuncName", String.class);
    private static final FancyInventory.DataName<Integer> keyboardSettingsDataType = new FancyInventory.DataName<>("keyboardSettings", Integer.class, 0);
    private static final FancyInventory.DataName<Boolean> formatTitleDataType = new FancyInventory.DataName<>("formatTitle", Boolean.class, true);
    private static final FancyInventory.DataName<Integer> cursorIndexDataType = new FancyInventory.DataName<>("cursorIndex", Integer.class, 0);
    private static final FancyInventory.DataName<Color> colorDataType = new FancyInventory.DataName<>("color", Color.class, new Color(255, 255, 255));
    private static final FancyInventory.DataName<Boolean> editColorDataTye = new FancyInventory.DataName<>("editColor", Boolean.class);
    private static final FancyInventory.DataName<String> defColorDataType = new FancyInventory.DataName<>("defColor", String.class, "#ffffff");
    private static final FancyInventory.DataName<ArrayList> colorFadeColorsDataType = new FancyInventory.DataName<>("colorFadeColors", ArrayList.class, new ArrayList<>());
    private static final FancyInventory.DataName<String> colorFadeStringDataType = new FancyInventory.DataName<>("colorFadeString", String.class, "");
    
    public static String getKeyboardOutput(FancyInventory inv) {
        return inv.getData(outputStorage);
    }
    
    private static final HashMap<Character, InventoryModel> keyModels = new HashMap<>();
    private static InventoryModel registerKeyModel(char forChar, @SuppressWarnings("SameParameterValue") Material material, String nameSpaceKey, InventoryModel previousModel) {
        InventoryModel p = new InventoryModel(material, previousModel, "tport", nameSpaceKey, "keyboard");
        keyModels.put(forChar, p);
        return p;
    }
    private static InventoryModel getModel(char key) {
        return keyModels.getOrDefault(key, char_not_found_model);
    }
    
    public static final InventoryModel keyboard_accept_model = new InventoryModel(Material.OAK_BUTTON, FancyInventory.previous_model, "tport", "keyboard_accept", "keyboard");
    public static final InventoryModel keyboard_reject_model = new InventoryModel(Material.OAK_BUTTON, keyboard_accept_model, "tport", "keyboard_reject", "keyboard");
    public static final InventoryModel keyboard_change_layout_model = new InventoryModel(Material.OAK_BUTTON, keyboard_reject_model, "tport", "keyboard_change_layout", "keyboard");
    public static final InventoryModel keyboard_quick_type_model = new InventoryModel(Material.OAK_BUTTON, keyboard_change_layout_model, "tport", "keyboard_quick_type", "keyboard");
    public static final InventoryModel keyboard_format_on_model = new InventoryModel(Material.OAK_BUTTON, keyboard_quick_type_model, "tport", "keyboard_format_on", "keyboard");
    public static final InventoryModel keyboard_format_off_model = new InventoryModel(Material.OAK_BUTTON, keyboard_format_on_model, "tport", "keyboard_format_off", "keyboard");
    public static final InventoryModel keyboard_cursor_model = new InventoryModel(Material.OAK_BUTTON, keyboard_format_off_model, "tport", "keyboard_cursor", "keyboard");
    
    public static final InventoryModel keyboard_color_model = new InventoryModel(Material.OAK_BUTTON, keyboard_cursor_model, "tport", "keyboard_color", "keyboard");
    public static final InventoryModel keyboard_color_grayed_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_model, "tport", "keyboard_color_grayed", "keyboard");
    public static final InventoryModel keyboard_color_accept_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_grayed_model, "tport", "keyboard_color_accept", "keyboard");
    public static final InventoryModel keyboard_color_reject_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_accept_model, "tport", "keyboard_color_reject", "keyboard");
    public static final InventoryModel keyboard_color_red_add_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_reject_model, "tport", "keyboard_color_red_add", "keyboard");
    public static final InventoryModel keyboard_color_red_remove_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_red_add_model, "tport", "keyboard_color_red_remove", "keyboard");
    public static final InventoryModel keyboard_color_green_add_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_red_remove_model, "tport", "keyboard_color_green_add", "keyboard");
    public static final InventoryModel keyboard_color_green_remove_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_green_add_model, "tport", "keyboard_color_green_remove", "keyboard");
    public static final InventoryModel keyboard_color_blue_add_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_green_remove_model, "tport", "keyboard_color_blue_add", "keyboard");
    public static final InventoryModel keyboard_color_blue_remove_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_blue_add_model, "tport", "keyboard_color_blue_remove", "keyboard");
    
    public static final InventoryModel keyboard_color_fade_accept_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_blue_remove_model, "tport", "keyboard_color_fade_accept", "keyboard");
    public static final InventoryModel keyboard_color_fade_reject_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_fade_accept_model, "tport", "keyboard_color_fade_reject", "keyboard");
    public static final InventoryModel keyboard_color_fade_edit_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_fade_reject_model, "tport", "keyboard_color_fade_edit", "keyboard");
    public static final InventoryModel keyboard_color_fade_red_add_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_fade_edit_model, "tport", "keyboard_color_fade_red_add", "keyboard");
    public static final InventoryModel keyboard_color_fade_red_remove_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_fade_red_add_model, "tport", "keyboard_color_fade_red_remove", "keyboard");
    public static final InventoryModel keyboard_color_fade_green_add_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_fade_red_remove_model, "tport", "keyboard_color_fade_green_add", "keyboard");
    public static final InventoryModel keyboard_color_fade_green_remove_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_fade_green_add_model, "tport", "keyboard_color_fade_green_remove", "keyboard");
    public static final InventoryModel keyboard_color_fade_blue_add_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_fade_green_remove_model, "tport", "keyboard_color_fade_blue_add", "keyboard");
    public static final InventoryModel keyboard_color_fade_blue_remove_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_fade_blue_add_model, "tport", "keyboard_color_fade_blue_remove", "keyboard");
    public static final InventoryModel keyboard_color_fade_chat_color_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_fade_blue_remove_model, "tport", "keyboard_color_fade_chat_color", "keyboard");
    
    public static final InventoryModel char_not_found_model = new InventoryModel(Material.OAK_BUTTON, keyboard_color_fade_chat_color_model, "tport", "char_not_found", "keyboard");
    public static final InventoryModel char_a_model = registerKeyModel('a', Material.OAK_BUTTON, "char_a", char_not_found_model);
    public static final InventoryModel char_b_model = registerKeyModel('b', Material.OAK_BUTTON, "char_b", char_a_model);
    public static final InventoryModel char_c_model = registerKeyModel('c', Material.OAK_BUTTON, "char_c", char_b_model);
    public static final InventoryModel char_d_model = registerKeyModel('d', Material.OAK_BUTTON, "char_d", char_c_model);
    public static final InventoryModel char_e_model = registerKeyModel('e', Material.OAK_BUTTON, "char_e", char_d_model);
    public static final InventoryModel char_f_model = registerKeyModel('f', Material.OAK_BUTTON, "char_f", char_e_model);
    public static final InventoryModel char_g_model = registerKeyModel('g', Material.OAK_BUTTON, "char_g", char_f_model);
    public static final InventoryModel char_h_model = registerKeyModel('h', Material.OAK_BUTTON, "char_h", char_g_model);
    public static final InventoryModel char_i_model = registerKeyModel('i', Material.OAK_BUTTON, "char_i", char_h_model);
    public static final InventoryModel char_j_model = registerKeyModel('j', Material.OAK_BUTTON, "char_j", char_i_model);
    public static final InventoryModel char_k_model = registerKeyModel('k', Material.OAK_BUTTON, "char_k", char_j_model);
    public static final InventoryModel char_l_model = registerKeyModel('l', Material.OAK_BUTTON, "char_l", char_k_model);
    public static final InventoryModel char_m_model = registerKeyModel('m', Material.OAK_BUTTON, "char_m", char_l_model);
    public static final InventoryModel char_n_model = registerKeyModel('n', Material.OAK_BUTTON, "char_n", char_m_model);
    public static final InventoryModel char_o_model = registerKeyModel('o', Material.OAK_BUTTON, "char_o", char_n_model);
    public static final InventoryModel char_p_model = registerKeyModel('p', Material.OAK_BUTTON, "char_p", char_o_model);
    public static final InventoryModel char_q_model = registerKeyModel('q', Material.OAK_BUTTON, "char_q", char_p_model);
    public static final InventoryModel char_r_model = registerKeyModel('r', Material.OAK_BUTTON, "char_r", char_q_model);
    public static final InventoryModel char_s_model = registerKeyModel('s', Material.OAK_BUTTON, "char_s", char_r_model);
    public static final InventoryModel char_t_model = registerKeyModel('t', Material.OAK_BUTTON, "char_t", char_s_model);
    public static final InventoryModel char_u_model = registerKeyModel('u', Material.OAK_BUTTON, "char_u", char_t_model);
    public static final InventoryModel char_v_model = registerKeyModel('v', Material.OAK_BUTTON, "char_v", char_u_model);
    public static final InventoryModel char_w_model = registerKeyModel('w', Material.OAK_BUTTON, "char_w", char_v_model);
    public static final InventoryModel char_x_model = registerKeyModel('x', Material.OAK_BUTTON, "char_x", char_w_model);
    public static final InventoryModel char_y_model = registerKeyModel('y', Material.OAK_BUTTON, "char_y", char_x_model);
    public static final InventoryModel char_z_model = registerKeyModel('z', Material.OAK_BUTTON, "char_z", char_y_model);
    public static final InventoryModel char_0_model = registerKeyModel('0', Material.OAK_BUTTON, "char_0", char_z_model);
    public static final InventoryModel char_1_model = registerKeyModel('1', Material.OAK_BUTTON, "char_1", char_0_model);
    public static final InventoryModel char_2_model = registerKeyModel('2', Material.OAK_BUTTON, "char_2", char_1_model);
    public static final InventoryModel char_3_model = registerKeyModel('3', Material.OAK_BUTTON, "char_3", char_2_model);
    public static final InventoryModel char_4_model = registerKeyModel('4', Material.OAK_BUTTON, "char_4", char_3_model);
    public static final InventoryModel char_5_model = registerKeyModel('5', Material.OAK_BUTTON, "char_5", char_4_model);
    public static final InventoryModel char_6_model = registerKeyModel('6', Material.OAK_BUTTON, "char_6", char_5_model);
    public static final InventoryModel char_7_model = registerKeyModel('7', Material.OAK_BUTTON, "char_7", char_6_model);
    public static final InventoryModel char_8_model = registerKeyModel('8', Material.OAK_BUTTON, "char_8", char_7_model);
    public static final InventoryModel char_9_model = registerKeyModel('9', Material.OAK_BUTTON, "char_9", char_8_model);
    public static final InventoryModel char_minus_model = registerKeyModel('-', Material.OAK_BUTTON, "char_minus", char_9_model);
    public static final InventoryModel char_equals_model = registerKeyModel('=', Material.OAK_BUTTON, "char_equals", char_minus_model);
    public static final InventoryModel char_square_open_model = registerKeyModel('[', Material.OAK_BUTTON, "char_square_open", char_equals_model);
    public static final InventoryModel char_square_close_model = registerKeyModel(']', Material.OAK_BUTTON, "char_square_close", char_square_open_model);
    public static final InventoryModel char_semicolon_model = registerKeyModel(';', Material.OAK_BUTTON, "char_semicolon", char_square_close_model);
    public static final InventoryModel char_apostrophe_model = registerKeyModel('\'', Material.OAK_BUTTON, "char_apostrophe", char_semicolon_model);
    public static final InventoryModel char_slash_model = registerKeyModel('/', Material.OAK_BUTTON, "char_slash", char_apostrophe_model);
    public static final InventoryModel char_dot_model = registerKeyModel('.', Material.OAK_BUTTON, "char_dot", char_slash_model);
    public static final InventoryModel char_comma_model = registerKeyModel(',', Material.OAK_BUTTON, "char_comma", char_dot_model);
    public static final InventoryModel char_space_model = registerKeyModel(' ', Material.OAK_BUTTON, "char_space", char_comma_model);
    public static final InventoryModel char_backspace_model = registerKeyModel('\b', Material.OAK_BUTTON, "char_backspace", char_space_model);
    public static final InventoryModel char_newline_model = registerKeyModel('\n', Material.OAK_BUTTON, "char_newline", char_backspace_model);
    
    public static final InventoryModel keyboard_chat_color_model = new InventoryModel(Material.OAK_BUTTON, char_newline_model, "tport", "keyboard_chat_color", "keyboard");
    public static final InventoryModel keyboard_chat_color_reject_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_model, "tport", "keyboard_chat_color_reject", "keyboard");
    public static final InventoryModel keyboard_chat_color_dark_blue_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_reject_model, "tport", "keyboard_chat_color_dark_blue", "keyboard");
    public static final InventoryModel keyboard_chat_color_dark_green_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_dark_blue_model, "tport", "keyboard_chat_color_dark_green", "keyboard");
    public static final InventoryModel keyboard_chat_color_dark_aqua_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_dark_green_model, "tport", "keyboard_chat_color_dark_aqua", "keyboard");
    public static final InventoryModel keyboard_chat_color_dark_red_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_dark_aqua_model, "tport", "keyboard_chat_color_dark_red", "keyboard");
    public static final InventoryModel keyboard_chat_color_dark_purple_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_dark_red_model, "tport", "keyboard_chat_color_dark_purple", "keyboard");
    public static final InventoryModel keyboard_chat_color_gold_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_dark_purple_model, "tport", "keyboard_chat_color_gold", "keyboard");
    public static final InventoryModel keyboard_chat_color_gray_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_gold_model, "tport", "keyboard_chat_color_gray", "keyboard");
    public static final InventoryModel keyboard_chat_color_dark_gray_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_gray_model, "tport", "keyboard_chat_color_dark_gray", "keyboard");
    public static final InventoryModel keyboard_chat_color_blue_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_dark_gray_model, "tport", "keyboard_chat_color_blue", "keyboard");
    public static final InventoryModel keyboard_chat_color_green_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_blue_model, "tport", "keyboard_chat_color_green", "keyboard");
    public static final InventoryModel keyboard_chat_color_aqua_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_green_model, "tport", "keyboard_chat_color_aqua", "keyboard");
    public static final InventoryModel keyboard_chat_color_red_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_aqua_model, "tport", "keyboard_chat_color_red", "keyboard");
    public static final InventoryModel keyboard_chat_color_light_purple_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_red_model, "tport", "keyboard_chat_color_light_purple", "keyboard");
    public static final InventoryModel keyboard_chat_color_yellow_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_light_purple_model, "tport", "keyboard_chat_color_yellow", "keyboard");
    public static final InventoryModel keyboard_chat_color_white_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_yellow_model, "tport", "keyboard_chat_color_white", "keyboard");
    public static final InventoryModel keyboard_chat_color_black_model = new InventoryModel(Material.OAK_BUTTON, keyboard_chat_color_white_model, "tport", "keyboard_chat_color_black", "keyboard");
    
    public static final int last_model_id = keyboard_chat_color_black_model.getCustomModelData();
    
    private static final TextComponent cursor = new TextComponent("|", ChatColor.DARK_RED);
    
    private static Message getKeyTitle(char key) {
        return switch (key) {
            case ' ' -> formatTranslation(varInfoColor, varInfoColor, "tport.fancyMessage.inventories.KeyboardGUI.key.space");
            case '\b' -> formatTranslation(varInfoColor, varInfoColor, "tport.fancyMessage.inventories.KeyboardGUI.key.backspace");
            case '\n' -> formatTranslation(varInfoColor, varInfoColor, "tport.fancyMessage.inventories.KeyboardGUI.key.newline");
            case 127 -> formatTranslation(varInfoColor, varInfoColor, "tport.fancyMessage.inventories.KeyboardGUI.key.delete");
            default -> formatTranslation(varInfoColor, varInfoColor, "tport.fancyMessage.inventories.KeyboardGUI.key.other", key);
        };
    }
    private static ItemStack getKey(char key, char alternate, ColorTheme colorTheme, JsonObject playerLang, UUID playerUUID, int keyboardSettings) {
        InventoryModel model = getModel(Character.toLowerCase(key));
        ItemStack item = model.getItem(playerUUID);
        
        boolean numbers  = (keyboardSettings & NUMBERS) == NUMBERS;
        boolean chars    = (keyboardSettings & CHARS)   == CHARS;
        boolean specials = (keyboardSettings & SPECIAL) == SPECIAL;
        boolean space    = (keyboardSettings & SPACE)   == SPACE;
        boolean newLine  = (keyboardSettings & NEWLINE) == NEWLINE;
        boolean lines    = (keyboardSettings & LINES)   == LINES;
        
        boolean keyAccepted = false;
        boolean alternateAccepted = false;
        
        if (numbers) {
            if (key >= '0' && key <= '9') keyAccepted = true;
            if (alternate >= '0' && alternate <= '9') alternateAccepted = true;
        }
        if (chars) {
            if (key >= 'A' && key <= 'Z' || key >= 'a' && key <= 'z') {
                keyAccepted = true;
            }
            if (alternate >= 'A' && alternate <= 'Z' || alternate >= 'a' && alternate <= 'z') {
                alternateAccepted = true;
            }
        }
        if (specials) {
            if (
                    (key >= '!' && key <= '/') ||
                    (key >= ':' && key <= '@') ||
                    (key >= '[' && key <= '`') ||
                    (key >= '{' && key <= '~')
            ) {
                keyAccepted = true;
            }
            if (
                    (alternate >= '!' && alternate <= '/') ||
                    (alternate >= ':' && alternate <= '@') ||
                    (alternate >= '[' && alternate <= '`') ||
                    (alternate >= '{' && alternate <= '~')
            ) {
                alternateAccepted = true;
            }
        }
        if (space) {
            if (key == ' ') keyAccepted = true;
            if (alternate == ' ') alternateAccepted = true;
        }
        if (newLine) {
            if (key == '\n') keyAccepted = true;
            if (alternate == '\n') alternateAccepted = true;
        }
        if (lines) {
            if (key == '-' || key == '_') keyAccepted = true;
            if (alternate == '-' || alternate == '_') alternateAccepted = true;
        }
        if (key == '\b' || key == (char) 127/*delete char*/) {
            keyAccepted = true;
        }
        if (alternate == '\b' || alternate == (char) 127/*delete char*/) {
            alternateAccepted = true;
        }
        
        Message title;
        Message leftClick = (!keyAccepted ? null : formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.key.normal.click", LEFT, getKeyTitle(key)));
        Message rightClick = (!alternateAccepted ? null : formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.key.alternate.click", RIGHT, getKeyTitle(alternate)));
        
        if (alternate != '\0') {
            title = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.key.title.withAlternate", getKeyTitle(key), getKeyTitle(alternate));
        } else { //has no right click alternate key
            title = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.key.title.withoutAlternate", getKeyTitle(key));
        }
        
        Message clearTextNewLine = null;
        Message clearText = null;
        if (key == '\b') {
            clearTextNewLine = new Message();
            clearText = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.key.clear.click", CONTROL_DROP);
            
            addFunction(item, CONTROL_DROP, ((whoClicked, clickType, pdc, fancyInventory) -> {
                fancyInventory.setData(cursorIndexDataType, 0);
                fancyInventory.setData(outputStorage, "");
                updateKeyboardTitle(whoClicked, fancyInventory);
            }));
        }
        
        
        setCustomItemData(item, colorTheme, title, Arrays.asList(new Message(), leftClick, rightClick, clearTextNewLine, clearText));
        
        NamespacedKey currentKey_normal = new NamespacedKey(Main.getInstance(), "keyValue_normal");
        NamespacedKey currentKey_alternate = new NamespacedKey(Main.getInstance(), "keyValue_alternate");
        setStringData(item, currentKey_normal, String.valueOf(key));
        setStringData(item, currentKey_alternate, String.valueOf(alternate));
        
        FancyClickRunnable onClick = (whoClicked, clickType, pdc, fancyInventory) -> {
            NamespacedKey clickedCurrentKey = new NamespacedKey(Main.getInstance(), (clickType == LEFT ? "keyValue_normal" : "keyValue_alternate"));
            String typedString = getKeyboardOutput(fancyInventory);
            
            if (pdc.has(clickedCurrentKey, STRING)) {
                String clickedKey = Main.getOrDefault(pdc.get(clickedCurrentKey, STRING), "");
                if (clickedKey.charAt(0) == '\n') {
                    clickedKey = "\n";
                }
                int cursorIndex = fancyInventory.getData(cursorIndexDataType);
                cursorIndex = Math.min(typedString.length(), cursorIndex);
                
                if (clickedKey.equals("\b")) {
                    //noinspection StatementWithEmptyBody
                    if (cursorIndex == 0) {
                        //do nothing
                    } else if (cursorIndex >= typedString.length()) {
                        typedString = typedString.substring(0, Math.max(typedString.length() - 1, 0));
                    } else {
                        typedString = typedString.substring(0, cursorIndex - 1) + typedString.substring(cursorIndex);
                    }
                    fancyInventory.setData(cursorIndexDataType, Math.max(cursorIndex - 1, 0));
                } else if (clickedKey.equals(String.valueOf((char)127))) {
                    //noinspection StatementWithEmptyBody
                    if (cursorIndex >= typedString.length()) {
                        //do nothing
                    } else if (cursorIndex <= 0) {
                        typedString = typedString.substring(1);
                    } else {
                        typedString = typedString.substring(0, cursorIndex) + typedString.substring(cursorIndex + 1);
                    }
                } else {
                    if (cursorIndex == 0) {
                        typedString = clickedKey + typedString;
                    } else if (cursorIndex >= typedString.length()) {
                        typedString = typedString + clickedKey;
                    } else {
                        typedString = typedString.substring(0, cursorIndex) + clickedKey + typedString.substring(cursorIndex);
                    }
                    
                    fancyInventory.setData(cursorIndexDataType, cursorIndex + 1);
                }
                
                fancyInventory.setData(outputStorage, typedString);
                updateKeyboardTitle(whoClicked, fancyInventory);
            }
        };
        
        if (keyAccepted)       addFunction(item, LEFT, onClick);
        if (alternateAccepted) addFunction(item, RIGHT, onClick);
        
        return item;
    }
    private static void updateKeyboardTitle(Player player, @Nonnull FancyInventory inv) {
        String typedString = getKeyboardOutput(inv);
        
        boolean showColors = inv.getData(formatTitleDataType);
        String defColor = inv.getData(defColorDataType, null);
        ArrayList<Message> coloredMessage = MessageUtils.transformColoredTextToMessage(typedString, defColor);
        ArrayList<String> typedArray = MessageUtils.transformColoredTextToArray(typedString);
        Message coloredTitle = new Message();
        int cursorIndex = inv.getData(cursorIndexDataType);
        if (showColors) {
            coloredMessage.stream()
                    .flatMap(m -> Stream.of( new Message("\n"), (Message) m.clone() ))
                    .skip(1)
                    .forEachOrdered(coloredTitle::addMessage);
            
            int currentCursorIndex = 0;
            int textCursor = 0;
            for (String element : typedArray) {
                currentCursorIndex += element.length();
                
                if (currentCursorIndex >= cursorIndex) { //cursor found for element
                    if (!MultiColor.isColor(element)) {
                        int subCursorIndex = element.length() - (currentCursorIndex - cursorIndex);
                        textCursor += subCursorIndex;
                    }
                    break;
                } else if (!MultiColor.isColor(element)) {
                    textCursor += element.length();
                }
                
            }
            cursorIndex = textCursor;
            
        } else {
            coloredTitle = new Message(typedString);
        }
        
        int currentCursorIndex = 0;
        Message newTitleMessage = new Message();
        
        final int totalAfterCursor = 15;
        final int totalLength = 25;
        final TextComponent cutOffText = new TextComponent("...", ChatColor.DARK_RED);
        ArrayList<TextComponent> coloredTitleText = coloredTitle.getText();
        
        for (int i = 0; i < coloredTitleText.size(); i++) { //find cursor placement
            TextComponent t = coloredTitleText.get(i);
            String text = t.getText();
            currentCursorIndex += text.length();
            
            if (currentCursorIndex >= cursorIndex) { //cursor placement found
                
                int subCursorIndex = text.length() - (currentCursorIndex - cursorIndex);
                
                newTitleMessage.addText(cursor);
                
                TextComponent t2 = (TextComponent) t.clone();
                t2.setText(text.substring(subCursorIndex, Math.min(text.length(), subCursorIndex + totalAfterCursor)));
                newTitleMessage.addText(t2);
                int currentAfterCursor = t2.getText().length();
                int charsPrintedAfter = t2.getText().length();
                
                if (totalAfterCursor < (text.length() - subCursorIndex) ) {
                    newTitleMessage.addText(cutOffText); //add ... when t2 was too long
                } else { //add components after cursor component
                    for (int j = i + 1; j < coloredTitleText.size(); j++) {
                        TextComponent tAfter = coloredTitleText.get(j);
                        String sAfter = tAfter.getText();
                        
                        if (currentAfterCursor + sAfter.length() <= totalAfterCursor) {
                            newTitleMessage.addText(tAfter);
                            currentAfterCursor += sAfter.length();
                            charsPrintedAfter += sAfter.length();
                            if (currentAfterCursor == totalAfterCursor) {
                                //add ... when last component was the last fit (and more components behind)
                                if (j + 1 < coloredTitleText.size()) newTitleMessage.addText(cutOffText);
                                break;
                            }
                        } else if (currentAfterCursor + sAfter.length() > totalAfterCursor) {
                            int cutOff = (totalAfterCursor - charsPrintedAfter);
                            tAfter.setText(sAfter.substring(0, cutOff));
                            newTitleMessage.addText(tAfter);
                            newTitleMessage.addText(cutOffText); //add ... when last component was too long
                            charsPrintedAfter += tAfter.getText().length();
                            break;
                        }
                    }
                }
                
                TextComponent t1 = t.setText(text.substring(Math.max(0, subCursorIndex - (totalLength - charsPrintedAfter)), subCursorIndex));
                newTitleMessage.getText().add(0, t1);
                
                int charsToPrintBefore = totalLength - charsPrintedAfter - t1.getText().length();
                int charsPrintedBefore = 0;
                
                if (subCursorIndex > totalLength - charsPrintedAfter) {
                    newTitleMessage.getText().add(0, cutOffText); //add ... when t1 was too long
                } else {
                    for (int j = i - 1; j >= 0; j--) {
                        TextComponent tBefore = coloredTitleText.get(j);
                        String sBefore = tBefore.getText();
                        charsToPrintBefore -= sBefore.length();
                        
                        if (charsToPrintBefore >= 0) {
                            newTitleMessage.getText().add(0, tBefore);
                            charsPrintedBefore += tBefore.getText().length();
                            if (charsToPrintBefore == 0) {
                                //add ... when last component was the last fit (and more components before)
                                if (j > 0) newTitleMessage.getText().add(0, cutOffText);
                                break;
                            }
                        } else {
                            int cutoff = totalLength - charsPrintedAfter - t1.getText().length() - charsPrintedBefore;
                            tBefore.setText(sBefore.substring(sBefore.length() - cutoff));
                            newTitleMessage.getText().add(0, tBefore);
                            
                            newTitleMessage.getText().add(0, cutOffText); //add ... when last component was too long
                            break;
                        }
                    }
                }
                break;
            }
        }
        
        Message invTitle = formatInfoTranslation("tport.fancyMessage.inventories.KeyboardGUI.title", newTitleMessage);
        inv.setTitle(invTitle);
        
        ColorTheme theme = ColorTheme.getTheme(player);
        ItemStack acceptItem = inv.getItem(inv.getSize() - 1);
        setCustomItemData(acceptItem, theme, null, coloredMessage);
        
        setFormatButton(inv, player, theme, null);
        updateColorButton(inv, player, theme, null);
        inv.open(player);
    }
    
    private static void populateQWERTY(FancyInventory inv, ColorTheme colorTheme, JsonObject playerLang, UUID uuid, int keyboardSettings) {
        inv.setItem(0, getKey('1', '!', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(1, getKey('2', '@', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(2, getKey('3', '#', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(3, getKey('4', '$', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(4, getKey('5', '%', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(5, getKey('6', '^', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(6, getKey('7', '&', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(7, getKey('8', '*', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(8, getKey('9', '(', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(9, getKey('0', ')', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(10, getKey('-', '_', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(11, getKey('=', '+', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(12, getKey('[', '{', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(13, getKey(']', '}', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(14, getKey(';', ':', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(15, getKey('\'', '"', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(16, getKey('/', '?', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(17, getKey('P', 'p', colorTheme, playerLang, uuid, keyboardSettings));
        
        inv.setItem(18, getKey('Q', 'q', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(19, getKey('W', 'w', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(20, getKey('E', 'e', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(21, getKey('R', 'r', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(22, getKey('T', 't', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(23, getKey('Y', 'y', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(24, getKey('U', 'u', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(25, getKey('I', 'i', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(26, getKey('O', 'o', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(27, getKey('A', 'a', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(28, getKey('S', 's', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(29, getKey('D', 'd', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(30, getKey('F', 'f', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(31, getKey('G', 'g', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(32, getKey('H', 'h', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(33, getKey('J', 'j', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(34, getKey('K', 'k', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(35, getKey('L', 'l', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(36, getKey('Z', 'z', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(37, getKey('X', 'x', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(38, getKey('C', 'c', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(39, getKey('V', 'v', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(40, getKey('B', 'b', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(41, getKey('N', 'n', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(42, getKey('M', 'm', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(43, getKey(',', '<', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(44, getKey('.', '>', colorTheme, playerLang, uuid, keyboardSettings));
    }
    private static void populateAlphabet(FancyInventory inv, ColorTheme colorTheme, JsonObject playerLang, UUID uuid, int keyboardSettings) {
        inv.setItem(0, getKey('1', '!', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(1, getKey('2', '@', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(2, getKey('3', '#', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(3, getKey('4', '$', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(4, getKey('5', '%', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(5, getKey('6', '^', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(6, getKey('7', '&', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(7, getKey('8', '*', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(8, getKey('9', '(', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(9, getKey('0', ')', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(10, getKey('-', '_', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(11, getKey('=', '+', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(12, getKey('[', '{', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(13, getKey(']', '}', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(14, getKey(';', ':', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(15, getKey('\'', '"', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(16, getKey('/', '?', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(17, getKey('J', 'j', colorTheme, playerLang, uuid, keyboardSettings));
        
        inv.setItem(18, getKey('A', 'a', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(19, getKey('B', 'b', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(20, getKey('C', 'c', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(21, getKey('D', 'd', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(22, getKey('E', 'e', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(23, getKey('F', 'f', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(24, getKey('G', 'g', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(25, getKey('H', 'h', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(26, getKey('I', 'i', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(27, getKey('K', 'k', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(28, getKey('L', 'l', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(29, getKey('M', 'm', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(30, getKey('N', 'n', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(31, getKey('O', 'o', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(32, getKey('P', 'p', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(33, getKey('Q', 'q', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(34, getKey('R', 'r', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(35, getKey('T', 's', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(36, getKey('S', 't', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(37, getKey('U', 'u', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(38, getKey('V', 'v', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(39, getKey('W', 'w', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(40, getKey('X', 'x', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(41, getKey('Y', 'y', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(42, getKey('Z', 'z', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(43, getKey(',', '<', colorTheme, playerLang, uuid, keyboardSettings));
        inv.setItem(44, getKey('.', '>', colorTheme, playerLang, uuid, keyboardSettings));
    }
    
    private static int valueToEdit(ClickType clickType) {
        return switch (clickType) {
            case LEFT -> 1;
            case RIGHT -> 10;
            case SHIFT_LEFT -> 25;
            case SHIFT_RIGHT -> 100;
            default -> 0;
        };
    }
    private static int constrain(int value, int max, int min) {
        return Math.max(Math.min(value, max), min);
    }
    private static void openColorKeyboard(Player player, @Nonnull FancyInventory keyboard) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Color color = keyboard.getData(colorDataType);
        MultiColor multiColor = new MultiColor(color);
        
        Message colorTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.title.color");
        FancyInventory colorKeyboard = new FancyInventory(3 * 9, formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.title", colorTitle));
        colorKeyboard.setData(colorDataType, color);
        
        colorKeyboard.transferData(keyboard);
        
        colorKeyboard.setData(outputStorage, getKeyboardOutput(keyboard));
        
        ItemStack acceptColor = keyboard_color_accept_model.getItem(player);
        setCustomItemData(acceptColor, colorTheme, formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.acceptColor"), null);
        addFunction(acceptColor, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            String accFuncName = fancyColorInventory.getData(acceptFuncNameDataType);
            FancyClickRunnable accFunc = getFunction(accFuncName);
            if (accFunc == null) accFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String rejFuncName = fancyColorInventory.getData(rejectFuncNameDataType);
            FancyClickRunnable rejFunc = getFunction(rejFuncName);
            if (rejFunc == null) rejFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String typedString = getKeyboardOutput(fancyColorInventory);
            int cursorIndex = fancyColorInventory.getData(cursorIndexDataType);
            cursorIndex = Math.min(typedString.length(), cursorIndex);
            
            Color c = fancyColorInventory.getData(colorDataType);
            
            if (fancyColorInventory.getData(editColorDataTye)) {
                int currentCursorIndex = 0;
                ArrayList<String> typedArray = MessageUtils.transformColoredTextToArray(typedString);
                
                for (int i = 0; i < typedArray.size(); i++) {
                    String element = typedArray.get(i);
                    currentCursorIndex += element.length();
                    if (currentCursorIndex >= cursorIndex) {
                        String addedColor = new MultiColor(c).getColorAsValue();
                        if (MultiColor.isColor(element)) {
                            String originalColor = typedArray.get(i);
                            typedArray.set(i, addedColor);
                            cursorIndex += -originalColor.length() + addedColor.length();
                        } else if (element.equals("\n")) {
                            typedArray.add(i + 1, addedColor);
                            cursorIndex += addedColor.length();
                        } else if (i > 0) {
                            String originalColor = typedArray.get(i-1);
                            if (originalColor.equals("\n")) {
                                typedArray.add(i, addedColor);
                                cursorIndex += addedColor.length();
                            } else {
                                typedArray.set(i-1, addedColor);
                                cursorIndex += -originalColor.length() + addedColor.length();
                            }
                        } else {
                            typedArray.add(0, addedColor);
                            cursorIndex += addedColor.length();
                        }
                        typedString = String.join("", typedArray);
                        break;
                    }
                }
            } else {
                if (cursorIndex == 0) {
                    typedString = new MultiColor(c).getColorAsValue() + typedString;
                } else if (cursorIndex >= typedString.length()) {
                    typedString = typedString + new MultiColor(c).getColorAsValue();
                } else {
                    typedString = typedString.substring(0, cursorIndex) + new MultiColor(c).getColorAsValue() + typedString.substring(cursorIndex);
                }
                cursorIndex += 7;
            }
            
            fancyColorInventory.setData(cursorIndexDataType, cursorIndex);
            
            int keyboardSettings = fancyColorInventory.getData(keyboardSettingsDataType);
            String defColor = fancyColorInventory.getData(defColorDataType);
            FancyInventory newKeyboard = openKeyboard(whoClicked, accFunc, rejFunc, typedString, defColor, keyboardSettings);
            newKeyboard.transferData(fancyColorInventory);
            newKeyboard.setData(outputStorage, typedString);
            updateKeyboardTitle(whoClicked, newKeyboard);
        }));
        colorKeyboard.setItem(colorKeyboard.getSize() - 1, acceptColor);
        
        ItemStack rejectColor = keyboard_color_reject_model.getItem(player);
        setCustomItemData(rejectColor, colorTheme, formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.cancel"), null);
        addFunction(rejectColor, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            String accFuncName = fancyColorInventory.getData(acceptFuncNameDataType);
            FancyClickRunnable accFunc = getFunction(accFuncName);
            if (accFunc == null) accFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String rejFuncName = fancyColorInventory.getData(rejectFuncNameDataType);
            FancyClickRunnable rejFunc = getFunction(rejFuncName);
            if (rejFunc == null) rejFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String typedString = getKeyboardOutput(fancyColorInventory);
            
            int keyboardSettings = fancyColorInventory.getData(keyboardSettingsDataType);
            String defColor = fancyColorInventory.getData(defColorDataType);
            FancyInventory newKeyboard = openKeyboard(whoClicked, accFunc, rejFunc, typedString, defColor, keyboardSettings);
            newKeyboard.transferData(fancyColorInventory);
            updateKeyboardTitle(whoClicked, newKeyboard);
        }));
        colorKeyboard.setItem(colorKeyboard.getSize() - 9, rejectColor);
        
        
        Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.hex", new MultiColor(color).getColorAsValue());
        Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.rgb", color.getRed(), color.getGreen(), color.getBlue());
        Message valueAdd1      = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.add",    LEFT, valueToEdit(LEFT));
        Message valueAdd10     = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.add",    RIGHT, valueToEdit(RIGHT));
        Message valueAdd25     = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.add",    SHIFT_LEFT, valueToEdit(SHIFT_LEFT));
        Message valueAdd100    = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.add",    SHIFT_RIGHT, valueToEdit(SHIFT_RIGHT));
        Message valueRemove1   = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.remove", LEFT, valueToEdit(LEFT));
        Message valueRemove10  = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.remove", RIGHT, valueToEdit(RIGHT));
        Message valueRemove25  = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.remove", SHIFT_LEFT, valueToEdit(SHIFT_LEFT));
        Message valueRemove100 = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.value.remove", SHIFT_RIGHT, valueToEdit(SHIFT_RIGHT));
        
        FancyClickRunnable onColorClick = (whoClicked, clickType, pdc, fancyColorInventory) -> {
            int valueToAdd = valueToEdit(clickType);
            if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_remove"))) valueToAdd *= -1;
            Color c = fancyColorInventory.getData(colorDataType);
            
            Color newColor = null;
            if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_red"))) {
                newColor = new Color(constrain(c.getRed() + valueToAdd, 255, 0), c.getGreen(), c.getBlue());
            } else if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_green"))) {
                newColor = new Color(c.getRed(), constrain(c.getGreen() + valueToAdd, 255, 0), c.getBlue());
            } else if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_blue"))) {
                newColor = new Color(c.getRed(), c.getGreen(), constrain(c.getBlue() + valueToAdd, 255, 0));
            }
            
            fancyColorInventory.setData(colorDataType, newColor);
            openColorKeyboard(whoClicked, fancyColorInventory);
        };
        
        ItemStack redAdd = keyboard_color_red_add_model.getItem(player);
        Message redAddTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.red.add").translateMessage(playerLang);
        setCustomItemData(redAdd, colorTheme, redAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
        setBooleanData(redAdd, new NamespacedKey(Main.getInstance(), "keyboard_color_red"), true);
        addFunction(redAdd, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        colorKeyboard.setItem(10, redAdd);
        
        ItemStack redRem = keyboard_color_red_remove_model.getItem(player);
        Message redRemTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.red.remove").translateMessage(playerLang);
        setCustomItemData(redRem, colorTheme, redRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        setBooleanData(redRem, new NamespacedKey(Main.getInstance(), "keyboard_color_red"), true);
        setBooleanData(redRem, new NamespacedKey(Main.getInstance(), "keyboard_color_remove"), true);
        addFunction(redRem, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        colorKeyboard.setItem(11, redRem);
        
        ItemStack greenAdd = keyboard_color_green_add_model.getItem(player);
        Message greenAddTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.green.add").translateMessage(playerLang);
        setCustomItemData(greenAdd, colorTheme, greenAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
        setBooleanData(greenAdd, new NamespacedKey(Main.getInstance(), "keyboard_color_green"), true);
        addFunction(greenAdd, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        colorKeyboard.setItem(12, greenAdd);
        
        ItemStack greenRem = keyboard_color_green_remove_model.getItem(player);
        Message greenRemTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.green.remove").translateMessage(playerLang);
        setCustomItemData(greenRem, colorTheme, greenRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        setBooleanData(greenRem, new NamespacedKey(Main.getInstance(), "keyboard_color_green"), true);
        setBooleanData(greenRem, new NamespacedKey(Main.getInstance(), "keyboard_color_remove"), true);
        addFunction(greenRem, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        colorKeyboard.setItem(13, greenRem);
        
        ItemStack blueAdd = keyboard_color_blue_add_model.getItem(player);
        Message blueAddTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.blue.add").translateMessage(playerLang);
        setCustomItemData(blueAdd, colorTheme, blueAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
        setBooleanData(blueAdd, new NamespacedKey(Main.getInstance(), "keyboard_color_blue"), true);
        addFunction(blueAdd, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        colorKeyboard.setItem(14, blueAdd);
        
        ItemStack blueRem = keyboard_color_blue_remove_model.getItem(player);
        Message blueRemTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.blue.remove").translateMessage(playerLang);
        setCustomItemData(blueRem, colorTheme, blueRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        setBooleanData(blueRem, new NamespacedKey(Main.getInstance(), "keyboard_color_blue"), true);
        setBooleanData(blueRem, new NamespacedKey(Main.getInstance(), "keyboard_color_remove"), true);
        addFunction(blueRem, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        colorKeyboard.setItem(15, blueRem);
        
        ItemStack builtInColorSelector = keyboard_chat_color_model.getItem(player);
        Message builtInColorSelectorTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.openBuiltInColorSelector");
        setCustomItemData(builtInColorSelector, colorTheme, builtInColorSelectorTitle,  null);
        addFunction(builtInColorSelector, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openBuiltInColorSelector(whoClicked, 0, fancyInventory)));
        colorKeyboard.setItem(16, builtInColorSelector);
        
        colorKeyboard.open(player);
    }
    private static void openBuiltInColorSelector(Player player, int page, @Nonnull FancyInventory colorKeyboard) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ChatColor chatColor : ChatColor.values()) {
            if (!chatColor.isColor()) continue;
            ItemStack is = getChatColorStack(chatColor, player);
            MultiColor chatMultiColor = new MultiColor(chatColor);
            Message title = formatTranslation(colorTheme.getInfoColor(), chatMultiColor,
                    "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector.chatColor", chatColor.name(), "&", chatColor.getChar()).translateMessage(playerLang);
            
            Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector.chatColor.hex", chatMultiColor.getColorAsValue());
            Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector.chatColor.rgb", chatMultiColor.getRed(), chatMultiColor.getGreen(), chatMultiColor.getBlue());
            Message clickToSelect = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector.clickToSelect", LEFT);
            
            setCustomItemData(is, colorTheme, title, List.of(new Message(), valueHEXMessage, valueRGBMessage, new Message(), clickToSelect));
            setStringData(is, new NamespacedKey(Main.getInstance(), "keyboard_chat_color"), chatColor.name());
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                ChatColor c = ChatColor.valueOf(pdc.get(new NamespacedKey(Main.getInstance(), "keyboard_chat_color"), STRING));
                fancyInventory.setData(colorDataType, new MultiColor(c).getColor());
                openColorKeyboard(whoClicked, fancyInventory);
            }));
            
            items.add(is);
        }
        for (DyeColor dyeColor : DyeColor.values()) {
            ItemStack is = getDyeStack(dyeColor);
            MultiColor chatMultiColor = new MultiColor(dyeColor);
            Message title = formatTranslation(colorTheme.getInfoColor(), chatMultiColor,
                    "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector.dyeColor", dyeColor.name()).translateMessage(playerLang);
            
            Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector.dyeColor.hex", chatMultiColor.getColorAsValue());
            Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector.dyeColor.rgb", chatMultiColor.getRed(), chatMultiColor.getGreen(), chatMultiColor.getBlue());
            Message clickToSelect = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector.clickToSelect", LEFT);
            
            setCustomItemData(is, colorTheme, title, List.of(new Message(), valueHEXMessage, valueRGBMessage, new Message(), clickToSelect));
            setStringData(is, new NamespacedKey(Main.getInstance(), "keyboard_dye_color"), dyeColor.name());
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                DyeColor c = DyeColor.valueOf(pdc.get(new NamespacedKey(Main.getInstance(), "keyboard_dye_color"), STRING));
                fancyInventory.setData(colorDataType, new MultiColor(c).getColor());
                openColorKeyboard(whoClicked, fancyInventory);
            }));
            
            items.add(is);
        }
        
        ItemStack backButton = keyboard_chat_color_reject_model.getItem(player);
        Message backTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector.cancel");
        setCustomItemData(backButton, colorTheme, backTitle, null);
        addFunction(backButton, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openColorKeyboard(whoClicked, fancyInventory)));
        
        Message title = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector.title");
        FancyInventory builtInColorSelectorKeyboard = getDynamicScrollableInventory(player, page, KeyboardGUI::openBuiltInColorSelector, title, items, backButton);
        builtInColorSelectorKeyboard.transferData(colorKeyboard, false);
        builtInColorSelectorKeyboard.setData(outputStorage, getKeyboardOutput(colorKeyboard));
        
        builtInColorSelectorKeyboard.open(player);
    }
    public static ItemStack getDyeStack(DyeColor dyeColor) {
        Material m = switch (dyeColor) {
            case WHITE -> Material.WHITE_DYE;
            case ORANGE -> Material.ORANGE_DYE;
            case MAGENTA -> Material.MAGENTA_DYE;
            case LIGHT_BLUE -> Material.LIGHT_BLUE_DYE;
            case YELLOW -> Material.YELLOW_DYE;
            case LIME -> Material.LIME_DYE;
            case PINK -> Material.PINK_DYE;
            case GRAY -> Material.GRAY_DYE;
            case LIGHT_GRAY -> Material.LIGHT_GRAY_DYE;
            case CYAN -> Material.CYAN_DYE;
            case PURPLE -> Material.PURPLE_DYE;
            case BLUE -> Material.BLUE_DYE;
            case BROWN -> Material.BROWN_DYE;
            case GREEN -> Material.GREEN_DYE;
            case RED -> Material.RED_DYE;
            case BLACK -> Material.BLACK_DYE;
        };
        
        return new ItemStack(m);
    }
    public static ItemStack getChatColorStack(ChatColor chatColor, Player player) {
        return switch (chatColor) {
            case DARK_BLUE -> keyboard_chat_color_dark_blue_model.getItem(player);
            case DARK_GREEN -> keyboard_chat_color_dark_green_model.getItem(player);
            case DARK_AQUA -> keyboard_chat_color_dark_aqua_model.getItem(player);
            case DARK_RED -> keyboard_chat_color_dark_red_model.getItem(player);
            case DARK_PURPLE -> keyboard_chat_color_dark_purple_model.getItem(player);
            case GOLD -> keyboard_chat_color_gold_model.getItem(player);
            case GRAY -> keyboard_chat_color_gray_model.getItem(player);
            case DARK_GRAY -> keyboard_chat_color_dark_gray_model.getItem(player);
            case BLUE -> keyboard_chat_color_blue_model.getItem(player);
            case GREEN -> keyboard_chat_color_green_model.getItem(player);
            case AQUA -> keyboard_chat_color_aqua_model.getItem(player);
            case RED -> keyboard_chat_color_red_model.getItem(player);
            case LIGHT_PURPLE -> keyboard_chat_color_light_purple_model.getItem(player);
            case YELLOW -> keyboard_chat_color_yellow_model.getItem(player);
            case WHITE -> keyboard_chat_color_white_model.getItem(player);
            case BLACK -> keyboard_chat_color_black_model.getItem(player);
            default -> keyboard_chat_color_black_model.getItem(player);
        };
    }
    
    private static void setFormatButton(FancyInventory keyboard, Player player, @Nullable ColorTheme colorTheme, @Nullable JsonObject playerLang) {
        if (colorTheme == null) colorTheme = ColorTheme.getTheme(player);
        if (playerLang == null) playerLang = getPlayerLang(player.getUniqueId());
        
        boolean showColors = keyboard.getData(formatTitleDataType);
        ItemStack formatTitleButton = (showColors ? keyboard_format_on_model.getItem(player) : keyboard_format_off_model.getItem(player));
        Message formatTitleTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.formatTitle.title", LEFT);
        setCustomItemData(formatTitleButton, colorTheme, formatTitleTitle, null);
        addFunction(formatTitleButton, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
            boolean innerShowColors = fancyInventory.getData(formatTitleDataType);
            fancyInventory.setData(formatTitleDataType, !innerShowColors);
            updateKeyboardTitle(whoClicked, fancyInventory);
        });
        keyboard.setItem(46, formatTitleButton);
    }
    
    private static Color getColorFromCursor(FancyInventory fancyInventory, ArrayList<String> typedArray) {
        String defColor = fancyInventory.getData(defColorDataType);
        Color color = new MultiColor(defColor).getColor();
        int cursorIndex = fancyInventory.getData(cursorIndexDataType);
        int currentCursorIndex = 0;
        for (int i = 0; i < typedArray.size(); i++) {
            String element = typedArray.get(i);
            currentCursorIndex += element.length();
            if (currentCursorIndex >= cursorIndex) {
                if (MultiColor.isColor(element)) {
                    color = new MultiColor(element).getColor();
                } else if (i > 0) {
                    if (typedArray.get(i - 1).equals("\n") || element.equals("\n")) {
                        break;
                    }
                    color = new MultiColor(typedArray.get(i - 1)).getColor();
                }
                break;
            }
        }
        return color;
    }
    private static void updateColorButton(FancyInventory keyboard, Player player, @Nullable ColorTheme colorTheme, @Nullable JsonObject playerLang) {
        int keyboardSettings = keyboard.getData(keyboardSettingsDataType);
        boolean hasColors = (keyboardSettings & COLOR) == COLOR;
        
        if (colorTheme == null) colorTheme = ColorTheme.getTheme(player);
        if (playerLang == null) playerLang = getPlayerLang(player.getUniqueId());
        
        ItemStack openColor = (hasColors ? keyboard_color_model : keyboard_color_grayed_model).getItem(player);
        Message openColorTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.colorEditor.title");
        List<Message> lore = new ArrayList<>();
        
        if (hasColors) {
            lore.add(new Message());
            lore.add(formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.insertColor.title", LEFT));
            
            String typedString = getKeyboardOutput(keyboard);
            
            if (!typedString.isEmpty()) {
                ArrayList<String> typedArray = MessageUtils.transformColoredTextToArray(typedString);
                Color color = getColorFromCursor(keyboard, typedArray);
                
                Message thisMessage = formatTranslation(new MultiColor(color), new MultiColor(color), "tport.fancyMessage.inventories.KeyboardGUI.editColor.this");
                thisMessage = translateMessage(thisMessage, playerLang);
                
                lore.add(formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.editColor.title", RIGHT, thisMessage));
                addFunction(openColor, RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
                    String innerTypedString = getKeyboardOutput(fancyInventory);
                    ArrayList<String> innerTypedArray = MessageUtils.transformColoredTextToArray(innerTypedString);
                    
                    Color innerColor = getColorFromCursor(fancyInventory, innerTypedArray);
                    
                    fancyInventory.setData(colorDataType, innerColor);
                    fancyInventory.setData(editColorDataTye, true);
                    openColorKeyboard(whoClicked, fancyInventory);
                });
                
                lore.add(formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.removeColor.title", SHIFT_RIGHT, thisMessage));
                addFunction(openColor, SHIFT_RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
                    String innerTypedString = getKeyboardOutput(fancyInventory);
                    ArrayList<String> innerTypedArray = MessageUtils.transformColoredTextToArray(innerTypedString);
                    
                    int cursorIndex = fancyInventory.getData(cursorIndexDataType);
                    int currentCursorIndex = 0;
                    int colorLength = 0;
                    for (int i = 0; i < innerTypedArray.size(); i++) {
                        String element = innerTypedArray.get(i);
                        currentCursorIndex += element.length();
                        if (currentCursorIndex >= cursorIndex) {
                            if (MultiColor.isColor(element)) {
                                colorLength = innerTypedArray.remove(i).length() - (currentCursorIndex - cursorIndex);
                            } else if (i > 0) {
                                if (innerTypedArray.get(i - 1).equals("\n") || element.equals("\n")) {
                                    break;
                                }
                                colorLength = innerTypedArray.remove(i-1).length();
                            }
                            break;
                        }
                    }
                    
                    fancyInventory.setData(cursorIndexDataType, Math.max(0, cursorIndex - colorLength));
                    fancyInventory.setData(outputStorage, String.join("", innerTypedArray));
                    updateKeyboardTitle(whoClicked, fancyInventory);
                });
                
                lore.add(formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.removeAllColor.title", SHIFT_LEFT, thisMessage));
                addFunction(openColor, SHIFT_LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                    String innerTypedString = getKeyboardOutput(fancyInventory);
                    ArrayList<String> innerTypedArray = MessageUtils.transformColoredTextToArray(innerTypedString);
                    int cursorIndex = fancyInventory.getData(cursorIndexDataType);
                    
                    int removeFromCursorIndex = 0;
                    int charIndex = 0;
                    
                    StringBuilder typedText = new StringBuilder();
                    for (String s : innerTypedArray) {
                        charIndex += s.length();
                        
                        if (!MultiColor.isColor(s)) {
                            typedText.append(s);
                        } else { // is color
                            if (charIndex <= cursorIndex) {
                                removeFromCursorIndex += s.length();
                            }
                        }
                    }
                    
                    fancyInventory.setData(cursorIndexDataType, Math.max(0, cursorIndex - removeFromCursorIndex));
                    fancyInventory.setData(outputStorage, typedText.toString());
                    updateKeyboardTitle(whoClicked, fancyInventory);
                });
                
                lore.add(formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.colorFade.createNew", DROP, thisMessage));
                addFunction(openColor, DROP, (whoClicked, clickType, pdc, fancyInventory) -> {
                    String innerTypedString = getKeyboardOutput(fancyInventory);
                    ArrayList<String> innerTypedArray = MessageUtils.transformColoredTextToArray(innerTypedString);
                    
                    StringBuilder typedText = new StringBuilder();
                    for (String s : innerTypedArray) {
                        if (!MultiColor.isColor(s)) {
                            typedText.append(s);
                        }
                    }
                    
                    openColorFadeKeyboard(whoClicked, null, typedText.toString(), 0, fancyInventory);
                });
                
                lore.add(formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.colorFade.create", CONTROL_DROP, thisMessage));
                addFunction(openColor, CONTROL_DROP, (whoClicked, clickType, pdc, fancyInventory) -> {
                    String innerTypedString = getKeyboardOutput(fancyInventory);
                    ArrayList<String> innerTypedArray = MessageUtils.transformColoredTextToArray(innerTypedString);

                    ArrayList<MultiColor> colors = new ArrayList<>();
                    StringBuilder typedText = new StringBuilder();
                    for (String s : innerTypedArray) {
                        if (MultiColor.isColor(s)) {
                            colors.add(new MultiColor(s));
                        } else {
                            typedText.append(s);
                        }
                    }

                    openColorFadeKeyboard(whoClicked, colors, typedText.toString(), 0, fancyInventory);
                });
                
            }
            
            addFunction(openColor, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                fancyInventory.setData(editColorDataTye, false);
                openColorKeyboard(whoClicked, fancyInventory);
            });
        }
        
        setCustomItemData(openColor, colorTheme, openColorTitle, lore);
        keyboard.setItem(48, openColor);
    }
    
    private static void openBuiltInColorSelector2(Player player, int page, @Nonnull FancyInventory prevKeyboard) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ChatColor chatColor : ChatColor.values()) {
            if (!chatColor.isColor()) continue;
            ItemStack is = getChatColorStack(chatColor, player);
            MultiColor chatMultiColor = new MultiColor(chatColor);
            Message title = formatTranslation(colorTheme.getInfoColor(), chatMultiColor,
                    "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector2.chatColor", chatColor.name(), "&", chatColor.getChar()).translateMessage(playerLang);
            
            Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector2.chatColor.hex", chatMultiColor.getColorAsValue());
            Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector2.chatColor.rgb", chatMultiColor.getRed(), chatMultiColor.getGreen(), chatMultiColor.getBlue());
            Message clickToSelect = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector2.clickToSelect", LEFT);
            
            setCustomItemData(is, colorTheme, title, List.of(new Message(), valueHEXMessage, valueRGBMessage, new Message(), clickToSelect));
            setStringData(is, new NamespacedKey(Main.getInstance(), "keyboard_chat_color"), chatColor.name());
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                ChatColor c = ChatColor.valueOf(pdc.get(new NamespacedKey(Main.getInstance(), "keyboard_chat_color"), STRING));
                
                int innerPage = (int) fancyInventory.getData("colorFadePage");
                String innerTypedText = fancyInventory.getData(colorFadeStringDataType);
                ArrayList<MultiColor> innerColors = fancyInventory.getData(colorFadeColorsDataType);
                int colorIndex = (int) fancyInventory.getData("colorFadeColorIndex");
                
                innerColors.set(colorIndex, new MultiColor(c));
                
                openColorFadeKeyboard(whoClicked, innerColors, innerTypedText, innerPage, fancyInventory);
            }));
            
            items.add(is);
        }
        for (DyeColor dyeColor : DyeColor.values()) {
            ItemStack is = getDyeStack(dyeColor);
            MultiColor chatMultiColor = new MultiColor(dyeColor);
            Message title = formatTranslation(colorTheme.getInfoColor(), chatMultiColor,
                    "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector2.dyeColor", dyeColor.name()).translateMessage(playerLang);
            
            Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector2.dyeColor.hex", chatMultiColor.getColorAsValue());
            Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector2.dyeColor.rgb", chatMultiColor.getRed(), chatMultiColor.getGreen(), chatMultiColor.getBlue());
            Message clickToSelect = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector2.clickToSelect", LEFT);
            
            setCustomItemData(is, colorTheme, title, List.of(new Message(), valueHEXMessage, valueRGBMessage, new Message(), clickToSelect));
            setStringData(is, new NamespacedKey(Main.getInstance(), "keyboard_dye_color"), dyeColor.name());
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                DyeColor c = DyeColor.valueOf(pdc.get(new NamespacedKey(Main.getInstance(), "keyboard_dye_color"), STRING));
                
                int innerPage = (int) fancyInventory.getData("colorFadePage");
                String innerTypedText = fancyInventory.getData(colorFadeStringDataType);
                ArrayList<MultiColor> innerColors = fancyInventory.getData(colorFadeColorsDataType);
                int colorIndex = (int) fancyInventory.getData("colorFadeColorIndex");
                
                innerColors.set(colorIndex, new MultiColor(c));
                
                openColorFadeKeyboard(whoClicked, innerColors, innerTypedText, innerPage, fancyInventory);
            }));
            
            items.add(is);
        }
        
        ItemStack backButton = keyboard_chat_color_reject_model.getItem(player);
        Message backTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector2.cancel");
        setCustomItemData(backButton, colorTheme, backTitle, null);
        addFunction(backButton, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            int innerPage = (int) fancyInventory.getData("colorFadePage");
            openColorFadeKeyboard(whoClicked, innerPage, fancyInventory);
        }));
        
        Message title = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openBuiltInColorSelector2.title");
        FancyInventory builtInColorSelectorKeyboard = getDynamicScrollableInventory(player, page, KeyboardGUI::openBuiltInColorSelector2, title, items, backButton);
        builtInColorSelectorKeyboard.transferData(prevKeyboard, false);
        builtInColorSelectorKeyboard.setData(outputStorage, getKeyboardOutput(prevKeyboard));
        
        builtInColorSelectorKeyboard.open(player);
    }
    private static void openColorFadeKeyboard(Player player, int page, @Nonnull FancyInventory keyboard) {
        openColorFadeKeyboard(player, keyboard.getData(colorFadeColorsDataType), keyboard.getData(colorFadeStringDataType), page, keyboard);
    }
    public static void openColorFadeKeyboard(Player player, @Nullable ArrayList<MultiColor> colors, String typedText, int page, @Nonnull FancyInventory keyboard) {
        
        if (colors == null) {
            colors = new ArrayList<>();
        }
        
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player);
        
        Message valueAdd1      = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.value.add",    LEFT, valueToEdit(LEFT));
        Message valueAdd10     = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.value.add",    RIGHT, valueToEdit(RIGHT));
        Message valueAdd25     = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.value.add",    SHIFT_LEFT, valueToEdit(SHIFT_LEFT));
        Message valueAdd100    = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.value.add",    SHIFT_RIGHT, valueToEdit(SHIFT_RIGHT));
        Message valueRemove1   = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.value.remove", LEFT, valueToEdit(LEFT));
        Message valueRemove10  = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.value.remove", RIGHT, valueToEdit(RIGHT));
        Message valueRemove25  = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.value.remove", SHIFT_LEFT, valueToEdit(SHIFT_LEFT));
        Message valueRemove100 = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.value.remove", SHIFT_RIGHT, valueToEdit(SHIFT_RIGHT));
        
        ArrayList<ItemStack> items = new ArrayList<>(colors.size() * 7);
        int editColorIndex = -1;
        
        FancyClickRunnable onColorClick = (whoClicked, clickType, pdc, fancyColorInventory) -> {
            int valueToAdd = valueToEdit(clickType);
            if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_remove"))) valueToAdd *= -1;
            
            int innerPage = fancyColorInventory.getData(pageDataName);
            String innerTypedText = fancyColorInventory.getData(colorFadeStringDataType);
            ArrayList<MultiColor> innerColors = fancyColorInventory.getData(colorFadeColorsDataType);
            int colorIndex = pdc.get(new NamespacedKey(Main.getInstance(), "colorIndex"), INTEGER);
            
            Color c = innerColors.get(colorIndex).getColor();
            
            Color newColor = null;
            if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_red"))) {
                newColor = new Color(constrain(c.getRed() + valueToAdd, 255, 0), c.getGreen(), c.getBlue());
            } else if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_green"))) {
                newColor = new Color(c.getRed(), constrain(c.getGreen() + valueToAdd, 255, 0), c.getBlue());
            } else if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_blue"))) {
                newColor = new Color(c.getRed(), c.getGreen(), constrain(c.getBlue() + valueToAdd, 255, 0));
            }
            
            innerColors.set(colorIndex, new MultiColor(newColor));
            
            openColorFadeKeyboard(whoClicked, innerColors, innerTypedText, innerPage, fancyColorInventory);
        };
        
        for (MultiColor multiColor : colors) {
            editColorIndex++;
            
            Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.value.hex", multiColor.getColorAsValue());
            Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.value.rgb", multiColor.getRed(), multiColor.getGreen(), multiColor.getBlue());
            
            ItemStack redAdd = keyboard_color_fade_red_add_model.getItem(player);
            Message redAddTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.red.add").translateMessage(playerLang);
            setCustomItemData(redAdd, colorTheme, redAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
            setBooleanData(redAdd, new NamespacedKey(Main.getInstance(), "keyboard_color_red"), true);
            addFunction(redAdd, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
            setIntData(redAdd, new NamespacedKey(Main.getInstance(), "colorIndex"), editColorIndex);
            items.add(redAdd);
            
            ItemStack redRem = keyboard_color_fade_red_remove_model.getItem(player);
            Message redRemTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.red.remove").translateMessage(playerLang);
            setCustomItemData(redRem, colorTheme, redRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
            setBooleanData(redRem, new NamespacedKey(Main.getInstance(), "keyboard_color_red"), true);
            setBooleanData(redRem, new NamespacedKey(Main.getInstance(), "keyboard_color_remove"), true);
            addFunction(redRem, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
            setIntData(redRem, new NamespacedKey(Main.getInstance(), "colorIndex"), editColorIndex);
            items.add(redRem);
            
            ItemStack greenAdd = keyboard_color_fade_green_add_model.getItem(player);
            Message greenAddTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.green.add").translateMessage(playerLang);
            setCustomItemData(greenAdd, colorTheme, greenAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
            setBooleanData(greenAdd, new NamespacedKey(Main.getInstance(), "keyboard_color_green"), true);
            addFunction(greenAdd, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
            setIntData(greenAdd, new NamespacedKey(Main.getInstance(), "colorIndex"), editColorIndex);
            items.add(greenAdd);
            
            ItemStack greenRem = keyboard_color_fade_green_remove_model.getItem(player);
            Message greenRemTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.green.remove").translateMessage(playerLang);
            setCustomItemData(greenRem, colorTheme, greenRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
            setBooleanData(greenRem, new NamespacedKey(Main.getInstance(), "keyboard_color_green"), true);
            setBooleanData(greenRem, new NamespacedKey(Main.getInstance(), "keyboard_color_remove"), true);
            addFunction(greenRem, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
            setIntData(greenRem, new NamespacedKey(Main.getInstance(), "colorIndex"), editColorIndex);
            items.add(greenRem);
            
            ItemStack blueAdd = keyboard_color_fade_blue_add_model.getItem(player);
            Message blueAddTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.blue.add").translateMessage(playerLang);
            setCustomItemData(blueAdd, colorTheme, blueAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
            setBooleanData(blueAdd, new NamespacedKey(Main.getInstance(), "keyboard_color_blue"), true);
            addFunction(blueAdd, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
            setIntData(blueAdd, new NamespacedKey(Main.getInstance(), "colorIndex"), editColorIndex);
            items.add(blueAdd);
            
            ItemStack blueRem = keyboard_color_fade_blue_remove_model.getItem(player);
            Message blueRemTitle = formatTranslation(multiColor, multiColor, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.blue.remove").translateMessage(playerLang);
            setCustomItemData(blueRem, colorTheme, blueRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
            setBooleanData(blueRem, new NamespacedKey(Main.getInstance(), "keyboard_color_blue"), true);
            setBooleanData(blueRem, new NamespacedKey(Main.getInstance(), "keyboard_color_remove"), true);
            addFunction(blueRem, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
            setIntData(blueRem, new NamespacedKey(Main.getInstance(), "colorIndex"), editColorIndex);
            items.add(blueRem);
            
            ItemStack builtInColorSelector = keyboard_color_fade_chat_color_model.getItem(player);
            Message builtInColorSelectorTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.openBuiltInColorSelector");
            setCustomItemData(builtInColorSelector, colorTheme, builtInColorSelectorTitle,  null);
            setIntData(builtInColorSelector, new NamespacedKey(Main.getInstance(), "colorIndex"), editColorIndex);
            addFunction(builtInColorSelector, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                fancyInventory.setData("colorFadePage", fancyInventory.getData(pageDataName));
                fancyInventory.setData("colorFadeColorIndex", pdc.get(new NamespacedKey(Main.getInstance(), "colorIndex"), INTEGER));
                openBuiltInColorSelector2(whoClicked, 0, fancyInventory);
            }));
            
            items.add(builtInColorSelector);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, KeyboardGUI::openColorFadeKeyboard, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.title", items, null);
        inv.transferData(keyboard, false);
        inv.setData(colorFadeStringDataType, typedText);
        inv.setData(colorFadeColorsDataType, colors);
        
        if (page == 0) {
            ItemStack addFirstColor = keyboard_color_fade_edit_model.getItem(player);
            addFunction(addFirstColor, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                // add color after this one
                int innerPage = fancyInventory.getData(pageDataName);
                String innerTypedText = fancyInventory.getData(colorFadeStringDataType);
                
                ArrayList<MultiColor> innerColors = fancyInventory.getData(colorFadeColorsDataType);
                innerColors.add(0, new MultiColor("#ffffff"));
                
                openColorFadeKeyboard(whoClicked, innerColors, innerTypedText, innerPage, fancyInventory);
            });
            Message addFirstColorTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.addFirst.title", LEFT);
            setCustomItemData(addFirstColor, colorTheme, addFirstColorTitle, null);
            inv.setItem(0, addFirstColor);
        }
        
        for (int i = 0; i < 3 && i < colors.size(); i++) {
            ItemStack is = keyboard_color_fade_edit_model.getItem(player);
            ItemMeta im = is.getItemMeta();
            setIntData(im, new NamespacedKey(Main.getInstance(), "colorIndex"), i + inv.getData(pageDataName));
            
            Message addTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.add.title", LEFT);
            addFunction(im, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                // add color after this one
                int colorIndex = pdc.get(new NamespacedKey(Main.getInstance(), "colorIndex"), INTEGER);
                int innerPage = fancyInventory.getData(pageDataName);
                String innerTypedText = fancyInventory.getData(colorFadeStringDataType);
                
                ArrayList<MultiColor> innerColors = fancyInventory.getData(colorFadeColorsDataType);
                innerColors.add(colorIndex + 1, new MultiColor("#ffffff"));
                
                openColorFadeKeyboard(whoClicked, innerColors, innerTypedText, innerPage, fancyInventory);
            });
            
            Message remTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.remove.title", RIGHT);
            addFunction(im, RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
                // remove this color
                int colorIndex = pdc.get(new NamespacedKey(Main.getInstance(), "colorIndex"), INTEGER);
                int innerPage = fancyInventory.getData(pageDataName);
                String innerTypedText = fancyInventory.getData(colorFadeStringDataType);
                
                ArrayList<MultiColor> innerColors = fancyInventory.getData(colorFadeColorsDataType);
                innerColors.remove(colorIndex);
                
                openColorFadeKeyboard(whoClicked, innerColors, innerTypedText, innerPage, fancyInventory);
            });
            
            Message moveUpTitle = null;
            if (i + inv.getData(pageDataName) != 0) { // add function if it is not first
                moveUpTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.moveUp.title", SHIFT_LEFT);
                addFunction(im, SHIFT_LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                    // move color up
                    int colorIndex = pdc.get(new NamespacedKey(Main.getInstance(), "colorIndex"), INTEGER);
                    int innerPage = fancyInventory.getData(pageDataName);
                    String innerTypedText = fancyInventory.getData(colorFadeStringDataType);
                    
                    ArrayList<MultiColor> innerColors = fancyInventory.getData(colorFadeColorsDataType);
                    MultiColor currentColor = innerColors.get(colorIndex);
                    MultiColor swapColor = innerColors.get(colorIndex - 1);
                    
                    innerColors.set(colorIndex - 1, currentColor);
                    innerColors.set(colorIndex, swapColor);
                    
                    openColorFadeKeyboard(whoClicked, innerColors, innerTypedText, innerPage, fancyInventory);
                });
            }
            Message moveDownTitle = null;
            if (i + inv.getData(pageDataName) != colors.size() - 1) {
                moveDownTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.moveDown.title", SHIFT_RIGHT);
                addFunction(im, SHIFT_RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
                    // move color down
                    int colorIndex = pdc.get(new NamespacedKey(Main.getInstance(), "colorIndex"), INTEGER);
                    int innerPage = fancyInventory.getData(pageDataName);
                    String innerTypedText = fancyInventory.getData(colorFadeStringDataType);
                    
                    ArrayList<MultiColor> innerColors = fancyInventory.getData(colorFadeColorsDataType);
                    MultiColor currentColor = innerColors.get(colorIndex);
                    MultiColor swapColor = innerColors.get(colorIndex + 1);
                    
                    innerColors.set(colorIndex + 1, currentColor);
                    innerColors.set(colorIndex, swapColor);
                    
                    openColorFadeKeyboard(whoClicked, innerColors, innerTypedText, innerPage, fancyInventory);
                });
            }
            
            is.setItemMeta(im);
            Message editColorTitle = formatTranslation(colors.get(i + inv.getData(pageDataName)), colors.get(i + inv.getData(pageDataName)), "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.color.edit.title").translateMessage(playerLang);
            setCustomItemData(is, colorTheme, editColorTitle, Arrays.asList(addTitle, remTitle, moveUpTitle, moveDownTitle));
            
            inv.setItem(i * 9 + 9, is);
        }
        
        ItemStack accept = keyboard_color_fade_accept_model.getItem(player);
        addFunction(accept, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
            ArrayList<MultiColor> innerColors = fancyInventory.getData(colorFadeColorsDataType);
            String innerTypedText = fancyInventory.getData(colorFadeStringDataType);
            
            Message newText = MessageUtils.createColorGradient(innerTypedText, innerColors);
            String fadedText = newText.getText().stream().map(textComponent -> textComponent.getColor() + textComponent.getText()).collect(Collectors.joining());
            
            String accFuncName = fancyInventory.getData(acceptFuncNameDataType);
            FancyClickRunnable accFunc = getFunction(accFuncName);
            if (accFunc == null) accFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String rejFuncName = fancyInventory.getData(rejectFuncNameDataType);
            FancyClickRunnable rejFunc = getFunction(rejFuncName);
            if (rejFunc == null) rejFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            
            int keyboardSettings = fancyInventory.getData(keyboardSettingsDataType);
            String defColor = fancyInventory.getData(defColorDataType);
            FancyInventory newKeyboard = openKeyboard(whoClicked, accFunc, rejFunc, fadedText, defColor, keyboardSettings);
            newKeyboard.transferData(fancyInventory);
            newKeyboard.setData(outputStorage, fadedText);
            updateKeyboardTitle(whoClicked, newKeyboard);
        });
        Message acceptTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.accept.title");
        setCustomItemData(accept, colorTheme, acceptTitle, null);
        inv.setItem(inv.getSize() - 2, accept);
        
        ItemStack reject = keyboard_color_fade_reject_model.getItem(player);
        addFunction(reject, LEFT, (whoClicked, clickType, pdc, fancyColorInventory) -> {
            String accFuncName = fancyColorInventory.getData(acceptFuncNameDataType);
            FancyClickRunnable accFunc = getFunction(accFuncName);
            if (accFunc == null) accFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String rejFuncName = fancyColorInventory.getData(rejectFuncNameDataType);
            FancyClickRunnable rejFunc = getFunction(rejFuncName);
            if (rejFunc == null) rejFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String typedString = getKeyboardOutput(fancyColorInventory);
            
            int keyboardSettings = fancyColorInventory.getData(keyboardSettingsDataType);
            String defColor = fancyColorInventory.getData(defColorDataType);
            FancyInventory newKeyboard = openKeyboard(whoClicked, accFunc, rejFunc, typedString, defColor, keyboardSettings);
            newKeyboard.transferData(fancyColorInventory);
            updateKeyboardTitle(whoClicked, newKeyboard);
        });
        Message rejectTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorFadeKeyboard.cancel.title");
        setCustomItemData(reject, colorTheme, rejectTitle, null);
        inv.setItem(inv.getSize() - 9, reject);
        
        inv.open(player);
        
    }
    
    
    public static FancyInventory openKeyboard(Player player, @Nonnull FancyClickRunnable onAccept, @Nullable FancyClickRunnable onReject) {
        return openKeyboard(player, onAccept, onReject, ALL);
    }
    public static FancyInventory openKeyboard(Player player, @Nonnull FancyClickRunnable onAccept, @Nullable FancyClickRunnable onReject, int keyboardSettings) {
        return openKeyboard(player, onAccept, onReject, "", null, keyboardSettings);
    }
    public static FancyInventory openKeyboard(Player player, @Nonnull FancyClickRunnable onAccept, @Nullable FancyClickRunnable onReject, @Nullable String startInput, @Nullable String defColor) {
        return openKeyboard(player, onAccept, onReject, startInput, defColor, ALL);
    }
    public static FancyInventory openKeyboard(Player player, @Nonnull FancyClickRunnable onAccept, @Nullable FancyClickRunnable onReject, @Nullable String startInput, @Nullable String defColor, int keyboardSettings) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        startInput = Main.getOrDefault(startInput, "");
        startInput = startInput.replace("\\n", "\n");
        
        Message invTitle = formatInfoTranslation("tport.fancyMessage.inventories.KeyboardGUI.title", cursor);
        FancyInventory inv = new FancyInventory(54, invTitle);
        inv.setData(outputStorage, startInput);
        inv.setData(layoutDataType, "qwerty");
        inv.setData(keyboardSettingsDataType, keyboardSettings);
        inv.setData(formatTitleDataType, true);
        inv.setData(cursorIndexDataType, startInput.length());
        inv.setData(defColorDataType, defColor);
        
        populateQWERTY(inv, colorTheme, playerLang, player.getUniqueId(), keyboardSettings);
        
        setFormatButton(inv, player, colorTheme, playerLang);
        
        ItemStack changeLayout = keyboard_change_layout_model.getItem(player);
        Message changeLayoutTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.changeLayout.title", LEFT);
        setCustomItemData(changeLayout, colorTheme, changeLayoutTitle, null);
        addFunction(changeLayout, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            int innerKeyboardSettings = fancyInventory.getData(keyboardSettingsDataType);
            if (fancyInventory.getData(layoutDataType).equals("qwerty")) {
                populateAlphabet(fancyInventory, ColorTheme.getTheme(whoClicked), getPlayerLang(whoClicked.getUniqueId()), whoClicked.getUniqueId(), innerKeyboardSettings);
                fancyInventory.setData(layoutDataType, "alphabet");
            } else {
                populateQWERTY(fancyInventory, ColorTheme.getTheme(whoClicked), getPlayerLang(whoClicked.getUniqueId()), whoClicked.getUniqueId(), innerKeyboardSettings);
                fancyInventory.setData(layoutDataType, "qwerty");
            }
            fancyInventory.open(whoClicked);
        }));
        inv.setItem(47, changeLayout);
        
        updateColorButton(inv, player, colorTheme, playerLang);
        
        inv.setItem(49, getKey(' ', '\n', colorTheme, playerLang, player.getUniqueId(), keyboardSettings));
        inv.setItem(50, getKey('\b', (char) 127/*delete char*/, colorTheme, playerLang, player.getUniqueId(), keyboardSettings));
        
        ItemStack quickType = keyboard_quick_type_model.getItem(player);
        Message quickTypeTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.quickType.title", LEFT);
        Message quickTypeRight = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.quickType.right", RIGHT);
        Message quickTypeShiftRight = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.quickType.shiftRight", SHIFT_RIGHT);
        setCustomItemData(quickType, colorTheme, quickTypeTitle, List.of(quickTypeRight, quickTypeShiftRight));
        addFunction(quickType, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            QuickType.Callback c = (lines) -> {
                String typedString = getKeyboardOutput(fancyInventory);
                String signOutput = String.join("\n", lines).stripTrailing().replace("\\", "");
                
                int cursorIndex = fancyInventory.getData(cursorIndexDataType);
                cursorIndex = Math.min(typedString.length(), cursorIndex);
                
                if (cursorIndex == 0) {
                    typedString = signOutput + typedString;
                } else if (cursorIndex >= typedString.length()) {
                    typedString = typedString + signOutput;
                } else {
                    typedString = typedString.substring(0, cursorIndex) + signOutput + typedString.substring(cursorIndex);
                }
                
                fancyInventory.setData(cursorIndexDataType, cursorIndex + signOutput.length());
                
                fancyInventory.setData(outputStorage, typedString);
                updateKeyboardTitle(whoClicked, fancyInventory);
            };
            QuickType.open(whoClicked, c);
        }));
        addFunction(quickType, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            QuickType.Callback c = (lines) -> {
                String typedString = getKeyboardOutput(fancyInventory);
                String signOutput = String.join(" ", lines).stripTrailing().replace("\\", "");
                
                int cursorIndex = fancyInventory.getData(cursorIndexDataType);
                cursorIndex = Math.min(typedString.length(), cursorIndex);
                
                if (cursorIndex == 0) {
                    typedString = signOutput + typedString;
                } else if (cursorIndex >= typedString.length()) {
                    typedString = typedString + signOutput;
                } else {
                    typedString = typedString.substring(0, cursorIndex) + signOutput + typedString.substring(cursorIndex);
                }
                
                fancyInventory.setData(cursorIndexDataType, cursorIndex + signOutput.length());
                
                fancyInventory.setData(outputStorage, typedString);
                updateKeyboardTitle(whoClicked, fancyInventory);
            };
            QuickType.open(whoClicked, c);
        }));
        addFunction(quickType, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            QuickType.Callback c = (lines) -> {
                String typedString = getKeyboardOutput(fancyInventory);
                String signOutput = String.join("", lines).stripTrailing().replace("\\", "");
                
                int cursorIndex = fancyInventory.getData(cursorIndexDataType);
                cursorIndex = Math.min(typedString.length(), cursorIndex);
                
                if (cursorIndex == 0) {
                    typedString = signOutput + typedString;
                } else if (cursorIndex >= typedString.length()) {
                    typedString = typedString + signOutput;
                } else {
                    typedString = typedString.substring(0, cursorIndex) + signOutput + typedString.substring(cursorIndex);
                }
                
                fancyInventory.setData(cursorIndexDataType, cursorIndex + signOutput.length());
                
                fancyInventory.setData(outputStorage, typedString);
                updateKeyboardTitle(whoClicked, fancyInventory);
            };
            QuickType.open(whoClicked, c);
        }));
        inv.setItem(51, quickType);
        
        ItemStack cursor = keyboard_cursor_model.getItem(player);
        addFunction(cursor, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            int cursorIndex = fancyInventory.getData(cursorIndexDataType);
            String typedString = getKeyboardOutput(fancyInventory);
            boolean showColors = fancyInventory.getData(formatTitleDataType);
            
            if (showColors) {
                ArrayList<String> elements = MessageUtils.transformColoredTextToArray(typedString);
                int searchCursorIndex = 0;
                for (int i = 0; i < elements.size(); i++) {
                    String element = elements.get(i);
                    searchCursorIndex += element.length();
                    
                    if (searchCursorIndex >= cursorIndex) {//cursor found
                        int subCursorIndex = element.length() - (searchCursorIndex - cursorIndex);
                        if (MultiColor.isColor(element)) {
                            cursorIndex -= subCursorIndex - 1; //remove color length, add +1 offset
                        } else if (subCursorIndex == 0) {
                            if (i - 1 >= 0) {
                                cursorIndex -= elements.get(i - 1).length() - 1; //remove color length
                            }
                        }
                        break;
                    }
                }
            }
            fancyInventory.setData(cursorIndexDataType, Math.max(0, cursorIndex - 1));
            
            updateKeyboardTitle(whoClicked, fancyInventory);
        }));
        addFunction(cursor, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            int cursorIndex = fancyInventory.getData(cursorIndexDataType);
            String typedString = getKeyboardOutput(fancyInventory);
            boolean showColors = fancyInventory.getData(formatTitleDataType);
            if (showColors) {
                ArrayList<String> elements = MessageUtils.transformColoredTextToArray(typedString);
                int searchCursorIndex = 0;
                for (int i = 0; i < elements.size(); i++) {
                    String element = elements.get(i);
                    searchCursorIndex += element.length();
                    
                    if (searchCursorIndex >= cursorIndex) {
                        int subCursorIndex = element.length() - (searchCursorIndex - cursorIndex);
                        if (subCursorIndex == element.length()) {
                            if (i + 1 < elements.size() && MultiColor.isColor(elements.get(i + 1))) {
                                cursorIndex += elements.get(i + 1).length() - 1;
                            }
                        } else if (MultiColor.isColor(element)) {
                            cursorIndex += element.length() - subCursorIndex - 1; //add remaining color length, remove 1 offset
                        }
                        break;
                    }
                }
            }
            fancyInventory.setData(cursorIndexDataType, Math.min(typedString.length(), cursorIndex + 1));
            
            updateKeyboardTitle(whoClicked, fancyInventory);
        }));
        addFunction(cursor, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            fancyInventory.setData(cursorIndexDataType, 0);
            updateKeyboardTitle(whoClicked, fancyInventory);
        }));
        addFunction(cursor, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            String typedString = getKeyboardOutput(fancyInventory);
            fancyInventory.setData(cursorIndexDataType, typedString.length());
            updateKeyboardTitle(whoClicked, fancyInventory);
        }));
        Message cursorTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.cursor.title");
        Message cursorLeft = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.cursor.moveLeft", LEFT);
        Message cursorRight = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.cursor.moveRight", RIGHT);
        Message cursorStart = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.cursor.moveStart", SHIFT_LEFT);
        Message cursorEnd = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.cursor.moveEnd", SHIFT_RIGHT);
        setCustomItemData(cursor, colorTheme, cursorTitle, List.of(new Message(), cursorLeft, cursorRight, cursorStart, cursorEnd));
        inv.setItem(52, cursor);
        
        ItemStack acceptButton = keyboard_accept_model.getItem(player);
        Message acceptTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.accept.title");
        setCustomItemData(acceptButton, colorTheme, acceptTitle, null);
        addFunction(acceptButton, LEFT, onAccept);
        inv.setData(acceptFuncNameDataType, getFunctionName(acceptButton, LEFT));
        inv.setItem(inv.getSize() - 1, acceptButton);
        
        ItemStack rejectButton = keyboard_reject_model.getItem(player);
        Message rejectTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.reject.title");
        setCustomItemData(rejectButton, colorTheme, rejectTitle, null);
        if (onReject == null) onReject = ((whoClicked, clickType, pdc, fancyInventory) -> whoClicked.closeInventory());
        addFunction(rejectButton, LEFT, onReject);
        inv.setData(rejectFuncNameDataType, getFunctionName(rejectButton, LEFT));
        inv.setItem(inv.getSize() - 9, rejectButton);
        
        if (startInput.isEmpty()) {
            inv.open(player);
        } else {
            updateKeyboardTitle(player, inv);
        }
        return inv;
    }
}
