package com.spaceman.tport.fancyMessage.inventories.keyboard;

import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.fancyMessage.language.Language;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

import static com.spaceman.tport.fancyMessage.MessageUtils.setCustomItemData;
import static com.spaceman.tport.fancyMessage.MessageUtils.translateMessage;
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
    
    public static String getKeyboardOutput(FancyInventory inv) {
        return inv.getData(outputStorage);
    }
    
    private static final HashMap<Character, InventoryModel> keyModels = new HashMap<>();
    private static InventoryModel registerKeyModel(char forChar, @SuppressWarnings("SameParameterValue") Material material, InventoryModel previousModel) {
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
    public static final InventoryModel keyboard_quick_type_model = new InventoryModel(Material.OAK_BUTTON, keyboard_change_layout_model, "keyboard");
    public static final InventoryModel keyboard_format_on_model = new InventoryModel(Material.OAK_BUTTON, keyboard_quick_type_model, "keyboard");
    public static final InventoryModel keyboard_format_off_model = new InventoryModel(Material.OAK_BUTTON, keyboard_format_on_model, "keyboard");
    public static final InventoryModel keyboard_cursor_model = new InventoryModel(Material.OAK_BUTTON, keyboard_format_off_model, "keyboard");
    
    public static final InventoryModel keyboard_color_model = new InventoryModel(Material.OAK_BUTTON, keyboard_cursor_model, "keyboard");
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
    public static final InventoryModel char_newline_model = registerKeyModel('\n', Material.OAK_BUTTON, char_backspace_model);
    public static final int last_model_id = char_newline_model.getCustomModelData();
    
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
    private static ItemStack getKey(char key, char alternate, ColorTheme colorTheme, JsonObject playerLang, UUID playerUUID) {
        if (key == '\0') { key = alternate; alternate = '\0'; }
        InventoryModel model = getModel(Character.toLowerCase(key));
        ItemStack item = model.getItem(playerUUID);
        if (key == '\0') return null;
        
        boolean hasRightClickAction = true;
        Message title;
        Message rightClick = null;
        if (alternate == '\0') { //has no right click alternate key
            title = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.key.title.withoutAlternate", getKeyTitle(key));
            hasRightClickAction = false;
        } else {
            title = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.key.title.withAlternate", getKeyTitle(key), getKeyTitle(alternate));
            rightClick = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.key.alternate.click", ClickType.RIGHT, getKeyTitle(alternate));
        }
        
        Message clearTextNewLine = null;
        Message clearText = null;
        if (key == '\b') {
            clearTextNewLine = new Message();
            clearText = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.key.clear.click", CONTROL_DROP);
            
            FancyClickEvent.addFunction(item, CONTROL_DROP, ((whoClicked, clickType, pdc, fancyInventory) -> {
                fancyInventory.setData(cursorIndexDataType, 0);
                fancyInventory.setData(outputStorage, "");
                updateKeyboardTitle(whoClicked, fancyInventory);
            }));
        }
        
        Message leftClick = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.key.normal.click", LEFT, getKeyTitle(key));
        
        setCustomItemData(item, colorTheme, title, Arrays.asList(new Message(), leftClick, rightClick, clearTextNewLine, clearText));
        
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
        
        addFunction(item, LEFT, onClick);
        if (hasRightClickAction) addFunction(item, ClickType.RIGHT, onClick);
        
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
            FancyClickEvent.FancyClickRunnable accFunc = FancyClickEvent.getFunction(accFuncName);
            if (accFunc == null) accFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String rejFuncName = fancyColorInventory.getData(rejectFuncNameDataType);
            FancyClickEvent.FancyClickRunnable rejFunc = FancyClickEvent.getFunction(rejFuncName);
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
                        } else if (i > 0) {
                            String originalColor = typedArray.get(i-1);
                            typedArray.set(i-1, addedColor);
                            cursorIndex += -originalColor.length() + addedColor.length();
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
            FancyInventory newKeyboard = openKeyboard(whoClicked, accFunc, rejFunc, typedString, keyboardSettings);
            newKeyboard.transferData(fancyColorInventory);
            newKeyboard.setData(outputStorage, typedString);
            updateKeyboardTitle(whoClicked, newKeyboard);
        }));
        colorKeyboard.setItem(colorKeyboard.getSize() - 1, acceptColor);
        
        ItemStack rejectColor = keyboard_color_reject_model.getItem(player);
        setCustomItemData(rejectColor, colorTheme, formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.cancel"), null);
        addFunction(rejectColor, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            String accFuncName = fancyColorInventory.getData(acceptFuncNameDataType);
            FancyClickEvent.FancyClickRunnable accFunc = FancyClickEvent.getFunction(accFuncName);
            if (accFunc == null) accFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String rejFuncName = fancyColorInventory.getData(rejectFuncNameDataType);
            FancyClickEvent.FancyClickRunnable rejFunc = FancyClickEvent.getFunction(rejFuncName);
            if (rejFunc == null) rejFunc = ((whoClicked1, clickType1, pdc1, fancyInventory1) -> { });
            
            String typedString = getKeyboardOutput(fancyColorInventory);
            
            int keyboardSettings = fancyColorInventory.getData(keyboardSettingsDataType);
            FancyInventory newKeyboard = openKeyboard(whoClicked, accFunc, rejFunc, typedString, keyboardSettings);
            newKeyboard.transferData(fancyColorInventory);
            updateKeyboardTitle(whoClicked, newKeyboard);
        }));
        colorKeyboard.setItem(colorKeyboard.getSize() - 9, rejectColor);
        
        
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
        addFunction(redAdd, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(Math.min(c.getRed() + 1, 255), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(redAdd, RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(Math.min(c.getRed() + 10, 255), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(redAdd, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(Math.min(c.getRed() + 25, 255), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(redAdd, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(Math.min(c.getRed() + 100, 255), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        colorKeyboard.setItem(10, redAdd);
        
        ItemStack redRem = keyboard_color_red_remove_model.getItem(player);
        Message redRemTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.red.remove");
        MessageUtils.setCustomItemData(redRem, colorTheme, redRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        addFunction(redRem, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(Math.max(c.getRed() - 1, 0), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(redRem, RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(Math.max(c.getRed() - 10, 0), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(redRem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(Math.max(c.getRed() - 25, 0), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(redRem, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(Math.max(c.getRed() - 100, 0), c.getGreen(), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        colorKeyboard.setItem(11, redRem);
        
        ItemStack greenAdd = keyboard_color_green_add_model.getItem(player);
        Message greenAddTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.green.add");
        MessageUtils.setCustomItemData(greenAdd, colorTheme, greenAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
        addFunction(greenAdd, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), Math.min(c.getGreen() + 1, 255), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(greenAdd, RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), Math.min(c.getGreen() + 10, 255), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(greenAdd, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), Math.min(c.getGreen() + 25, 255), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(greenAdd, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), Math.min(c.getGreen() + 100, 255), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        colorKeyboard.setItem(12, greenAdd);
        
        ItemStack greenRem = keyboard_color_green_remove_model.getItem(player);
        Message greenRemTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.green.remove");
        MessageUtils.setCustomItemData(greenRem, colorTheme, greenRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        addFunction(greenRem, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), Math.max(c.getGreen() - 1, 0), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(greenRem, RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), Math.max(c.getGreen() - 10, 0), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(greenRem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), Math.max(c.getGreen() - 25, 0), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(greenRem, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), Math.max(c.getGreen() - 100, 0), c.getBlue()));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        colorKeyboard.setItem(13, greenRem);
        
        ItemStack blueAdd = keyboard_color_blue_add_model.getItem(player);
        Message blueAddTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.blue.add");
        MessageUtils.setCustomItemData(blueAdd, colorTheme, blueAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
        addFunction(blueAdd, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), c.getGreen(), Math.min(c.getBlue() + 1, 255)));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(blueAdd, RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), c.getGreen(), Math.min(c.getBlue() + 10, 255)));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(blueAdd, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), c.getGreen(), Math.min(c.getBlue() + 25, 255)));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(blueAdd, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), c.getGreen(), Math.min(c.getBlue() + 100, 255)));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        colorKeyboard.setItem(14, blueAdd);
        
        ItemStack blueRem = keyboard_color_blue_remove_model.getItem(player);
        Message blueRemTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.openColorKeyboard.blue.remove");
        MessageUtils.setCustomItemData(blueRem, colorTheme, blueRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        addFunction(blueRem, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), c.getGreen(), Math.max(c.getBlue() - 1, 0)));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(blueRem, RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), c.getGreen(), Math.max(c.getBlue() - 10, 0)));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(blueRem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), c.getGreen(), Math.max(c.getBlue() - 25, 0)));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        addFunction(blueRem, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            Color c = fancyColorInventory.getData(colorDataType);
            fancyColorInventory.setData(colorDataType, new Color(c.getRed(), c.getGreen(), Math.max(c.getBlue() - 100, 0)));
            openColorKeyboard(whoClicked, fancyColorInventory);
        }));
        colorKeyboard.setItem(15, blueRem);
        
        colorKeyboard.open(player);
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
                    color = new MultiColor(typedArray.get(i - 1)).getColor();
                }
                break;
            }
        }
        return color;
    }
    private static void updateColorButton(FancyInventory keyboard, Player player, @Nullable ColorTheme colorTheme, @Nullable JsonObject playerLang) {
        int keyboardSettings = keyboard.getData(keyboardSettingsDataType);
        if ((keyboardSettings & COLOR) == COLOR) {
            if (colorTheme == null) colorTheme = ColorTheme.getTheme(player);
            if (playerLang == null) playerLang = getPlayerLang(player.getUniqueId());
            
            ItemStack openColor = keyboard_color_model.getItem(player);
            Message openColorTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.colorEditor.title");
            List<Message> lore = new ArrayList<>();
            lore.add(new Message());
            
            lore.add(formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.insertColor.title", LEFT));
            
            String typedString = getKeyboardOutput(keyboard);
            
            if (!typedString.isEmpty()) {
                ArrayList<String> typedArray = MessageUtils.transformColoredTextToArray(typedString);
                Color color = getColorFromCursor(keyboard, typedArray);
                
                Message thisMessage = formatTranslation(new MultiColor(color), new MultiColor(color), "tport.fancyMessage.inventories.KeyboardGUI.editColor.this");
                thisMessage = translateMessage(thisMessage, playerLang);
                lore.add(formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.editColor.title", RIGHT, thisMessage));
            }
            setCustomItemData(openColor, colorTheme, openColorTitle, lore);
            
            addFunction(openColor, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                fancyInventory.setData(editColorDataTye, false);
                openColorKeyboard(whoClicked, fancyInventory);
            }));
            if (!typedString.isEmpty()) addFunction(openColor, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                String innerTypedString = getKeyboardOutput(fancyInventory);
                ArrayList<String> typedArray = MessageUtils.transformColoredTextToArray(innerTypedString);
                
                Color color = getColorFromCursor(fancyInventory, typedArray);
                
                fancyInventory.setData(colorDataType, color);
                fancyInventory.setData(editColorDataTye, true);
                openColorKeyboard(whoClicked, fancyInventory);
            }));
            keyboard.setItem(48, openColor);
        }
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
    public static FancyInventory openKeyboard(Player player, @Nonnull FancyClickEvent.FancyClickRunnable onAccept, @Nullable FancyClickEvent.FancyClickRunnable onReject, @Nullable String startInput, int keyboardSettings) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player.getUniqueId());
        startInput = Main.getOrDefault(startInput, "");
        
        Message invTitle = formatInfoTranslation("tport.fancyMessage.inventories.KeyboardGUI.title", cursor);
        FancyInventory inv = new FancyInventory(54, invTitle);
        inv.setData(outputStorage, startInput);
        inv.setData(layoutDataType, "qwerty");
        inv.setData(keyboardSettingsDataType, keyboardSettings);
        inv.setData(formatTitleDataType, true);
        inv.setData(cursorIndexDataType, startInput.length());
        
        populateQWERTY(inv, colorTheme, playerLang, player.getUniqueId());
        
        setFormatButton(inv, player, colorTheme, playerLang);
        
        ItemStack changeLayout = keyboard_change_layout_model.getItem(player);
        Message changeLayoutTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.changeLayout.title", LEFT);
        setCustomItemData(changeLayout, colorTheme, changeLayoutTitle, null);
        addFunction(changeLayout, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            if (fancyInventory.getData(layoutDataType).equals("qwerty")) {
                populateAlphabet(fancyInventory, ColorTheme.getTheme(whoClicked), Language.getPlayerLang(whoClicked.getUniqueId()), whoClicked.getUniqueId());
                fancyInventory.setData(layoutDataType, "alphabet");
            } else {
                populateQWERTY(fancyInventory, ColorTheme.getTheme(whoClicked), Language.getPlayerLang(whoClicked.getUniqueId()), whoClicked.getUniqueId());
                fancyInventory.setData(layoutDataType, "qwerty");
            }
            fancyInventory.open(whoClicked);
        }));
        inv.setItem(47, changeLayout);
        
        updateColorButton(inv, player, colorTheme, playerLang);
        
        char space = (keyboardSettings & SPACE) == SPACE ? ' ' : '\0';
        char newLine = (keyboardSettings & NEWLINE) == NEWLINE ? '\n' : '\0';
        inv.setItem(49, getKey(space, newLine, colorTheme, playerLang, player.getUniqueId()));
        inv.setItem(50, getKey('\b', (char) 127/*delete char*/, colorTheme, playerLang, player.getUniqueId()));
        
        ItemStack quickType = keyboard_quick_type_model.getItem(player);
        Message quickTypeTitle = formatInfoTranslation(playerLang, "tport.fancyMessage.inventories.KeyboardGUI.quickType.title", LEFT);
        setCustomItemData(quickType, colorTheme, quickTypeTitle, null);
        addFunction(quickType, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            QuickType.Callback c = (lines) -> {
                String typedString = getKeyboardOutput(fancyInventory);
                String signOutput = String.join("\n", lines).stripTrailing();
                
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
