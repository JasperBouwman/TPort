package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;

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
