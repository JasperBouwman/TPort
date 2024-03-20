### Quick Start

This section should help you get a bit comfortable with TPort.

#### User

If you want to use TPort as a user, there are a few important features you should know about.<br />
- **TPort creation**<br />
  To create a TPort you must use this command: `/tport add <TPort name> [description...]`.
  The item in your main hand will be the display item.
  If the feature _TPortTakesItem_ (to check the state use `/tport features TPortTakesItem`) is enabled you will lose that item, otherwise it will create a copy.
  You can add a description during the creation of the TPort. It is possible to edit this description later using `/tport edit <TPort name> description <set|remove>`
  To edit the display item use `/tport edit <TPort name> item`. If _TPortTakesItem_ was enabled during the creation you will get the old item back.
- **Removing a TPort**<br />
  To remove a TPort use the command `/tport remove <TPort name>`. The command does not ask for confirmation, so be careful.
  If you removed a TPort by accident, you can get it back using `/tport restore`.
  Removing a TPort can also be found in the QuickEdit for that TPort. TPort restore can be found in the settings.
- **Edit TPorts**<br />
  There are a couple of options to edit with a TPort, and for each edit there are two ways to edit: `/tport edit <TPort name> [edit]` or using the QuickEdit.
  To open the QuickEdit `shift + right click` on any TPorts in your own TPort GUI. This opens a new window where you can select the different edit modes.
  Types of edits:
    - Description: `/tport edit <TPort name> description <get|set|remove>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_description.png?raw=true "TPort description")<br />
      The description should give a clear explanation of the location/usage for this TPort.
      Colors are supported. There are three ways to add colors: Minecraft color code (`&a`), HEX notation (`#123456`) and RGB notation (`$RRR$GGG$BBB`)
      To add a new line in the description use `\n`.
      The QuickEdit opens a keyboard where you can type your description and add colors using the color editor.
    - Name: `/tport edit <TPort name> name <new name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_name.png?raw=true "TPort name")<br />
      The name of the TPort is main indicator to differentiate different TPorts (besides the display item). You can not own multiple TPorts with the same name, but you can share it with other players.
      Only the standard alphabet (`a-z` and `A-Z`), hyphen (`-`) and underscore (`_`) can be used in the TPort name.
    - Item: `/tport edit <TPort name> item` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_item.png?raw=true "TPort item")<br />
      The display item is the main indicator to differentiate different TPorts in the inventories in TPort.<br />
      When _TPortTakesItem_ is disabled you will keep the item you set as display item.<br />
      When _TPortTakesItem_ is enabled you will lose the item, but you will get it back when you set a different item as display item.
    - Location: `/tport edit <TPort name> location` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_location.png?raw=true "TPort location")<br />
      A TPort stores the location (of the owner) during creation. It is fairly simple to edit this location. If any player opens this TPort they will teleport to this location.
    - Private: `/tport edit <TPort name> private [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_private.png?raw=true)<br />
      The private state is the main safety feature a TPort has. This has 6 states:
      - open ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_open.png?raw=true "TPort private state open")<br />
      All players can teleport to this TPort<br />
      Available for PublicTP.
      - private ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_private.png?raw=true "TPort private state private")<br />
      Only players in the whitelist can teleport to this TPort.
      - online ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_online.png?raw=true "TPort private state online")<br />
      When the owner is online, the state is equal to *open*.<br />
      When the owner is offline, the state is equal to *private*<br />
      Available for PublicTP.
      - private online ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_private_online.png?raw=true "TPort private state private online")<br />
      When the owner is online, the state is equal to *private*.<br />
      When the owner is offline, the TPort will close for all players.
      - consent private ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_consent_private.png?raw=true "TPort private state consent private")<br />
      When the owner is online, all players have to ask for consent to teleport to the TPort.<br />
      When the owner is offline, the state is equal to *private*.
      - consent close ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_private_consent_close.png?raw=true "TPort private state consent close")<br />
      When the owner is online, players in the whitelist have to ask for consent, and players not in the whitelist can not teleport to this TPort.<br />
      When the owner is offline, the TPort will close for all players.
  - Whitelist: `/tport edit <TPort name> whitelist <add|remove|clone|visibility|list>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_whitelist.png?raw=true "TPort whitelist")<br />
    The whitelist holds a list of players that is used for the private state.
    By default, the whitelist is shown in the TPort description (hover with your mouse over the TPort in a GUI, or hover over the TPort name in chat). You can hide the whitelist using the visibility state.
  - Move: `/tport edit <TPort name> move <slot|TPort name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_move.png?raw=true "Move a TPort within your TPort window")<br />
    Moving a TPort is different that relocating it via `/tport edit <TPort name> location`. To move the TPort, you will move it in your TPort GUI.
  - Range: `/tport edit <TPort name> range [range]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_range.png?raw=true "TPort range")<br />
    You can set a range to a TPort. When the owner is within that range, other players can teleport to that TPort. When the owner is outside the range, players can not teleport to the TPort. When the owner is in a different world, the range is 'infinite'. To disable this set the range to `0`.
  - Tag: `/tport edit <TPort name> tag <add|remove>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_tag.png?raw=true "TPort tag")<br />
    Adding a tag to a TPort further explains the type of TPort it is. Examples of build-in tags: `home` and `farm`. When players search by tag (`/tport search tag <tag>`) will be shown.
  - Preview: `/tport edit <TPort name> preview [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_preview_off.png?raw=true "Preview state off")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_preview_notified.png?raw=true "Preview state notified")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_preview_on.png?raw=true "Preview state on")<br />
    The preview state controls the state over the preview feature. When enabled other players can preview the TPort, and should help them to make a decision if they want to teleport or not.
  - Log: `/tport log <add|remove>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log.png?raw=true)<br />

  - Notify: `/tport log notify <TPort name> [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_none.png?raw=true "Notify state none")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_log.png?raw=true "Notify state log")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_online.png?raw=true "Notify state online")<br />
    Notifying is part of the logging feature. Notify has three states:
      - none ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_none.png?raw=true "Notify state none")<br />
        The owner won't be notified when a player teleport to that TPort.
      - log ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_log.png?raw=true "Notify state log")<br />
        When the owner is online, they will be notified when a player that is logged teleports to that TPort.
      - online ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_notify_online.png?raw=true "Notify state online")<br />
        When the owner is online, they will be notified when a player teleports to that TPort.
  - Remove: `/tport remove <TPort name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_remove.png?raw=true "TPort remove")<br />
    This quick edit is used to remove a TPort. More explanation is above at the **Removing a TPort** section
  - Offer: `/tport transfer [offer|revoke]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_offer.png?raw=true "Offer a TPort")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_revoke.png?raw=true "Revoke an offer")<br />
    In the quick edit you can only offer and revoke an offer of a TPort.
  - PublicTP: `/tport publicTP add <TPort name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_public_tp_on.png?raw=true "TPort is a Public TPort")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_public_tp_off.png?raw=true "TPort is not a Public TPort")<br />
    To add your TPort to the publicTP list you can use this quick edit / command.
  - Dynmap: `/tport edit <TPort name> dynmap [show|icon]` <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_dynmap_show_on.png?raw=true" title="TPort is shown on Dynmap" width="32"/><img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_dynmap_show_off.png?raw=true" title="TPort is hidden on Dynmap" width="32"/><img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_dynmap_show_grayed.png?raw=true" title="Dynmap support is not enabled" width="32"/><br />
    When Dynmap support is enabled your TPort will be shown at the Dynmap map. If you do not want this you can disable this via the `show` option.
    All loaded Dynmap icons can be used for your TPort Dynmap icon.
  - BlueMap: `/tport edit <TPort name> BlueMap [show|icon]` <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_bluemap_show_on.png?raw=true" title="TPort is shown on BlueMap" width="32"/><img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_bluemap_show_off.png?raw=true" title="TPort is hidden on BlueMap" width="32"/><img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_bluemap_show_grayed.png?raw=true" title="BlueMap support is not enabled" width="32"/><br />
    When BlueMap support is enabled your TPort will be shown at the BlueMap map. If you do not want this you can disable this via the `show` option.



