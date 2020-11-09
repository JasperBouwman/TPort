package com.spaceman.tport.fancyMessage.events;

import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.Message;
import org.json.simple.JSONObject;

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
    public JSONObject translateJSON(ColorTheme theme) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("objective", objective);
        return jsonObject;
    }
    
    @Override
    public String name() {
        return "score";
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
