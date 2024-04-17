# Quick Start

This section should help you get a bit comfortable with TPort.

## User

If you want to use TPort as a user, there are a few important features you should know about.


### TPort creation

To create a TPort you must use this command: `/tport add <TPort name> [description...]`.
The item in your main hand will be the display item.
If the feature _TPortTakesItem_ (to check the state use `/tport features TPortTakesItem`) is enabled you will lose that item, otherwise it will create a copy.
You can add a description during the creation of the TPort. It is possible to edit this description later using `/tport edit <TPort name> description <set|remove>`
To edit the display item use `/tport edit <TPort name> item`. If _TPortTakesItem_ was enabled during the creation you will get the old item back.

### Removing a TPort

To remove a TPort use the command `/tport remove <TPort name>`. The command does not ask for confirmation, so be careful.
If you removed a TPort by accident, you can get it back using `/tport restore` (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_restore.png?raw=true "TPort restore")).
Removing a TPort can also be found in the QuickEdit for that TPort. TPort restore can be found in the settings.

### Edit TPorts

There are a couple of options to edit with a TPort, and for each edit there are two ways to edit: `/tport edit <TPort name> [edit]` or using the QuickEdit.
To open the QuickEdit `shift + right click` on any TPorts in your own TPort GUI. This opens a new window where you can select the different edit modes.
Types of edits:

#### Description: `/tport edit <TPort name> description <get|set|remove>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_description.png?raw=true "TPort description")<br />

The description should give a clear explanation of the location/usage for this TPort.
Colors are supported. There are three ways to add colors: Minecraft color code (`&a`), HEX notation (`#123456`) and RGB notation (`$RRR$GGG$BBB`)
To add a new line in the description use `\n`.
The QuickEdit opens a keyboard where you can type your description and add colors using the color editor.

#### Name: `/tport edit <TPort name> name <new name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_name.png?raw=true "TPort name")<br />

The name of the TPort is main indicator to differentiate different TPorts (besides the display item). You can not own multiple TPorts with the same name, but you can share it with other players.
Only the standard alphabet (`a-z` and `A-Z`), hyphen (`-`) and underscore (`_`) can be used in the TPort name.

#### Item: `/tport edit <TPort name> item` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_item.png?raw=true "TPort item")<br />

The display item is the main indicator to differentiate different TPorts in the inventories in TPort.<br />
When _TPortTakesItem_ is disabled you will keep the item you set as display item.<br />
When _TPortTakesItem_ is enabled you will lose the item, but you will get it back when you set a different item as display item.

#### Location: `/tport edit <TPort name> location` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_location.png?raw=true "TPort location")<br />

A TPort stores the location (of the owner) during creation. It is fairly simple to edit this location. If any player opens this TPort they will teleport to this location.

#### Private: `/tport edit <TPort name> private [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_private.png?raw=true)<br />

The private state is the main safety feature a TPort has. This has 6 states:

* open ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_open.png?raw=true "TPort private state open")

  All players can teleport to this TPort<br />
  Available for PublicTP.
* private ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_private.png?raw=true "TPort private state private")

  Only players in the whitelist can teleport to this TPort.
* online ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_online.png?raw=true "TPort private state online")

  When the owner is online, the state is equal to *open*.<br />
  When the owner is offline, the state is equal to *private*<br />
  Available for PublicTP.
* private online ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_private_online.png?raw=true "TPort private state private online")
  
  When the owner is online, the state is equal to *private*.<br />
  When the owner is offline, the TPort will close for all players.
* consent private ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_consent_private.png?raw=true "TPort private state consent private")

  When the owner is online, all players have to ask for consent to teleport to the TPort.<br />
  When the owner is offline, the state is equal to *private*.
* consent close ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_consent_close.png?raw=true "TPort private state consent close")

  When the owner is online, players in the whitelist have to ask for consent, and players not in the whitelist can not teleport to this TPort.<br />
  When the owner is offline, the TPort will close for all players.

#### Whitelist: `/tport edit <TPort name> whitelist <add|remove|clone|visibility|list>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_whitelist.png?raw=true "TPort whitelist")<br />

The whitelist holds a list of players that is used for the private state.
By default, the whitelist is shown in the TPort description (hover with your mouse over the TPort in a GUI, or hover over the TPort name in chat).
You can hide the whitelist using the visibility state.

#### Move: `/tport edit <TPort name> move <slot|TPort name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_move.png?raw=true "Move a TPort within your TPort window")<br />