- **Basics**<br />
  You should now know how to create a TPort and customize it however you like. To use these TPorts and the plugin for their intended purposes, a list is made:
    - Opening a TPort
        - `/tport open <player> <TPort name>`<br />
          This is the main command to open a TPort. The visual way is this: `/tport`, select the TPort owner, select the TPort to open. To directly open the owners TPort GUI use `/tport open <player>`
        - `/tport own <TPort name>`<br />
          This is the shortcut to open your own TPorts. `/tport own` opens your own TPort GUI.
    - Home ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/home.png?raw=true "TPort home")<br />
      The home is a TPort you set as your home. To teleport to your home use `/tport home`. To set you home use `/tport home set <player> <TPort name>`.
      To open your home visually: `/tport`, click `left` on the home button.
      To set your home visually: `/tport`, click `right` on the home button, select the TPort owner, select the TPort.
    - PLTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp.png?raw=true "PLTP")<br />
      PLTP stands for **Pl**ayer **T**ele**p**ortation. It is basically the same as `/tp <player>`, but with more features and a tighter integration with the TPort plugin.
      The visual way to edit your PLTP settings are in the PLTP settings (`/tport settings`, select PLTP) or in your own TPort GUI (`/tport own`) and click your own head.
        - tp: `/tport PLTP tp <player>`<br />
          This is the command to teleport to other players. There are a few settings you can change to your liking.
        - state: `/tport PLTP state [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_state_on.png?raw=true "PLTP state on")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_state_off.png?raw=true "PLTP state off")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_state_grayed.png?raw=true "PLTP is disabled")<br />
          With the state you can turn on/off PLTP. When disabled only players in your whitelist can teleport to you.
        - consent: `/tport PLTP consent [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_consent_on.png?raw=true "PLTP consent on")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_consent_off.png?raw=true "PLTP consent off")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_consent_grayed.png?raw=true "PLTP is disabled")<br />
          When consent is turned on players who are not in your whitelist will have to ask for consent first. This is done automatically when they try.
          To accept their request use `/tport requests accept <player>`. To reject their request use `/tport requests reject <player>`.
          When the consent is asked you will get a message in chat. In this message there are clickable elements that contain these commands and will run on click.
        - whitelist: `/tport PLTP whitelist <add|remove|list>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_whitelist.png?raw=true "PLTP whitelist")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_whitelist_grayed.png?raw=true "PLTP is disabled")<br />
          This whitelist is used by the PLTP state and consent state.
        - offset: `/tport PLTP offset <IN|BEHIND>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_offset_behind.png?raw=true "PLTP offset BEHIND")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_offset_in.png?raw=true "PLTP offset IN")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_offset_grayed.png?raw=true "PLTP is disabled")<br />
          If you don't want players to teleport to your exact location and blocking your view temporarily you can set up an offset. There are 2 states: `IN` and `BEHIND`.
          When the offset is set to `BEHIND` TPort will try to teleport the player 1 meter behind you. If that space is occupied with blocks the player will teleport in you.
        - preview: `/tport PLTP preview [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_preview_off.png?raw=true "Preview state off")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_preview_notified.png?raw=true "Preview state notified")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_preview_on.png?raw=true "Preview state on")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_pltp_preview_grayed.png?raw=true "Preview is disabled")<br />
          The preview state controls the state over the preview feature. When enabled other players can preview your current location, and should help them to make a decision if they want to teleport to you or not.
    - Preview `/tport preview <player> [TPort]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_preview.png?raw=true "Preview")<br />
      With this feature you can preview a player or a TPort. This has fewer restrictions than normal teleporting.
      And it is the safest option, since while you preview you will be in spectator mode.
      To preview a player use `/tport preview <player>`. The visual way is to open the main TPort window (`/tport`), hover your mouse over your selected player and press your `drop` key.
      To preview a TPort use `/tport preview <player> <TPort name>`. The visual way is to open the TPort window of the TPort owner (`/tport open <player>`), hover your mouse over your selected TPort and press your `drop` key.
    - Safety Check `/tport safetyCheck [source] [state]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_safety_check.png?raw=true)<br />
      If you do not trust other players TPorts, you can set up the safety check.
      When the safety check is turned on, it checks the location before teleporting. If the location is not safe, the teleport is canceled.
      If you know that the location is to be trusted you can override the check. Each teleporting command has an extra argument to override your default safety check state.
      `/tport open <player> <TPort> [safetyCheck]`, `/tport home [safetyCheck]`, `/tport own <TPort> [safetyCheck]`, `/tport PLTP tp <player> [safetyCheck]` and `/tport public open <TPort> [safetyCheck]` are the commands.
      In the inventories you add `shift` while clicking (`shift + left click` or `shift + right click`).
    - BiomeTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/biome_tp.png?raw=true "BiomeTP")<br />
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
    - FeatureTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/feature_tp.png?raw=true "FeatureTP")<br />
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
    - WorldTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp.png?raw=true "WorldTP")<br />
      If you want to teleport to the spawn of another world, WorldTP is the feature for you. The command is `/tport world <world>`. Via the main TPort window you can open the WorldTP window, in witch you can select the world to teleport to.
      The icons for the world are denoted by their dimension: ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_overworld.png?raw=true "Over world")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_nether.png?raw=true "Nether")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_the_end.png?raw=true "The End")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/world_tp_other_environments.png?raw=true "Other environments")
    - Transfer ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_transfer.png?raw=true "TPort transfer")<br />
      It is possible to give the ownership of a TPort away to another player. Via the transfer commands is this possible.
      To offer a TPort use `/tport transfer offer <player> <TPort name>`.
      Another option to offer is via the QuickEdit of the TPort (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_offer.png?raw=true "Offer a TPort")).
      While the offer stands it is not possible to edit the TPort. There are three ways an offer stops: revoking (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_revoke.png?raw=true "Revoke an offer")), accepting and rejecting (there is not time out limit).
      The owner of the TPort can revoke the offer if they change their mind (`/tport transfer revoke <TPort name>`).
      The player who gets the TPort offered has two options: accepting (`/tport transfer accept <player> <TPort name>`) or rejecting (`/tport transfer reject <player> <TPort name>`).
      You can only accept a TPort if you don't already have a TPort with the same name.
      If the command `/tport transfer` is used the Transfer window will open. In here you can see the TPorts offered to you (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_transfer_switch_offers.png?raw=true "TPort offers to you")) and the TPorts you are offering (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_transfer_switch_offered.png?raw=true "Your offered TPort")).
    - Search `/tport search` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search.png?raw=true "TPort Search")<br />
      Using search you can search for players and TPorts. The search types are:
        - TPort `/tport search tport <mode> <TPort name>` <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_tport.png?raw=true" title="Search for TPorts by their name" width="32"/><br />
          Search for TPorts by their name. You can use the 5 search modes.
        - world `/tport search world <world>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_world.png?raw=true "Search for TPorts by the world they are located in")<br />
          Search for TPorts by the world they are located in.
        - biome `/tport search biome <biome...>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_biome.png?raw=true "Search for TPorts in the biome they are located")<br />
          Search for TPorts in the biome they are located in.
        - biomePreset `/tport search biomePreset <preset>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_biome_preset.png?raw=true "Search for TPorts in the biome they are located, using a biome preset")<br />
          Search for TPorts in the biome they are located, using a biome preset.
        - description `/tport search description <mode> <description...>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_description.png?raw=true "Search for TPorts by their description")<br />
          Search for TPorts by their description. You can use the 6 search modes.
        - tag `/tport search tag <tag>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_tag.png?raw=true "Search for TPorts by their tags")<br />
          Search for TPorts by their tags.
        - canTP `/tport search canTP` <img height="32" src="https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_can_tp.png?raw=true" title="Search for TPorts you can teleport to" width="32"/><br />
          Search for TPorts you can teleport to.
        - dimension `/tport search dimension <dimension>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_dimension.png?raw=true "Search for TPorts by the dimension they are located in")<br />
          Search for TPorts by the dimension they are located in.
        - player `/tport search player <mode> <player name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_player.png?raw=true "Search for players")<br />
          Search for players. You can use the 6 search modes.
        - ownedTPorts `/tport search ownedTPorts <mode> <amount>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_player.png?raw=true "Search for players by their owned TPorts")<br />
          Search for players by their owned TPorts. This type is an integer search. You can use 4 search modes. (`ends with` and `starts with` not supported)

      There are 5 search modes. Not all search types use these modes. The 5 modes are:
        - equals ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_equal.png?raw=true "Equals search mode")<br />
          The full element must match the searched. This is the same for an integer search.
        - not equals ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_not_equal.png?raw=true "Not equals search mode")<br />
          The full element must not match the searched. This is the same for an integer search.
        - contains ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_contains.png?raw=true "Contains search mode")<br />
          The element must contain in the searched. For an integer search, the element must be bigger than the searched (example: `element = 10`, `searched = 5`. `10` contains `5`).
        - not contains ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_not_contains.png?raw=true "Not contains search mode")<br />
          The element must not contain in the searched. For an integer search, the element must be smaller than the searched (example: `element = 5`, `searched = 10`. `5` does not contain `10`).
        - starts with ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_starts.png?raw=true "Starts with search mode")<br />
          The searched must start with the query. This does not support integer search.
        - ends with ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_search_mode_ends.png?raw=true "Ends with search mode")<br />
          The searched must end with the query. This does not support integer search.
    - PublicTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/public_tp.png?raw=true)<br />

      [//]: # (    todo)
    - Log ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_log.png?raw=true "TPort log")<br />
      If you want to keep track on players teleporting to your TPorts you can use the logging feature.
      If you add a TPort to your log, TPort will keep track on all logged player teleporting to that TPort.
      Each TPort has a default log mode and a logged player list.
        - Default log mode `/tport log default <TPort name> <logMode>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_none.png?raw=true "Log mode none")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_all.png?raw=true "Log mode all")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_offline.png?raw=true "Log mode offline")![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_online.png?raw=true "Log mode online")<br />
          The default log mode is the general logging state of a TPort. This mode has 4 states:
            - None ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_none.png?raw=true "Log mode none")<br />
              The player will never be logged.
            - All ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_all.png?raw=true "Log mode all")<br />
              The player will always be logged. This is a logging mode.
            - Offline ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_offline.png?raw=true "Log mode offline")<br />
              Only when the owner is offline the player will be logged. This is a logging mode.
            - Online ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_mode_online.png?raw=true "Log mode online")<br />
              Only when the owner is online the player will be logged. This is a logging mode.
        - Adding / removing player to the TPort log list `/tport log <add|remove>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_edit.png?raw=true "Edit logged TPorts")<br />
          Each player can have a custom logging mode. If you alter the logging mode of a player, they will be added to the logged player list.
          This mode will override the default logging mode.
          To add a player use: `/tport log add <TPort name> <player:[logmode]...>` (example: `/tport log add home Herobrine:All`).
          To remove a player use: `/tport log remove <TPort name> <player...>`.
          A TPort is only logged if the default log mode is not set to `none`, or there is no player in the logged player list that has not the mode `none`.
        - Un-log a TPort `/tport log delete <TPort name>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_delete.png?raw=true "Unload a TPort")<br />
          To un-log a TPort you can set the default log mode to `none` and remove every player from the logged player list.
          Or you can use this command: `/tport log delete <TPort name>`.
        - Reading the log `/tport log read <TPort name> [player]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_read.png?raw=true "Reading the log of a TPort")<br />
          There are two ways to read a log. Via the chat or via a window. The default is via the window.
          The command `/tport log read <TPort name>` opens the log.
          In here you can filter by player (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_read_filter.png?raw=true "Player filter")).
          Or you can show the log in the chat (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_read_chat.png?raw=true "View log in chat")).
        - Clearing the log `/tport log clear <TPort name...>` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_clear.png?raw=true "Clear the log of a TPort")<br />
          To clear the log you can use this command: `/tport log clear <TPort name...>`. Or you can reset it in the logging window (![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/quick_edit_log_clear.png?raw=true "Clear the log of a TPort"))
        - Time formatting `/tport log timeFormat [format...]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_log_set_time_format.png?raw=true "Edit your time format for viewing the log")<br />
          You can customize the time format. With this you can add / remove information about the time of the log.
          There are 22 elements to be used, and with the TPort Keyboard you can further customize the formatting.
          More information about these elements can be found [here](https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html).
        - Time zone `/tport log timeZone [timeZone]` ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_log_time_zone_id.png?raw=true "Edit your time zone for viewing the log")<br />
          If you live in a different time zone of the server, you can set your time zone so that the log will be more customized for you.

    - LookTP ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_features_look_tp.png?raw=true "LookTP")<br />
