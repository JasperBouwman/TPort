package com.spaceman.tport.tpEvents.animations;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.keyValueHelper.ExtendedKey;
import com.spaceman.tport.keyValueHelper.KeyValueError;
import com.spaceman.tport.keyValueHelper.KeyValueHelper;
import com.spaceman.tport.keyValueHelper.KeyValueTabArgument;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;

public class ExplosionAnimation extends ParticleAnimation {
    
    private Particle particle = Particle.FLAME;
    private double radius = 5;
    private double resolution = 50;
    private boolean explosion = true;
    private double velocity = 0.25;
    
    @Override
    public String getAnimationName() {
        return "ExplosionAnimation";
    }
    
    public void setExplosion(boolean explosion) {
        this.explosion = explosion;
    }
    
    public void setParticle(Particle particle) {
        this.particle = particle;
    }
    
    public void setRadius(double radius) {
        this.radius = radius;
    }
    
    public void setResolution(double resolution) {
        this.resolution = resolution;
    }
    
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
    
    @Override
    public void show(Player player, Location l) {
        World world = l.getWorld();
        if (world == null) return;
        
        l = l.add(0, 1, 0);
        
        double two_pi = Math.PI * 2;
        double increment = two_pi / resolution;
        
        for (double phi = 0; phi < Math.PI; phi += increment) {
            double sinPhi = Math.sin(phi);
            double cosPhi = Math.cos(phi);
            for (double theta = 0; theta < two_pi; theta += increment) {
                double xOffset = Math.cos(theta);
                double zOffset = Math.sin(theta);
                
                try {
                    if (explosion)
                        world.spawnParticle(particle, l.getX(), l.getY(), l.getZ(), 0, (float) xOffset * sinPhi, (float) cosPhi, (float) zOffset * sinPhi, velocity);
                    else
                        world.spawnParticle(particle,
                                l.getX() + xOffset * sinPhi * radius,
                                l.getY() + cosPhi * radius,
                                l.getZ() + zOffset * sinPhi * radius,
                                0, (float) -xOffset * sinPhi, (float) -Math.cos(phi), (float) -zOffset * sinPhi, velocity);
                } catch (IllegalArgumentException ignore) {
                }
            }
        }
    }
    
    @Override
    public boolean edit(Player player, String[] data) {
        try {
            KeyValueHelper.extendedConstructObject(data[0], this,
                    (ExtendedKey) new ExtendedKey("particle", (s) -> Particle.valueOf(s.toUpperCase()), true, ((o, value) -> ((ExplosionAnimation) o).setParticle((Particle) value))).setErrorMessageID("tport.tpEvents.animations.explosionAnimation.notAParticle"),
                    (ExtendedKey) new ExtendedKey("explosion", Boolean::parseBoolean, true, (o, value) -> ((ExplosionAnimation) o).setExplosion((Boolean) value)).setErrorMessageID("tport.tpEvents.animations.explosionAnimation.notAState"),
                    (ExtendedKey) new ExtendedKey("radius", Double::parseDouble, true, (o, value) -> ((ExplosionAnimation) o).setRadius((Double) value)).setErrorMessageID("tport.tpEvents.animations.explosionAnimation.notANumber"),
                    (ExtendedKey) new ExtendedKey("velocity", Double::parseDouble, true, (o, value) -> ((ExplosionAnimation) o).setVelocity((Double) value)).setErrorMessageID("tport.tpEvents.animations.explosionAnimation.notANumber"),
                    (ExtendedKey) new ExtendedKey("resolution", Double::parseDouble, true, (o, value) -> ((ExplosionAnimation) o).setResolution((Double) value)).setErrorMessageID("tport.tpEvents.animations.explosionAnimation.notANumber")
            );
            return true;
        } catch (KeyValueError keyValueError) {
            keyValueError.sendMessage(player);
            return false;
        }
    }
    
    @Override
    public void save(ConfigurationSection section) {
        section.set("p", particle.name());
        section.set("e", explosion);
        section.set("ra", radius);
        section.set("re", resolution);
        section.set("v", velocity);
    }
    
    @Override
    public void load(ConfigurationSection section) {
        this.particle = (Particle.valueOf(section.getString("p", Particle.EXPLOSION_NORMAL.name()).toUpperCase()));
        this.explosion = (section.getBoolean("e", true));
        this.radius = (section.getDouble("ra", 5));
        this.resolution = (section.getDouble("re", 50));
        this.velocity = (section.getDouble("v", 0.3));
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return KeyValueHelper.constructTab(args[args.length - 1], Arrays.asList(
                new KeyValueTabArgument("particle", Arrays.stream(Particle.values()).map(Enum::name).collect(Collectors.toList())),
                new KeyValueTabArgument("explosion", Arrays.asList("true", "false")),
                new KeyValueTabArgument("radius", Collections.singletonList("<X>")),
                new KeyValueTabArgument("velocity", Collections.singletonList("<X>")),
                new KeyValueTabArgument("resolution", Collections.singletonList("<X>"))
        ));
    }
    
    @Override
    public Message getDescription() {
        return formatInfoTranslation("tport.tpEvents.animations.explosionAnimation.description", "explosion");
    }
}