Moving a TPort is different that relocating it via `/tport edit <TPort name> location`.
To move the TPort, you will move it in your TPort GUI.

#### Range: `/tport edit <TPort name> range [range]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_range.png?raw=true "TPort range")<br />

You can set a range to a TPort. When the owner is within that range, other players can teleport to that TPort.
When the owner is outside the range, players can not teleport to the TPort. When the owner is in a different world, the range is 'infinite'.
To disable this set the range to `0`.

#### Tag: `/tport edit <TPort name> tag <add|remove>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_tag.png?raw=true "TPort tag")<br />

Adding a tag to a TPort further explains the type of TPort it is.
Examples of build-in tags: `home` and `farm`. When players search by tag (`/tport search tag <tag>`) will be shown.

#### Preview: `/tport edit <TPort name> preview [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_preview_off.png?raw=true "Preview state off")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_preview_notified.png?raw=true "Preview state notified")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_preview_on.png?raw=true "Preview state on")<br />

The preview state controls the state over the preview feature.
When enabled other players can preview the TPort, and should help them to make a decision if they want to teleport or not.

#### Log: `/tport log <add|remove>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log.png?raw=true)<br />

Description coming soon

[//]: # (todo)

#### Notify: `/tport log notify <TPort name> [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_none.png?raw=true "Notify state none")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_log.png?raw=true "Notify state log")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_online.png?raw=true "Notify state online")<br />

Notifying is part of the logging feature. Notify has 3 states:

* none ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_none.png?raw=true "Notify state none")

  The owner won't be notified when a player teleport to that TPort.
* log ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_log.png?raw=true "Notify state log")

  When the owner is online, they will be notified when a player that is logged teleports to that TPort.
* online ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_online.png?raw=true "Notify state online")

  When the owner is online, they will be notified when a player teleports to that TPort.

#### Remove: `/tport remove <TPort name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_remove.png?raw=true "TPort remove")<br />

This quick edit is used to remove a TPort. More explanation is above at the **Removing a TPort** section

#### Offer: `/tport transfer [offer|revoke]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_offer.png?raw=true "Offer a TPort")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_revoke.png?raw=true "Revoke an offer")<br />

In the quick edit you can only offer and revoke an offer of a TPort.

#### PublicTP: `/tport publicTP add <TPort name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_public_tp_on.png?raw=true "TPort is a Public TPort")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_public_tp_off.png?raw=true "TPort is not a Public TPort")<br />

To add your TPort to the publicTP list you can use this quick edit or command.

#### Dynmap: `/tport edit <TPort name> dynmap [show|icon]` <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_dynmap_show_on.pngg?raw=true" title="TPort is shown on Dynmap" width="32"/><img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_dynmap_show_off.png?raw=true" title="TPort is hidden on Dynmap" width="32"/><img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_dynmap_show_grayed.png?raw=true" title="Dynmap support is not enabled" width="32"/><br />

When Dynmap support is enabled your TPort will be shown at the Dynmap map. If you do not want this you can disable this via the `show` option.
All loaded Dynmap icons can be used for your TPort Dynmap icon.

#### BlueMap: `/tport edit <TPort name> BlueMap [show|icon]` <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_bluemap_show_on.png?raw=true" title="TPort is shown on BlueMap" width="32"/><img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_bluemap_show_off.png?raw=true" title="TPort is hidden on BlueMap" width="32"/><img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_bluemap_show_grayed.png?raw=true" title="BlueMap support is not enabled" width="32"/><br />

When BlueMap support is enabled your TPort will be shown at the BlueMap map. If you do not want this you can disable this via the `show` option.

***

### Basics

You should now know how to create a TPort and customize it however you like. To use these TPorts and the plugin for their intended purposes, a list is made:

#### Commands

TPort has over 200 commands.
Each command has their own command description which can be looked up in game using `/tport help <command>`.
Example: `/tport help TPort biomeTP`, this shows all the commands that start with `/tport biomeTP` with their command description and permissions (if enabled).
If a command has these `<>` brackets, that means that this argument is required for the command.
If a command has these `[]` brackets, that means that this argument is optional for the command.
Most of the time the arguments inside one of those brackets are variables that you can choose, like `<TPort name>`.  
The help command supports these variables when searching for commands,
example: `/tport help tport edit home tag`, this shows these commands: `/tport edit <TPort name> tag add [tag]` and `/tport edit <TPort name> tag remove [tag]`.
As you can see `home` gets replaced with `<TPort name>`. 
So if you get stuck with a command, you can add `/tport help ` before your command to get the description and correct usage.

