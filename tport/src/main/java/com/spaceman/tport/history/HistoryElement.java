package com.spaceman.tport.history;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.history.HistoryEvents.teleportHistory;

public record HistoryElement(
        Location oldLocation,
        LocationSource newLocation,
        String cause,
        @Nullable String application) {
    
}