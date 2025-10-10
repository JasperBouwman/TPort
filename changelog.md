# Version 1.21.6

### Adapters

The Adaptive adapter should be working again.  
Created an adapter for 1.21.9 / 1.21.10

### Waypoints

Added the beta for the waypoint.
Since the release of the locator bar, TPorts now will show on the locator bar as waypoints.
To configure this you can use the `/tport waypoints` command.

For now the only argument is `type [type]`. You can select what TPorts will show on your locator bar, or turn it off.
The available types are:
- All  
Showing all TPorts that show on the locator bar
- CanTP  
Showing only TPorts you can teleport to
- None  
Showing no TPorts
- Own  
Showing only own TPorts
- Public  
Showing all publicly available TPorts 
- PublicTP  
Showing all PublicTP TPorts

You can customize the TPort on the locator bar by using `/tport edit <TPort name> waypoint`.  
Use the command `/tport edit <TPort name> waypoint show [state]` to show/hide the TPort on the locator bar.
Use the command `/tport edit <TPort name> waypoint color [chat color|hex color]` to edit the icon color.
In the next update you can also change the icon that shows on the locator bar.



---

# Version 1.21.5

### Adapter 1.21.6

Created the adapter for Minecraft/Bukkit 1.21.6

### Version command update

The version command (`/tport version`) now displays more information,
like the server/bukkit version and loaded TPort adapter.
This should help with problem-solving.

### Copy the color theme of a player

Using `/tport colorTheme copy <player>` you can copy the color theme of the selected player.
This is also available in the color theme inventory (accessible via `/tport settings` -> color theme -> right click the last button)

### PlayerResourcePackStatusEvent

TPort now listens to the PlayerResourcePackStatusEvent event.
When the player declines or discards the download of the resource pack, 
the state will automatically turn off.
When there is an error during download/reload, the resource pack state will automatically turn off.
When the resource pack is working correctly, the player will get a success message.

### GeneratePermFile
This is a new command (`/tport generatePermFile [filter none] [file type]`).
With this command you can create a file containing all command with their permission(s).

### DisplayDisabledFeatures
This is a new feature that allows the server admin to have more control over what is shown in windows.
When this feature is enabled (default) every button is always shown.
When this feature is disabled, the button will only show if:
- the feature is enabled
- and
- the player has permission

Each button has their own permission:

In the settings:
- reload: tport.showInSettings.reload
- Features: tport.showInSettings.features
- Tag: tport.showInSettings.biomeTP
- Backup: tport.showInSettings.backup
- RemovePlayer: tport.showInSettings.removePlayer
- Redirect: tport.showInSettings.redirect
- Metrics: tport.showInSettings.metrics
- Cooldown: tport.showInSettings.cooldown
- Dynmap: tport.showInSettings.dynmap
- BlueMap: tport.showInSettings.blueMap
- Public: tport.showInSettings.public
- Adapter: tport.showInSettings.adapter
- Delay: tport.showInSettings.delay
- Restriction: tport.showInSettings.restriction
- Texture Debug: tport.showInSettings.textureDebug

---

# Version 1.21.4

Fixed issue where teleporting to a world using History (with a World location source) would give an error showing that the world does not exist.

Changes where made in the FancyMessage JSON translator to reflect the new changes in Minecraft since 25w02a


### Advancements

Added advancements to TPort.
These advancements are visible in the advancement window of Minecraft self.
Using the `Advancement` feature you can enable/disable this feature.
There are currently 35 advancements to be earned, with 1 hidden easter egg.

