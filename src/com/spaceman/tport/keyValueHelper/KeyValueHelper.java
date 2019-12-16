package com.spaceman.tport.keyValueHelper;

import com.spaceman.tport.Pair;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.fancyMessage.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

@SuppressWarnings({"WeakerAccess", "unused"})
public class KeyValueHelper {
    
    /*
     * This util helps you create objects/tab completes that are using the format: "<key>=<value>,<key>=<value>"
     * This util uses the following utils:
     * - Pair
     * - ColorFormatter
     * - FancyMessage
     *
     * Example/usage of the Tab Constructor
     * This method lets you create tab completion for this util
     *
     * Usage:
     * KeyValueHelper.constructTab('the argument', 'KeyValueTabArguments format'...);
     * Example:
     * List<string> list = KeyValueHelper.constructTab(args[args.length - 1],
     *         new KeyValueTabArgument("m", Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList())),
     *         new KeyValueTabArgument("d", Arrays.stream(BlockFace.values()).map(BlockFace::name).collect(Collectors.toList())),
     *         new KeyValueTabArgument("i", Collections.singletonList("<X>"))
     * );
     *
     * The Tab Constructor uses the KeyValueTabArgument object, it requires the following arguments:
     * - name of the key
     * - the available values
     *
     * When argument equals 'm=' the list looks like:
     * - m=STONE
     * - m=PLANKS
     * - ect
     * When argument equals 'm=STONE,d' the list looks like:
     * - m=STONE,d=NORTH
     * - m=STONE,d=EAST
     * - m=STONE,d=SOUTH
     * - m=STONE,d=WEST
     * - ect
     * When argument equals 'm=STONE,d=EAST' the list looks like:
     * - m=STONE,d=EAST,i=
     *
     * Example/usage of the Object Constructor
     * This method returns the objects from the given argument based from the template
     * the format you define is not fixed to its order.
     *
     * Usage:
     * KeyValueHelper.constructObject('the argument', 'the keys that define the format'...);
     * Example:
     * This returns a HashMap with the created objects in it
     * HashMap<String, Object> map = KeyValueHelper.constructObject(data.get(0),
     *         new Key("m", Material::matchMaterial, true),
     *         new Key("d", s -> BlockFace.valueOf(s.toUpperCase()), true),
     *         new Key("i", Integer::valueOf, true)
     * );
     *
     * The Object Constructor uses the Key Object, it requires the following arguments:
     * - name of the key
     * - the value checker
     * - boolean if its required
     *
     * When argument equals 'm=stone,d=east,i=2' the map looks like:
     * m = Material.STONE
     * d = BlockFace.EAST
     * i = 2
     *
     *
     *
     * Example/usage of the Extended Object Constructor:
     * The Extended version does not only return the objects from the argument, but it also puts the objects in the base object
     * the format you define is not fixed to its order.
     *
     * Usage:
     * KeyValueHelper.extendedConstructObject('the argument', 'the base object', 'the keys that define the format'...);
     * Example:
     * This returns a HashMap with the created Object and the sub objects within it
     * HashMap<String, Object> map = KeyValueHelper.extendedConstructObject(argument, new Object,
     *         (ExtendedKey) new ExtendedKey("m", Material::matchMaterial, true, (o, v) -> o.setMaterial(v)).setErrorMessage("is not a valid material"),
     *         (ExtendedKey) new ExtendedKey("d", s -> BlockFace.valueOf(s.toUpperCase()), true, (o, v) -> o.setFace(v)).setErrorMessage("is not a valid block face"),
     *         (ExtendedKey) new ExtendedKey("i", Integer::valueOf, false, (o, v) -> o.setInt(v)).setErrorMessage("is not a valid number")
     * );
     *
     * The Extended Object Constructor uses the ExtendedKey Object, it requires the following arguments:
     * - name of the key
     * - the value checker
     * - boolean if its required
     * - value setter
     *
     * When argument equals 'm=stone,d=east,i=2' the map looks like:
     * m = Material.STONE
     * d = BlockFace.EAST
     * i = 2
     * null = the new created object which has the in it.
     *
     * */
    
    public static List<String> constructTab(String arg, KeyValueTabArgument... keyValueTabArguments) {
        return constructTab(arg, Arrays.asList(keyValueTabArguments));
    }
    
    private static List<Pair<String, String>> getWrittenPairs(String arg) {
        List<Pair<String, String>> list = new ArrayList<>();
        
        String[] split = (arg + " ").split(",");
        for (int i = 0; i < split.length - 1; i++) {
            String[] keyValue = split[i].split("=");
            if (keyValue.length == 2) {
                list.add(new Pair<>(keyValue[0], keyValue[1]));
            }
        }
        return list;
    }
    
    private static List<KeyValueTabArgument> filterNotContaining(String arg, List<KeyValueTabArgument> keyValueTabArguments) {
        List<String> keys = Arrays.stream(arg.split(",")).map(s -> s.split("=")[0]).collect(Collectors.toList());
        return keyValueTabArguments.stream().filter(tabArgument -> !keys.contains(tabArgument.getKey())).collect(Collectors.toList());
    }
    
