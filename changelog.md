# Version 1.21.2

#### In this version:

Created a new adapter for Minecraft 1.21.3

## History
This feature is now in pre-release state. This feature records all teleports a player has, and stores it in a list.
This will replace `/tport back` in the future. 
Your teleport history will record all teleports you have done, even from other plugins and Minecraft self.
Each teleport is an entry in your history, each entry contains several elements:
- From  
    This is the location from which you teleported from.
- To  
    This is the location from which you teleported to. This can be a location or an object.
    This can be for example a TPort or a player, this allows it to be adaptable.
    If the tport is moved, this history element will teleport you to the correct location.
    This adaptable location will only work if the plugin who executed the teleport support the TPort History feature.
    For an example: if you teleport to a biome using BiomeTP, it will show two `to` locations, one is the name of the biome, and the other is the actual xyz location.
- Cause  
    The cause is the reason you have teleported, this can be `ENDER_PEARL` or `NETHER_PORTAL`, or `PLUGIN:TPort`.
    It is possible to filter for these causes.
- Plugin (Optional)  
    If the teleport is executed by a plugin, TPort will search for which plugin was responsible and shows this in the `plugin` tag.
    It is possible to filter for these plugins.
- Type (Optional)
    If the plugin has implemented this feature, the plugin can also name the feature/type of teleport.
    For example, if you teleport to a player using TPort, the `plugin` will be `TPort`, and the `type` will be `PLTP`. 





To open your history, use `/tport history`, or open it in the main TPort window (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/history.png?raw=true "History")  )

The commands are:
- /tport history
    open your teleport history.
- /tport history back [filter]
    teleport to the last element from location.
- /tport history tmpName [filter]
    teleport to the last element to location.  
    This command has yet to be renamed.
- /tport history clear [filter]
    Clear your teleport history
- /tport history size [size]
    Get/set the maximum size of a teleport history.

---

#### Add History support to your plugin
If you are a developer and want to add support for the History, you have to tell TPort before teleporting what type of teleport your plugin is executing via this one line:

``` java
TeleportHistory.setLocationSource(player.getUniqueId(), new CraftLocationSource());
player.teleport(location);
```
This tells the history manager how to handle this location.
You can also use a custom location source:
``` java
TeleportHistory.setLocationSource(player.getUniqueId(), new ExampleLocationSource(liveLocationObject));
```

A Location Source is an object that handles the teleport information and teleporting.
The LocationSource extends the Encapsulation class.
This class is for formatting the object to text (in windows and chat).  

In this small tutorial we will create a custom Location Source, named ExampleLocationSource.
Create your own class implementing LocationSource (com.spaceman.tport.history.locationSource.LocationSource)
After this step, implement all methods, it then should look like this:

``` java
public class ExampleLocationSource implements LocationSource {
    
    @Nullable
    @Override
    public Location getLocation(Player player) {
        return null;
    }
    
    @Override
    public void setLocation(Location location) {
        
    }
    
    @Override
    public void teleportToLocation(Player player) {
        
    }
    
    @Override
    public void notSafeToTeleport(Player player) {
        
    }
    
    @Nullable
    @Override
    public InventoryModel getInventoryModel() {
        return null;
    }
    
    @Nullable
    @Override
    public String getType() {
        return "";
    }
    
    @Override
    public boolean getSafetyCheckState(Player player) {
        return false;
    }
    
    @Override
    public String asString() {
        return "";
    }
    
    @Nonnull
    @Override
    public Message toMessage(String color, String varColor) {
        return null;
    }
    
    @Nullable
    @Override
    public HoverEvent getHoverEvent() {
        return null;
    }
    
    @Nullable
    @Override
    public ClickEvent getClickEvent() {
        return null;
    }
    
    @Nullable
    @Override
    public String getInsertion() {
        return "";
    }
}

```

Methods from Encapsulation:

- String asString()  
    This should return the String value of the location source.
    Examples: the biome, player name, TPort name, feature name, ect.
- Message toMessage(String color, String varColor)  
    This should return the Message object. This object is the actual text used for the chat and TPort windows.
    The default implementation looks like this:
    ``` java
      @Nonnull
      @Override  
      Message toMessage(String color, String varColor) {
        return new Message(new TextComponent(asString(), varColor));
      }
    ```
    But more customisation is possible.
- HoverEvent getHoverEvent()  
    The hover event is a message wrapped as a HoverEvent object that shows when you hover your mouse over the location source in chat. 
- ClickEvent getClickEvent()  
    The click event is the event that runs when a player clicks the location source in chat. 