To use the advancements you need the following plugin:
CrazyAdvancementAPI (https://www.spigotmc.org/resources/crazy-advancements-api.51741/)

### Language

The language settings window has been created.
In the settings window you can now use the language button to change your language,
and the language of the server.

### Adapters

The adapter for 1.21.4 & 1.21.3 contains a bug where FeatureTP is not working correctly.
This issue has been addressed in this update for adapter 1.21.4 & 1.21.3.

There is now also an adapter for 1.21.5.

---

# Version 1.21.3

## In this version:

### Created adapter for Minecraft 1.21.4


### Quick type in the keyboard got improved:

When left click, each new line in the sign will show as a new line
example:
- line 1: 'test a'
- line 2: 'test b'
- line 3: ''
- line 4: ''
- output:  
  'test a
  test b'

When right click, each new line in the sign will show as a space
example:
- line 1: 'test a'
- line 2: 'test b'
- line 3: ''
- line 4: ''
- output:  
  'test a test b'

When shift right click, each new line in the sign will show behind each other
example:
- line 1: 'test a'
- line 2: 'test b'
- line 3: ''
- line 4: ''
- output:  
  'test atest b'

### Color editor in the keyboard:
Shift+left removed all color from text

Click your drop key (+ shift) on the color editor to create a color fade. You can add colors to create the fade with.

### Offline/Online mode
On startup TPort will check the online/offline mode of the server. If this mode has changed since the installation of TPort, a warning will be displayed.
This warning is to inform the server admin that changing this mode is not supported.
Players will lose their settings/TPorts.

---

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

---

# Version 1.21.1

#### In this version:

History is still in beta. Only the history window is available. In here you can see your teleport history.

The adapters are working again for Paper servers.

---

# Version 1.21.0

#### In this version:

Added SafetyCheck to the Feature list.
If enabled (default),
players have the ability to turn of/off the safety check of a teleportation source
(for more information about SafetyCheck, see [here](https://github.com/JasperBouwman/TPort/blob/master/quickStart.md#safety-check-tport-safetycheck-source-state-)).
If the feature is disabled, the state of every safety check is disabled (nobody can use it).

Added adapter for 1.21

Added copper grates for the structure icon

---

# Version 1.20.7

#### In this version:

### Fixed for older versions

No longer getting the error on plugin load: java.lang.NoSuchFieldError: EXPLOSION.

Adaptive adapter now works as normal

Fixed an NMS error. If the lore of an item in a window was empty, the error did occur. This won't happen anymore

---

# Version 1.20.6

#### In this version:

### updated to Minecraft 1.20.5

With NBT tags getting removed, some changed in the code where made.
With this a new adapter is made: 1.20.5.
The adaptive adapter is not fully functional yet.
The resource pack _pack format_ is updated to 32.

The default particle of the animations where: `EXPLOSION_NORMAL`. Since 1.20.5 this does not exist anymore. This is now `EXPLOSION`
When using older versions, an error will occur. This is fixed in this version.

### Fixes some bugs with Search.

- Looped queries now work in the Search window (biome searcher).
- Minecraft tag list are now updated when searching for BiomePresets.
- Whitelist and Blacklist biome presets are now used correctly.

### Language files are now correctly loaded on platforms other than Windows.

### CompanionTP

Teleporting with a boat now won't change your chest boat into a regular boat.  
Added CompanionTP as a feature. With this you can enable/disable that boats, horses and leashed entities will teleport with you.
To change this setting: ``/tport features CompanionTP state <state>``

### Added the History beta feature
This keeps track on every teleport a player makes.
Depending on the final form of History, this will fully replace the `/tport back` system.
You can enable/disable History via the Features command (`/tport features history state <state>`)

How History works:  
For every teleport a player makes, TPort will check the cause of the teleport.
If the cause of the teleport comes from a plugin, TPort will look up which plugin issued
the teleport. Every data is stored in a History Element. This looks like this:
```
location before teleport -> location after teleport.
Cause: teleport cause, Source: plugin
```
Example of an ender pearl:
```
{world, x=0, y=0, z=0} -> {world, x=1, y=1, z=1}.
Cause: ENDER_PEARL, Source: null
```
Or teleporting to a TPort:
```
{world, x=0, y=0, z=0} -> home.
Cause: PLUGIN, Source: TPort
```

There are a few commands to use the history:

* `/tport history`  
  This is the command to see your history
* `/tport history back [filter]`  
  This is the command to teleport back. It uses the _location before teleport_ of the last history element
* `/tport history last`  
  This is the command to teleport back. It uses the _location after teleport_ of the last history element
* `/tport history secondLast`  
  This is the command to teleport back. It uses the _location after teleport_ of the second last history element
* `/tport history clear`  
  This command is used to clear your history
* `/tport history size [size]`  
  This command is used to edit the size of the history

### The icons for WorldTP have been renamed:
- overworld -> world_tp_overworld![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_overworld.png?raw=true "Over world")
- nether -> world_tp_nether![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_nether.png?raw=true "Nether")
- the_end -> world_tp_the_end![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_the_end.png?raw=true "The End")
- other_environments -> world_tp_other_environments![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_other_environments.png?raw=true "Other environments")

### BlueMap:
TPort BlueMap states are now saved. In the previous version the show state was not saved.  
Added icon support. When BlueMap support is enabled,
TPort will create the `blueMapImages` folder inside the TPort folder.
Every `png` and `jpg` will be added to the assets storage of BlueMap.
Only these images are usable for TPort.
The images will be scaled to 32x32 resolution.
To select an icon for TPort use this command `/tport edit <TPort name> blueMap icon [icon name]`.
Or edit the icons via the QuickEdit (show state: <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_bluemap_show_on.png?raw=true" title="BlueMap show state on" width="32"/><img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_bluemap_show_off.png?raw=true" title="BlueMap show state off" width="32"/><img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_bluemap_show_grayed.png?raw=true" title="BlueMap not enabled" width="32"/>, icon: ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_bluemap_icon.png?raw=true "BlueMap icon")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_bluemap_icon_grayed.png?raw=true "BlueMap not enabled"))

### Added four new redirects:

- TPA_PLTP_TP  
  Redirect `/tpa <player>` to `/tport pltp tp <player>`
- TPAccept_Requests_accept  
  Redirect `/tpaccept [player]` to `/tport requests accept [player]`
- TPDeny_Requests_reject  
  Redirect `/tpdeny [player]` to `/tport requests reject [player]`
- TPRevoke_Requests_revoke  
  Redirect `/tprevoke` to `/tport requests revoke`
- TPRandom_BiomeTP_random  
  Redirect `/randomtp` and `/tprandom` to `/tport biomeTP random`

default state for all is `false`.

---

# Version 1.20.5

### In this version:

TPort does not crash anymore on server load.

`/tport help` is now a bit smarter.
Previously when searches for `/tport edit home item` it would not find any commands matching. Now it will ignore variables.
Now when searching `/tport edit home item` it will find this command.  
If you are struggling with a command, you can just add `tport help` before your command.  
Also added back propagation for searches. When searching for `/tport biomeTP accuracy default` these command will be found:
- /tport biomeTP
- /tport biomeTP accuracy
- /tport biomeTP accuracy <size>

This should help you in showing more relatable matches

---

# Version 1.20.4

updated the resource pack format to 22

Finished the Logging setting  
Auto backups are working again

Added Item Selection in the Quick Edit menu  
Added whitelist clone to the Quick Edit menu

Full update for Search
- Added World search type. Search for TPorts that are in the given world
- Added OwnedTPorts search type. Search for players owned TPorts
- Search can now be run with inventories, use `/tport search` or find it in the settings screen
- Added new search modes: not equals, ends with, not contains

Added full adapter support
- server admins now can select the best adapter for their server
- The default is set to automatic, this chooses based upon the server version the best adapter
- The end goal is that each Minecraft version has their own adapter
- The adaptive adapter should mainly be used if your version does not exist (example: using TPort for a new Minecraft version that TPort does not yet have a adapter for)
- If the selected adapter does not load the adaptive is used as a backup
- command: /tport adapter [adapter]
- permissions: tport.adapter or tport.admin for setting the adapter
- selecting the adapter can also be done via the setting menu

Added beta support for BlueMap
- Works the same as the Dynmap support
- to enable: `/tport features blueMap state true`, and make sure that BlueMap is successfully loaded into your server
- For now only a toggle for showing state on the map is available using: `/tport edit <TPort name> blueMap show [state]  `

Added a safety check to PLTP
- `/tport PLTP tp <player> [safetyCheck]  `
- when teleport needs to be requested, the check is preformed after the request is accepted  
  Another safety check is preformed for teleporting to a TPort
- the owner of the TPort can move the TPort to a dangerous location in between the request and accepting

More features for the Keyboard:
- you can now delete a color
- in the color selector, you can select a color from Minecraft (Chat Colors & Dye Colors)  
  This built-in color selector is also used for creating your own color theme

Renamed TPort private state 'prion' to PRIVATE_ONLINE

New icons for the transfer system. Check them out in: settings, transfer window offered/offers filter and in the Quick Edit window

Added a command to fully stop logging a TPort
- `/tport log delete <TPort name>`
- This removes all players from the logged players list, and sets the default log mode to NONE

Added LookTP to the features list. Default value is on  
Added EnsureUniqueUUID to the features list. Default value is off.
- This feature was already in TPort, but its now changeable.
- When enabled, TPort will look at all existing TPorts and check if the new UUID for the new TPort is truly unique.
- The changes of it randomly creating a UUID that is already in use is animatronic low.

When Permissions are disabled, they won't show anymore in the help page (`/tport help <command>`)

Added `/tport language repair <language> [repair with] [dump]`.  
When dump is set to true, it prints all repaired ID's in the console

`/tport back <safetyCheck>` now uses the correct safetyCheck permissions.
- old: (TPort.Back and TPort.safetyCheck.TPORT_PUBLIC) or TPort.basic
- new: (TPort.Back and TPort.safetyCheck.TPORT_BACK) or TPort.basic

fixed some minor bugs

---

# Changelog 1.20.3 update:

* Fixed for Minecraft/Bukkit 1.20.4
*
* Light mode textures now available for x16 and x32
* Quick Edit Type names are now translatable via this key: tport.quickEditInventories.quickEditType.QUICK_EDIT_NAME.displayName
* Quick Edit Types now have a description
*
* Created the 'setting' Items Debug, it shows all TPort items, made for texture creators
*
* Created the Cooldown setting inside the Settings GUI
*
* Added filter to the PublicTP GUI, this is a toggle to show all Public TPorts or only your own Public TPorts
*
* FeatureTP now filters features that do not generate in the world (just like BiomeTP does)
* fixed FeatureTP, FeatureTP can search for strongholds again
*
* TPort display items now hides all Item Flags (attributes (flight duration, ect), enchantments, armor trim, ect)
*
* Updated the 'K' texture of the keyboard
*
* fixed some minor bugs


---

# Changelog  1.20.2 update:

* added /tport look [type]
* With this command players can teleport the block/entity/fluid that they are looking at.
* For this a new cooldown type is created: LookTP
*
* The TPort Keyboard got updated:
* added:
*  - Quick Type. This opens a sign where players can type with their own keyboard and paste from their clipboard
*  - Title formatting. This is a toggle where players can preview their typed text with colors or as plain text
*      - example formatting on (imagine with colors): GREEN BLUE, example formatting off: &aGREEN &9BLUE
*      - note that typing colors in this example is not possible, but when formatting is off you see the color codes
*  - Cursor. The keyboard now has a cursor for easy typing in between text. It is shown as a red |
*      - when formatting is on the cursor jumps over the color
*  - Color edit. When clicking (left/right) on the color button you can now choose if you want to insert a color, or edit the current color
*      - when the cursor is on a color, it edits this color
*      - when the cursor is on text, it edits the previous color (since this color is used for the selected text)
*  - added 'delete' to the backspace button
*
* added to the Quick Edit menu:
*  - Offer/Revoke. This is from the transfer function set. Accepting/Rejecting is in the settings menu
* added to the settings menu:
*  - Restore
*  - Home
*  - Transfer
*  - Features
*  - Redirects
*  - Tags
*  - PLTP
*  - Resource Pack
*  - PublicTP

---

# Changelog 1.20.1 update:

* Fixed Unsupported API version
* Added Cherry Log to the biome Cherry Grove in BiomeTP
* Added Suspicious Gravel to the feature in FeatureTP
* Added icons for the new tag lists in BiomeTP - Presets
* TPort Keyboard has now a dynamic title length. Inputted colors work better in the title
* Added new line button icon 'char_newline.png'
*
* TPort should now work on multiple versions (1.20.1, 1.20, 1.19.4, 1.18.2).
* Note: FeatureTP only works on 1.20.1 or 1.20

---

# Changelog 1.20 update:

* added '/tport restore', this allows you to restore the last removed TPort (starting from the last reload/startup)
* added '/tport public reset' this allows you to make all TPort not public anymore
*
* '/tport edit <TPort name> item' now follows the TPortTakesItem feature. This allowed players to duplicate items
*
* added an icon for the placeholder with using the move quick edit to move a TPort
*
* removed permissions from '/tport'
* added permissions to '/tport world' -> 'TPort.world.tp'
*
* fixed '/tport delay get <player>' when delay is set to be managed by permissions
*
* added toggle for feature (in /tport feature):
* - InterdimensionalTeleporting: When disabled players can not teleport between worlds via TPort (This does not effect worldTP)
* - DeathTP: When enabled players can teleport to their death location, when disabled TPort stops listening for deaths for BackTP
*
* Tports on Dynmap now have as default the TPort logo
* TPorts on Dynmap use the same description in all GUI's/chat, the language is the same as the server language
* Added colors to the labels of the TPorts shown in Dynmap. To edit the colors use: '/tport dynmap colors <color theme>'
* When Dynmap support is successfully enabled you can now click control+(your drop key) to search the TPort/Player on Dynmap. It used the command '/tport dynmap search <player> [TPort]'
*
* Give description system of Tport a change. Colors now should work as intended. You can use 3 ways to add colors
* - #123456
* - &8
* - $2$20$200
* Example 'This is some text, #ff0000This text is red, &9This text is blue, $0$255$0This text is green'
*
* added GUI for:
* - settings
* Not all settings are working, ony working now are: version, reload, backup, color theme, remove player
* new inventories for:
* - ColorTheme
* - backup
* - Remove Player
* - adding/removing players in the whitelist for PLTP, to access this use your drop key on your own head in your TPort GUI
* - QuickEdits is now its own GUI instead of looping through all the edits. From here you can select what Quick Edit you want on your right click action in your
* own TPort GUI. With left click in the inventory you can use that quick edit. Some Quick Edits have their own sub GUI and others don't.
* new inventories for:
* - Tag selection
* - TPort whitelist selection
* - Remove TPort
* - Edit TPort location
* - TPort private state
* - TPort range
* - Dynmap icon
* - TPort log
* - read log
* - player selection
* - name (uses the TPort Keyboard)
* - description (uses the TPort Keyboard)
* - PublicTP is now added as a toggle (/tport public add <TPort name> or /tport public remove <TPort name>)

---

# Changelog 1.20 update:

* added '/tport restore', this allows you to restore the last removed TPort (starting from the last reload/startup)
* added '/tport public reset' this allows you to make all TPort not public anymore
*
* '/tport edit <TPort name> item' now follows the TPortTakesItem feature. This allowed players to duplicate items
*
* added an icon for the placeholder with using the move quick edit to move a TPort
*
* removed permissions from '/tport'
* added permissions to '/tport world' -> 'TPort.world.tp'
*
* fixed '/tport delay get <player>' when delay is set to be managed by permissions
*
* added toggle for feature (in /tport feature):
* - InterdimensionalTeleporting: When disabled players can not teleport between worlds via TPort (This does not effect worldTP)
* - DeathTP: When enabled players can teleport to their death location, when disabled TPort stops listening for deaths for BackTP
*
* Tports on Dynmap now have as default the TPort logo
* TPorts on Dynmap use the same description in all GUI's/chat, the language is the same as the server language
* Added colors to the labels of the TPorts shown in Dynmap. To edit the colors use: '/tport dynmap colors <color theme>'
* When Dynmap support is successfully enabled you can now click control+(your drop key) to search the TPort/Player on Dynmap. It used the command '/tport dynmap search <player> [TPort]'
*
* Give description system of Tport a change. Colors now should work as intended. You can use 3 ways to add colors
* - #123456
* - &8
* - $2$20$200
* Example 'This is some text, #ff0000This text is red, &9This text is blue, $0$255$0This text is green'
*
* added GUI for:
* - settings
* Not all settings are working, ony working now are: version, reload, backup, color theme, remove player
* new inventories for:
* - ColorTheme
* - backup
* - Remove Player
* - adding/removing players in the whitelist for PLTP, to access this use your drop key on your own head in your TPort GUI
* - QuickEdits is now its own GUI instead of looping through all the edits. From here you can select what Quick Edit you want on your right click action in your
* own TPort GUI. With left click in the inventory you can use that quick edit. Some Quick Edits have their own sub GUI and others don't.
* new inventories for:
* - Tag selection
* - TPort whitelist selection
* - Remove TPort
* - Edit TPort location
* - TPort private state
* - TPort range
* - Dynmap icon
* - TPort log
* - read log
* - player selection
* - name (uses the TPort Keyboard)
* - description (uses the TPort Keyboard)
* - PublicTP is now added as a toggle (/tport public add <TPort name> or /tport public remove <TPort name>)

---

# Changelog 1.19.8

Fixed for Minecraft 1.19.4

---

# Changelog  1.19.7 update:

* default state of permissions is now set to false, so that new users who are not using permissions and are all not OP can still use TPort
* default state of backup is now set to true
*
* You can now preview players with the command '/tport preview <player> [TPort name]'
* To edit these PLTP preview settings use '/tport PLTP preview [state]'
*
* TPort now automatically checks if its up-to-date
*
* Added to the feature settings:
*  - TPortTakesItem
* This controls if when adding a TPort it takes the item out of your inventory or not. When set to true it takes the item, when set to false it does not take the item
*
* added a max of 50 characters to a TPort name (applies only to newly added TPorts)
*
* updated the pack format for the resource packs

---

# Changelog 1.19.6 update:

* fixed FeatureTP

---

# Changelog 1.19.5 update:

* fixed TPort from crashing at startup
* fixed BiomeTP
* FeatureTP is disabled for this update (next update it should be working again)

---

# Changelog 1.19.4 update:

* Fixed messages for servers running Paper
*
* Fixed command description for incomplete command given by the command /tport help tport
* these missing command descriptions where due to the command not being complete, example: '/tport edit' alone is not valid therefor had no description
*
* Fixed issue in the Main TPort GUI where the button for HomeTP was not correct when the player has no home set

---

# Changelog 1.19.3 update:

* added a confirmation when using /tport resourcePack resolution <resolution>
*
* fixed command /tport public open <TPort name> [safetyCheck]
* fixed error with command /tport open
* fixed command redirection of /locate <biome|structure>
*
* the /tport setHome command has moved into the /tport home command:
*  /tport home                             teleport to your home
*  /tport home <safetyCheck>               teleport to your home with selected safety check
*  /tport home get                         gives the current home
*  /tport home set <player> <TPort name>   set the selected TPort as your home
*
* Dynmap TPort icons now show more information about the TPort

---

# Changelog 1.19.2 update:

* changed permission from '/tport edit <tport name> add <tag>' from TPort.edit.tag.add.<tag> to TPort.tags.type.<tag>
* removed permissions: TPort.delay.get.own and TPort.restriction.get.own. A player should always be able to get their delay/restriction
* removed permissions: TPort.particleAnimation.old.enable.get and TPort.particleAnimation.new.enable.get
*
* added the implementation for the permission of the command /tport safetyCheck check
*
* added custom model data IDs to buttons in TPort GUI's. These work with the TPort Resource Pack.
* use /tport resourcePack state [state] to enable/disable the resource pack. When set to enabled, TPort prompts the sender to download the resource pack
* Commands:
*  /tport resourcePack
*  /tport resourcePack state [state]
*  /tport resourcePack resolution [resolution]
* These resource packs are freely downloadable on the github repository
*
* set all permission detection after the syntax detection
*
* added /tport world
* added a GUI for the command /tport world [world]
* This GUI can be opened from the command /tport world, or from the Main GUI
*
* if a player opened a chest while previewing a TPort, the inventory opens. When the player started the preview near that chest the inventory stayed opened.
* Now the inventory won't even open
*
* with shift+left click you can invert the safety check for this teleport
* when using /tport open with one of your own TPort, it now used the TPORT_OWN safety check source
*
* added a home button on the Main GUI
* added '/tport setHome', this allows you to check what your home TPort is set to
*
* fixed /tport log add <tport name> <player[:LogMode]...>
* added error check for a non-existing log mode, previous it defaulted to ALL
*
*
* changed language key: 'tport.events.inventoryClick.onInventoryClick.clearSelectedBiomes' to 'tport.tportInventories.openBiomeTP.clearSelected.succeeded'

---

# Changelog 1.19.1

This update fixes messages for Minecraft 1.19.2

---

# Changelog 1.19 update:

* fixed permissions of the commands:
*  /tport language repair <language> [repair with]     inverted check for permissions
*  /tport language server [language]                   did not check for permissions
*
* added a TPort preview. With this you can preview a tport without less permission of the Tport owner.
*  /tport preview <player> <TPort name>
*  /tport features preview state [state]
*  /tport edit <TPort name> preview [state]
*  added the quick edit for the preview settings in your TPort GUI
*  in a TPort gui you can preview the TPort by pressing your drop key
*
* fixed /tport backup load <name>
* fixed some inconsistencies
*
* created a Discord server for TPort
*
* /tport delay permissions [state] is now /tport delay handler [state]
* /tport restriction permissions [state] is now /tport restriction handler [state]
*
* added /tport setHome. This allows you to see what your home is set to
*
* Fixed item duplication when the server was reloading but a player had still a TPort GUI open. After the reload the player could take all the items out of the inventory.
* Now all TPort inventories will be closed on reload

---

# Changelog 1.18.2 update:

* TPort now supports translations. A server can set a default language, every player can follow the server of select their own one.
* When a server has not installed a language a player wants, they can set their language to 'custom'.
* This way TPort does not translate any messages (for them), but lets their Minecraft do it. The player must use a TPort Language Resource Pack,
* this works the same as selecting a texture/resource pack. Now TPort is translatable in any language anyone wants (not restricted by the server).
* commands:
*  /tport language server [language]
*  /tport language get
*  /tport language set custom
*  /tport language set server
*  /tport language set <server language>
*  /tport language repair <language> [repair with]
*  /tport language test <id>
*
* Changed FeatureTP (now works with Minecraft 1.18.2)
*  /tport featureTP search <feature> [mode] -> /tport featureTP search [mode] <feature...>
*  You can search for multiple features at the same time
*  The Taglists from Minecraft can be used
*
* Changed BiomeTP
*  When the TPort version supports the Minecraft version (no legacy support):
*      - tab completes only shows the biomes that are generated in the world the player is in
*      - BiomeTP GUI only shows the biomes that are generated in the world the player is in
*      - The Taglists from Minecraft are added to the Presets
*  When an error occurs BiomeTP Legacy support is turned on
*
* Removed converting functions. If you are upgrading to 1.18.2 (or higher) from a version lower than 1.15.3 you need to install 1.16.2 first.
* you could suffer from data loss if you upgrade to 1.18.2 (or higher) from a version lower than 1.15.3
*
* Improved some GUI's. Middle Click is now replaced by Shift Right Click. Only Creative players could use the Middle Click Button.
* In the future there will be more functions bind to other mouse clicks (like Shift Left Click)
*
* removed bug when you create a TPort you will get the error if you don't have permission to add a description when you aren't adding a description
* improved stability
* when a TPort is transferred, the old owner will now be automatically added to the whitelist
* fixed /tport edit <TPort name> whitelist add <player names...>
*
* changed /tport safetyCheck
*  new commands:
*   /tport safetyCheck
*   /tport safetyCheck <source> [state]
*   /tport safetyCheck check
*   /tport open <player> [TPort name] [safetyCheck]
*   /tport own [safetyCheck]
*   /tport home [safetyCheck]
*   /tport public open <TPort name> [safetyCheck]
* The source stands for the source of reason for teleportation. open, own, home and public is their own source.
* This way you can set the default state for each command
* '/tport safetyCheck check' is to check if the location you are at is considered safe by the safety checker
*
* added/changed TPort private settings:
*  OFF -> OPEN. better name for its function
*  ON -> PRIVATE. better name for its function
*  + CONSENT_PRIVATE. when the owner is online players must get consent of owner, when the owner is offline the TPort goes to PRIVATE
*  + CONSENT_CLOSE. when the owner is online players in the whitelist can ask for consent to teleport, when the owner is offline the TPort closes
* for CONSENT settings some commands where added, these work the same as the ones for PLTP:
*  /tport requests
*  /tport requests accept [player...]
*  /tport requests reject [player...]
*  /tport requests revoke
* '/tport requests' now handles all the teleportation requests, from PLTP and normal TPort teleportation.
*   As of now you only can have only one request (to a player with PLTP or to a TPort)
*
* You are now able to log yourself.
*  To start: /tport log add <TPort name> YourName:[ALL|ONLINE]
*  To stop:  /tport log add <TPort name> YourName:[NONE|OFFLINE]
*            /tport log remove <TPort name> YourName
*
* changed '/tport redirect <redirect> [state]' to '/tport redirect [redirect] [state]'
*
* added /tport features [feature] state [state]
* This command allows you to enable/disable features without the use of permissions
* /tport metrics enable [state]     -> /tport features Metrics state [state]
* /tport permissions enable [state] -> /tport features Permissions state [state]
* /tport dynmap enable [state]      -> /tport features Dynmap state [state]
*
* added /tport edit <tport name> whitelist visibility [state]
* This command is used to control the visibility of the whitelist of a TPort in chat and GUI
*
* When a TPort is selected to move in your TPort GUI, dummy items will appear in the GUI at the empty slots to move that TPort to an empty slot
*
* fixed usage of permissions in the cooldown configuration. The permission now support linking other cooldown.
*
* added the tp restriction 'interactRestriction'. Players with this restriction can't interact with the world while a TP is pending
* added the tp restriction 'doSneakRestriction'. Players with this restriction have to sneak at least once while a TP is pending (This restriction is more of a concept for creating your own)
*
* TPort now should start when it's not run from a Spigot server

---

# Changelog 1.18.0

This version does not have anything new

---

# Changelog 1.17.0

This version is only created so that the messages in the chat will appear again.

This verion is only a beta (FeatureTP is disabled, and maybe some other features won't work).

Its recommended to backup the TPort folder in the plugins folder.

---

# Changelog  1.16.2 update:

Fixed that TPort wont start on servers that aren't Spigot

---

# Changelog 1.16.1

It is finally here: 1.16.1 version with all new added features



Keys features:

- Dynmap support

- added metrics (all anonymous stats)

- /tport search <type>

- /tport tag (to give TPorts tags for easier sorting/searching)

- /tport safetyCheck (preforms a safety check (check if you can teleport to it) before you teleport)

- /tport mainLayout

- colorTheme support rgb colors (hex notation)

- featureTP search for village types (Village_Desert, Village_Plains, Village_Savanna, Village_Snowy, Village_Taiga)

full changelog:

### Changelog 1.16.1 update:

* added:
*  /tport log notify
*  /tport search <type>
*  /tport edit <TPort name> tag add <tag>
*  /tport edit <TPort name> tag remove <tag>
*  /tport tag create <tag> <permission>
*  /tport tag delete <tag>
*  /tport tag list
*  /tport tag reset
*  /tport sort [sorter]
*  /tport safetyCheck [default state]
*  /tport open <player> <TPort name> [safetyCheck]
*  /tport own <TPort name> [safetyCheck]
*  /tport back [safetyCheck]
*  /tport permissions enabled [state]
*  /tport mainLayout players [state]
*  /tport mainLayout TPorts [state]
*  /tport world <world>   (used to teleport to the spawn of the given world)
*  '/tport search' cooldown, default value is 10 seconds
* fixed:
*  tport back from '/tport biomeTP random'
* Teleporter items (old name compass) are now TPort rename friendly
* new buttons in the main TPort GUI to go to BiomeTP, FeatureTP and Public TPorts and to teleport back (/tport back)
* added the permission 'TPort.featureTP.open' to open the FeatureTP gui (/tport featureTP)
*
* players in your PLTP whitelist don't have to ask for consent anymore (when PLTP consent is set to true)
*
* added metrics (powered by bStats)
*  /tport metrics enable [state]
*  /tport metrics viewStats
*
* added Dynmap support.
* new commands:
*  /tport dynmap
*  /tport dynmap enable [state]
*  /tport dynmap show <player> <tport name>
*  /tport dynmap IP [IP]
*  /tport edit <TPort name> dynmap show [state]
*  /tport edit <TPort name> dynmap icon [icon]
*
* improved PLTP Offset BEHIND
*
* if a TPort is public (/tport public add <TPort name>) it can't be renamed to a name that contains as a Public TPort
*
* when a player gets offline, his location won't be stored to use for TPort range. When he is offline he is just out of range.
* fixed error when owner is in different dimension than TPort, when trying to measure the distance between owner and TPort
*
* updated '/tport teleporter create' command descriptions
*
* ColorTheme now supports HEX colors (#123456), to use '/tport colorTheme set <type> <hex color>'
* added some more color themes, if you have made any for yourself you should share them with me. If I like it I will add it to TPort
*
* updated the permissions system for the commands. There may be some bugs that the permission does not properly works for that command.
* If you found any leave a command at https://dev.bukkit.org/projects/tport
*
* you can now search for the different village types (Village_Desert, Village_Plains, Village_Savanna, Village_Snowy, Village_Taiga),
* it won't always gets the closest village when the FeatureTP mode is set to CLOSEST
* Updated feature display items in GUI
*
* added a SafetyCheck, to use this use the command '/tport safetyCheck [default state]'
* when true it will preform a safetyCheck before you teleport. It checks if the location does not exist of solid blocks, and the block you are standing on is not lava/fire
*
* the walkRestriction now cancels your TP request when you walk. Before it canceled your teleportation, but it forgot to remove the request.
* TPort restriction can now be used with permissions, permission: TPort.restriction.type.<restriction name>
* To enable use '/tport restriction permission true'
*
* TPort back form TPorts is now TPort rename friendly
*
* permission 'TPort.biomeTP.all' is replaced by 'TPort.biomeTP.*'

---

# Changelog 1.16

This update is purely for the usage of TPort for 1.16 with the same features of 1.15.4 (removed message error).



There are a lot of new features ready/being created right now, but that are not in this update yet.

I want the update to be done before I upload anything.

Why this took so long is that I didn't thought that all the new featueres would take so long to make.

---

# Changelog 1.15.4 update:

* added
*  /tport biomeTP searchTries [tries]
*  /tport log logSize [size]
*  /tport public listSize [size]
* changed/added
*  /tport featureTP
*  /tport featureTP search <feature> [mode]
*  /tport featureTP mode [mode]
*  shortcut from FeatureTP GUI to '/tport featureTP mode <mode>'
*  when using /tport biomeTP whitelist|blacklist <biome...> it will give you a confirmation that its searching before the lag-spike
* fixed minor bugs
* added /back to TPort Redirects
* updated the permission 'TPort.admin' from command '/tport redirect <redirect> [state]' to 'TPort.admin.redirect'

---

# Changelog 1.15.3 update:

* added featureTP modes:
*   modes available now: CLOSEST and RANDOM
*   when selected RANDOM it will find the feature at random
*   CLOSEST is the default
*   usage: /tport featureTP <feature> <mode>
* redid the biomeTP commands:
* /tport biomeTP
* /tport biomeTP whitelist <biome...> (teleport to a random biome that is in your list)
* /tport biomeTP blacklist <biome...> (teleport to a random biome that is not in your list)
* /tport biomeTP preset [preset]
* /tport biomeTP random
* made biomeTP safe for the nether and the end to teleport to
* added presets to biomeTP (a preset is a pre generated white/blacklist to use for biomeTP)
* added the cooldown if the biomeTP search failed
* the amount of biome searches can now be edited in the TPortConfig.yml file
* redid/improved TPort back
* added to TPort back:
*   - biomeTP
*   - featureTP
* updated/renamed the compass mechanism. Old compasses don't work anymore, I'm sorry. The new name is 'teleporter'
* to remove the old compass data of your compass item, just remove the lore of the item
*  new commands for the teleporter command
*   /tport teleporter create <type> [data...]
*   /tport teleporter remove
* added redirects
*  /tport redirect <redirect> [state]
*  a redirect can redirect commands, i.e. the Minecraft command '/tp <player>' to the TPort command '/tport PLTP tp <player>'
*  for now its just commands
* added to PLTP an editable offset
*  When the offset is set to 'BEHIND' the player who teleports to you will be teleported 1 meter behind you, instead of in you
*
* you can create you own biomeTP preset:
*   1. create your own plugin
*   2. add TPort to your libraries
*   3. in your onEnable() put:
*      'com.spaceman.tport.commands.tport.BiomeTP.BiomeTPPresets.
*        registerPreset("PresetName", Arrays.asList(Biome.YOUR_BIOMES), (true if whitelist, false if blacklist), Material.YOUR_DISPLAY_MATERIAL);'
*      it returns true if successfully registered, false when not

---

# Changelog hangelog 1.15.2 update:

fixed TPort back: when teleporting back to a TPort it did not store the correct location

---

# Changelog 1.15 update:

* /tport edit <TPort> move <slot> can now also swap TPorts, in stead of only moving to an empty slot,
*  when giving a TPort name as slot it swaps (so is now: /tport edit <TPort name> move <TPort name|slot>)
* tab list of '/tport back' shows back location
* you need to hold a compass to use the command /tport compass to turn it into a TPort Compass (no free items)
* minor chat improvements
* removed name duplication bug with renaming TPorts (did not check if name is already in use)
* rename TPort lore to TPort description, '/tport edit <TPort name> lore' is now '/tport edit <TPort name> description'
* added:
*  /tport colorTheme
*  /tport colorTheme set <theme>
*  /tport colorTheme set <type> <color>
*  /tport colorTheme get <type>
*  /tport home
*  /tport setHome <player> <TPort name>
*  /tport edit <TPort name> range [range] (you have to be in your set range of that TPort so other players can teleport to it)
*  /tport edit <TPort name> description get
*  /tport edit <TPort name> whitelist clone <TPort name>
*  public TPorts (right-click to select a Public TPort to swap)
*  /tport public
*  /tport public open <TPort name|page>
*  /tport public move <TPort name> <slot|TPort name>
*  /tport public add <TPort name>
*  /tport public remove <TPort name>
*  /tport public list [own|all]
* added these commands (transfer) to give someone one of your TPorts
*  /tport transfer offer <player> <TPort name>
*  /tport transfer revoke <TPort name>
*  /tport transfer accept <player> <TPort name>
*  /tport transfer reject <player> <TPort name>
*  /tport transfer list
*  /tport version
*  /tport log read <TPort name> [player]
*  /tport log TimeZone [TimeZone]
*  /tport log timeFormat [format...]
*  /tport log clear <TPort name...>
*  /tport log logData [TPort name] [player]
*  /tport log add <TPort name> <player[:LogMode]...>
*  /tport log remove <TPort name> <player...>
*  /tport log default <TPort name> [default LogMode]
*  /tport log notify <TPort name> [state]
*  /tport backup save <name>
*  /tport backup load <name>
*  /tport backup auto [state|count]
*  /tport PLTP state
*  /tport PLTP state <state>
*  /tport PLTP consent
*  /tport PLTP consent <state>
*  /tport PLTP accept [player...]
*  /tport PLTP reject <player>
*  /tport PLTP revoke <player>
* added Particle animations, the new is for the new location (where you are going), old is for the old location (where you came from)
*  /tport particleAnimation new set <particleAnimation> [data...]
*  /tport particleAnimation new edit <data...>
*  /tport particleAnimation new test
*  /tport particleAnimation new enable [state]
*  /tport particleAnimation old set <particleAnimation> [data...]
*  /tport particleAnimation old edit <data...>
*  /tport particleAnimation old test
*  /tport particleAnimation old enable [state]
* added a customizable delay to your teleportation
*  /tport delay permission [state]
*  /tport delay set <player> <delay> (works only when '/tport delay permission' is set to false)
*  /tport delay get [player]
* added a teleport restriction to your teleportation, these are customizable. During your delay time
* the restriction is started, when you fail your restriction you wont be teleported (now available: none and walking)
*  /tport restriction permission [state]
*  /tport restriction set <player> <type>
*  /tport restriction get [player]
*  /tport cancel (to cancel your teleport request during the delay time)
* removed /tport PLTP <on|off>, is now /tport PLTP state [state]
* While a TPort is offered you can't edit it, remove it, make it public and make it not public
*  TPort range:
*      Owner must be in range to teleport to.
*      Private statement is also active.
*      When set to 0 its turned off.
*      When owner is offline the last known location is used
* tport lore supports colors, use char & and a color code to add colors
* improved /tport help
* improved Quick Edit in TPort GUI. Middle-click to change edit function (if you have remapped your middle-click button to something else, use that!),
*      right-click to edit. Already in Quick Edit: Private statement (on, off, online, prion) and Range (0, 50, 100), Swap, Log (ONLINE, OFFLINE, ALL, NONE).
*      Only works in you are in your own TPort GUI, can't edit others TPorts
* add private statement online whitelist (PRION)
*  like online, but when your are offline players in your TPort whitelist can teleport
* in some cases TPorts are rename friendly, friendly at:
*      /tport home
*      public TPorts
*  not friendly at:
*      TPort Compass, why not? When you look at your compass it shows the name. And its not handy to change all the made compasses the server has
* TPort names now can't be a number, but it can contain a number like 'TPort3'
* added more options to the back buttons
* removed permissions for /tport remove <TPort>, you should always be able to remove a TPort
* removed PermissionStrip, found out that Bukkit already has this. Use the '*' as 'everything' wildcard
* edited a lot of permissions, check 'permissions.txt' in the plugin folder to see all permissions
* added the permissions to the help command
* fixed item duplication bug where pressing 1 while hovering on an item in a TPort gui or BiomeTP/FeatureTP gui you got the item

---

# Changelog 1.14 update:

* add permissions
* changed the command structure (code update)
* changed the tabComplete (more efficient) (code update)
* changed the cooldown names
* fixed:
*  not showing the last TPort in TPort gui
*  feature finder does not break on update anymore (for now)
*  fixed for 1.14
* /tport PLTP tp <player>
* /tport compass [player] [TPort name] -> /tport compass <type> [data]

to do:
fix /tport help -> it does not open the book for you, it now just gives you the book
finish /tport log -> there is already a small sneak preview
finish /tport colorTheme
rewrite TPort almost complete... more information about this to come

---

# Changelog 1.13.3 update:

* fixes /tport edit <TPort name> private
* added: /tport edit <TPort name> move <slot>

with /tport edit <TPort name> move <slot> you can give a TPort another place in your TPort gui

---

# Changelog 1.13.2

* fixed bugs:  
  when TPort private is set to 'online' players in whitelist could not teleport to TPort  
  and other minor bugs
* added cooldown
* removed secret function
* added /tport reload

added new file: TPortConfig.yml
in this file you can edit the cooldowns. the values are in milliseconds.
when you use another cooldown name as value those will be linked.
when you use the value 'permission' the permission 'TPort.<cooldown>.X' is used. the <cooldown> is the cooldown name and the X is the value, this way you can give each player custom cooldown times.

---

# Changelog 1.13.1

fixed an error while creating a new TPort with lore

---

# Changelog 1.13

* better text
* more TPorts available
* when clicking on own head in your TPort gui, you can now toggle PLTP
* /tport removePlayer <player name>
* /tport compass [player name] [TPort name]  
      works if compass is in ItemFrame
* /tport back
* /tport biomeTP [biome]
* /tport featureTP [feature]
* added tp back/biomeTP/featureTP to gui
* /tport own [TPort name]
* /tport whitelist <TPort name> <add:remove> <player name>     -->   /tport edit <TPort name> whitelist <add:remove> <players names...>
* /tport whitelist <TPort name> list                           -->   /tport edit <TPort name> whitelist list
* /tport edit <TPort name> private <true:false>                -->   /tport edit <TPort name> private <on:off:online>
* /tport edit                                                  -->   /tport PLTP
* /tport edit tp <on:off>                                      -->   /tport PLTP <on:off>
* /tport edit whitelist <list:add:remove>                      -->   /tport PLTP whitelist <list:add:remove>
* horse friendly
* zombie horse friendly
* skeleton horse friendly
* pig friendly
* llama friendly
* username change friendly
* whitelists can be cleared on plugin update
* removed /tport extra item
* better tabComplete
* improved /tport help  
     /tport help is now a book with clickable text to go to the right pages
* right-click on a TPort in your own TPort gui to cycle through the private states (on, off, online).
* added 'online' to the private statement. other players only can use that TPort when the owner is online
* right-click on your own head in your own TPort gui will cycle though some private states (on, off)
* item names will be saved, so when you remove a TPort the old name from when you added the TPort (only after the 1.13 version) will show

there may be some more features added/removed...
Will work on version 1.13 and 1.13.1. TPort is mainly tested on version 1.13.1

If i have anything missed, please tell me.
Do you have any questions feel free to ask me.
Did you find any bugs or strange behavior? tell me what happend(or didn't happen) and what you did, if there is an error in the log include it.

---

# Changelog 1.12.2

update:
1. the main gui for TPort is now dynamic to the amount of players in the list.
2. you don't have to relog when the plugin is added to the server
3. /tport help is now a book that will open for you. this book is more detailed of how TPort works with examples and all.

this version is tested on 1.12/1.12.1. but I think it works on 1.11 aswell.

---

# Changelog 1.12

this is an update that isn't required for the usage for this plugin, but it has some new features:

1: a new way to teleport to a location

usage: /tport open <playername> <tport name>



if you are familiar with this plugin you might know about /tport open <playername>, but now you can teleport to a location with ony using 1 command and not the inventory



2: better tab complete

example: /tport e

tab complete: edit, extra

before it was not based on the non complete sub-command, but now it looks for a non complete sub-command and creates a new list with suggestions that is based on your pre-written command

---
# Changelog 1.11.3

It will now send a message if you don't have an item in your main hand while you add lore to your item.

---

# Changelog 1.11.2

small change

---

# Changelog 1.11

minor bugfix, if the player to teleport to is offline when your in his gui the head will display "player not online". but when the player get online the head will check if player is online and you will still teleport to the selected player, even the head will say the player is offline.

---

First release

---