#### Opening a TPort

When you open a TPort, you teleport to that location. The main way is via this command: `/tport open <player> <TPort name>`
The visual way is this: `/tport`, select the TPort owner, select the TPort to open. To directly open the owners TPort window, use `/tport open <player>`.
There is a shortcut for your own TPorts `/tport own [TPort name]`

#### Home ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/home.png?raw=true "TPort home")

The home is a TPort you set as your home. To teleport to your home use `/tport home`. To set you home use `/tport home set <player> <TPort name>`.
To open your home visually: `/tport`, click `left` on the home button.
To set your home visually: `/tport`, click `right` on the home button, select the TPort owner, select the TPort.

#### PLTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp.png?raw=true "PLTP")

PLTP stands for **Pl**ayer **T**ele**p**ortation.
It is basically the same as `/tp <player>`, but with more features and a tighter integration with the TPort plugin.
The visual way to edit your PLTP settings are in the PLTP settings (`/tport settings`, select PLTP) or in your own TPort GUI (`/tport own`) and click your own head.

* tp: `/tport PLTP tp <player>`

  This is the command to teleport to other players. There are a few settings you can change to your liking.
* state: `/tport PLTP state [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_state_on.png?raw=true "PLTP state on")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_state_off.png?raw=true "PLTP state off")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_state_grayed.png?raw=true "PLTP is disabled")

  With the state you can turn on/off PLTP. When disabled only players in your whitelist can teleport to you.
* consent: `/tport PLTP consent [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_consent_on.png?raw=true "PLTP consent on")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_consent_off.png?raw=true "PLTP consent off")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_consent_grayed.png?raw=true "PLTP is disabled")

  When consent is turned on players who are not in your whitelist will have to ask for consent first. This is done automatically when they try.
  To accept their request use `/tport requests accept <player>`. To reject their request use `/tport requests reject <player>`.
  When the consent is asked you will get a message in chat. In this message there are clickable elements that contain these commands and will run on click.
* whitelist: `/tport PLTP whitelist <add|remove|list>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_whitelist.png?raw=true "PLTP whitelist")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_whitelist_grayed.png?raw=true "PLTP is disabled")

  This whitelist is used by the PLTP state and consent state.
* offset: `/tport PLTP offset <IN|BEHIND>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_offset_behind.png?raw=true "PLTP offset BEHIND")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_offset_in.png?raw=true "PLTP offset IN")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_offset_grayed.png?raw=true "PLTP is disabled")

  If you don't want players to teleport to your exact location and blocking your view temporarily you can set up an offset. There are 2 states: `IN` and `BEHIND`.
  When the offset is set to `BEHIND` TPort will try to teleport the player 1 meter behind you. If that space is occupied with blocks the player will teleport in you.
* preview: `/tport PLTP preview [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_preview_off.png?raw=true "Preview state off")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_preview_notified.png?raw=true "Preview state notified")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_preview_on.png?raw=true "Preview state on")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_preview_grayed.png?raw=true "Preview is disabled")

  The preview state controls the state over the preview feature. When enabled other players can preview your current location, and should help them to make a decision if they want to teleport to you or not.

#### Preview `/tport preview <player> [TPort]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_preview.png?raw=true "Preview")

With this feature you can preview a player or a TPort. This has fewer restrictions than normal teleporting.
And it is the safest option, since while you preview you will be in spectator mode.
To preview a player use `/tport preview <player>`. The visual way is to open the main TPort window (`/tport`), hover your mouse over your selected player and press your `drop` key.
To preview a TPort use `/tport preview <player> <TPort name>`. The visual way is to open the TPort window of the TPort owner (`/tport open <player>`), hover your mouse over your selected TPort and press your `drop` key.

#### Safety Check `/tport safetyCheck [source] [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_safety_check.png?raw=true "Safety check")

If you do not trust other players TPorts, you can set up the safety check.
When the safety check is turned on, it checks the location before teleporting. If the location is not safe, the teleport is canceled.
If you know that the location is to be trusted you can override the check. Each teleporting command has an extra argument to override your default safety check state.
`/tport open <player> <TPort> [safetyCheck]`, `/tport home [safetyCheck]`, `/tport own <TPort> [safetyCheck]`, `/tport PLTP tp <player> [safetyCheck]` and `/tport public open <TPort> [safetyCheck]` are the commands.
In the inventories you add `shift` while clicking (`shift + left click` or `shift + right click`).

#### BiomeTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/biome_tp.png?raw=true "BiomeTP")

