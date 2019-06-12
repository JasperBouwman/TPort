package com.spaceman.tport.commands.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;

public class ColorThemeCommand extends SubCommand {
    
    @Override
    public String getName() {
        return "colorTheme";
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.stream(ColorTheme.values()).map(ColorTheme::name).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //command colorTheme [colorTheme]
        if (args.length == 1) {
            sendInfoTheme(player, "You are using %s as color theme", getTheme(player).name());
        } else {
            try {
                ColorTheme.setTheme(player, ColorTheme.valueOf(args[1]));
                sendSuccessTheme(player, "Edited your theme to %s", getTheme(player).name());
            } catch (IllegalArgumentException iae) {
                sendErrorTheme(player, "Given color theme does not exist");
            }
        }
    }
}