    public static List<String> constructTab(String arg, List<KeyValueTabArgument> keyValueTabArguments) {
        
        String lastKeyValue;
        if (arg.endsWith(",")) {
            lastKeyValue = "";
        } else {
            lastKeyValue = arg.split(",")[arg.split(",").length - 1];
        }
        
        String lastKey = lastKeyValue.split("=")[0];
        String object = arg.substring(0, arg.lastIndexOf(lastKeyValue));
        
        if (lastKeyValue.contains("=")) {
            KeyValueTabArgument tabArgument = keyValueTabArguments.stream().filter(s -> s.getKey().equalsIgnoreCase(lastKey)).findFirst().get();
            if (lastKeyValue.split("=").length == 2 && tabArgument.getValues(new ArrayList<>()).stream().anyMatch(s -> s.equalsIgnoreCase(lastKeyValue.split("=")[1]))) {
                return filterNotContaining(arg, keyValueTabArguments).stream().map(KeyValueTabArgument::getKey).map(s -> object + lastKeyValue + "," + s).collect(Collectors.toList());
            } else {
                List<String> list = new ArrayList<>();
                for (String s : tabArgument.getValues(getWrittenPairs(arg))) {
                    list.add(object + lastKey + "=" + s);
                }
                return list;
            }
        } else {
            for (KeyValueTabArgument tabArgument : keyValueTabArguments) {
                if (tabArgument.getKey().equalsIgnoreCase(lastKey)) {
                    List<String> list = new ArrayList<>();
                    for (String s : tabArgument.getValues(getWrittenPairs(arg))) {
                        list.add(object + lastKey + "=" + s);
                    }
                    return list;
                }
            }
            return filterNotContaining(arg, keyValueTabArguments).stream().map(KeyValueTabArgument::getKey).map(s -> object + s).collect(Collectors.toList());
        }
    }
    
    public static HashMap<String, Object> constructObject(String obj, Key... keys) throws KeyValueError {
        return constructObject(obj, Arrays.asList(keys));
    }
    
    public static HashMap<String, Object> constructObject(String stringObj, List<? extends Key> keys) throws KeyValueError {
        //key=value,key1=value1,value
        
        ArrayList<String> neededList = keys.stream().filter(key -> !key.isOptional()).map(Key::getKey).map(String::toLowerCase).collect(Collectors.toCollection(ArrayList::new));
        
        HashMap<String, Object> keysMap = new HashMap<>();
        for (String keyValue : stringObj.split(",")) {
            if (keyValue.matches(".+=.+")) {
                Key newKey = Key.getKey(keys, keyValue.split("=")[0]);
                if (newKey != null) {
                    String[] split = keyValue.split("=");
    
                    Object value = newKey.check(split[1]);
                    if (value != null || newKey.isAcceptNullValue()) {
                        keysMap.put(newKey.getKey(), split[1]);
                    } else {
                        Message message = new Message();
                        message.addText(textComponent(split[1], ColorTheme.ColorType.varErrorColor));
                        message.addText(textComponent(newKey.getErrorMessage(), ColorTheme.ColorType.errorColor));
                        throw new KeyValueError(message);
                    }
                    
                    keysMap.put(split[0].toLowerCase(), value);
                    neededList.remove(split[0].toLowerCase());
                } else {
                    Message message = new Message();
                    message.addText(textComponent("Key ", ColorTheme.ColorType.errorColor));
                    message.addText(textComponent(keyValue.split("=")[0], ColorTheme.ColorType.varErrorColor));
                    message.addText(textComponent(" is not accepted, please use the format: ", ColorTheme.ColorType.errorColor));
                    message.addMessage(getFormat(keys));
                    throw new KeyValueError(message);
                }
            } else {
                Message message = new Message();
                message.addText(textComponent("Error in ", ColorTheme.ColorType.errorColor));
                message.addText(textComponent(keyValue, ColorTheme.ColorType.varErrorColor));
                message.addText(textComponent(", please use the format: ", ColorTheme.ColorType.errorColor));
                message.addMessage(getFormat(keys));
                throw new KeyValueError(message);
            }
        }
        
        if (!neededList.isEmpty()) {
            Message message = new Message();
            message.addText(textComponent("Not all needed keys are used, please use the format: ", ColorTheme.ColorType.errorColor));
            message.addMessage(getFormat(keys));
            throw new KeyValueError(message);
        }
        
        return keysMap;
    }
    
    private static Message getFormat(List<? extends Key> keys) {
        Message message = new Message(textComponent(""));
        for (Key key : keys) {
            if (key.getKey() != null && !key.getKey().equals("")) {
                if (key.isOptional()) {
                    message.addText(textComponent(key.getKey() + "=[value]", ColorTheme.ColorType.varErrorColor, hoverEvent(textComponent("This Key is optional", ColorTheme.ColorType.errorColor))));
                } else {
                    message.addText(textComponent(key.getKey() + "=<value>", ColorTheme.ColorType.varErrorColor));
                }
            }
            message.addText(textComponent(",", ColorTheme.ColorType.varErrorColor));
        }
        message.removeLast();
        return message;
    }
    
    public static HashMap<String, Object> extendedConstructObject(String stringObj, Object obj, ExtendedKey... keys) throws KeyValueError {
        return extendedConstructObject(stringObj, obj, Arrays.asList(keys));
    }
    
    public static HashMap<String, Object> extendedConstructObject(String stringObj, Object obj, List<ExtendedKey> keys) throws KeyValueError {
        
        HashMap<String, Object> map = constructObject(stringObj, keys);
        
        for (String keyName : map.keySet()) {
            Key key = Key.getKey(keys, keyName);
            if (key != null) {
                if (key instanceof ExtendedKey) {
                    ((ExtendedKey) key).set(obj, map.get(key.getKey()));
                }
            }
        }
        map.put(null, obj);
        return map;
    }
}