BiomeTP is used to search for biomes (similar to `/locate biome <biome>`).
You can search for multiple biomes in one search, or you can search for a random biome (random teleportation): `/tport biomeTP random`.
To search for biomes use this command: `/tport biomeTP whitelist <biome...>`. You can also search for biomes using a blacklist: `/tport biomeTP blacklist <biome...>`.
BiomeTP can use presets for searching. These presets are a list of biomes to search for, they can be a blacklist of whitelist.
There are two types of presets: preset from TPort or tag list from Minecraft (annotated with `#<tag name>`). To use a preset the command is `/tport biomeTP preset [preset]`.
Two search modes exist: `closest` and `random`. To change your mode use `/tport biomeTP mode <mode>`.<br />
BiomeTP has next to these commands a windowed counterpart. To open this use `/tport biomeTP`. This opens a window where you can select the biomes you want to search for.
For random teleportation click the elytra (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/biome_tp_random_tp.png?raw=true "RandomTP")) that is first in the biome list.
To select a biome for your search use `left click` on any biome. To clear your selection click top left button (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/biome_tp_clear.png?raw=true "Clear biome selection")).
To run your search click middle left button (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/biome_tp_run.png?raw=true "Run BiomeTP with selected biomes")).
To select/add a preset to your selection click bottom left button (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/biome_tp_presets.png?raw=true "BiomeTP presets")),
this opens a new window where you can select a preset. In here you can add the biomes to your selection (`left click`) or replace your selection with the preset (`shift + left click`). To run this preset directly `right click` the preset.

#### FeatureTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/feature_tp.png?raw=true "FeatureTP")

FeatureTP is used to search for features (similar to `/locate structure <structure>`).
You can search for multiple features in one search. To search for features use this command: `/tport featureTP search [mode] <feature...>`
FeatureTP can use presets for searching. These presets are a list of features to search for. FeatureTP only has one type of preset: tag list form Minecraft (annotated with `#<tag name>`).
To use a preset, add the preset name in the search command (`/tport featureTP search <feature|preset...>`). It is possible to mix presets and features in the same command, like this `/tport featureTP search #village trail_ruins`.
Two search modes exist: `closest` and `random`. To change your mode use `/tport featureTP mode <mode>`, or as a temporary override add the search mode in the search command (`/tport featureTP search CLOSEST #village`).<br />
FeatureTP has next to these commands a windowed counterpart. To open this use `/tport featureTP`. This opens a window where you can select the features you want to search for.
To select a feature for your search use `left click` on any feature. To clear your selection click top left button (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/feature_tp_clear.png?raw=true "Clear feature selection")).
To run your search click middle left button (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/feature_tp_run.png?raw=true "Run FeatureTP with selected features")).
To select/add a preset to your selection, go to the bottom of the list, and select your preset.
Down here you can add the features to your selection (`left click`) or replace your selection with the preset (`shift + left click`). To run this preset directly `right click` the preset.

#### WorldTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp.png?raw=true "WorldTP")

If you want to teleport to the spawn of another world, WorldTP is the feature for you. The command is `/tport world <world>`. Via the main TPort window you can open the WorldTP window, in witch you can select the world to teleport to.
The icons for the world are denoted by their dimension: ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_overworld.png?raw=true "Over world")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_nether.png?raw=true "Nether")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_the_end.png?raw=true "The End")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_other_environments.png?raw=true "Other environments")

#### Transfer ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_transfer.png?raw=true "TPort transfer")

It is possible to give the ownership of a TPort away to another player. Via the transfer commands is this possible.
To offer a TPort use `/tport transfer offer <player> <TPort name>`.
Another option to offer is via the QuickEdit of the TPort (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_offer.png?raw=true "Offer a TPort")).
While the offer stands it is not possible to edit the TPort. There are three ways an offer stops: revoking (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_revoke.png?raw=true "Revoke an offer")), accepting and rejecting (there is not time out limit).
The owner of the TPort can revoke the offer if they change their mind (`/tport transfer revoke <TPort name>`).
The player who gets the TPort offered has two options: accepting (`/tport transfer accept <player> <TPort name>`) or rejecting (`/tport transfer reject <player> <TPort name>`).
You can only accept a TPort if you don't already have a TPort with the same name.
If the command `/tport transfer` is used the Transfer window will open. In here you can see the TPorts offered to you (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_transfer_switch_offers.png?raw=true "TPort offers to you")) and the TPorts you are offering (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_transfer_switch_offered.png?raw=true "Your offered TPort")).

