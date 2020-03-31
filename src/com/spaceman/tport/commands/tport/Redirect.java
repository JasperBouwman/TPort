package com.spaceman.tport.commands.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Redirect extends SubCommand {
    
    public Redirect() {
        EmptyCommand emptyRedirectState = new EmptyCommand();
        emptyRedirectState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyRedirectState.setCommandDescription(textComponent("This command is used to set the state of the given redirect", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.redirect.set", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.admin.redirect", ColorTheme.ColorType.varInfoColor));
        
        EmptyCommand emptyRedirect = new EmptyCommand();
        emptyRedirect.setCommandName("redirect", ArgumentType.REQUIRED);
        emptyRedirect.setCommandDescription(textComponent("This command is used to get the description and state of the given redirect." +
                        "\nFor more information about redirect use ", ColorType.infoColor),
                textComponent("/tport help redirects", ColorType.varInfoColor));
        emptyRedirect.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyRedirect.addAction(emptyRedirectState);
        addAction(emptyRedirect);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.stream(Redirects.values()).map(Enum::name).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport redirect <redirect> [state]
        
        if (args.length == 2) {
            Redirects redirect = Redirects.get(args[1]);
            if (redirect != null) {
                sendInfoTheme(player, "Redirection %s is: %s", redirect.name(), (redirect.isEnabled() ? "enabled" : "disabled"));
                sendInfoTheme(player, "Description of redirection %s:", redirect.name());
                redirect.getDescription().sendMessage(player);
            } else {
                sendErrorTheme(player, "Given redirect does not exist");
            }
        } else if (args.length == 3) {
            if (!hasPermission(player, true, true, "TPort.redirect.set", "TPort.admin.redirect")) {
                return;
            }
            
            Redirects redirect = Redirects.get(args[1]);
            if (redirect != null) {
                if (args[2].equalsIgnoreCase("true")) {
                    redirect.setEnabled(true);
                    sendSuccessTheme(player, "Successfully enabled redirect %s", redirect.name());
                } else if (args[2].equalsIgnoreCase("false")) {
                    sendSuccessTheme(player, "Successfully disabled redirect %s", redirect.name());
                    redirect.setEnabled(false);
                } else {
                    sendErrorTheme(player, "Given state %s must be %s or %s", args[2], "true", " false");
                }
            } else {
                sendErrorTheme(player, "Redirect %s does not exist", args[1]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport redirect <redirect> [state]");
        }
    }
    
    public enum Redirects {
        ConsoleFeedback(true, new Message(textComponent("When enabled the server console receives redirection logs", ColorTheme.ColorType.infoColor))),
        TP_PLTP(true, new Message(textComponent("Redirects the Minecraft command ", ColorTheme.ColorType.infoColor),
                textComponent("/tp <player>", ColorTheme.ColorType.varInfoColor),
                textComponent(" to ", ColorTheme.ColorType.infoColor),
                textComponent("/tport PLTP tp <player>", ColorTheme.ColorType.varInfoColor))),
        Locate_FeatureTP(true, new Message(textComponent("Redirects the Minecraft command ", ColorTheme.ColorType.infoColor),
                textComponent("/locate <StructureType>", ColorTheme.ColorType.varInfoColor),
                textComponent(" to ", ColorTheme.ColorType.infoColor),
                textComponent("/tport FeatureTP <feature>", ColorTheme.ColorType.varInfoColor))),
        LocateBiome_BiomeTP(true, new Message(textComponent("Redirects the Minecraft command ", ColorTheme.ColorType.infoColor),
                textComponent("/locateBiome <biome>", ColorTheme.ColorType.varInfoColor),
                textComponent(" to ", ColorTheme.ColorType.infoColor),
                textComponent("/tport BiomeTP whitelist <biome>", ColorTheme.ColorType.varInfoColor))),
        Home_TPortHome(false, new Message(textComponent("Redirects the command ", ColorTheme.ColorType.infoColor),
                textComponent("/home", ColorTheme.ColorType.varInfoColor),
                textComponent(" to ", ColorTheme.ColorType.infoColor),
                textComponent("/tport home", ColorTheme.ColorType.varInfoColor),
                textComponent(". Most likely to be used when another plugin is installed that has the command ", ColorType.infoColor),
                textComponent("/home", ColorType.varInfoColor),
                textComponent(", but you (as admin) prefer the mechanics of the TPort home system", ColorType.infoColor))),
        Back_TPortBack(false, new Message(textComponent("Redirects the command ", ColorTheme.ColorType.infoColor),
                textComponent("/back", ColorTheme.ColorType.varInfoColor),
                textComponent(" to ", ColorTheme.ColorType.infoColor),
                textComponent("/tport back", ColorTheme.ColorType.varInfoColor),
                textComponent(". Most likely to be used when another plugin is installed that has the command ", ColorType.infoColor),
                textComponent("/back", ColorType.varInfoColor),
                textComponent(", but you (as admin) prefer the mechanics of the TPort back system", ColorType.infoColor)));
        
        private boolean enabled;
        private Message description;
        
        Redirects(boolean defaultState, Message description) {
            this.enabled = defaultState;
            this.description = description;
        }
        
        public static void saveRedirects() {
            Files tportConfig = getFile("TPortConfig");
            for (Redirects redirect : Redirects.values()) {
                tportConfig.getConfig().set("redirects." + redirect.name(), redirect.enabled);
            }
            tportConfig.saveConfig();
        }
        
        public static void loadRedirects() {
            Files tportConfig = getFile("TPortConfig");
            for (Redirects redirect : Redirects.values()) {
                redirect.enabled = tportConfig.getConfig().getBoolean("redirects." + redirect.name(), redirect.enabled);
            }
        }
        
        public static Redirects get(String name) {
            for (Redirects redirect : Redirects.values()) {
                if (redirect.name().equalsIgnoreCase(name)) {
                    return redirect;
                }
            }
            return null;
        }
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public Message getDescription() {
            return description;
        }
    }
}