- String getInsertion()  
    The insertion is the string that copies to the chat of the player when they shift click the location source.
    The default implementation could look like this:
    ``` java
      @Nullable
      @Override  
      String getInsertion() {
        return asString();
      }
    ```
    But more customisation is possible.


These methods are used to convert the LocationSource to a string to show in chat and in the TPort windows.
The remaining methods are for the location source.
When the teleport occurs, TPort will catch the event and assigns the new location to the location source via the setLocation method.
But if you use a live location (adaptable) you have to store a way in the source to get the live location.

After implementing all methods your class should look something like this:

```java
public class ExampleLocationSource implements LocationSource {
    
    // implementation for a live location (adaptable)
    private final Object liveLocation;
    
    // implementation for a static location
    private Location location;
    
    public ExampleLocationSource(Object liveLocation) {
        // Constructor to store your live location.
        // This object can be anything, for a TPort teleportation, the TPort object is stored, so when the owner of the TPort changed the location, this reference will also update.
        this.liveLocation = liveLocation;
    }
    
    @Nullable
    @Override
    public Location getLocation(Player player) {
        // live location implementation
        // Use the correct way to get the location
        return (Location) liveLocation;
        
        // static location implementation
        //return location
    }
    
    @Override
    public void setLocation(Location location) {
        // If you use a custom live location (adaptable), this method can stay empty.
        // Otherwise, use this location
        this.location = location;
    }
    
    @Override
    public void teleportToLocation(Player player) {
        // If a custom teleport sequence is needed, put that here, otherwise use player.teleport(location).
        // Or use the teleport from TPort (this is boat/horse friendly and works with the delay/restriction of TPort):
        //      TPEManager.requestTeleportPlayer(player, getLocation(player),
        //            () -> /*send message that teleport is succeeded*/,
        //            (player, delay, tickMessage, seconds, secondMessage) -> /*send message that teleport is requested*/);
        // Or use only the boat/horse friendly teleport:
        //      TPEManager.teleportPlayer(player, getLocation(player));
        // It is also possible to handle the teleportation by another command
        //      Bukkit.dispatchCommand(player, "tp " + player + " " + getLocation(player));
        
        // Use the getLocation(player) for the teleport.
    }
    
    @Override
    public void notSafeToTeleport(Player player) {
        // Send message that it is not safe to teleport to the location
    }
    
    @Nullable
    @Override
    public InventoryModel getInventoryModel() {
        // If you don't have texture pack support just return null, the default texture will be used
        // If you do have a resource pack, create a new InventoryModel object:
        // new InventoryModel(material, custom model data)
        return null;
    }
    
    @Nullable
    @Override
    public String getType() {
        // return the type of the location source, return null if you don't have any subtypes of sources.
        return "example";
    }
    
    @Override
    public boolean getSafetyCheckState(Player player) {
        // Return the default state of the safety check state, by default use this:
        // return TPORT_BACK.getState(player);
        return TPORT_BACK.getState(player);
    }
    
    @Override
    public String asString() {
        // This should return the String value of the location source.
        // Examples: the biome, player name, TPort name, feature name.
        return "example";
    }
    
    @Nonnull
    @Override
    public Message toMessage(String color, String varColor) {
        // This should return the Message object. This object is the actual text used for the chat and TPort windows.
        // The default implementation could look like this:
        //
        // @Nonnull
        // @Override
        // Message toMessage(String color, String varColor) {
        //     return new Message(new TextComponent(asString(), varColor));
        // }
        //
        // But more customisation is possible.
        return new Message(new TextComponent(asString(), varColor));
    }
    
    @Nullable
    @Override
    public HoverEvent getHoverEvent() {
        // The hover event is the message wrapped as a HoverEvent object that shows when you hover your mouse over the location source in chat.
        return null;
    }
    
    @Nullable
    @Override
    public ClickEvent getClickEvent() {
        // The click event is the event that runs when a player clicks the location source in chat.
        return null;
    }
    
    @Nullable
    @Override
    public String getInsertion() {
        // The insertion is the string that copies to the chat of the player when they shift click the location source.
        // The default implementation could look like this:
        //
        // @Nullable
        // @Override
        // String getInsertion() {
        //     return asString();
        // }
        //
        // But more customisation is possible.
        return asString();
    }
}

```

If you are using custom textures, you can TPort what InventoryModel it should use to display in the filter window.
Each plugin can set their own icon as filter icon.
To register your InventoryModel, use this line:
``` java
TeleportHistory.registerPluginFilterModel(this, YOUR_INVENTORY_MODEL);
```