#### Search `/tport search` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search.png?raw=true "TPort Search")

Using search you can search for players and TPorts. The search types are:

* TPort `/tport search tport <mode> <TPort name>` <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_tport.png?raw=true" title="Search for TPorts by their name" width="32"/>
  
  Search for TPorts by their name. You can use the 5 search modes.
* world `/tport search world <world>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_world.png?raw=true "Search for TPorts by the world they are located in")
  
  Search for TPorts by the world they are located in.
* biome `/tport search biome <biome...>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_biome.png?raw=true "Search for TPorts in the biome they are located")
  
  Search for TPorts in the biome they are located in.
* biomePreset `/tport search biomePreset <preset>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_biome_preset.png?raw=true "Search for TPorts in the biome they are located, using a biome preset")
  
  Search for TPorts in the biome they are located, using a biome preset.
* description `/tport search description <mode> <description...>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_description.png?raw=true "Search for TPorts by their description")
  
  Search for TPorts by their description. You can use the 6 search modes.
* tag `/tport search tag <tag>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_tag.png?raw=true "Search for TPorts by their tags")
 
  Search for TPorts by their tags.
* canTP `/tport search canTP` <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_can_tp.png?raw=true" title="Search for TPorts you can teleport to" width="32"/>
  
  Search for TPorts you can teleport to.
* dimension `/tport search dimension <dimension>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_dimension.png?raw=true "Search for TPorts by the dimension they are located in")
  
  Search for TPorts by the dimension they are located in.
* player `/tport search player <mode> <player name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_player.png?raw=true "Search for players")
  
  Search for players. You can use the 6 search modes.
* ownedTPorts `/tport search ownedTPorts <mode> <amount>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_player.png?raw=true "Search for players by their owned TPorts")
  
  Search for players by their owned TPorts. This type is an integer search. You can use 4 search modes. (`ends with` and `starts with` not supported)

There are 5 search modes. Not all search types use these modes. The 5 modes are:

* equals ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_equal.png?raw=true "Equals search mode")

  The full element must match the searched. This is the same for an integer search.
* not equals ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_not_equal.png?raw=true "Not equals search mode")

  The full element must not match the searched. This is the same for an integer search.
* contains ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_contains.png?raw=true "Contains search mode")

  The element must contain in the searched. For an integer search, the element must be bigger than the searched (example: `element = 10`, `searched = 5`. `10` contains `5`).
* not contains ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_not_contains.png?raw=true "Not contains search mode")

  The element must not contain in the searched. For an integer search, the element must be smaller than the searched (example: `element = 5`, `searched = 10`. `5` does not contain `10`).
* starts with ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_starts.png?raw=true "Starts with search mode")

  The searched must start with the query. This does not support integer search.
* ends with ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_ends.png?raw=true "Ends with search mode")

  The searched must end with the query. This does not support integer search.

#### PublicTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/public_tp.png?raw=true)

Description coming soon

[//]: # (todo)

#### Log ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_log.png?raw=true "TPort log")

If you want to keep track on players teleporting to your TPorts you can use the logging feature.
If you add a TPort to your log, the plugin will keep track on all logged player teleporting to that TPort.
Each TPort has a default log mode and a logged player list.

* Default log mode `/tport log default <TPort name> <logMode>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_none.png?raw=true "Log mode none")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_all.png?raw=true "Log mode all")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_offline.png?raw=true "Log mode offline")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_online.png?raw=true "Log mode online")<br />
  
  The default log mode is the general logging state of a TPort. This mode has 4 states:
  
  - None ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_none.png?raw=true "Log mode none")
    
    The player will never be logged.
  - All ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_all.png?raw=true "Log mode all")
    
    The player will always be logged. This is a logging mode.
  - Offline ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_offline.png?raw=true "Log mode offline")
    
    Only when the owner is offline the player will be logged. This is a logging mode.
  - Online ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_online.png?raw=true "Log mode online")
    
    Only when the owner is online the player will be logged. This is a logging mode.
  
* Adding / removing player to the TPort log list `/tport log <add|remove>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_edit.png?raw=true "Edit logged TPorts")
  
  Each player can have a custom logging mode. If you alter the logging mode of a player, they will be added to the logged player list.
  This mode will override the default logging mode.
  To add a player use: `/tport log add <TPort name> <player:[logmode]...>` (example: `/tport log add home Herobrine:All`).
  To remove a player use: `/tport log remove <TPort name> <player...>`.
  A TPort is only logged if the default log mode is not set to `none`, or there is no player in the logged player list that has not the mode `none`.