- **Customisation**<br />
    - Color Theme ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_color_theme.png?raw=true "Color theme")<br />
    - Resource Pack ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_resource_pack.png?raw=true "TPort resource pack")<br />
    - Language ![](https://github.com/JasperBouwman/TPort/blob/master/texture_generator/src/main/resources/icons/x32/settings_language.png?raw=true "Language")<br />

#### Admin
- reload
- features
    - BiomeTP
    - FeatureTP
    - BackTP
    - PublicTP
    - PLTP
    - Dynmap
    - Metrics
    - Permissions
    - ParticleAnimation
    - Redirects
    - History
    - Preview
    - WorldTP
    - TPortTakesItem
    - InterdimensionalTeleporting
    - DeathTP
    - LookTP
    - EnsureUniqueUUID
    - PrintErrorsInConsole
    - FeatureSettings
- BiomeTP
- Tags
- backup
- remove player
- redirect
    - ConsoleFeedback
    - TP_PLTP
    - Locate_featureTP
    - LocateBiome_biomeTP
    - Home_TPortHome
    - Back_TPortBack
- metrics
- dynmap
- cooldown
    - TPortTP
    - PlayerTP
    - FeatureTP
    - BiomeTP
    - Search
    - Back
    - LookTP
- publicTP
- Adapters
- Delay / Restriction
