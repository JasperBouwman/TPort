package com.spaceman.tport.history;

import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.history.locationSource.LocationSource;
import org.bukkit.Location;

import javax.annotation.Nullable;

public record HistoryElement(
        Location oldLocation,
        LocationSource newLocation,
        String cause,
        @Nullable String application,
        @Nullable InventoryModel inventoryModel) {
    
}