* Un-log a TPort `/tport log delete <TPort name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_delete.png?raw=true "Unload a TPort")

  To un-log a TPort you can set the default log mode to `none` and remove every player from the logged player list.
  Or you can use this command: `/tport log delete <TPort name>`.
* Reading the log `/tport log read <TPort name> [player]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_read.png?raw=true "Reading the log of a TPort")

  There are two ways to read a log. Via the chat or via a window. The default is via the window.
  The command `/tport log read <TPort name>` opens the log.
  In here you can filter by player (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_read_filter.png?raw=true "Player filter")).
  Or you can show the log in the chat (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_read_chat.png?raw=true "View log in chat")).
* Clearing the log `/tport log clear <TPort name...>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_clear.png?raw=true "Clear the log of a TPort")

  To clear the log you can use this command: `/tport log clear <TPort name...>`. Or you can reset it in the logging window (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_clear.png?raw=true "Clear the log of a TPort"))
* Time formatting `/tport log timeFormat [format...]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_log_set_time_format.png?raw=true "Edit your time format for viewing the log")

  You can customize the time format. With this you can add / remove information about the time of the log.
  There are 22 elements to be used, and with the TPort Keyboard you can further customize the formatting.
  More information about these elements can be found [here](https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html).
* Time zone `/tport log timeZone [timeZone]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_log_time_zone_id.png?raw=true "Edit your time zone for viewing the log")

  If you live in a different time zone of the server, you can set your time zone so that the log will be more customized for you.

#### LookTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_look_tp.png?raw=true "LookTP")<br />

Description coming soon

[//]: # (todo)

#### TPort Keyboard

TPort has a built-in keyboard. This keyboard is used for a variety of functions, like editing the description/name of a TPort, or for searching via windows. 

The layout of the TPort Keyboard:  
![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_1.png?raw=true "Key: 1, !")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_2.png?raw=true "Key: 2, @")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_3.png?raw=true "Key: 3, #")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_4.png?raw=true "Key: 4, $")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_5.png?raw=true "Key: 5, %")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_6.png?raw=true "Key: 6, ^")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_7.png?raw=true "Key: 7, &")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_8.png?raw=true "Key: 8, *")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_9.png?raw=true "Key: 9, (")  
![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_o.png?raw=true "Key: 0, )")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_minus.png?raw=true "Key: -, _")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_equals.png?raw=true "Key: =, +")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_square_open.png?raw=true "Key: [, {")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_square_close.png?raw=true "Key: ], }")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_semicolon.png?raw=true "Key: ;, :")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_apostrophe.png?raw=true "Key: ', \"")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_slash.png?raw=true "Key: /, ?")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_p.png?raw=true "Key: P, p")  
![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_q.png?raw=true "Key: Q, q")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_w.png?raw=true "Key: W, w")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_e.png?raw=true "Key: E, e")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_r.png?raw=true "Key: R, r")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_t.png?raw=true "Key: T, t")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_y.png?raw=true "Key: Y, y")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_u.png?raw=true "Key: U, u")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_i.png?raw=true "Key: I, i")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_o.png?raw=true "Key: O, o")  
![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_a.png?raw=true "Key: A, a")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_s.png?raw=true "Key: S, s")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_d.png?raw=true "Key: D, d")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_f.png?raw=true "Key: F, f")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_g.png?raw=true "Key: G, g")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_h.png?raw=true "Key: H, h")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_j.png?raw=true "Key: J, j")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_k.png?raw=true "Key: K, k")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_l.png?raw=true "Key: L, l")  
![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_z.png?raw=true "Key: Z, z")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_x.png?raw=true "Key: X, x")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_c.png?raw=true "Key: C, c")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_v.png?raw=true "Key: V, v")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_b.png?raw=true "Key: B, b")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_n.png?raw=true "Key: N, n")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_m.png?raw=true "Key: M, m")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_comma.png?raw=true "Key: ,, <")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_dot.png?raw=true "Key: ., >")  
![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_reject.png?raw=true "Reject")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_format_on.png?raw=true "Change title formatting")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_change_layout.png?raw=true "Change keyboard layout")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_color.png?raw=true "Color Editor")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_space.png?raw=true "Key: Space, new line")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/char_backspace.png?raw=true "Key: Backspace, delete")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_quick_type.png?raw=true "QuickType")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_cursor.png?raw=true "Cursor")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_accept.png?raw=true "Accept")

The title of the window is the typed input. This is also shown in the accept button (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_accept.png?raw=true "Accept")).
In the title, there is a red `|`. This is the cursor. You can move this cursor via the cursor button (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_cursor.png?raw=true "Cursor")). Left click moves the cursor left, right click moves the cursor right.

QuickType is the _fast typing_ method. When clicking the button (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_quick_type.png?raw=true "QuickType")), it opens a sign. In this sign you can type and past text.
Each line of the sign is also a new line in the typed input. When closing the sign, your typed text will append to the already typed input.

It is possible to change form _qwerty_ into _abcdef_. You do this via the Change keyboard layout button (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_change_layout.png?raw=true "Change keyboard layout")).

There are two ways to add colors. The first one is adding it your self, by typing `#ff55ff` (hex value), `&d` (Chat color code) or `$255$85$255` (RGB values).
The second way is using the Color Editor (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_color.png?raw=true "Color Editor")).
When opening the Color Editor, 6 buttons will appear to create a color (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_color_red_add.png?raw=true "Add red"), ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_color_red_remove.png?raw=true "Remove red"), ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_color_green_add.png?raw=true "Add green"), ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_color_green_remove.png?raw=true "Remove green"), ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_color_blue_add.png?raw=true "Add blue"), ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_color_blue_remove.png?raw=true "Remove blue")),
and the seventh button (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_chat_color.png?raw=true "Choose from Minecraft")) is to choose a color from Minecraft (chat colors & dye colors).

