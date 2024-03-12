# TPort

TPort is the plugin that adds a lot of teleportation functionality while focusing on visuals.
The main functionality is the location manager.

TPort has an official [Discord](https://discord.gg/tq5RTmSbHU "Discord") server, any type of question can be asked here.

A quick start can be found here: [QuickStart.md](https://github.com/JasperBouwman/TPort/blob/master/QuickStart.md)

### Location manager

A saved location is called 'TPort', the name comes from ***t***ele***port***. In the context of this plugin it is a saved location<br />
Any TPort has a display *icon* (an item), if set correctly it should give a clear indication of the type of location. This display icon can be set by the TPort owner.<br />
Each TPort can also have a description/tag to further explain the location.<br />
Players can save up to 24 TPorts. This amount can be edited using this permission: *TPort.add.[X]*, where X is the max.

You can make your TPort safe, so that random players can't mess with it.
Some safety measurements:

- Private<br />
  This has 6 states:
  - open ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_open.png?raw=true)<br />
    All players can teleport to this TPort<br />
    Available for PublicTP.
  - private ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_private.png?raw=true)<br />
    Only players in the whitelist can teleport to this TPort.
  - online ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_online.png?raw=true)<br />
    When the owner is online, the state is equal to *open*.<br />
    When the owner is offline, the state is equal to *private*<br />
    Available for PublicTP.
  - private online ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_private_online.png?raw=true)<br />
    When the owner is online, the state is equal to *private*.<br />
    When the owner is offline, the TPort will close for all players.
  - consent private ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_consent_private.png?raw=true)<br />
    When the owner is online, all players have to ask for consent to teleport to the TPort.<br />
    When the owner is offline, the state is equal to *private*.
  - consent close ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_consent_close.png?raw=true)<br />
    When the owner is online, players in the whitelist have to ask for consent, and players not in the whitelist can not teleport to this TPort.<br />
    When the owner is offline, the TPort will close for all players.
- Whitelist ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_whitelist.png?raw=true)<br />
  You can create a whitelist for players, which is used for the TPort state.
- Range ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_range.png?raw=true)<br />
  You can set a range to a TPort. When the owner is within that range, other players can teleport to that TPort. When the owner is outside the range, players can not teleport to the TPort.
- Logging ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log.png?raw=true)<br />
  It is possible log players teleporting to a TPort. This is customizable per TPort. Different modes are available for logging.
- Notify ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_none.png?raw=true)![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_log.png?raw=true)![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_online.png?raw=true)<br />
  If set up, you will be notified when a player teleports to a TPort you own.

### Some of the extra functions this plugin has to offer:<br />

- **Visualisation**<br />
  TPort is very focused on visualisation. This means that for most functions both commands and inventories are available.<br />
  The inventories are filled with buttons that enable the functions listed below.
- **Color theme** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme.png?raw=true)<br />
  You can create your own color theme (or use pre-build ones), and the plugin will change to those colors.
- **Custom icons** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_resource_pack.png?raw=true)<br />
  TPort has for every button in the inventories custom icons. These can be enabled using: `/tport resourcePack state <state>`.<br />
  The nearly 400 custom icons (in x16 and x32 resolution) helps you identify the buttons in the inventories.
- **Translations** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_language.png?raw=true)<br />
  TPort is fully translatable, even when the server does not have your preferred language. The language files have the same layout from Minecraft, so you can use them in a Resource Pack.
- **PLTP** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp.png?raw=true)<br />
  **pl**ayer **t**ele**p**ortation, safely teleport to other players. You can customize which players can teleport to you, and more.
- **BiomeTP** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/biome_tp.png?raw=true)<br />
  Teleport to a given biome/biome preset. It supports build-in Minecraft tag lists. It has also safety systems so that you won't teleport into the void in The End, or into a lava lake in The Nether.
- **FeatureTP** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/feature_tp.png?raw=true)<br />
  Teleport to a given feature/feature list. It supports build-in Minecraft tag lists.
- **WorldTP** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp.png?raw=true)<br />
  Teleport to the given world spawn.
- **back teleportation** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/back.png?raw=true)<br />
  Saves your last location, works with death location, BiomeTP, FeatureTP and PLTP.
- **Preview**![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_preview.png?raw=true)<br />
  It is possible to preview a TPort before teleporting. And you can preview a player before teleporting.
- **Teleporters**<br />
  Create items to teleport/do action with right click of the teleporter (teleporters work even when in an item frame).
- **Home** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/home.png?raw=true)<br />
  Set a TPort as home for quick teleportation.
- **Safety check** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_safety_check.png?raw=true)<br />
  This helps you with safely teleporting. If the safety check is enabled, TPort will check the teleport location if it is safe. If it is dangerous you won't teleport.
- **Log** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_log.png?raw=true)<br />
  Keep track of players teleporting to logged TPorts.
- **Cooldown** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_cooldown.png?raw=true)<br />
  A cooldown can help lighten the load for the server. This prevents players spamming certain commands.
- **Delay/Restriction** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_delay.png?raw=true) ![color theme](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_restriction.png?raw=true)<br />
  A delay can be set up so that a player can not instantly teleport away. This feature is mainly made for PvP. So that it is not possible to teleport away with a macro.<br />
  A restriction can be set up so that a player has a restriction during the delay. Examples: 
  - WalkRestriction: The player can not walk during the delay. If the player does, the teleport is canceled.
  - InteractRestriction: The player can not interact with the world during the delay. If the player does, the teleport is canceled.
- **Public TPorts** ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/public_tp.png?raw=true)<br />
  This is a list of TPorts that usually is useful for the whole server, but these Public TPorts can be whatever you want.
- **Horse, boat and leash friendly**<br />
  Then you are riding a horse, driving a boat or holding entities with a leash. They will all teleport with you.
- **Dynmap support** <img src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_dynmap.png?raw=true" width="32" height="32"><br />
  When [Dynmap](https://dev.bukkit.org/projects/dynmap) is loaded in the server, it is possible to show TPorts on the map. This is customizable for each TPort.

There are over 200 commands that can be used. Don't worry, all commands have their own explanation which can be looked at in game.
For help with TPort use `/tport help <command>`.<br />
Example: `/tport help TPort biomeTP`, this shows all the commands that start with `/tport biomeTP` with their command description.<br />

TPort does not support offline mode.

If you want to donate you can do that here: [https://ko-fi.com/the_spaceman](https://ko-fi.com/the_spaceman)
