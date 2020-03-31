package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Delay;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.events.InventoryClick.requestTeleportPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Random extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to teleport to a random biome", infoColor),
                textComponent("\n\nPermissions: ", infoColor), textComponent("TPort.biomeTP.random", varInfoColor),
                textComponent(" or ", infoColor), textComponent("TPort.biomeTP.all", varInfoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP random
        
        if (args.length == 2) {
            if (!hasPermission(player, true, true, "TPort.biomeTP.random", "TPort.biomeTP.all")) {
                return;
            }
            if (!CooldownManager.BiomeTP.hasCooled(player)) {
                return;
            }
            // todo world size
            // todo end/nether not safe
            java.util.Random random = new java.util.Random();
            int x = random.nextInt(6000000) - 3000000;
            int z = random.nextInt(6000000) - 3000000;
            requestTeleportPlayer(player, player.getWorld().getHighestBlockAt(x, z).getLocation().add(0.5, 0, 0.5));
            if (Delay.delayTime(player) == 0) {
                sendSuccessTheme(player, "Successfully teleported to a random location");
            } else {
                sendSuccessTheme(player, "Successfully requested teleportation to a random location");
            }
            CooldownManager.BiomeTP.update(player);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport biomeTP random");
        }
    }
}