[//]: # (todo)

***

### Customisation

#### Color Theme ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme.png?raw=true "Color theme")<br />

With a color theme you can customize the look of TPort in chat and in the windows.
You can select out of more than 10 default themes, or you can create your own theme.
You can edit your own theme via commands or via the Color Theme windows (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme.png?raw=true "Color theme")).
The command to select out of a default theme is `/tport colorTheme set <theme>`.
To edit your current theme use `/tport colorTheme set <type> <color>`.
The `type` is the type of color. There are 12 color types:

- infoColor ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_info.png?raw=true "info color")  
  This is the default color of an information message
- varInfoColor ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_success.png?raw=true "var info color")  
  This is the color of variables in an information message
- varInfo2Color ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_error.png?raw=true "secondary var info color")    
  This is the secondary color of variables in an information message
- successColor ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_info.png?raw=true "success color")  
  This is the default color of a success message
- varSuccessColor ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_success.png?raw=true "var success color")  
  This is the color of variables in a success message
- varSuccess2Color ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_error.png?raw=true "secondary var success color")  
  This is the secondary color of variables in a success message
- errorColor ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_info.png?raw=true "error color")  
  This is the default color of an error message
- varErrorColor ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_success.png?raw=true "var error color")  
  This is the color of variables in an error message
- varError2Color ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_error.png?raw=true "secondary var error color")  
  This is the secondary color of variables in an error message 
- goodColor ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_good.png?raw=true "good color")  
  This is the color for good/enabled
- badColor ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_bad.png?raw=true "bad color")  
  This is the color for bad/disabled
- titleColor ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme_title.png?raw=true "title color")  
  This is the color of titles

Via windows, you can directly edit the colors, or you can select from the 16 chat colors and 16 dye colors as a base (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/keyboard_chat_color.png?raw=true "Select colors from Minecraft")).

#### Resource Pack ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_resource_pack.png?raw=true "TPort resource pack")<br />

The default look of TPort is mostly oak buttons.
The over 400 custom icons will greatly improve the feel and usability of TPort.
Therefor is it greatly advised on using the TPort Resource Pack.
This is very simple to do, you first need to enable this using `/tport resourcePack state <state>`.
After this you can select the resolution. There are standard 2 resolutions, x16 (default minecraft) and x32 (popular resolution).
Each resolution has a light and a dark theme variant.
When using these resolutions, your client will be prompted to download these resource packs automatically.
These packs are located on GitHub as a free download.
If you want to make changes and use your own custom version, you can use the `custom` resolution to use the resource pack loaded in your client.

#### Language ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_language.png?raw=true "Language")<br />

Description coming soon.

***

## Admin

Description coming soon.

