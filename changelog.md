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
