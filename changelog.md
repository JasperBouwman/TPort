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



# test

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