#### reload ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_reload.png?raw=true "Reload")
#### features ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features.png?raw=true "TPort Features")
* BiomeTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_biome_tp.png?raw=true "Feature BiomeTP")
* FeatureTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_feature_tp.png?raw=true "Feature FeatureTP")
* BackTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_back_tp.png?raw=true "Feature BackTP")
* PublicTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_public_tp.png?raw=true "Feature PublicTP")
* PLTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_pltp.png?raw=true "Feature PLTP")
* Dynmap <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_dynmap.png?raw=true" title="Feature Dynmap" width="32"/>
* BlueMap <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_bluemap.png?raw=true" title="Feature BlueMap" width="32"/>
* Metrics ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_metrics.png?raw=true "Feature Metrics")
* Permissions ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_permissions.png?raw=true "Feature Permissions")
* ParticleAnimation ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_particle_animation.png?raw=true "Feature ParticleAnimation")
* Redirects ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_redirects.png?raw=true "Feature Redirects")
* History ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_history.png?raw=true "Feature History")
* Preview ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_preview.png?raw=true "Feature Preview")
* WorldTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_world_tp.png?raw=true "Feature WorldTP")
* TPortTakesItem ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_tport_takes_item.png?raw=true "Feature TPortTakesItem")
* InterdimensionalTeleporting ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_interdimensional_teleporting.png?raw=true "Feature InterdimensionalTeleporting")
* DeathTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_death_tp.png?raw=true "Feature DeathTP")
* LookTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_look_tp.png?raw=true "Feature LookTP")
* EnsureUniqueUUID ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_ensure_unique_uuid.png?raw=true "Feature EnsureUniqueUUID")
* PrintErrorsInConsole ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_print_errors_in_console.png?raw=true "Feature PrintErrorsInConsole")
* FeatureSettings ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_feature_settings.png?raw=true "Feature FeatureSettings")
#### BiomeTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/biome_tp.png?raw=true "BiomeTP")
#### Tags ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_tag.png?raw=true "Tags")
#### backup ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_backup.png?raw=true "Backup")
#### remove player ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_remove_player.png?raw=true "Remove a player")
#### redirect ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect.png?raw=true "Redirecting commands")

* ConsoleFeedback ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect_console_feedback.png?raw=true "ConsoleFeedback")

  When a redirect occurs, print a message in the console
* TP_PLTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect_tp_pltp.png?raw=true "TP_PLTP")

  Redirect `/tp <player>` to `/tport pltp tp <player>`
* Locate_featureTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect_locate_feature_tp.png?raw=true "Locate_featureTP")

  Redirect `/locate structure <structure>` or `/locate <structure>` to `/tport FeatureTP search <feature>`
* LocateBiome_biomeTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect_locate_biome_biome_tp.png?raw=true "LocateBiome_biomeTP")

  Redirect `/locate biome <biome>` or `/locatebiome <biome>` to `/tport BiomeTP whitelist <biome>`
* Home_TPortHome ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect_home_tport_home.png?raw=true "Home_TPortHome")

  Redirect `/home` to `/tport home`
* Back_TPortBack ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect_back_tport_back.png?raw=true "Back_TPortBack")

  Redirect `/back` to `/tport back`
* TPA_PLTP_TP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect_tpa_pltp_tp.png?raw=true "TPA_PLTP_TP")

  Redirect `/tpa <player>` to `/tport pltp tp <player>`
* TPAccept_Requests_accept ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect_tpaccept_requests_accept.png?raw=true "TPAccept_Requests_accept")

  Redirect `/tpaccept [player]` to `/tport requests accept [player]`
* TPDeny_Requests_reject ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect_tpdeny_requests_reject.png?raw=true "TPDeny_Requests_reject")

  Redirect `/tpdeny [player]` to `/tport requests reject [player]`
* TPRevoke_Requests_revoke ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect_tprevoke_requests_revoke.png?raw=true "TPRevoke_Requests_revoke")

  Redirect `/tprevoke` to `/tport requests revoke`
* TPRandom_BiomeTP_random ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_redirect_tprandom_biome_tp_random.png?raw=true "TPRandom_BiomeTP_random")

  Redirect `/tprandom`, `/randomtp` or `/rtp` to `/tport biomeTP random`

#### metrics ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_metrics.png?raw=true "bStats metrics")
#### Dynmap <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_dynmap.png?raw=true" title="Dynmap" width="32"/>
#### BlueMap <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_bluemap.png?raw=true" title="BlueMap" width="32"/>
#### cooldown ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_cooldown.png?raw=true "Cooldown")
* TPortTP
* PlayerTP
* FeatureTP
* BiomeTP
* Search
* Back
* LookTP
#### publicTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_public.png?raw=true "PublicTP")
#### Adapters ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_adapter.png?raw=true "Adapters")
#### Delay / Restriction ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_delay.png?raw=true "Delay") ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_restriction.png?raw=true "Restriction")
