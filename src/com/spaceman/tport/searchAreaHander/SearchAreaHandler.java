package com.spaceman.tport.searchAreaHander;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class SearchAreaHandler {
    
    private final ISearchArea searchArea;
    private final SubCommand configureCommand;
    private final String name;
    
    private static SearchAreaHandler searchAreaHandler = null;
    private static final HashMap<String, SearchAreaHandler> areas = new HashMap<>();
    
    public SearchAreaHandler(SubCommand configureCommand, ISearchArea searchArea) {
        this.configureCommand = configureCommand;
        this.searchArea = searchArea;
        this.name = configureCommand.getCommandName();
    }
    
    public String getName() {
        return name;
    }
    
    @Nullable
    public Location getRandomLocation(Player player) {
        Random random = new Random();
        
        Polygon polygon = getSearchArea(player);
        Rectangle bounds = polygon.getBounds();
        Location rLoc;
        
        for (int i = 0; i < 100; i++) {
            rLoc = new Location(player.getWorld(), random.nextInt(bounds.width) + bounds.x, 0, random.nextInt(bounds.height) + bounds.y);
            if (polygon.contains(rLoc.getX(), rLoc.getZ())) {
                return rLoc;
            }
        }
        
        return null;
    }
    
    @Nullable
    public Location getClosestLocation(Player player) {
        Polygon polygon = getSearchArea(player);
        
        if (polygon.contains(player.getLocation().getX(), player.getLocation().getZ())) {
            return player.getLocation();
        } else {
            /*
            * used code from https://stackoverflow.com/questions/8103451/ from answer from 'Peter'
            * */
            
            ArrayList<Line2D.Double> areaSegments = new ArrayList<>();
            Point2D.Double closestPoint = new Point2D.Double(-1, -1);
            Point2D.Double bestPoint = new Point2D.Double(-1, -1);
            Point2D.Double playerPoint = new Point2D.Double(player.getLocation().getX(), player.getLocation().getZ());
            
            // Note: we're storing double[] and not Point2D.Double
            ArrayList<double[]> areaPoints = new ArrayList<>();
            double[] coords = new double[6];
            
            for (PathIterator pi = polygon.getPathIterator(null); !pi.isDone(); pi.next()) {
                
                // Because the Area is composed of straight lines
                int type = pi.currentSegment(coords);
                // We record a double array of {segment type, x coord, y coord}
                double[] pathIteratorCoords = {type, coords[0], coords[1]};
                areaPoints.add(pathIteratorCoords);
            }
            
            double[] start = new double[3]; // To record where each polygon starts
            for (int i = 0; i < areaPoints.size(); i++) {
                // If we're not on the last point, return a line from this point to the next
                double[] currentElement = areaPoints.get(i);
                
                // We need a default value in case we've reached the end of the ArrayList
                double[] nextElement = {-1, -1, -1};
                if (i < areaPoints.size() - 1) {
                    nextElement = areaPoints.get(i + 1);
                }
                
                // Make the lines
                if (currentElement[0] == PathIterator.SEG_MOVETO) {
                    start = currentElement; // Record where the polygon started to close it later
                }
                
                if (nextElement[0] == PathIterator.SEG_LINETO) {
                    areaSegments.add(
                            new Line2D.Double(
                                    currentElement[1], currentElement[2],
                                    nextElement[1], nextElement[2]
                            )
                    );
                } else if (nextElement[0] == PathIterator.SEG_CLOSE) {
                    areaSegments.add(
                            new Line2D.Double(
                                    currentElement[1], currentElement[2],
                                    start[1], start[2]
                            )
                    );
                }
            }
            
            // Calculate the nearest point on the edge
            for (Line2D.Double line : areaSegments) {
                
                // From: https://stackoverflow.com/questions/6176227
                double u =
                        ((playerPoint.getX() - line.x1) * (line.x2 - line.x1) + (playerPoint.getY() - line.y1) * (line.y2 - line.y1))
                                / ((line.x2 - line.x1) * (line.x2 - line.x1) + (line.y2 - line.y1) * (line.y2 - line.y1));
                
                if (u < 0) {
                    closestPoint.setLocation(line.getP1());
                } else if (u > 1) {
                    closestPoint.setLocation(line.getP2());
                } else {
                    double xu = line.x1 + u * (line.x2 - line.x1);
                    double yu = line.y1 + u * (line.y2 - line.y1);
                    
                    closestPoint.setLocation(xu, yu);
                }
                
                if (closestPoint.distance(playerPoint) < bestPoint.distance(playerPoint)) {
                    bestPoint.setLocation(closestPoint);
                }
            }
            
            return new Location(player.getLocation().getWorld(), bestPoint.getX(), player.getLocation().getY(), bestPoint.getY());
        }
    }
    
    public Polygon getSearchArea(Player player) {
        return searchArea.get(player);
    }
    
    public SubCommand getConfigureCommand() {
        return configureCommand;
    }
    
    public static boolean addAreaHandler(ISearchArea searcher, SubCommand configureCommand) {
        if (searcher != null && !areas.containsKey(configureCommand.getCommandName())) {
            SearchAreaHandler handler = new SearchAreaHandler(configureCommand, searcher);
            areas.put(configureCommand.getCommandName().toLowerCase(), handler);
            return true;
        }
        return false;
    }
    
    public static Set<String> getAreas() {
        return areas.keySet();
    }
    
    public static SearchAreaHandler getSearchAreaHandler(String areaHandler) {
        return areas.getOrDefault(areaHandler.toLowerCase(), null);
    }
    
    public static SearchAreaHandler getSearchAreaHandler() {
        if (searchAreaHandler == null) {
            Files tportConfig = GettingFiles.getFile("TPortConfig");
            searchAreaHandler = getSearchAreaHandler(Main.getOrDefault(tportConfig.getConfig().getString("searchArea"), "worldborder"));
        }
        return searchAreaHandler;
    }
    
    public static void setSearchAreaHandler(SearchAreaHandler searchAreaHandler) {
        Files tportConfig = GettingFiles.getFile("TPortConfig");
        tportConfig.getConfig().set("searchArea", searchAreaHandler.getName());
        tportConfig.saveConfig();
        SearchAreaHandler.searchAreaHandler = searchAreaHandler;
    }
    
    @FunctionalInterface
    public interface ISearchArea {
        @Nonnull
        Polygon get(Player player);
    }
}