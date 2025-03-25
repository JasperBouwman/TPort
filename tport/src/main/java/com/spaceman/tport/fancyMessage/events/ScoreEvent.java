package com.spaceman.tport.fancyMessage.events;

import com.google.gson.JsonObject;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;

public class ScoreEvent implements TextEvent {
    
    private String name;
    private String objective;
    
    public ScoreEvent() {
    }
    
    @Override
    public String toString() {
        return "ScoreEvent{" +
                "name='" + name + '\'' +
                ", objective='" + objective + '\'' +
                '}';
    }
    
    public ScoreEvent(String name, String objective) {
        this.name = name;
        this.objective = objective;
    }
    
    public static ScoreEvent scoreEvent(String name, String objective) {
        return new ScoreEvent(name, objective);
    }
    
    @Override
    public JsonObject translateJSON(ColorTheme theme) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("objective", objective);
        return jsonObject;
    }
    
    @Override
    public String[] name() {
        return new String[]{"score"};
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getObjective() {
        return objective;
    }
    
    public void setObjective(String objective) {
        this.objective = objective;
    }
}
